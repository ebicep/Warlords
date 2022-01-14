package com.ebicep.warlords.database.cache;

import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.ebicep.warlords.database.repositories.player.PlayersCollections.LIFETIME;
import static com.ebicep.warlords.database.repositories.player.PlayersCollections.values;


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
        List<Object> list = Arrays.asList(context.getArgs());
        for (PlayersCollections value : values()) {
            if (list.contains(value)) {
                caches.add(playersCacheManager.getCache(value.cacheName));
                break;
            }
        }
        if (caches.isEmpty()) {
            caches.add(playersCacheManager.getCache(LIFETIME.cacheName));
        }
        return caches;
    }

}
