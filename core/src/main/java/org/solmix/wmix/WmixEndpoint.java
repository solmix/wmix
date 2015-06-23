/*
 * Copyright 2014 The Solmix Project
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

package org.solmix.wmix;

import org.solmix.exchange.Endpoint;
import org.solmix.wmix.condition.Condition;
import org.solmix.wmix.exchange.WmixMessage;

/**
 * 根据Condition判断Endpoint是否允许接收并处理Message。
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年6月12日
 */

public interface WmixEndpoint extends Endpoint
{

    /**
     * 在Controller初始化时调用。
     * @param controller
     */
    void init(Component component);
    Condition getCondition();

    void setCondition(Condition condition);

    void service(WmixMessage message);
}
