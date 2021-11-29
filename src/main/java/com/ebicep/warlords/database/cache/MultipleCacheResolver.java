package com.ebicep.warlords.database.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static com.ebicep.warlords.database.repositories.player.PlayersCollections.*;


public class MultipleCacheResolver implements CacheResolver {

    public static CacheManager playersCacheManager;
    public static CacheManager leaderboardsCacheManager;

    public MultipleCacheResolver(CacheManager playersCacheManager, CacheManager leaderboardsCacheManager) {
        MultipleCacheResolver.playersCacheManager = playersCacheManager;
        MultipleCacheResolver.leaderboardsCacheManager = leaderboardsCacheManager;
    }

    @Nonnull
    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Collection<Cache> caches = new ArrayList<>();
        if(Arrays.asList(context.getArgs()).contains(ALL_TIME)) {
            caches.add(playersCacheManager.getCache(ALL_TIME.cacheName));
        } else if(Arrays.asList(context.getArgs()).contains(WEEKLY)) {
            caches.add(playersCacheManager.getCache(WEEKLY.cacheName));
        } else if(Arrays.asList(context.getArgs()).contains(DAILY)) {
            caches.add(playersCacheManager.getCache(DAILY.cacheName));
        } else {
            caches.add(playersCacheManager.getCache(ALL_TIME.cacheName));
        }
        return caches;
    }

}
