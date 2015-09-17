package net.sllmdilab.t5fhir.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import net.sllmdilab.t5fhir.server.T5FHIRServlet;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class T5FHIRWebAppInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext container) {
		container.addFilter("CORS", "com.thetransactioncompany.cors.CORSFilter").addMappingForUrlPatterns(null, false,
				"/*");

		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(ApplicationConfig.class);

		container.addListener(new ContextLoaderListener(rootContext));

		ServletRegistration.Dynamic servlet = container.addServlet("dispatcher", new T5FHIRServlet());
		servlet.setLoadOnStartup(1);
		servlet.addMapping("/fhir/*");
	}
}
