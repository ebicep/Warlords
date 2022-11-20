package com.ebicep.warlords.database.cache;

import com.ebicep.warlords.util.chat.ChatUtils;
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
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .removalListener((o, o2, removalCause) -> ChatUtils.MessageTypes.PLAYER_SERVICE.sendMessage("Removed: " + o + " from cache - " + removalCause))
                .recordStats()
        );
        cacheManager.setAllowNullValues(false);
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
