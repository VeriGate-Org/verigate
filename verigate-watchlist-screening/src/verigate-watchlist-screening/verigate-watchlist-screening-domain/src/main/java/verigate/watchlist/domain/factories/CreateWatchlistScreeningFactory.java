/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.factories;

import verigate.watchlist.domain.commands.CreateWatchlistScreeningCommand;
import verigate.watchlist.domain.models.ScreeningRequest;
import verigate.watchlist.domain.models.WatchlistScreeningAggregateRoot;

import java.util.Map;
import java.util.UUID;

/**
 * Factory interface for creating watchlist screening requests and aggregates.
 */
public interface CreateWatchlistScreeningFactory {

    WatchlistScreeningAggregateRoot create(CreateWatchlistScreeningCommand command);
    
    ScreeningRequest createPersonScreeningRequest(String firstName, String lastName, String dateOfBirth, String countryOfResidence);
    
    ScreeningRequest createPersonScreeningRequest(String firstName, String lastName, String dateOfBirth, String countryOfResidence, Map<String, String> additionalFields);
    
    ScreeningRequest createCompanyScreeningRequest(String companyName, String countryOfIncorporation);
    
    ScreeningRequest createCompanyScreeningRequest(String companyName, String countryOfIncorporation, Map<String, String> additionalFields);
    
    WatchlistScreeningAggregateRoot createWatchlistScreening(UUID screeningId, String partnerId, ScreeningRequest request);
}
