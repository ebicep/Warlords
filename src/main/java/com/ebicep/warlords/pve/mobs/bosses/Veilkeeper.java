package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public class Veilkeeper extends AbstractMob implements BossMob {

    public Veilkeeper(Location spawnLocation) {
        super(spawnLocation,
                "Veilkeeper",
                300000,
                0.05f,
                50,
                7000,
                10000
        );
    }

    public Veilkeeper(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
    }

    @Override
    public Component getDescription() {
        return Component.text("The Commandment of Unrivaled Chains");
    }

    @Override
    public Mob getMobRegistry() {
        return null;
    }
}
