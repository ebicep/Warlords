package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;

import java.util.List;

public class Spelunker extends BaseSet {

    private int rareLootChanceIncreasePercent;
    private int spelunkerChestDropChancePercent;

    @Override
    public void init() {
        super.init();
        this.rareLootChanceIncreasePercent = getValue("rareLootChanceIncreasePercent", int.class);
        this.spelunkerChestDropChancePercent = getValue("spelunkerChestDropChancePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "spelunker";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(rareLootChanceIncreasePercent, spelunkerChestDropChancePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            // Implementation for:
            // 1. Modifying the player's global rare loot find chance.
            // 2. Hooking into mob death events to roll for a spelunker chest drop.
        }

    }

}