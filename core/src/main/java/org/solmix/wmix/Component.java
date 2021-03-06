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

import java.util.List;

import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerAware;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年2月1日
 */

public interface Component extends ContainerAware {

    /**
     * 组件在注册的时候标示自己应该属于那个components,components在添加组件的时候只找属于自己的.
     */
    String COMP_BELONG_TO="org.solmix.wmix.components";
    /**
     * 所属Components集合。
     * @return
     */
    Components getComponents();
    
    void setComponents(Components components);
    
    /**
     * 获取Component名称。
     * @return
     */
    String getName();
    
    /**
     * 获取Components path，为默认Component返回空字符串。
     * 
     * @return
     */
    String getComponentPath();
    
    /**
     * 获取Component的配置。
     * 
     * @return
     */
    ComponentConfig getComponentConfig();
    
    /**
     * 处理当前Component的controller。
     * 
     * @return
     */
    Controller getController();
    
    @Override
    void setContainer(Container container);

    Container getContainer();

    /**
     * @return
     */
    List<WmixEndpoint> getEndpoints();
}
