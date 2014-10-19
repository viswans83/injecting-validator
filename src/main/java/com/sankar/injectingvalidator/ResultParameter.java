package com.sankar.injectingvalidator;

class ResultParameter implements Parameter {
	
	static ResultParameter INSTANCE = new ResultParameter();
	
	private ResultParameter() {}

	@Override
	public boolean isResultParameter() {
		return true;
	}

	@Override
	public Object resolve(ValueResolver valueResolver) {
		throw new UnsupportedOperationException();
	}

}
