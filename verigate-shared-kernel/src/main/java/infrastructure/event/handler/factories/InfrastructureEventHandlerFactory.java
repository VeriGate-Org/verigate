/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.factories;

import crosscutting.patterns.SimpleFactory;
import domain.events.BaseEvent;
import infrastructure.event.handler.InfrastructureEventHandler;

/**
 * This interface represents a factory for creating {@link InfrastructureEventHandler} instances. It
 * extends {@link SimpleFactory} to specify the creation of infrastructure event handlers that can
 * process events of a specific type.
 */
public interface InfrastructureEventHandlerFactory<RawMessageT, EventT extends BaseEvent<?>>
    extends SimpleFactory<InfrastructureEventHandler<RawMessageT, EventT>> {}
