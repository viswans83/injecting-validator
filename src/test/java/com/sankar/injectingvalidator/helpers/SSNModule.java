package com.sankar.injectingvalidator.helpers;

import com.google.inject.AbstractModule;


public class SSNModule extends AbstractModule {

	@Override
	public void configure() {
		bind(SSNService.class).to(SSNServiceImpl.class);
	}

}
