/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package domain.messages;

/**
 * A marker interface to identify class of {@link MessageQueue} used for invalid messages.
 *
 * @param <T> The type of message this queue is designed to support.
 */
public interface InvalidMessageQueue<T> extends MessageQueue<T> {

}
