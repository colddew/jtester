package org.jtester.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation enabling to specify if tests should be run in a transaction and,
 * if yes, whether at the end of the test, the transaction should be
 * <i>committed</i> or <i>rollbacked</i>.
 * <p/>
 * If this annotation is specified at class-level, it is valid for all tests in
 * the annotated class and its subclasses. A class level annotation overrides
 * the settings of a superclass annotation. It can also be specified at method
 * level, to specify method specific transactional behavior.
 * <p/>
 * The value attribute defines whether the annotated test(s) run in a
 * transaction and, if yes, what will be the commit/rollback behavior. The
 * default behavior is defined by the unitils property
 * <code>DatabaseModule.Transactional.value.default</code>. This configured
 * default will be used when the value property is unspecified or explicitly set
 * to {@link TransactionMode#DEFAULT}.
 * 
 * @see TransactionMode
 * 
 */
@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Inherited
public @interface Transactional {

	/**
	 * Defines whether the annotated test(s) run in a transaction and, if yes,
	 * what will be commit/rollback behavior. The default behavior is defined by
	 * the unitils property
	 * <code>DatabaseModule.Transactional.value.default</code>. This configured
	 * default will be used when the value property is unspecified or explicitly
	 * set to {@link TransactionMode#DEFAULT}.
	 * 
	 * @return The TransactionMode
	 */
	TransactionMode value() default TransactionMode.DEFAULT;

	/**
	 * Defining the available transaction modes for a test. Defines whether a
	 * test must be run in a transaction and, if yes, what is the
	 * commit/rollback behavior.
	 * 
	 */
	public static enum TransactionMode {

		/**
		 * Value indicating that transactions should be disabled, i.e. the test
		 * should not be run in a transaction
		 */
		DISABLED,

		/**
		 * Value indicating that the test should be executed in a transaction,
		 * and that this transaction must be committed at the end of the test.
		 */
		COMMIT,

		/**
		 * Value indicating that the test should be executed in a transaction,
		 * and that this transaction must be rollbacked at the end of the test.
		 */
		ROLLBACK,

		/**
		 * Value indicating that the default behavior is defined by the unitils
		 * property <code>DatabaseModule.Transactional.value.default</code> is
		 * in use.
		 */
		DEFAULT;

	}
}
