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

import org.solmix.runtime.exchange.ExchangeException;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.Processor;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年2月2日
 */

public class TestProcessor implements Processor {

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Processor#process(org.solmix.runtime.exchange.Message)
     */
    @Override
    public void process(Message message) throws ExchangeException {

    }

}