package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom.ItemAdditiveCooldown;
import com.ebicep.warlords.pve.items.ItemTier;

import java.util.HashMap;

public enum ItemBucklerStatPool implements ItemStatPool<ItemBucklerStatPool> {

    AGGRO_PRIO("Aggression Priority") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            warlordsPlayer.setBonusAgroWeight(warlordsPlayer.getBonusAgroWeight() + value);
        }
    },
    THORNS("Thorns") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            // TODO
        }
    },
    KB_RES("Knockback Resistance") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            ItemAdditiveCooldown.increaseKBRes(warlordsPlayer, value);
        }
    },
    REGEN_TIMER("Shorter Regen Timer") {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
            warlordsPlayer.setRegenTickTimerModifier((1 - value) / 100f);
        }
    },
    ;

    public static final ItemBucklerStatPool[] VALUES = values();
    public static final HashMap<ItemBucklerStatPool, ItemTier.StatRange> STAT_RANGES = new HashMap<>() {{
        put(ItemBucklerStatPool.AGGRO_PRIO, new ItemTier.StatRange(3, 15));
        put(ItemBucklerStatPool.THORNS, new ItemTier.StatRange(3, 15));
        put(ItemBucklerStatPool.KB_RES, new ItemTier.StatRange(3, 15));
        put(ItemBucklerStatPool.REGEN_TIMER, new ItemTier.StatRange(5, 45));
    }};
    public final String name;

    ItemBucklerStatPool(String name) {
        this.name = name;
    }

    @Override
    public ItemBucklerStatPool[] getPool() {
        return VALUES;
    }

    @Override
    public HashMap<ItemBucklerStatPool, ItemTier.StatRange> getStatRange() {
        return STAT_RANGES;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Operation getOperation() {
        return Operation.MULTIPLY;
    }

}
