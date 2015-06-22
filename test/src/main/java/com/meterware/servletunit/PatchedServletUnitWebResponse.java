package com.meterware.servletunit;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.solmix.wmix.test.TestUtils;

import com.meterware.httpunit.FrameSelector;
import com.meterware.httpunit.cookies.PatchedCookieJar;

public class PatchedServletUnitWebResponse extends ServletUnitWebResponse {
    public PatchedServletUnitWebResponse(ServletUnitClient client, FrameSelector frame, URL url,
                                         HttpServletResponse response, boolean throwExceptionOnError)
            throws IOException {
        super(client, frame, url, response, throwExceptionOnError);
        setCookieJar();
    }

    public PatchedServletUnitWebResponse(ServletUnitClient client, FrameSelector frame, URL url,
                                         HttpServletResponse response) throws IOException {
        super(client, frame, url, response);
        setCookieJar();
    }

    void setCookieJar() {
        try {
            Field _cookies = TestUtils.getAccessibleField(getClass(), "_cookies");

            if (_cookies.get(this) == null) {
                _cookies.set(this, new PatchedCookieJar(this));
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
