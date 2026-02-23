/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.metrics;

import com.google.inject.matcher.Matcher;
import io.micrometer.core.annotation.Counted;
import java.lang.reflect.Method;

/**
 * A Guice method matcher which can be used for matching methods to apply the
 * {@link CountedMethodInterceptor} to for metrics.
 * This looks for the {@link Counted} annotation, but also ignore "synthetic" methods. Synthetic
 * methods are methods not visible in code but introduced by the Java compiler for various reasons.
 * Usually, these synthetic methods delegates to the hand coded ones. So if these are not ignored,
 * the interceptor matches both and gets run twice.
 */
public class CountedMethodMatcher implements Matcher<Method> {

  @Override
  public boolean matches(Method method) {
    return method.isAnnotationPresent(Counted.class) && !method.isSynthetic();
  }

}
