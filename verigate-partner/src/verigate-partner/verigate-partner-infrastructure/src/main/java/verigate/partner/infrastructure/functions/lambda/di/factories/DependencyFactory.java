/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.partner.infrastructure.functions.lambda.di.factories;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import infrastructure.functions.lambda.serializers.internal.InternalTransportJsonSerializer;
import verigate.partner.infrastructure.functions.lambda.di.modules.PartnerServiceModule;

public class DependencyFactory {

    protected final Injector injector;

    public DependencyFactory() {
        this.injector = Guice.createInjector(new PartnerServiceModule());
    }

    public DependencyFactory(Injector injector) {
        this.injector = injector;
    }

    public InternalTransportJsonSerializer getSerializer() {
        return injector.getInstance(
            Key.get(new TypeLiteral<InternalTransportJsonSerializer>() {},
                Names.named("artifactSerializer")));
    }
}
