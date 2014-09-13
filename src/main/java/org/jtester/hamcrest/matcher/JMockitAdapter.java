package org.jtester.hamcrest.matcher;

import static mockit.internal.util.Utilities.getField;
import ext.jtester.hamcrest.Description;
import ext.jtester.hamcrest.Matcher;
import ext.jtester.hamcrest.StringDescription;
import ext.jtester.hamcrest.core.IsEqual;
import ext.jtester.hamcrest.core.IsSame;
import ext.jtester.hamcrest.number.OrderingComparison;

/**
 * Adapts the<br>
 * <br> {@code ext.jtester.hamcrest.Matcher} interface to
 * {@link mockit.external.hamcrest.Matcher}.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class JMockitAdapter<T> extends mockit.external.hamcrest.BaseMatcher<T> {
	private final Matcher hamcrestMatcher;

	public static <T> JMockitAdapter<T> create(final Matcher matcher) {
		return new JMockitAdapter<T>(matcher);
	}

	private JMockitAdapter(Matcher<T> matcher) {
		hamcrestMatcher = matcher;
	}

	public boolean matches(Object item) {
		return hamcrestMatcher.matches(item);
	}

	public void describeTo(mockit.external.hamcrest.Description description) {
		Description strDescription = new StringDescription();
		hamcrestMatcher.describeTo(strDescription);
		description.appendText(strDescription.toString());
	}

	public Object getInnerValue() {
		Matcher innerMatcher = hamcrestMatcher;

		while (innerMatcher instanceof ext.jtester.hamcrest.core.Is
				|| innerMatcher instanceof ext.jtester.hamcrest.core.IsNot) {
			innerMatcher = getField(innerMatcher.getClass(), Matcher.class, innerMatcher);
		}

		if (innerMatcher instanceof IsEqual || innerMatcher instanceof IsSame
				|| innerMatcher instanceof OrderingComparison) {
			return getField(innerMatcher.getClass(), Object.class, innerMatcher);
		} else {
			return null;
		}
	}
}
