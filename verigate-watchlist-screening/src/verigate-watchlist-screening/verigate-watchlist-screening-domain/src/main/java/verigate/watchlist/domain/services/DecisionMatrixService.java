/*
 * VeriGate (c) 2025. All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package verigate.watchlist.domain.services;

import verigate.watchlist.domain.models.ProviderResult;
import verigate.watchlist.domain.models.ScreeningDecision;
import verigate.watchlist.domain.models.ScreeningConfiguration;

import java.util.List;

/**
 * Service interface for applying decision matrix logic for watchlist screening results.
 * Evaluates match scores against partner-defined thresholds.
 */
public interface DecisionMatrixService {
    
    /**
     * Applies decision matrix based on provider results and partner configuration.
     */
    ScreeningDecision evaluateResults(List<ProviderResult> results, ScreeningConfiguration config);
    
    /**
     * Applies enhanced decision logic considering multiple factors.
     */
    ScreeningDecision evaluateWithEnhancedLogic(List<ProviderResult> results, ScreeningConfiguration config);
}