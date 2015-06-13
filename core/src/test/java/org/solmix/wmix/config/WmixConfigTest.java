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

import org.junit.Assert;
import org.junit.Test;
import org.solmix.wmix.WmixConfiguration;
import org.solmix.wmix.WmixConfiguration.ComponentsConfig;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年2月2日
 */

public class WmixConfigTest extends Assert {

    @Test
    public void test() {
        String location = "org/solmix/wmix/web/config/container.xml";
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(location);
        assertNotNull(ctx);
        WmixConfiguration o = (WmixConfiguration) ctx.getBean("wmix-configuration");
        assertNotNull(o);
        assertTrue(o.isProductionMode());
        WmixConfiguration wcf = ctx.getBean(WmixConfiguration.class);
        assertNotNull(wcf);
        ComponentsConfig cc= wcf.getComponentsConfig();
        assertNotNull(cc.getRootController());
        assertTrue(cc.isAutoDiscovery());
        
        assertNotNull(cc.getComponents().get("app1").getController());

    }
}
