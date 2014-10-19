injecting-validator
===================

A method annotation (contrast with bean annotation) based validator inspired by JUnit.


**Define a class containing your validation rules**

````java
public class SSNRules {
	
	private SSNService ssnService;
	
	@Inject
	public SSNRules(SSNService ssnService) {
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

}
````

Any `public void` method annotated with `@Rule` is considered as validation rule. Rule methods have
inputs (arguments) annotated with `@Path` and `@Optional`. The framework supplies parameters that
the rule methods require to execute. A Rule method must also have a single argument `Result`
argument, which is supplied by the framework. This object allows you to signal rule failures.  Use
any conditional logic in your rule methods and call the `fail` method when you want the rule to
fail.


**Build the RuleSet object that will be passed into a Validator**

````java
// Keep just the two rules
RuleSet ssnRules1 = RuleSet.from(SSNRules.class).keep("ssn_required", "ssn_length");

// Keep all rules except the one
RuleSet ssnRules2 = RuleSet.from(SSNRules.class).remove("ssn_unique");
````


**Build a Validator instance**

````java
Validator validator = new Validator(ssnRules1);
````


**Now ask the validator to execute**

````java
// The object used to obtain instances of classes representing your validation rules
// (you can use a DI container)
DependencyResolver dependencyResolver = createDependencyResolver();

// The object that knows how to fetch parameters that your rules depend on
ValueResolver valueResolver = createValueResolver();

// The object that gets notified of rule failures
RuleFailureHandler results = createResultHolder();

// Run the validation rules
validator.runRules(dependencyResolver, valueResolver, results);

// Check for rule failures in the results object
````

The framework uses the supplied `DependencyResolver` to obtain an instances of the rule class. You
could have an implementation that *injects* dependencies required by your rule class and returns a
fully injected instance (Guice for example).

A single instance of the rule class is obtained, after which the framework will execute every rule
method contained in the `RuleSet` used when create the `Validator` instance. In order to supply
parameters for your rule methods, the framework utilizes the provided `ValueResolver` asking it to
return the value at the parameters access path (value of the `@Path` annotation).  Rule parameters
*not* marked with `@Optional` for which the value resolver cannot lookup a value (the lookup returns
null) are automatically skipped.

When the code in your rule methods invoke `fail` on the `Result` instance, the framework notifies
the `RuleFailureHandler`, passing it the ruleId along with the remaining parameters.
