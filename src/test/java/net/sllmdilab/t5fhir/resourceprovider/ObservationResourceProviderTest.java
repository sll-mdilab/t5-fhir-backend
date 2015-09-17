package net.sllmdilab.t5fhir.resourceprovider;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.verify;

import java.util.Date;

import net.sllmdilab.t5fhir.resourceprovider.ObservationResourceProvider;
import net.sllmdilab.t5fhir.service.ObservationService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu.valueset.QuantityCompararatorEnum;
import ca.uhn.fhir.model.dstu2.resource.Device;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

@RunWith(MockitoJUnitRunner.class)
public class ObservationResourceProviderTest {
	private static final String MDC_MOCK_CODE = "MDC_ECG_CARD_BEAT_RATE";
	private static final String MOCK_DEVICE_ID = "2535j2lkh5h2h53";
	private static final String MOCK_PATIENT_ID = "1912121212";

	private static final Date start = new Date(1337);
	private static final Date end = new Date(424242);

	@Before
	public void init() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Mock
	private ObservationService observationService;

	@InjectMocks
	private ObservationResourceProvider observationResourceProvider;

	@Test
	public void searchWithoutDatesForDevice() throws Exception {

		ReferenceParam deviceParam = new ReferenceParam(Device.SP_IDENTIFIER, MOCK_DEVICE_ID);
		deviceParam.setValue(MOCK_DEVICE_ID);

		observationResourceProvider.search(null, deviceParam, null, new StringParam(MDC_MOCK_CODE), null);

		verify(observationService).searchObservationsForPatient((String) isNull(), eq(MOCK_DEVICE_ID), eq(MDC_MOCK_CODE),
				(Date) notNull(), (Date) notNull());
	}

	@Test
	public void searchSummaryWithStartDate() throws Exception {
		DateParam startDateParam = new DateParam(QuantityCompararatorEnum.GREATERTHAN_OR_EQUALS, start);
		startDateParam.setPrecision(TemporalPrecisionEnum.MILLI);

		DateRangeParam dateRangeParam = new DateRangeParam(startDateParam);

		TokenParam patientId = new TokenParam();
		patientId.setValue(MOCK_PATIENT_ID);
		observationResourceProvider.search(patientId, null, dateRangeParam, new StringParam(MDC_MOCK_CODE),
				new StringParam("true"));

		verify(observationService).searchObservationsSummaryForPatient(eq(MOCK_PATIENT_ID), (String) isNull(), eq(start),
				(Date) notNull());
	}

	@Test
	public void searchSummaryWithStartDateForDevice() throws Exception {
		DateParam startDateParam = new DateParam(QuantityCompararatorEnum.GREATERTHAN_OR_EQUALS, start);
		startDateParam.setPrecision(TemporalPrecisionEnum.MILLI);

		DateRangeParam dateRangeParam = new DateRangeParam(startDateParam);

		ReferenceParam deviceParam = new ReferenceParam(Device.SP_IDENTIFIER, MOCK_DEVICE_ID);
		deviceParam.setValue(MOCK_DEVICE_ID);

		observationResourceProvider.search(null, deviceParam, dateRangeParam, new StringParam(MDC_MOCK_CODE),
				new StringParam("true"));

		verify(observationService).searchObservationsSummaryForPatient((String) isNull(), eq(MOCK_DEVICE_ID), eq(start),
				(Date) notNull());
	}

	public void searchWithoutDatesForPatient() throws Exception {

		TokenParam patientId = new TokenParam();
		patientId.setValue(MOCK_PATIENT_ID);

		observationResourceProvider.search(patientId, null, null, new StringParam(MDC_MOCK_CODE), null);

		verify(observationService).searchObservationsForPatient(eq(MOCK_PATIENT_ID), (String) isNull(), eq(MDC_MOCK_CODE),
				(Date) notNull(), (Date) notNull());

	}

	@Test
	public void searchWithStartDateForPatient() throws Exception {
		DateParam startDateParam = new DateParam(QuantityCompararatorEnum.GREATERTHAN_OR_EQUALS, start);
		startDateParam.setPrecision(TemporalPrecisionEnum.MILLI);

		DateRangeParam dateRangeParam = new DateRangeParam(startDateParam);

		TokenParam patientId = new TokenParam();
		patientId.setValue(MOCK_PATIENT_ID);
		observationResourceProvider.search(patientId, null, dateRangeParam, new StringParam(MDC_MOCK_CODE), null);

		verify(observationService).searchObservationsForPatient(eq(MOCK_PATIENT_ID), (String) isNull(), eq(MDC_MOCK_CODE),
				eq(start), (Date) notNull());
	}

	@Test
	public void searchWithEndDateForPatient() throws Exception {
		DateParam endDateParam = new DateParam(QuantityCompararatorEnum.LESSTHAN_OR_EQUALS, end);
		endDateParam.setPrecision(TemporalPrecisionEnum.MILLI);
		DateRangeParam dateRangeParam = new DateRangeParam(endDateParam);

		TokenParam patientId = new TokenParam();
		patientId.setValue(MOCK_PATIENT_ID);
		observationResourceProvider.search(patientId, null, dateRangeParam, new StringParam(MDC_MOCK_CODE), null);

		verify(observationService).searchObservationsForPatient(eq(MOCK_PATIENT_ID), (String) isNull(), eq(MDC_MOCK_CODE),
				(Date) notNull(), eq(end));
	}
}
