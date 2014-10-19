package com.sankar.injectingvalidator;

import java.lang.reflect.InvocationTargetException;

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
	
	@Test
	public void validates_correctly() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Person person = new Person();
		person.setName("Sankaranarayanan Viswanathan Narayanaswamy");
			
		RuleSet ruleSet = RuleSet.from(PersonRules.class);
		Validator validator = new Validator(ruleSet);
		
		ValidationResultHolder results = new ValidationResultHolder();
		DependencyResolver dependencyResolver = createDependencyResolver();
		ValueResolver valueResolver = createValueResolver(person);
		
		validator.runRules(dependencyResolver, valueResolver, results);
		
		Assert.assertEquals(2, results.failureCount());
		Assert.assertTrue(results.gotFailure("ssn_required"));
		Assert.assertTrue(results.gotFailure("name_length"));
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
	
	private ValueResolver createValueResolver(final Object context) {
		
		return new ValueResolver() {

			@Override
			public Object lookup(String path) {
				try {
					return PropertyUtils.getNestedProperty(context, path);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					return null;
				}
			}
			
		};
	}

} 