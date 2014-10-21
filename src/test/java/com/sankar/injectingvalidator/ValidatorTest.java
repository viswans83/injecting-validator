package com.sankar.injectingvalidator;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sankar.injectingvalidator.helpers.Address;
import com.sankar.injectingvalidator.helpers.Person;
import com.sankar.injectingvalidator.helpers.PersonRules;
import com.sankar.injectingvalidator.helpers.SSNModule;
import com.sankar.injectingvalidator.helpers.ValidationResultHolder;

public class ValidatorTest {
	
	RuleSet personruleSet = RuleSet.from(PersonRules.class).build();
	RuleSet testruleSet = RuleSet.from(TestRules.class).build();
	
	ValidationResultHolder results = new ValidationResultHolder();
	DependencyResolver dependencyResolver = createDependencyResolver();
	
	@Test
	public void validates_correctly() {
		Person person = new Person();
		person.setName("Sankaranarayanan Viswanathan Narayanaswamy");
		
		List<String> otherNames = Arrays.asList("Sankar", "Shankar", "Sankar V", "Sankaranarayanan Viswanathan Narayanaswamy");
		person.setAlternateNames(otherNames);
		
		ValueResolver valueResolver = createValueResolver(person, false);
		
		Validator validator = new Validator(personruleSet);
		validator.runRules(dependencyResolver, valueResolver, results);
		
		Assert.assertEquals(4, results.failureCount());
		Assert.assertTrue(results.gotFailure("ssn_required"));
		Assert.assertTrue(results.gotFailure("name_length"));
		Assert.assertTrue(results.gotFailure("max_alternate_names"));
		Assert.assertTrue(results.gotFailure("alternate_names_not_different"));
	}
	
	@Test
	public void all_validations_pass() {
		Object object = new Object();
		
		RuleSet rules = testruleSet.modify().keeping("generic_type").build();
		ValueResolver valueResolver = createValueResolver(object, false);
		
		Validator validator = new Validator(rules);
		validator.runRules(dependencyResolver, valueResolver, results);
	}
	
	@Test(expected = ValidationExecutionException.class)
	public void test_UnmatchedTypeException() {
		Object object = new Object();
		
		ValueResolver valueResolver = createValueResolver(object, true);
		
		Validator validator = new Validator(testruleSet);
		validator.runRules(dependencyResolver, valueResolver, results);
	}
	
	@Test
	public void test_custom_ruleId() {
		Object object = new Object();
		
		ValueResolver valueResolver = createValueResolver(object, false);
		
		Validator validator = new Validator(testruleSet);
		validator.runRules(dependencyResolver, valueResolver, results);
		
		Assert.assertEquals(1, results.failureCount());
		Assert.assertTrue(results.gotFailure("rule_without_id"));
	}
	
	@Test
	public void ruleSet_keeps_correctly() {
		Person person = new Person();
		person.setName("Sankaranarayanan Viswanathan Narayanaswamy");
		
		RuleSet personruleSet = RuleSet.from(PersonRules.class).keeping("name_length").build();
		ValueResolver valueResolver = createValueResolver(person, false);
		
		Validator validator = new Validator(personruleSet);
		validator.runRules(dependencyResolver, valueResolver, results);
		
		Assert.assertEquals(1, results.failureCount());
		Assert.assertTrue(results.gotFailure("name_length"));
	}
	
	@Test
	public void ruleSet_removes_correctly() {
		Person person = new Person();
		person.setName("Sankaranarayanan Viswanathan Narayanaswamy");
		
		List<String> otherNames = Arrays.asList("Sankar", "Shankar", "Sankar V", "Sankaranarayanan Viswanathan Narayanaswamy");
		person.setAlternateNames(otherNames);
		
		RuleSet personruleSet = RuleSet.from(PersonRules.class).removing("name_length", "alternate_names_not_different").build();
		ValueResolver valueResolver = createValueResolver(person, false);
		
		Validator validator = new Validator(personruleSet);
		validator.runRules(dependencyResolver, valueResolver, results);
		
		Assert.assertEquals(2, results.failureCount());
		Assert.assertTrue(results.gotFailure("ssn_required"));
		Assert.assertTrue(results.gotFailure("max_alternate_names"));
	}
	
	@Test
	public void resolves_relative_paths() {
		Address address = new Address();
		address.setStreet1("1 Testing Drive (near California blvd)");
		address.setStreet2("Route 10 N");
		
		Person person = new Person();
		person.setAddress(address);
		
		RuleSet rules = personruleSet.modify().keeping("street_address_length").build();
		ValueResolver valueResolver = createValueResolver(person, false);
		
		Validator validator = new Validator(rules);
		validator.runRules(dependencyResolver, valueResolver, results);
		
		Assert.assertEquals(1, results.failureCount());
		Assert.assertTrue(results.gotFailure("street_address_length"));
	}
	
	
	private DependencyResolver createDependencyResolver() {
		
		return new DependencyResolver() {
			
			final Injector injector = Guice.createInjector(new SSNModule());

			@Override
			public <T> T resolve(Class<T> clazz) {
				return injector.getInstance(clazz);
			}
			
		};
	}
	
	private ValueResolver createValueResolver(final Object context, final boolean throwUnmatchedTypeException) {
		
		return new ValueResolver() {

			@Override
			public Object lookup(String path, Class<?> type, Type genericType) throws UnmatchedTypeException {
				try {
					if (throwUnmatchedTypeException)
						throw new UnmatchedTypeException(path, genericType);
					
					else
						return PropertyUtils.getNestedProperty(context, path);
					
				} catch (UnmatchedTypeException e) {
					throw e;
				} catch (Exception e) {
					return null;
				}
			}

			@Override
			public Object lookup(String pathPrefix, String path, Class<?> type, Type genericType) throws UnmatchedTypeException {
				String completePath = pathPrefix + "." + path;
				return lookup(completePath, type, genericType);
			}
			
		};
	}

}

class TestRules {
	
	@Rule("generic_type")
	public void test_rule(@Path("test") List<String> names, Result result) {
		
	}
	
	@Rule
	public void test_rule_without_ruleId(Result result) {
		result.failRule("rule_without_id","Custom RuleId message");
	}
	
}