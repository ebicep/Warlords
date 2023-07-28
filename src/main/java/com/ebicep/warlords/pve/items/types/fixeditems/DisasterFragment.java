package com.ebicep.warlords.pve.items.types.fixeditems;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractFixedItem;
import com.ebicep.warlords.pve.items.types.ItemType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class DisasterFragment extends AbstractFixedItem implements FixedItemAppliesToPlayer {

    public static final HashMap<BasicStatPool, Integer> STAT_POOL = new HashMap<>() {{
        put(BasicStatPool.MAX_ENERGY, 25);
        put(BasicStatPool.EPH, 2);
        put(BasicStatPool.SPEED, 70);
        put(BasicStatPool.CRIT_MULTI, 150);

    }};

    public DisasterFragment() {
        super(ItemTier.DELTA);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {

    }

    @Override
    public String getName() {
        return "Disaster Fragment";
    }

    @Override
    public HashMap<BasicStatPool, Integer> getStatPool() {
        return STAT_POOL;
    }

    @Override
    public int getWeight() {
        return 45;
    }

    @Override
    public ItemType getType() {
        return ItemType.GAUNTLET;
    }

    @Override
    protected ItemStack getItemStack() {
        return new ItemStack(Material.AMETHYST_SHARD);
    }

    @Override
    public String getEffect() {
        return "Mark of Chaos";
    }

    @Override
    public String getEffectDescription() {
        return "Your strikes have 10% chance to give mobs a random debuff 2s.";
    }
}