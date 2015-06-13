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
package org.solmix.wmix;

import org.solmix.runtime.Container;


/**
 * 一组{@link Component}信息。
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年2月1日
 */

public interface Components extends Iterable<Component>{

    /**
     * 获取所有{@link Component}名称。
     * 
     * @return
     */
    String[] getComponentNames();
    
    /**
     * 获取制定名称的{@link Component}
     * @param name
     * @return
     */
    Component getComponent(String name);
    
    /**
     * 获取默认{@link Component}。
     * 
     * @return 未设置返回null。
     */
    Component getDefaultComponent();
    
    /**
     * 根据路径查找与之匹配的{@link Component}。<br>
     * {@link RootController}中调用该方法将请求分发给其他Component。
     * 
     * @param path
     * @return
     */
    Component matchedComponent(String path);
    
    /**
     * 处理请求的Controller。
     * 
     * @return
     */
    RootController getRootController();
    
    /**
     * 取得Wmix 配置。
     * 
     * @return
     */
    WmixConfiguration getParentWmixConfiguration();
    
    /**
     * 所有{@link Component}的父{@link org.solmix.runtime.Container}。
     * 
     * @return
     */
    Container getParentContainer();
}
