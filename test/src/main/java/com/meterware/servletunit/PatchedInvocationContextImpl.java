
package com.meterware.servletunit;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;

import org.solmix.wmix.test.TestUtils;

import com.meterware.httpunit.FrameSelector;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class PatchedInvocationContextImpl extends InvocationContextImpl {
    @SuppressWarnings("rawtypes")
    PatchedInvocationContextImpl(ServletUnitClient client, ServletRunner runner, FrameSelector frame,
                                 WebRequest request, Dictionary clientHeaders, byte[] messageBody) throws IOException,
                                                                                                          MalformedURLException {
        super(client, runner, frame, request, clientHeaders, messageBody);
    }

    @Override
    public WebResponse getServletResponse() throws IOException {
        try {
            Field _webResponse = TestUtils.getAccessibleField(getClass(), "_webResponse");
            boolean newWebResponse = _webResponse.get(this) == null;

            super.getServletResponse();

            if (newWebResponse) {
                _webResponse.set(this, new PatchedServletUnitWebResponse( //
                		TestUtils.getFieldValue(this, "_client", ServletUnitClient.class), //
                		TestUtils.getFieldValue(this, "_frame", FrameSelector.class), //
                		TestUtils.getFieldValue(this, "_effectiveURL", URL.class), //
                		getResponse(), 
                		TestUtils.getFieldValue(this, "_client", ServletUnitClient.class)
                        .getExceptionsThrownOnErrorStatus()));
            }

            return (WebResponse) _webResponse.get(this);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
