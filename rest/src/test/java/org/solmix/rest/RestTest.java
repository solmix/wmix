package org.solmix.rest;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class RestTest extends AbstractWmixTests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() throws Exception{
		  prepareServlet("rest");
	        Assert.assertNotNull(component);
//	        invokePost("/rest/datax/1?a=b&d=e", "");
	        invoke("/rest/admin/user-name/1");
	        controller.service(request, response);
	        response.getOutputStream();
	}

}
