package net.sllmdilab.t5fhir.config;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.sllmdilab.commons.converter.ObservationToT5XmlConverter;
import net.sllmdilab.commons.converter.T5QueryToFHIRConverter;
import net.sllmdilab.commons.database.MLDBClient;
import net.sllmdilab.commons.exceptions.RosettaInitializationException;
import net.sllmdilab.commons.t5.validators.RosettaValidator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.exceptions.XccConfigException;

@Configuration
@ComponentScan({ "net.sllmdilab.t5fhir.*" })
public class ApplicationConfig {
	
	@Value("${T5_FHIR_DATABASE_HOST}")
	private String databaseHost;
	
	@Value("${T5_FHIR_DATABASE_PORT}")
	private String databasePort;
	
	@Value("${T5_FHIR_DATABASE_USER}")
	private String databaseUser;
	
	@Value("${T5_FHIR_DATABASE_PASSWORD}")
	private String databasePassword;
	
	@Value("${T5_FHIR_DATABASE_NAME}")
	private String databaseName;
	
    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setSystemPropertiesModeName("SYSTEM_PROPERTIES_MODE_OVERRIDE");
        return placeholderConfigurer;
    }
	
	@Bean
	public MLDBClient mldbClient() throws XccConfigException, URISyntaxException {
		return new MLDBClient(contentSource());
	}
	
	@Bean
	public RosettaValidator rosettaValidator() throws IOException, RosettaInitializationException {
		return new RosettaValidator();
	}
	
	@Bean
	public ContentSource contentSource() throws URISyntaxException, XccConfigException {
		URI uri = new URI("xcc://" + databaseUser + ":" + databasePassword
				+ "@" + databaseHost + ":" + databasePort + "/"
				+ databaseName);

		return ContentSourceFactory.newContentSource(uri);
	}
	
	@Bean
	public ObservationToT5XmlConverter observationToT5XmlConverter() {
		return new ObservationToT5XmlConverter();
	}

	@Bean
	public T5QueryToFHIRConverter t5QueryToFHIRConverter() throws Exception {
		return new T5QueryToFHIRConverter(rosettaValidator());
	}
}
