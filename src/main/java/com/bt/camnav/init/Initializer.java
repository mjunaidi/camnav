package com.bt.camnav.init;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class Initializer implements WebApplicationInitializer {

	private static final String INITIALIZED = "initialized";

	public void onStartup(ServletContext servletContext)
			throws ServletException {

		String initialized = (String) servletContext.getAttribute(INITIALIZED);

		if (initialized == null || !INITIALIZED.equals(initialized)) {
			AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
			ctx.register(WebAppConfig.class);
			ctx.register(DataSourceConfig.class);

			servletContext.addListener(new ContextLoaderListener(ctx));

			ctx.setServletContext(servletContext);

			Dynamic servlet = servletContext.addServlet("dispatcher",
					new DispatcherServlet(ctx));
			if (servlet != null) {
				servlet.addMapping("/");
				servlet.setLoadOnStartup(1);
			}
			servletContext.setAttribute(INITIALIZED, INITIALIZED);

			initSecurityConfig(servletContext);
		}
	}
	
	private void initSecurityConfig(ServletContext servletContext) {
        // security filter
        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy("springSecurityFilterChain");
        FilterRegistration.Dynamic securityFilterDynamic = servletContext.addFilter("securityFilter", delegatingFilterProxy);

        if (securityFilterDynamic != null) {
            securityFilterDynamic.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        }
	}
}
