/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.factories;

import verigate.watchlist.domain.commands.CreateWatchlistScreeningCommand;
import verigate.watchlist.domain.models.EntityType;
import verigate.watchlist.domain.models.ScreeningRequest;
import verigate.watchlist.domain.models.WatchlistScreeningAggregateRoot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Default implementation of the CreateWatchlistScreeningFactory interface.
 * Responsible for creating watchlist screening requests and aggregates.
 */
public class DefaultCreateWatchlistScreeningFactory implements CreateWatchlistScreeningFactory {
    
    @Override
    public WatchlistScreeningAggregateRoot create(CreateWatchlistScreeningCommand command) {
        UUID screeningId = UUID.randomUUID();
        String partnerId = command.getPartnerId();
        
        // Create the screening request
        ScreeningRequest request = new ScreeningRequest(
            null,
            null,
            null,
            null,
            null,
            new HashMap<>()
        );  

        // Create the aggregate root
        return new WatchlistScreeningAggregateRoot(screeningId, partnerId, request);
    }

    @Override
    public ScreeningRequest createPersonScreeningRequest(String firstName, String lastName, String dateOfBirth, String countryOfResidence) {
        return new ScreeningRequest(
            firstName,
            lastName,
            dateOfBirth,
            countryOfResidence,
            EntityType.PERSON,
            new HashMap<>()
        );
    }

    @Override
    public ScreeningRequest createPersonScreeningRequest(String firstName, String lastName, String dateOfBirth, String countryOfResidence, Map<String, String> additionalFields) {
        return new ScreeningRequest(
            firstName,
            lastName,
            dateOfBirth,
            countryOfResidence,
            EntityType.PERSON,
            additionalFields != null ? new HashMap<>(additionalFields) : new HashMap<>()
        );
    }

    @Override
    public ScreeningRequest createCompanyScreeningRequest(String companyName, String countryOfIncorporation) {
        return new ScreeningRequest(
            null,
            companyName,
            null,
            countryOfIncorporation,
            EntityType.COMPANY,
            new HashMap<>()
        );
    }

    @Override
    public ScreeningRequest createCompanyScreeningRequest(String companyName, String countryOfIncorporation, Map<String, String> additionalFields) {
        return new ScreeningRequest(
            null,
            companyName,
            null,
            countryOfIncorporation,
            EntityType.COMPANY,
            additionalFields != null ? new HashMap<>(additionalFields) : new HashMap<>()
        );
    }

    @Override
    public WatchlistScreeningAggregateRoot createWatchlistScreening(UUID screeningId, String partnerId, ScreeningRequest request) {
        return new WatchlistScreeningAggregateRoot(screeningId, partnerId, request);
    }
    
}
