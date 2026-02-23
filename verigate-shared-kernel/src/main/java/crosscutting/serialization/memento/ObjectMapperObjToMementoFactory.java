/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.serialization.memento;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import crosscutting.patterns.ImmutablePropertyBag;
import crosscutting.patterns.Memento;
import crosscutting.patterns.MementoFactory;
import crosscutting.serialization.DataContractAnnotationIntrospector;
import crosscutting.serialization.MoneySerializer;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.javamoney.moneta.Money;
import org.jetbrains.annotations.Contract;

/**
 * FooMementoCreator is a utility class designed to capture and store the state of an object of
 * generic type T into an {@link ImmutablePropertyBag}. It leverages Jackson's ObjectMapper to
 * serialize the object into a map of properties, which can then be encapsulated within an
 * ImmutablePropertyBag, effectively creating a memento of the object's current state. This class
 * can be used in situations where the state of an object needs to be saved and possibly restored at
 * a later point, ensuring that the original object's state can be captured without modification.
 *
 * @param <ObjT> The type of object whose state is to be saved into a memento.
 */
public final class ObjectMapperObjToMementoFactory<ObjT> implements MementoFactory<Memento, ObjT> {

  private final Class<ObjT> objClass;

  public ObjectMapperObjToMementoFactory(Class<ObjT> objClass) {
    this.objClass = objClass;
  }

  /**
   * Saves the state of the specified object into a memento represented by an
   * {@link ImmutablePropertyBag}. This method uses Jackson's ObjectMapper to serialize the object's
   * state into a Map, which is then used to create the ImmutablePropertyBag.
   *
   * @param obj The object whose state is to be saved. Must not be null.
   * @return An ImmutablePropertyBag containing the serialized state of the object.
   * @throws IllegalArgumentException If the input object cannot be serialized to a Map. This can
   *                                  occur if the object type T is not suitable for serialization
   *                                  by Jackson's ObjectMapper, or if serialization policies
   *                                  prevent it.
   */
  @NotNull
  @Contract("-> new")
  public Memento create(ObjT obj) {
    var objectMapper = getObjectMapper();

    Map<String, Object> map;
    try {
      map = objectMapper.convertValue(obj, new TypeReference<>() {});
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Failed to serialize object of type " + objClass.getSimpleName() + " to a Map.", e);
    }
    return new Memento(new ImmutablePropertyBag(map));
  }

  @NotNull
  private ObjectMapper getObjectMapper() {
    var objectMapper = new ObjectMapper();
    // Register modules for enhanced Java 8 date/time and optional type support
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerModule(new Jdk8Module());

    // Register a custom module for domain-specific serialization, including money types
    SimpleModule customModule = new SimpleModule();
    customModule.addSerializer(Money.class, new MoneySerializer());
    objectMapper.registerModule(customModule);

    // Configure visibility to only include fields directly
    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    // Use a custom annotation introspector for domain-specific serialization annotations
    objectMapper.setAnnotationIntrospector(new DataContractAnnotationIntrospector());

    return objectMapper;
  }
}
