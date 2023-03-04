package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemModifier;
import com.ebicep.warlords.pve.items.statpool.ItemStatPool;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractItem<
        T extends Enum<T> & ItemStatPool<T>,
        R extends Enum<R> & ItemModifier<R>,
        U extends Enum<U> & ItemModifier<U>> {

    protected UUID uuid;
    protected Instant obtained = Instant.now();
    protected ItemTier tier;
    @Field("stat_pool")
    protected Map<T, Float> statPool = new HashMap<>();
    protected int modifier;

    public AbstractItem(UUID uuid, ItemTier tier, Set<T> statPool) {
        this.uuid = uuid;
        this.tier = tier;
        HashMap<T, ItemTier.StatRange> tierStatRanges = getTierStatRanges();
        for (T t : statPool) {
            this.statPool.put(t, (float) tierStatRanges.get(t).generateValue());
        }
    }

    public abstract HashMap<T, ItemTier.StatRange> getTierStatRanges();

    public abstract R[] getBlessings();

    public abstract U[] getCurses();

    public int getModifier() {
        return modifier;
    }


}
