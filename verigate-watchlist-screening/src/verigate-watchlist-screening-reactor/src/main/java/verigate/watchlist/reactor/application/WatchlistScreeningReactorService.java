/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.reactor.application;

/**
 * Interface for the reactor service that coordinates the initialization
 * and startup of all modules.
 */
public interface WatchlistScreeningReactorService {
    
    /**
     * Starts the reactor service and initializes all modules.
     */
    void start();
    
    /**
     * Stops the reactor service and all associated modules.
     */
    void stop();
}
