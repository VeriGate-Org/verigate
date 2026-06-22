package verigate.adapter.dharesponse.infrastructure.functions.lambda.di.factories;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import verigate.adapter.dharesponse.application.handlers.DefaultProcessDhaResponseCommandHandler;
import verigate.adapter.dharesponse.infrastructure.functions.lambda.di.modules.ProcessDhaResponseServiceModule;

/** Factory for creating DHA response processing dependencies via Guice. */
public class ProcessDhaResponseDependencyFactory {

  private final Injector injector;

  public ProcessDhaResponseDependencyFactory() {
    this.injector = Guice.createInjector(
        Stage.PRODUCTION, new ProcessDhaResponseServiceModule());
  }

  public ProcessDhaResponseDependencyFactory(Injector injector) {
    this.injector = injector;
  }

  public DefaultProcessDhaResponseCommandHandler getCommandHandler() {
    return injector.getInstance(
        DefaultProcessDhaResponseCommandHandler.class);
  }
}
