/**
 * Copyright (c) 2015 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */
package org.solmix.wmix.config;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.DOMUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.support.spring.AbstractBeanDefinitionParser;
import org.solmix.wmix.WmixConfiguration;
import org.solmix.wmix.WmixEndpoint;
import org.solmix.wmix.config.WmixConfigurationImpl.ComponentConfigImpl;
import org.solmix.wmix.config.WmixConfigurationImpl.ComponentsConfigImpl;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年2月2日
 */

public class WmixDefinitionParser extends AbstractBeanDefinitionParser
implements BeanDefinitionParser {
    private static AtomicInteger counter = new AtomicInteger(0);  
    public WmixDefinitionParser(){
        super();
        setBeanClass(WmixConfigurationImpl.class);
    }
 
    @Override
    protected String resolveId(Element element,
        AbstractBeanDefinition definition, ParserContext ctx) {
           String  name = element.getAttribute("name");
           if (StringUtils.isEmpty(name)) {
               name = WmixConfiguration.DEFAULT_NAME;
           }
           if(ctx.getRegistry().containsBeanDefinition(name)){
               name = name + counter.getAndIncrement();
           }
        return name;
    }
    
    @Override
    protected void parseElement(ParserContext ctx, BeanDefinitionBuilder bean,
        Element e, String name) {
        if("processor".equals(name)||"faultProcessor".equals(name)){
           firstChildAsProperty(e, ctx, bean, name);
        }else if("components".equals(name)){
            BeanDefinitionBuilder components = BeanDefinitionBuilder.genericBeanDefinition(ComponentsConfigImpl.class);
            parseAttributes(e, ctx, components);
            List<Element> els= DOMUtils.getChildrenWithName(e, e.getNamespaceURI(), "rootController");
            if(els!=null&&els.size()==1){
                firstChildAsProperty(els.get(0), ctx, components, "rootController");
            }
            ManagedMap<String, AbstractBeanDefinition> compMap = new ManagedMap<String, AbstractBeanDefinition>();

            compMap.setSource(ctx.getReaderContext().extractSource(e));

            List<Element> comps= DOMUtils.getChildrenWithName(e, e.getNamespaceURI(), "component");
            if(comps!=null){
                for(Element comp:comps){
                    String cname = Assert.assertNotNull(comp.getAttribute("name"),"no component name");
                    BeanDefinitionBuilder component = BeanDefinitionBuilder.genericBeanDefinition(ComponentConfigImpl.class);
                    parseAttributes(comp, ctx, component);
                    List<Element> cls= DOMUtils.getChildrenWithName(comp, comp.getNamespaceURI(), "controller");
                    if(cls!=null&&cls.size()==1){
                        firstChildAsProperty(cls.get(0), ctx, component, "controller");
                    }
                    List<Element> endpoints= DOMUtils.getChildrenWithName(comp, comp.getNamespaceURI(), "endpoint");
                    ManagedList< AbstractBeanDefinition> eps=paraseEndpints(ctx,endpoints);
                    component.addPropertyValue("endpoints", eps);
                    compMap.put(cname, component.getBeanDefinition());
                }
                components.addPropertyValue("components", compMap);
            }
            
            bean.addPropertyValue("componentsConfig", components.getBeanDefinition());
        }
    }

    private ManagedList<AbstractBeanDefinition> paraseEndpints(ParserContext ctx,List<Element> endpoints) {
        ManagedList<AbstractBeanDefinition> epMap = new ManagedList<AbstractBeanDefinition>();
        if (endpoints == null || endpoints.isEmpty()) {
            return epMap;
        }
        for(Element ep:endpoints){
            String cname = Assert.assertNotNull(ep.getAttribute("class"),"no specify endpoint class ");
            Class<?> endpintClass=null;
            try {
                endpintClass= ClassLoaderUtils.loadClass(cname, this.getClass());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.getException());
            }
            if(WmixEndpoint.class.isAssignableFrom(endpintClass)){
                throw new RuntimeException("class:"+cname+" type error");
            }
            BeanDefinitionBuilder endpoint = BeanDefinitionBuilder.genericBeanDefinition(endpintClass);
            parseAttributes(ep, ctx, endpoint);
            epMap.add(endpoint.getBeanDefinition());
        }
        return epMap;
    }

}
