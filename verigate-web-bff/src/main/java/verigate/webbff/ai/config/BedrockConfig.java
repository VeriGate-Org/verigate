/*
 * VeriGate (c) 2025. All rights reserved.
 */

package verigate.webbff.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

@Configuration
public class BedrockConfig {

    @Bean
    public BedrockRuntimeClient bedrockRuntimeClient(
            @Value("${verigate.ai.region:us-east-1}") String region) {
        return BedrockRuntimeClient.builder()
                .region(Region.of(region))
                .build();
    }
}
