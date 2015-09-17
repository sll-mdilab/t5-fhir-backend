package net.sllmdilab.t5fhir.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import net.sllmdilab.t5fhir.config.ApplicationConfig;
import net.sllmdilab.t5fhir.resourceprovider.ObservationResourceProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;

@Import(ApplicationConfig.class)
@Component
public class T5FHIRServlet extends RestfulServer {
	private static final long serialVersionUID = 1L;

	@Autowired
	private ObservationResourceProvider observationResourceProvider;
	
	@Override
	protected void initialize() throws ServletException {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		
		List<IResourceProvider> resourceProviders = new ArrayList<IResourceProvider>();
		
		resourceProviders.add(observationResourceProvider);
	
		setResourceProviders(resourceProviders);
	}
}
