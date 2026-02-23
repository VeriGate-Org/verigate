package verigate.watchlist.application;

/**
 * Application service interface following Clean Architecture principles.
 * This interface defines the main application use cases and serves as a
 * boundary between the domain layer and the infrastructure layer.
 */
public interface WatchlistScreeningApplicationService {
    
    /**
     * Starts the application and initializes all required components.
     */
    void start();
    
    /**
     * Stops the application and releases resources.
     */
    void stop();
}
