package de.unifr.proglang.testing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * <p>
 * The custom runner <code>LabeledParameterized</code> implements parameterized tests.
 * When running a parameterized test class, instances are created for the
 * cross-product of the test methods and the test data elements.
 * Unlike {@link Parameterized}, each data set must also contain a String label
 * as a first array element, before the actual constructor parameters. 
 * </p>
 * <p>
 * This is a copy-paste clone of {@link Parameterized}, not a subclass, because
 * the changed behaviour happens in private member class {@link TestClassRunnerForParameters}.
 * </p>
 * @see Parameterized
 */
public class LabeledParameterized extends Suite {
	/**
	 * Annotation for a method which provides parameters to be injected into the
	 * test class constructor by <code>Parameterized</code>
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface LabeledParameters {
	}

	/**
	 * Runner which runs one particular parametrization of one test case class.
	 * @author anton
	 *
	 */
	private class TestClassRunnerForParameters extends
			BlockJUnit4ClassRunner {
		/**
		 * Our index into {@link #fParameterList}
		 */
		private final int fParameterSetNumber;

		/**
		 * The list of test-case-arrays, as produced by the testcase's data method.
		 * (We'll only read the {@link #fParameterSetNumber}th element)
		 */
		private final List<Object[]> fParameterList;

		/**
		 * The constructor parameter array (extracted from {@link #fParameterList}).
		 */
		private Object[] params;

		/**
		 * The label which we found in the parameter list.
		 */
		private String label;

		/**
		 * Ctor.
		 * @param type the test case class
		 * @param parameterList the list of arrays, each describing a concrete test case.
		 * @param i index into parameterList: this runner will run a test case from that
		 *              particular array.
		 * @throws InitializationError if the ith element of parameterList is invalid
		 */
		TestClassRunnerForParameters(Class<?> type,
				List<Object[]> parameterList, int i) throws InitializationError {
			super(type);
			fParameterList= parameterList;
			fParameterSetNumber= i;
			unpackParamArray();
		}

		@Override
		public Object createTest() throws Exception {
			return getTestClass().getOnlyConstructor().newInstance(params);
		}

		/**
		 * Unpack the parameter array specified by {@link #fParameterList}
		 * and {@link #fParameterSetNumber} into a label 
		 * (0th becomes {@link #label}) and a constructor parameter array 
		 * (rest of the array becomes {@link #params})
		 * <p>
		 * To be called from ctor.
		 *   
		 * @throws InitializationError if the parameter array isn't an array, is too short, 
		 *        or doesn't contain a label
		 */
		private void unpackParamArray() throws InitializationError {
			Object[] rawParams;
			try {
				rawParams = fParameterList.get(fParameterSetNumber);				
				this.label = (String)rawParams[0];
			} catch (ClassCastException e) {
				throw new InitializationError(String.format(
						"%s.%s() must return a Collection of arrays, " +
						"0th elem of each being a String",
						getTestClass().getName(), getParametersMethod(
								getTestClass()).getName()));
			}
			if(rawParams.length <= 1){
				throw new InitializationError(String.format(
						"%s.%s() returned a too short array at index %d.",
						getTestClass().getName(), 
						getParametersMethod(getTestClass()).getName(),
						fParameterSetNumber));
			}
			this.params = Arrays.copyOfRange(rawParams, 1, rawParams.length);
		}

		@Override
		protected String getName() {
			return String.format("[%d:%s]", fParameterSetNumber, label);
		}

		@Override
		protected String testName(final FrameworkMethod method) {
			return String.format("%s[%s:%s]", method.getName(),
					fParameterSetNumber, label);
		}

		@Override
		protected void validateZeroArgConstructor(List<Throwable> errors) {
			// constructor can, nay, should have args.
		}

		@Override
		protected Statement classBlock(RunNotifier notifier) {
			return childrenInvoker(notifier);
		}
	}

	/**
	 * All the test runners, each of them corresponding to one concrete test case.
	 */
	private final ArrayList<Runner> runners= new ArrayList<Runner>();

	/**
	 * Only called reflectively. Do not use programmatically.
	 * @param klass the class on which this annotation has been annotated
	 * @throws Throwable if anything goes wrong while generating concrete test cases 
	 */
	public LabeledParameterized(Class<?> klass) throws Throwable {
		super(klass, Collections.<Runner>emptyList());
		List<Object[]> parametersList= getParametersList(getTestClass());
		for (int i= 0; i < parametersList.size(); i++)
			runners.add(new TestClassRunnerForParameters(getTestClass().getJavaClass(),
					parametersList, i));
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	/**
	 * Let the test class generate test data.
	 * @param klass the test case class
	 * @return a list of arrays, as produced by the test case's data-generating method
	 * @throws Throwable if reflective invocation fails somehow
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> getParametersList(TestClass klass)
			throws Throwable {
		return (List<Object[]>) getParametersMethod(klass).invokeExplosively(
				null);
	}

	/**
	 * Get the method which will produce test data (by reflection). 
	 * It's the one with {@link LabeledParameters} annotation.
	 * 
	 * @param testClass the test case class
	 * @return a parameterless method
	 * @throws InitializationError if reflection fails or no suitable method
	 */
	private FrameworkMethod getParametersMethod(TestClass testClass)
			throws InitializationError {
		List<FrameworkMethod> methods= testClass
				.getAnnotatedMethods(LabeledParameters.class);
		for (FrameworkMethod each : methods) {
			int modifiers= each.getMethod().getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers))
				return each;
		}

		throw new InitializationError("No public static parameters method on class "
				+ testClass.getName());
	}

}
