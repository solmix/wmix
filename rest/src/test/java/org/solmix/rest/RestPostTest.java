package org.solmix.rest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.solmix.rest.http.ContentType;

public class RestPostTest extends AbstractWmixTests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void skip() {
		
	}
	public void test() throws Exception{
		  prepareServlet("rest");
	        Assert.assertNotNull(component);
	        invokePost("/rest/admin/update-user", "{\"name\":\"aa\",\"time\":123,\"status\":1}",ContentType.JSON.value());
//	        invoke("/rest/admin/user-name/1");
	        controller.service(request, response);
	        response.getOutputStream();
	}

}
