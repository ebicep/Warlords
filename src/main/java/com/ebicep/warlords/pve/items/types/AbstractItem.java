package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemBlessing;
import com.ebicep.warlords.pve.items.modifiers.ItemCurse;
import com.ebicep.warlords.pve.items.statpool.ItemStatPool;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractItem<
        T extends Enum<T> & ItemStatPool<T>,
        R extends Enum<R> & ItemBlessing<R>,
        U extends Enum<U> & ItemCurse<U>> {

    protected UUID uuid;
    protected Instant obtained = Instant.now();
    protected ItemTier tier;
    protected Map<T, Float> statPool = new HashMap<>();
    protected R blessing;
    protected U curse;

    public AbstractItem(UUID uuid, ItemTier tier, Set<T> statPool) {
        this.uuid = uuid;
        this.tier = tier;
        HashMap<T, ItemTier.StatRange> tierStatRanges = getTierStatRanges();
        for (T t : statPool) {
            this.statPool.put(t, (float) tierStatRanges.get(t).generateValue());
        }
    }

    public abstract HashMap<T, ItemTier.StatRange> getTierStatRanges();

}
