/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.builders;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import domain.events.BaseEvent;
import infrastructure.event.handler.factories.InfrastructureEventHandlerFactory;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/** EventHandlerModuleBuilder. */
public final class EventHandlerModuleBuilder {
  private InternalTransportJsonSerializer serializer;
  private final Set<ModuleBuilder> infrastructureEventHandlerFactoryBuilders = new HashSet<>();

  public EventHandlerModuleBuilder setSerializer(InternalTransportJsonSerializer serializer) {
    this.serializer = serializer;
    return this;
  }

  public EventHandlerModuleBuilder enableLogging(boolean enable) {
    return this;
  }

  /** addInfrastructureEventHandler. */
  public <RawMessageT, EventT extends BaseEvent<?>, KeyT>
      EventHandlerModuleBuilder addInfrastructureEventHandler(
          TypeLiteral<InfrastructureEventHandlerFactory<RawMessageT, EventT>> factoryTypeLiteral,
          Function<
                  InfrastructureEventHandlerFactoryBuilder<RawMessageT, EventT, KeyT>,
                  InfrastructureEventHandlerFactoryBuilder<RawMessageT, EventT, KeyT>>
              function) {
    infrastructureEventHandlerFactoryBuilders.add(
        function.apply(new InfrastructureEventHandlerFactoryBuilder<>(factoryTypeLiteral)));
    return this;
  }

  /** build. */
  public AbstractModule build() {
    return new AbstractModule() {

      @Override
      protected void configure() {
        if (serializer != null) {
          bind(InternalTransportJsonSerializer.class).to(serializer.getClass());
        }

        infrastructureEventHandlerFactoryBuilders.forEach(
            builder -> {
              install(builder.build());
            });
      }
    };
  }
}
