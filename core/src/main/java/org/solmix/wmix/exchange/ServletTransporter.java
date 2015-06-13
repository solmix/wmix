
package org.solmix.wmix.exchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ServletUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.Container;
import org.solmix.runtime.exchange.Attachment;
import org.solmix.runtime.exchange.Exchange;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.MessageUtils;
import org.solmix.runtime.exchange.Pipeline;
import org.solmix.runtime.exchange.Transporter;
import org.solmix.runtime.exchange.attachment.AttachmentDataSource;
import org.solmix.runtime.exchange.model.EndpointInfo;
import org.solmix.runtime.exchange.support.AbstractTransporter;
import org.solmix.runtime.exchange.support.DefaultExchange;
import org.solmix.runtime.helper.HttpHeaderHelper;
import org.solmix.runtime.interceptor.Fault;
import org.solmix.runtime.interceptor.SuspendedException;
import org.solmix.runtime.io.AbstractWrappedOutputStream;
import org.solmix.runtime.io.CopyingOutputStream;
import org.solmix.runtime.io.DelegatingInputStream;
import org.solmix.runtime.io.IOUtils;

public class ServletTransporter extends AbstractTransporter implements Transporter
{

    private static final Logger LOG = LoggerFactory.getLogger(ServletTransporter.class);
    public static final String HTTP_REQUEST = "HTTP.REQUEST";
    public static final String HTTP_RESPONSE = "HTTP.RESPONSE";
    public static final String HTTP_REQUEST_METHOD="HTTP.REQUEST.METHOD";
    public static final String REQUEST_REDIRECTED = "http.request.redirected";
    protected boolean isServlet3;

