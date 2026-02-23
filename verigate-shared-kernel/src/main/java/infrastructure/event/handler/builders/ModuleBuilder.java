/*
 * VeriGate (c) 2024. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package infrastructure.event.handler.builders;

import com.google.inject.AbstractModule;

interface ModuleBuilder {
  AbstractModule build();
}
