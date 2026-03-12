package verigate.webbff.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import verigate.webbff.config.properties.CacheProperties;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class CacheConfiguration {

  @Bean
  CacheManager cacheManager(CacheProperties props) {
    if (!props.isEnabled()) {
      return new NoOpCacheManager();
    }

    CaffeineCacheManager manager = new CaffeineCacheManager();
    manager.setAllowNullValues(false);

    manager.registerCustomCache("policies",
        Caffeine.newBuilder()
            .maximumSize(props.getPolicies().getMaxSize())
            .expireAfterWrite(props.getPolicies().getTtlMinutes(), TimeUnit.MINUTES)
            .build());

    manager.registerCustomCache("risk-assessments",
        Caffeine.newBuilder()
            .maximumSize(props.getRiskAssessments().getMaxSize())
            .expireAfterWrite(props.getRiskAssessments().getTtlMinutes(), TimeUnit.MINUTES)
            .build());

    manager.registerCustomCache("command-status",
        Caffeine.newBuilder()
            .maximumSize(props.getCommandStatus().getMaxSize())
            .expireAfterWrite(props.getCommandStatus().getTtlMinutes(), TimeUnit.MINUTES)
            .build());

    return manager;
  }
}
