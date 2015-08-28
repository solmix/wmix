/*
 * Copyright 2015 The Solmix Project
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

import javax.servlet.ServletContext;

import org.solmix.runtime.Container;
import org.solmix.wmix.context.WmixContextLoaderListener;


/**
 * 获取其他组件的Container。
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月25日
 */

public class ComponentLoader
{
    private ComponentLoader(){
        
    }

    /**
     * @param compoent 当前组件
     * @param other 指定组件的名称
     * @return 返回指定组件的Container
     */
    public static Container getContainer( Component compoent,String other){
        Container container = compoent.getContainer();
        ServletContext sc =container.getExtension(ServletContext.class);
        if(sc!=null){
           Object o= sc.getAttribute(WmixContextLoaderListener.CONTAINER_KEY+"_"+other);
           if(o instanceof Container)
               return (Container)o;
        }
        return null;
    }
}
