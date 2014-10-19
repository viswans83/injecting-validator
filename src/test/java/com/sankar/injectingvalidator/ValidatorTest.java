package com.sankar.injectingvalidator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sankar.injectingvalidator.helpers.Person;
import com.sankar.injectingvalidator.helpers.PersonRules;
import com.sankar.injectingvalidator.helpers.SSNModule;
import com.sankar.injectingvalidator.helpers.ValidationResultHolder;

public class ValidatorTest {
	
	RuleSet personruleSet = RuleSet.from(PersonRules.class);
	RuleSet testruleSet = RuleSet.from(TestRules.class);
	
	ValidationResultHolder results = new ValidationResultHolder();
	DependencyResolver dependencyResolver = createDependencyResolver();
	
	@Test
	public void validates_correctly() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
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
	
	@Test(expected = ValidationExecutionException.class)
	public void test_UnmatchedTypeException() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object object = new Object();
		
		ValueResolver valueResolver = createValueResolver(object, true);
		
		Validator validator = new Validator(testruleSet);
		validator.runRules(dependencyResolver, valueResolver, results);
	}
	
	@Test
	public void test_custom_ruleId() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object object = new Object();
		
		ValueResolver valueResolver = createValueResolver(object, false);
		
		Validator validator = new Validator(testruleSet);
		validator.runRules(dependencyResolver, valueResolver, results);
		
		Assert.assertEquals(1, results.failureCount());
		Assert.assertTrue(results.gotFailure("rule_without_id"));
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
					
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					return null;
				}
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