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

package org.solmix.wmix.config.spring;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.support.spring.AbstractBeanDefinitionParser;
import org.solmix.wmix.ComponentConfig;
import org.solmix.wmix.config.ComponentConfigImpl;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年2月2日
 */

public class ComponentDefinitionParser extends AbstractBeanDefinitionParser implements BeanDefinitionParser
{

    private static AtomicInteger counter = new AtomicInteger(0);

    public ComponentDefinitionParser()
    {
        super();
        setBeanClass(ComponentConfigImpl.class);
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext ctx) {
        String name = element.getAttribute("name");
        if (StringUtils.isEmpty(name)) {
            name = ComponentConfig.DEFAULT_NAME;
        }
        if (ctx.getRegistry().containsBeanDefinition(name)) {
            name = name + counter.getAndIncrement();
        }
        return name;
    }

    @Override
    protected void parseElement(ParserContext ctx, BeanDefinitionBuilder bean, Element e, String name) {
        if ("controller".equals(name)) {
            firstChildAsProperty(e, ctx, bean, "controller");
        } else if ("endpoints".equals(name)) {
            List<?> lis = ctx.getDelegate().parseListElement(e, bean.getBeanDefinition());
            bean.addPropertyValue("endpoints", lis);
        }
    }

    @Override
    protected void parseNameAttribute(Element element, ParserContext ctx, BeanDefinitionBuilder bean, String val) {
        parseAttribute(bean, element, "name", val, ctx);
    }
}
