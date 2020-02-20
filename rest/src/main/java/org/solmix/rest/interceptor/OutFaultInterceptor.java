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
import org.solmix.rest.exception.WebException;
import org.solmix.rest.http.HttpStatus;
import org.solmix.rest.route.RouteRepositoryImpl;
import org.solmix.runtime.exception.InvokerException;
import org.solmix.wmix.exchange.WmixMessage;
import org.solmix.wmix.mapper.MapperService;

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
		WebException we = getWebException(e, 0);

		try {
			if (we != null) {
				response.sendError(we.getStatus().getCode());
			} else {
				int error = handleException(e);
				response.sendError(error);
			}

		} catch (IOException e2) {
			LOG.warn("Error send error code:" + StringUtils.toString(e2));
		}
	}

	private WebException getWebException(Throwable e, int count) {
		if (e == null) {
			return null;
		} else if (e instanceof WebException) {
			return (WebException) e;
		} else if (count < 10) {
			return getWebException(e.getCause(), count++);
		} else {
			return null;
		}
	}

	private int handleException(Exception e) {
		int code = HttpStatus.INTERNAL_SERVER_ERROR.getCode();
		if (e != null) {
			if (e instanceof Fault) {
				Integer c;
				if (e.getCause() instanceof InvokerException && e.getCause().getCause() != null) {
					c = mapperString(e.getCause().getCause());
				} else {
					c = mapperString(e.getCause());
				}
				if (c != null) {
					code = c.intValue();
				}
			}
		}
		return code;
	}

	private Integer mapperString(java.lang.Throwable e) {
		if (mapperService != null) {
			String exception = e.getClass().getName();
			try {
				String code = mapperService.map("exception", exception);
				return Integer.valueOf(code);
			} catch (Exception e1) {

			}
		}
		return null;
	}

}
