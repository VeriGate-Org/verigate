/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.reactor.application;

import com.google.inject.Guice;
import com.google.inject.Injector;
import verigate.watchlist.reactor.config.WatchlistScreeningReactorModule;

/**
 * Main Reactor application class that orchestrates the building and integration
 * of all VeriGate Watchlist Screening modules.
 * 
 * This reactor follows the same architectural structure as the core modules
 * but serves as an aggregator and bootstrapper for the entire system.
 */
public class WatchlistScreeningReactorApplication {

    /**
     * Main entry point for the reactor application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Starting VeriGate Watchlist Screening Reactor...");
        
        // Initialize Guice injector with all module bindings
        Injector injector = Guice.createInjector(new WatchlistScreeningReactorModule());
        
        // Get the reactor service from Guice
        WatchlistScreeningReactorService reactorService = 
            injector.getInstance(WatchlistScreeningReactorService.class);
            
        // Start the reactor service
        reactorService.start();
    }
}
