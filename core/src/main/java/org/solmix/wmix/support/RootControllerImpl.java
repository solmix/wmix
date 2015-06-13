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
package org.solmix.wmix.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.solmix.commons.util.ServletUtils;
import org.solmix.runtime.Container;
import org.solmix.wmix.Component;
import org.solmix.wmix.RootController;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年2月21日
 */

public class RootControllerImpl extends AbstractRootController implements RootController {

	@Override
	protected boolean invoke(HttpServletRequest request,HttpServletResponse response) throws Exception  {
		  String path = ServletUtils.getResourcePath(request);

	      Component component = getComponents().matchedComponent(path);
	      boolean served = false;
	      Container c= component.getContainer();
	      Component orign= c.getExtension(Component.class);
	      if (component != null) {
	            try {
	                c.setExtension(component, Component.class);
	                served = component.getController().service(request,response);
	            } finally {
	                if(orign!=null)
	            	c.setExtension(orign, Component.class);
	            }
	        }
		return served;
	}

  

}
