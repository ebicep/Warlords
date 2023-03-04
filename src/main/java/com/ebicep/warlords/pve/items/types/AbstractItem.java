package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemModifier;
import com.ebicep.warlords.pve.items.statpool.ItemStatPool;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.*;

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

    public abstract ItemTypes getType();

    public ItemStack generateItemStack() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM)
                .name(getName())
                .lore("")
                .addLore(getStatPoolLore());
        return itemBuilder.get();
    }

    public String getName() {
        String name = "";
        if (modifier != 0) {
            if (modifier > 0) {
                name += ChatColor.GREEN + getBlessings()[modifier - 1].getName();
            } else {
                name += ChatColor.RED + getCurses()[-modifier - 1].getName();
            }
        }
        name += tier.getColoredName() + " " + ChatColor.GRAY + getType().name;
        return name;
    }

    public List<String> getStatPoolLore() {
        List<String> lore = new ArrayList<>();
        statPool.forEach((stat, value) -> lore.add(stat.getValueFormatted(value)));
        return lore;
    }

    public abstract R[] getBlessings();

    public abstract U[] getCurses();

    public int getModifier() {
        return modifier;
    }


}
