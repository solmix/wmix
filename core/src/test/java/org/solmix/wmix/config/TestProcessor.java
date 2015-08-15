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

import javax.annotation.Resource;

import org.solmix.exchange.ExchangeRuntimeException;
import org.solmix.exchange.Message;
import org.solmix.exchange.Processor;
import org.solmix.runtime.Container;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年2月2日
 */

public class TestProcessor implements Processor {

    @Resource
    private Container container;
    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Processor#process(org.solmix.exchange.Message)
     */
    @Override
    public void process(Message message) throws ExchangeRuntimeException {

    }
    
    public Container getContainer() {
        return container;
    }
    
    public void setContainer(Container container) {
        this.container = container;
    }

}
