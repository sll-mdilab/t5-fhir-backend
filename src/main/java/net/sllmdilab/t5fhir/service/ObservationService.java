package net.sllmdilab.t5fhir.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sllmdilab.commons.converter.T5QueryToFHIRConverter;
import net.sllmdilab.commons.database.MLDBClient;
import net.sllmdilab.commons.exceptions.XmlParsingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ca.uhn.fhir.model.dstu2.resource.Observation;

@Service
public class ObservationService {
	private Logger logger = LoggerFactory.getLogger(ObservationService.class);

	private static final String DEFAULT_CODE_SYSTEM = "MDC";
	
	@Autowired
	private MLDBClient mldbClient;
	
	@Autowired
	private T5QueryToFHIRConverter converter;
	
	private DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		return dbf.newDocumentBuilder();
	}

	public List<Observation> searchObservationsSummaryForPatient(String patientId, String deviceId, Date start, Date end) {
		logger.info("Fetching from DB.");
		long clockStartMillis = System.currentTimeMillis();

		String response;
		if (deviceId == null) {
			response = mldbClient.getObservationSummary(patientId, start, end);
		} else {
			response = mldbClient.getObservationSummaryForDevice(deviceId, start, end);
		}

		logger.info("Fetching from DB took " + (System.currentTimeMillis() - clockStartMillis) + " ms.");

		logger.info("Parsing response.");
		clockStartMillis = System.currentTimeMillis();
		Document parsedResponse;
		try {
			parsedResponse = getDocumentBuilder().parse(new InputSource(new StringReader(response)));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new XmlParsingException("Exception when parsing database response.", e);
		}
		logger.info("Parsing response took " + (System.currentTimeMillis() - clockStartMillis) + " ms.");

		logger.info("Converting to Observation objects.");
		clockStartMillis = System.currentTimeMillis();
		List<Observation> observations = converter.convertToObservationSummary(parsedResponse);
		logger.info("Conversion took " + (System.currentTimeMillis() - clockStartMillis) + " ms.");

		return observations;
	}

	public List<Observation> searchObservationsForPatient(String patientId, String deviceId,
			String observationCode, Date start, Date end) {

		logger.info("Fetching from DB.");
		long clockStartMillis = System.currentTimeMillis();

		String response;

		if (deviceId == null) {
			response = mldbClient.getTrends(patientId, observationCode, start,
					end);
		} else {
			response = mldbClient.getTrendsForDevice(deviceId, observationCode,
					start, end);
		}

		logger.info("Fetching from DB took " + (System.currentTimeMillis() - clockStartMillis) + " ms.");

		logger.info("Parsing response.");
		clockStartMillis = System.currentTimeMillis();
		Document parsedResponse;
		try {
			parsedResponse = getDocumentBuilder().parse(new InputSource(new StringReader(response)));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new XmlParsingException("Exception when parsing database response.", e);
		}
		logger.info("Parsing response took " + (System.currentTimeMillis() - clockStartMillis) + " ms.");

		logger.info("Converting to Observation objects.");
		clockStartMillis = System.currentTimeMillis();
		List<Observation> observations = converter.convertToObservations(patientId, deviceId,
				observationCode, DEFAULT_CODE_SYSTEM, parsedResponse);
		logger.info("Conversion took " + (System.currentTimeMillis() - clockStartMillis) + " ms.");

		return observations;
	}
}
