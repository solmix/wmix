
package org.solmix.wmix.servlet;

import java.util.Enumeration;
import java.util.Set;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.solmix.commons.util.Assert;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.support.ServletContextResourceLoader;

public class WmixSpringFilter extends WmixFilter
{

 @Override
protected void initContext(FilterConfig config) throws ServletException {

     try {
         PropertyValues pvs = new FilterConfigPropertyValues(getFilterConfig(), requiredProperties);
         BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
         ResourceLoader resourceLoader = new ServletContextResourceLoader(getServletContext());
         bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader));
         initBeanWrapper(bw);
         bw.setPropertyValues(pvs, true);
     } catch (Exception e) {
         throw new ServletException("Failed to set bean properties on filter: " + getFilterName(), e);
     }

    }
   

    protected void initBeanWrapper(BeanWrapper bw) throws BeansException {
    }

    private static class FilterConfigPropertyValues extends MutablePropertyValues
    {

        private static final long serialVersionUID = -5359131251714023794L;

        public FilterConfigPropertyValues(FilterConfig config, Set<String> requiredProperties) throws ServletException
        {

            for (Enumeration<?> e = config.getInitParameterNames(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                String value = config.getInitParameter(key);

                addPropertyValue(new PropertyValue(key, value));
                requiredProperties.remove(key);
            }

            Assert.assertTrue(requiredProperties.isEmpty(),
                "Initialization for filter %s failed.  " + "The following required properties were missing: %s", config.getFilterName(),
                requiredProperties);
        }
    }
}
