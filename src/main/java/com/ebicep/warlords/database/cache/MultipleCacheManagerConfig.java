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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ebicep.warlords.database.repositories.player.PlayersCollections.values;


@Configuration
@EnableCaching
public class MultipleCacheManagerConfig extends CachingConfigurerSupport {

    @Bean
    @Primary
    @Override
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(Arrays.stream(values()).map(collections -> collections.cacheName).collect(Collectors.toList()));
        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES));
        return cacheManager;
    }

    @Bean
    public CacheManager alternateCacheManager() {
        return new ConcurrentMapCacheManager("leaderboards");
    }

    @Bean
    @Override
    public CacheResolver cacheResolver() {
        return new MultipleCacheResolver(cacheManager(), alternateCacheManager());
    }
}
