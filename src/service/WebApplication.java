package service;

import javax.servlet.ServletContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

@ApplicationPath("/app/*")
public class WebApplication extends Application {

	public WebApplication(@Context ServletContext servletContext) {
		InitData.getInstance().Initialize(servletContext.getRealPath(""));
	}

}
