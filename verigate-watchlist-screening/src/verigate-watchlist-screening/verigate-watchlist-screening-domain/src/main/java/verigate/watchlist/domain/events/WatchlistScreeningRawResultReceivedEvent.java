/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.events;

import verigate.watchlist.domain.models.ProviderResult;
import verigate.watchlist.domain.models.ScreeningRequest;

import java.util.UUID;

/**
 * Event emitted when raw results are received from a screening provider.
 * This is an input event that triggers the watchlist screening evaluation process.
 */
public class WatchlistScreeningRawResultReceivedEvent extends WatchlistScreeningEvent {
    
    private final ScreeningRequest request;
    private final ProviderResult providerResult;
    
    public WatchlistScreeningRawResultReceivedEvent(UUID screeningId, String partnerId, 
                                                  ScreeningRequest request, ProviderResult providerResult) {
        super(screeningId, partnerId);
        this.request = request;
        this.providerResult = providerResult;
    }
    
    @Override
    public String getEventType() {
        return "WatchlistScreeningRawResultReceived";
    }
    
    // Getters
    public ScreeningRequest getRequest() { return request; }
    public ProviderResult getProviderResult() { return providerResult; }
}