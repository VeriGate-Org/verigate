/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package crosscutting.patterns;

/**
 * A generic factory interface for creating Memento objects from a given object of any type. This
 * interface extends a more general Factory interface, specifying the creation of Memento instances.
 * It's designed to be used in scenarios where objects need to be converted or saved into a memento
 * form for purposes such as undo functionality, snapshots, or state serialization.
 *
 * @param <MementoT> The type of the Memento object that this factory produces. This is the product
 *     type created by the factory.
 * @param <ObjT> The type of the object from which the Memento will be created. This is the input to
 *     the factory's creation process.
 */
public interface MementoFactory<MementoT, ObjT> extends Factory<MementoT, ObjT> {
}
