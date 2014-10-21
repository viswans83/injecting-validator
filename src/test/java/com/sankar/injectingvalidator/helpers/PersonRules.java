package com.sankar.injectingvalidator.helpers;

import java.util.List;

import com.google.inject.Inject;
import com.sankar.injectingvalidator.Optional;
import com.sankar.injectingvalidator.Path;
import com.sankar.injectingvalidator.Result;
import com.sankar.injectingvalidator.Rule;

public class PersonRules {
	
	private SSNService ssnService;
	
	@Inject
	public PersonRules(SSNService ssnService) {
		this.ssnService = ssnService;
	}
	
	@Rule("ssn_required")
	public void validate_ssn_present(@Path("ssn") @Optional String ssn, Result result) {
		if (ssn == null)
			result.fail("SSN is required");
	}
	
	@Rule("ssn_length")
	public void validate_ssn_length(@Path("ssn") String ssn, Result result) {
		if (ssn.length() != 9)
			result.fail("SSN should be exactly {0} characters", 9);
	}
	
	@Rule("ssn_unique")
	public void validate_ssn_unique(@Path("ssn") String ssn, Result result) {
		if (ssnService.isKnown(ssn))
			result.fail("There is already an employee registered with SSN {0}", ssn);
	}
	
	@Rule("name_required")
	public void validate_name_required(@Path("name") @Optional String name, Result result) {
		if (name == null)
			result.fail("Name is required");
	}
	
	@Rule("name_length")
	public void validate_name_length(@Path("name") String name, Result result) {
		if (name.length() > 25)
			result.fail("Name is too long");
	}
	
	@Rule("alternate_names_not_different")
	public void validate_alternate_names(@Path("name") String givenName, @Path("alternateNames") @Optional List<String> names, Result result) {
		if (names != null && names.size() > 0)
			if (names.contains(givenName))
				result.fail("Alternate names should differ from given name");
	}
	
	@Rule("max_alternate_names")
	public void validate_max_alternate_names(@Path("alternateNames") List<String> names, Result result) {
		if (names.size() > 2)
			result.fail("Only {0} alternate names can be provided", 2);
	}
	
	@Rule("street_address_length")
	@Path("address")
	public void validate_address_streets(@Path("street1") String street1, @Path("street2") String street2, Result result) {
		if (street1.length() > 30 || street2.length() > 30)
			result.fail("Address lines can be a maximum of {0} characters long", 30);
	}

}
