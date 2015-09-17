package net.sllmdilab.t5fhir.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import net.sllmdilab.commons.converter.ObservationToT5XmlConverter;
import net.sllmdilab.commons.converter.T5QueryToFHIRConverter;
import net.sllmdilab.commons.database.MLDBClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class) 
public class ObservationServiceTest {

	private static final String MDC_MOCK_CODE = "MDC_ECG_CARD_BEAT_RATE";
	private static final String MOCK_DEVICE_ID = "2535j2lkh5h2h53";
	private static final String DEFAULT_CODE_SYSTEM = "MDC";
	private static final String PATIENT_ID = "1912-121212";
	
	private static final Date start = new Date(1337);
	private static final Date end = new Date(424242);

	@Before
	public void init() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(mldbClient.getTrends(any(), any(), any(), any())).thenReturn("<trend />");
		when(mldbClient.getTrendsForDevice(any(), any(), any(), any())).thenReturn("<trend />");
		when(mldbClient.getObservationSummary(any(), any(), any())).thenReturn("<trend />");
		when(mldbClient.getObservationSummaryForDevice(any(), any(), any())).thenReturn("<trend />");
	}

	@Mock
	private T5QueryToFHIRConverter converter;
	
	@Mock
	private ObservationToT5XmlConverter observationToT5XmlConverter;

	@Mock
	private MLDBClient mldbClient;
	
	@InjectMocks
	private ObservationService observationService;

	@Test
	public void searchForPatientCallsDbAndConverter() throws Exception {

		observationService.searchObservationsForPatient(PATIENT_ID, null, MDC_MOCK_CODE, start, end);
		verify(mldbClient)
				.getTrends(eq(PATIENT_ID), eq(MDC_MOCK_CODE), eq(start), eq(end));
		verify(converter).convertToObservations(eq(PATIENT_ID), eq(null), eq(MDC_MOCK_CODE), eq(DEFAULT_CODE_SYSTEM), any());
	}
	
	@Test
	public void searchSummaryForPatientCallsDbAndConverter() throws Exception {
		observationService.searchObservationsSummaryForPatient(PATIENT_ID, null, start, end);
		verify(mldbClient)
				.getObservationSummary(eq(PATIENT_ID), eq(start), eq(end));
		verify(converter).convertToObservationSummary(any());
	}
	
	@Test
	public void searchForDeviceCallsDbAndConverter() throws Exception {

		observationService.searchObservationsForPatient(null, MOCK_DEVICE_ID , MDC_MOCK_CODE, start, end);
		verify(mldbClient)
				.getTrendsForDevice(eq(MOCK_DEVICE_ID), eq(MDC_MOCK_CODE), eq(start), eq(end));
		verify(converter).convertToObservations(eq(null), eq(MOCK_DEVICE_ID), eq(MDC_MOCK_CODE), eq(DEFAULT_CODE_SYSTEM), any());
	}
	
	@Test
	public void searchSummaryWithoutDatesForDeviceCallsDbAndConverter() throws Exception {
		observationService.searchObservationsSummaryForPatient(null, MOCK_DEVICE_ID, start, end);

		verify(mldbClient).getObservationSummaryForDevice(eq(MOCK_DEVICE_ID), eq(start), eq(end));
		verify(converter).convertToObservationSummary(any());
	}
}
