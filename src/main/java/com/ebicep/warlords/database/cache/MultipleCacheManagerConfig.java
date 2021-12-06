package com.ebicep.warlords.database.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

import static com.ebicep.warlords.database.repositories.player.PlayersCollections.*;


@Configuration
@EnableCaching
public class MultipleCacheManagerConfig extends CachingConfigurerSupport {

    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                ALL_TIME.cacheName,
                WEEKLY.cacheName,
                DAILY.cacheName
        );
        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES));
        return cacheManager;
    }

    @Bean
    public CacheManager alternateCacheManager() {
        return new ConcurrentMapCacheManager("leaderboards");
    }

    @Bean
    public CacheResolver cacheResolver() {
        return new MultipleCacheResolver(cacheManager(), alternateCacheManager());
    }
}
