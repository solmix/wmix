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

import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.solmix.commons.io.LocaleOutputStream;
import org.solmix.exchange.Endpoint;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.Message;
import org.solmix.exchange.Service;
import org.solmix.exchange.data.DataProcessor;
import org.solmix.exchange.data.ObjectWriter;
import org.solmix.exchange.interceptor.Fault;
import org.solmix.exchange.interceptor.phase.Phase;
import org.solmix.exchange.interceptor.phase.PhaseInterceptorSupport;
import org.solmix.wmix.exchange.WmixMessage;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年8月19日
 */

public class RestOutInterceptor extends PhaseInterceptorSupport<Message>
{

	/**
     * @param phase
     */
    public RestOutInterceptor()
    {
        super(Phase.MARSHAL);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        final Exchange exchange = message.getExchange();
        final Endpoint endpoint = exchange.get(Endpoint.class);
        final Service service = endpoint.getService();
        final DataProcessor dataProcessor = service.getDataProcessor();
        
        Object content_type =exchange.get(Message.ACCEPT_CONTENT_TYPE);
        Object encoding = exchange.get(Message.ENCODING);
        final HttpServletResponse response= (HttpServletResponse)message.get(WmixMessage.HTTP_RESPONSE);
        if(content_type!=null){
            response.setContentType(content_type.toString());
        }
        if(encoding!=null){
            response.setCharacterEncoding(encoding.toString());
        }
        
        OutputStream out= message.getContent(OutputStream.class);
        
        Object language =exchange.get(Message.ACCEPT_LANGUAGE);
        //no set used default
        if(language==null) {
        	language=Locale.getDefault().toLanguageTag();
        }
        LocaleOutputStream lout= new LocaleOutputStream(out, language.toString());
        //JSON两类，数组类或者key-value
        Object result = message.getContent(List.class);
        if (result == null) {
            result= message.getContent(Object.class);
        }
        try {
            ObjectWriter<OutputStream> writer= dataProcessor.createWriter(OutputStream.class);
            writer.write(result, lout);
        } finally{
            if(lout!=null){
                IOUtils.closeQuietly(lout);
            }
        }

    }

}
