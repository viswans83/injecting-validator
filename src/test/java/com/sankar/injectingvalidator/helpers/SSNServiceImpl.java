package com.sankar.injectingvalidator.helpers;

public class SSNServiceImpl implements SSNService {

	@Override
	public boolean isKnown(String ssn) {
		if (ssn.equals("1234"))
			return true;
		else
			return false;
	}

}
