/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.adapter.qlink.infrastructure.functions.lambda.di.factories;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import io.micrometer.core.instrument.MeterRegistry;
import verigate.adapter.qlink.infrastructure.functions.lambda.di.modules.ServiceModule;

/** Wrapper for core dependencies. Allows for overrides via Guice. */
public class DependencyFactory {

  protected final Injector injector;

  /** Creates a factory with default Guice module. */
  public DependencyFactory() {
    this.injector = Guice.createInjector(Stage.PRODUCTION, new ServiceModule());
  }

  /** Creates a factory with specified Guice injector. Useful for test purposes. */
  public DependencyFactory(Injector injector) {
    this.injector = injector;
  }

  /**
   * Gets the serializer that is used to serialize internal transport objects.
   * These artifacts are used to communicate between services via the use of
   * events and commands.
   *
   * @return The serializer instance.
   */
  public InternalTransportJsonSerializer getSerializer() {
    return injector.getInstance(
        Key.get(
            new TypeLiteral<InternalTransportJsonSerializer>() {},
            Names.named("artifactSerializer")));
  }

  public MeterRegistry getMeterRegistry() {
    return injector.getInstance(MeterRegistry.class);
  }
}
