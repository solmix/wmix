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

package org.solmix.rest.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.StringUtils;
import org.solmix.exchange.Message;
import org.solmix.exchange.interceptor.Fault;
import org.solmix.exchange.interceptor.phase.Phase;
import org.solmix.exchange.interceptor.phase.PhaseInterceptorSupport;
import org.solmix.rest.http.HttpStatus;
import org.solmix.rest.route.RouteRepositoryImpl;
import org.solmix.runtime.exception.InvokerException;
import org.solmix.wmix.exchange.WmixMessage;
import org.solmix.wmix.mapper.MapperException;
import org.solmix.wmix.mapper.MapperService;
import org.solmix.wmix.mapper.MapperTypeNotFoundException;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年8月25日
 */

public class OutFaultInterceptor extends PhaseInterceptorSupport<Message> {

	private static final Logger LOG = LoggerFactory.getLogger(RouteRepositoryImpl.class);
	private MapperService mapperService;

	public OutFaultInterceptor() {
		super(Phase.MARSHAL);
	}

	public MapperService getMapperService() {
		return mapperService;
	}

	public void setMapperService(MapperService mapperService) {
		this.mapperService = mapperService;
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		final HttpServletResponse response = (HttpServletResponse) message.get(WmixMessage.HTTP_RESPONSE);
		Exception e = message.getContent(Exception.class);
		String error = handleException(e);
		try {
			response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.getCode(), error);
		} catch (IOException e2) {
			LOG.warn("Error send error code:" + StringUtils.toString(e2));
		}
	}

	private String handleException(Exception e) {
		if (e == null) {
			return "";
		} else {
			if (e instanceof Fault) {
				if (e.getCause() instanceof InvokerException && e.getCause().getCause() != null) {
					return mapperString(e.getCause().getCause());
				} else {
					return mapperString(e.getCause());
				}
			}
		}
		return StringUtils.toString(e);
	}

	private String mapperString(java.lang.Throwable e) {
		if (mapperService != null) {
			String exception = e.getClass().getName();
			try {
				return mapperService.map("exception", exception);
			} catch (MapperTypeNotFoundException e1) {
				return StringUtils.toString(e);
			} catch (MapperException e1) {
				return StringUtils.toString(e);
			}
		} else {
			return StringUtils.toString(e);
		}
	}

}
