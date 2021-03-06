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

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.solmix.wmix.Components;
import org.solmix.wmix.RootController;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年2月21日
 */

public class MockRootController implements RootController {

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.wmix.RootController#init(org.solmix.wmix.Components)
     */
    @Override
    public void init(Components components) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public void service(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
