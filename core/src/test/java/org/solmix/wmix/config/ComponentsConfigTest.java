/**
 * Copyright (container) 2015 The Solmix Project
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

import org.junit.Assert;
import org.junit.Test;
import org.solmix.wmix.ComponentConfig;
import org.solmix.wmix.ComponentsConfig;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年2月2日
 */

public class ComponentsConfigTest extends Assert {

    @Test
    public void test() {
        String location = "org/solmix/wmix/config/rootComponents.xml";
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(location);
        assertNotNull(ctx);
        ComponentsConfig o = (ComponentsConfig) ctx.getBean(ComponentsConfig.DEFAULT_NAME);
        assertNotNull(o);
        ComponentsConfig wcf = ctx.getBean(ComponentsConfig.class);
        assertNotNull(wcf);
        assertNotNull(wcf.getRootController());
        assertTrue(wcf.isAutoDiscovery());
        assertEquals("value", wcf.getProperties().get("key"));
        
        assertNotNull(wcf.getComponents().get("app1").getController());
        assertTrue(wcf.getComponents().get("app1").getEndpoints().size()>=1);

    }
    @Test
    public void testComponent() {
        String location = "org/solmix/wmix/config/component.xml";
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(location);
        assertNotNull(ctx);
        ComponentConfig o = (ComponentConfig) ctx.getBean("app1");
        assertNotNull(o);
        assertEquals("app1", o.getName());
        ComponentConfig wcf = ctx.getBean(ComponentConfig.class);
        assertNotNull(wcf);
        assertNotNull(wcf.getController());

    }
}
