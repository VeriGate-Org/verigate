/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.serialization.memento;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import crosscutting.patterns.Factory;
import crosscutting.patterns.Memento;
import crosscutting.serialization.DataContractAnnotationIntrospector;
import crosscutting.serialization.MoneyDeserializer;
import javax.validation.constraints.NotNull;
import org.javamoney.moneta.Money;

/**
 * Factory class for creating aggregate instances from mementos. It utilizes Jackson for
 * deserialization, configured specifically for this domain's needs, including custom deserializers
 * and annotation introspectors.
 *
 * @param <ObjT> The type of object this factory is responsible for creating.
 */
public final class ObjetMapperMementoToObjFactory<ObjT> implements Factory<ObjT, Memento> {

  private final Class<ObjT> objType;

  /**
   * Constructs a new instance of AggregateFromMementoFactory for the specified aggregate type.
   *
   * @param aggregateType The Class type of the aggregate root that this factory will create
   *                      instances of.
   */
  public ObjetMapperMementoToObjFactory(Class<ObjT> aggregateType) {
    this.objType = aggregateType;
  }

  /**
   * Creates an instance of the aggregate root from the provided memento.
   *
   * @param memento The memento from which the aggregate instance is to be reconstituted.
   * @return An instance of the specified aggregate type, reconstituted from the given memento.
   */
  @Override
  public ObjT create(Memento memento) {
    var objectMapper = getObjectMapper();
    return objectMapper.convertValue(memento.asMap(), objType);
  }

  /**
   * Configures and returns an ObjectMapper tailored for deserializing the aggregate from a memento.
   * The configuration includes custom modules for Java 8 Date and Time API, optional types, and
   * a custom deserializer for the Money class. It also sets a custom annotation introspector to
   * handle serialization and deserialization directives.
   *
   * @return A configured ObjectMapper instance suitable for deserializing objects from mementos.
   */
  @NotNull
  private ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();

    // Register modules for enhanced Java 8 date/time and optional type support
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new Jdk8Module());

    // Register a custom module for domain-specific deserialization, including money types
    SimpleModule customModule = new SimpleModule();
    customModule.addDeserializer(Money.class, new MoneyDeserializer());
    objectMapper.registerModule(customModule);

    // Configure visibility to only include fields directly
    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    // Use a custom annotation introspector for domain-specific serialization annotations
    objectMapper.setAnnotationIntrospector(new DataContractAnnotationIntrospector());

    return objectMapper;
  }
}