    public ServletTransporter(Container c, EndpointInfo ei, String address)
    {
        super(address, ei, c);
        try {
            ServletRequest.class.getMethod("isAsyncSupported");
            isServlet3 = true;
        } catch (Throwable t) {
            // servlet 2.5 or earlier, no async support
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Transporter#shutdown()
     */
    @Override
    public void shutdown() {
        // for servlet nothing to do.

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Transporter#getBackPipeline(org.solmix.runtime.exchange.Message)
     */
    @Override
    public Pipeline getBackPipeline(Message msg) throws IOException {
        HttpServletResponse response =(HttpServletResponse)msg.get(HTTP_RESPONSE);
        return new BackPipeline(response);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.Transporter#getDefaultPort()
     */
    @Override
    public int getDefaultPort() {
       throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.runtime.exchange.support.AbstractTransporter#getLogger()
     */
    @Override
    protected Logger getLogger() {
        return LOG;
    }

    public void invoke(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Message msg = setupMessage(request,response);
        try{
            processor.process(msg);
        }catch(SuspendedException e){
            if(e.getRuntimeException()!=null){
                throw e.getRuntimeException();
            }
        } catch (Fault ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            } else {
                throw ex;
            }
        }catch(RuntimeException e){
            throw e;
        }finally{
            LOG.trace("Finished servicing Http request on Thread {}",Thread.currentThread());
        }
        
    }
    
    private WmixMessage setupMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WmixMessage msg = new WmixMessage();
        Exchange ex = new DefaultExchange();
        ex.setIn(msg);
        DelegatingInputStream in= new DelegatingInputStream(request.getInputStream());
        msg.setContent(DelegatingInputStream.class, in);
        msg.setContent(InputStream.class, in);
        msg.put(HTTP_REQUEST, request);
        msg.put(HTTP_RESPONSE, response);
        msg.put(HTTP_REQUEST_METHOD, request.getMethod());
        String reqUri=request.getRequestURI();
        msg.put(Message.REQUEST_URI, reqUri);
        String reqUrl=request.getRequestURL().toString();
        msg.put(Message.REQUEST_URL, reqUrl);
       
        msg.put(Message.PATH_INFO,ServletUtils.getResourcePath(request) );
        
        String basePath = ServletUtils.getBaseURL(request);
        if (!StringUtils.isEmpty(basePath)) {
            msg.put(Message.BASE_PATH, basePath);
        }
        
        String contentType = request.getContentType();
        msg.put(Message.CONTENT_TYPE, contentType);
        setEncoding(msg,request,contentType);
        
        msg.put(Message.QUERY_STRING, request.getQueryString());
        msg.put(Message.ACCEPT_CONTENT_TYPE, request.getHeader("Accept"));
        
      
        return msg;
    }
    
    protected String getBasePath(String contextPath) throws IOException {
        String address=endpointInfo.getAddress();
        if (StringUtils.isEmpty(address)) {
            return contextPath;
        }
        if(address.startsWith("http")){
            return URI.create(address).getPath();
        }
        return contextPath+address;
    }
    
    private String setEncoding(final Message inMessage, final HttpServletRequest req, final String contentType) throws IOException {

        String enc = HttpHeaderHelper.findCharset(contentType);
        if (enc == null) {
            enc = req.getCharacterEncoding();
        }
        
        if (enc != null && enc.endsWith("\"")) {
            enc = enc.substring(0, enc.length() - 1);
        }
        if (enc != null || "POST".equals(req.getMethod()) || "PUT".equals(req.getMethod())) {
            // allow gets/deletes/options to not specify an encoding
            String normalizedEncoding = HttpHeaderHelper.mapCharset(enc);
            if (normalizedEncoding == null) {
                LOG.warn("Invalid encoding {}",enc);
                throw new IOException("Invalid encoding "+enc);
            }
            inMessage.put(Message.ENCODING, normalizedEncoding);
        }
        return contentType;
    }
    
    protected OutputStream flushHeaders(Message outMessage) throws IOException {
        return flushHeaders(outMessage, true);
    }
    
    protected OutputStream flushHeaders(Message outMessage, boolean getStream) throws IOException {
        if (isResponseRedirected(outMessage)) {
            return null;
        }
        cacheInput(outMessage);
        
        OutputStream responseStream = null;
        boolean oneWay = isOneWay(outMessage);
        
        HttpServletResponse response = getHttpResponseFromMessage(outMessage);
        int responseCode = getReponseCodeFromMessage(outMessage);
        if (responseCode >= 300) {
            String ec = (String)outMessage.get(Message.ERROR_MESSAGE);
            if (!StringUtils.isEmpty(ec)) {
                response.sendError(responseCode, ec);
                return null;
            }
        }
        response.setStatus(responseCode);
        if (hasNoResponseContent(outMessage)) {
            response.setContentLength(0);
            response.flushBuffer();
            response.getOutputStream().close();
        } else if (!getStream) {
            response.getOutputStream().close();
        } else {
            responseStream = response.getOutputStream();                
        }

        if (oneWay) {
            outMessage.remove(HTTP_RESPONSE);
        }
        return responseStream;
    }
    private int getReponseCodeFromMessage(Message message) {
        Integer i = (Integer)message.get(Message.RESPONSE_CODE);
        if (i != null) {
            return i.intValue();
        } else {
            int code = hasNoResponseContent(message) ? HttpURLConnection.HTTP_ACCEPTED : HttpURLConnection.HTTP_OK;
            // put the code in the message so that others can get it
            message.put(Message.RESPONSE_CODE, code);
            return code;
        }
    }
    
    private boolean hasNoResponseContent(Message message) {
        final boolean ow = isOneWay(message);
        final boolean pr = MessageUtils.isPartialResponse(message);
        final boolean epr = MessageUtils.isEmptyPartialResponse(message);

        //REVISIT may need to provide an option to choose other behavior?
        // old behavior not suppressing any responses  => ow && !pr
        // suppress empty responses for oneway calls   => ow && (!pr || epr)
        // suppress additionally empty responses for decoupled twoway calls =>
        return (ow && !pr) || epr;
    }
    
    protected final boolean isOneWay(Message message) {
        Exchange ex = message.getExchange();
        return ex == null ? false : ex.isOneWay();
    }
    
    private HttpServletResponse getHttpResponseFromMessage(Message message) throws IOException {
        Object responseObj = message.get(HTTP_RESPONSE);
        if (responseObj instanceof HttpServletResponse) {
            return (HttpServletResponse)responseObj;
        } else if (null != responseObj) {
            LOG.warn("Unexpected http response {}",responseObj.getClass());
            throw new IOException("Unexpected http response");   
        } else {
            LOG.warn("Unexpected http response");
            throw new IOException("Null http response");
        }
    }

    /**
     * 输出时，保证输入已经被读取或缓存
     * @param outMessage
     */
    private void cacheInput(Message outMessage) {
        if (outMessage.getExchange() == null) {
            return;
        }
        Message inMessage = outMessage.getExchange().getIn();
        if (inMessage == null) {
            return;
        }
        Object o = inMessage.get("solmix.io.cacheinput");
        DelegatingInputStream in = inMessage.getContent(DelegatingInputStream.class);
        if (MessageUtils.isTrue(o)) {
            Collection<Attachment> atts = inMessage.getAttachments();
            if (atts != null) {
                for (Attachment a : atts) {
                    if (a.getDataHandler().getDataSource() instanceof AttachmentDataSource) {
                        try {
                            ((AttachmentDataSource)a.getDataHandler().getDataSource()).cache(inMessage);
                        } catch (IOException e) {
                            throw new Fault(e);
                        }
                    }
                }
            }
            if (in != null) {
                in.cacheInput();
            }
        }else  if (in != null){
            try {
                IOUtils.consume(in, 16 * 1024 * 1024);
            } catch (IOException ioe) {
                //ignore
            }
        }
    }
    private boolean isResponseRedirected(Message outMessage) {
        Exchange exchange = outMessage.getExchange();
        return exchange != null 
               && Boolean.TRUE.equals(exchange.get(REQUEST_REDIRECTED));
    }
    
    public class BackPipeline extends AbstractTransporter.AbstractBackPipeline{

        protected HttpServletResponse response;
        BackPipeline(HttpServletResponse response){
            this.response=response;
        }
        @Override
        public void prepare(Message message) throws IOException {
            message.put(HTTP_RESPONSE, response);
            OutputStream os = message.getContent(OutputStream.class);
            if (os == null) {
                message.setContent(OutputStream.class, 
                               new WrappedOutputStream(message, response));
            }
            
        }
        
    }
    private class WrappedOutputStream extends AbstractWrappedOutputStream implements CopyingOutputStream {
        protected HttpServletResponse response;
        private Message outMessage;
        
        WrappedOutputStream(Message m, HttpServletResponse resp) {
            super();
            this.outMessage = m;
            response = resp;
        }

        
        @Override
        public int copyFrom(InputStream in) throws IOException {
            if (!written) {
                onFirstWrite();
                written = true;
            }
            if (wrappedStream != null) {
                return IOUtils.copy(in, wrappedStream);
            }
            return IOUtils.copy(in, this, IOUtils.DEFAULT_BUFFER_SIZE);
        }
        
        /**
         * Perform any actions required on stream flush (freeze headers,
         * reset output stream ... etc.)
         */
        protected void onFirstWrite() throws IOException {
            OutputStream responseStream = flushHeaders(outMessage);
            if (null != responseStream) {
                wrappedStream = responseStream;
            }
        }

        /**
         * Perform any actions required on stream closure (handle response etc.)
         */
        public void close() throws IOException {
            if (!written && wrappedStream == null) {
                OutputStream responseStream = flushHeaders(outMessage, false);
                if (null != responseStream) {
                    wrappedStream = responseStream;
                }
            }
            if (wrappedStream != null) {
                wrappedStream.close();
                response.flushBuffer();
            }
        }
        
    }

    
}
