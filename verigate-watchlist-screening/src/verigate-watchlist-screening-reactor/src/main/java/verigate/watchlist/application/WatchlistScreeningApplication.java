package verigate.watchlist.application;

import com.google.inject.Guice;
import com.google.inject.Injector;
import verigate.watchlist.infrastructure.config.WatchlistScreeningModule;

/**
 * Main application entry point following Clean Architecture principles.
 * This class orchestrates the initialization of the application using
 * dependency injection via Google Guice and enforces the dependency rule
 * where inner layers don't depend on outer layers.
 */
public class WatchlistScreeningApplication {
    
    /**
     * Main entry point for the application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Initialize Guice injector with all module bindings
        Injector injector = Guice.createInjector(new WatchlistScreeningModule());
        
        // Get the application service from Guice
        WatchlistScreeningApplicationService applicationService = 
            injector.getInstance(WatchlistScreeningApplicationService.class);
            
        // Start the application
        applicationService.start();
    }
}
