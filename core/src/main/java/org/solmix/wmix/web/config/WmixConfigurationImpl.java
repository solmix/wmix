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
package org.solmix.wmix.web.config;

import org.solmix.runtime.exchange.Processor;
import org.solmix.wmix.web.WmixConfiguration;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年2月2日
 */

public class WmixConfigurationImpl implements WmixConfiguration {

    private boolean productionMode;
    private Processor processor;
    private Processor faultProcessor;
    
    @Override
    public Processor getProcessor() {
        return processor;
    }
    
    public void setProcessor(Processor p){
        processor=p;
    }

    
    @Override
    public Processor getFaultProcessor() {
        return faultProcessor;
    }
    
    public void setFaultProcessor(Processor p){
        this.faultProcessor=p;
    }

   
    @Override
    public boolean isProductionMode() {
        return productionMode;
    }
    
    public void setProductionMode(boolean productionMode) {
        this.productionMode=productionMode;
    }

    
    @Override
    public ComponentsConfig getComponentsConfig() {
        // TODO Auto-generated method stub
        return null;
    }

}
