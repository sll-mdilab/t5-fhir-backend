package net.sllmdilab.t5fhir.resourceprovider;

import java.util.Date;
import java.util.List;

import net.sllmdilab.commons.converter.ObservationToT5XmlConverter;
import net.sllmdilab.commons.util.T5FHIRUtils;
import net.sllmdilab.t5fhir.service.ObservationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.model.dstu2.resource.Device;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;

@Component
public class ObservationResourceProvider implements IResourceProvider {

	@Autowired
	private ObservationService observationService;

	@Autowired
	private ObservationToT5XmlConverter observationToT5XmlConverter;

	@Override
	public Class<Observation> getResourceType() {
		return Observation.class;
	}

	@Search
	public List<Observation> search(
			@OptionalParam(name = Observation.SP_SUBJECT) TokenParam patientId,
			@OptionalParam(name = Observation.SP_DEVICE, chainWhitelist = { Device.SP_IDENTIFIER }, targetTypes = { Device.class }) ReferenceParam device,
			@OptionalParam(name = Observation.SP_DATE) DateRangeParam dateRange,
			@OptionalParam(name = Observation.SP_CODE) StringParam observationCode,
			@OptionalParam(name = "_summary") StringParam summary) throws Exception {
		TokenParam deviceId = getIdentifierOrNull(device);

		if ((patientId == null || patientId.isEmpty()) && deviceId == null) {
			throw new InvalidRequestException("Subject or device parameters must be present.");
		}

		Date start = T5FHIRUtils.getStartTimeFromNullableRange(dateRange);
		Date end = T5FHIRUtils.getEndTimeFromNullableRange(dateRange);

		if ("true".equalsIgnoreCase(T5FHIRUtils.getValueOrNull(summary))) {
			return observationService.searchObservationsSummaryForPatient(T5FHIRUtils.getValueOrNull(patientId),
					T5FHIRUtils.getValueOrNull(deviceId), start, end);
		} else {
			if (observationCode == null) {
				throw new InvalidRequestException("Observation type code must be present.");
			} else {
				return observationService.searchObservationsForPatient(T5FHIRUtils.getValueOrNull(patientId),
						T5FHIRUtils.getValueOrNull(deviceId), observationCode.getValue(), start, end);
			}
		}
	}

	private TokenParam getIdentifierOrNull(ReferenceParam device) {
		TokenParam deviceId = null;
		if (device != null) {
			String chain = device.getChain();
			if (Device.SP_IDENTIFIER.equals(chain)) {
				deviceId = device.toTokenParam();
			}
		}
		return deviceId;
	}
}
