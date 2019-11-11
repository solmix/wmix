package org.solmix.rest.api;

import org.solmix.rest.annotation.API;
import org.solmix.rest.annotation.GET;
import org.solmix.rest.annotation.POST;

@API("/admin")
public class UserAdmin {

	@GET("user-name/:id")
	public String getUserName(String id) {
		
		return id;
	}
	@POST("update-user")
	public void updateUser(UserBean user) {
		System.out.print(user.getName());
	}
}
