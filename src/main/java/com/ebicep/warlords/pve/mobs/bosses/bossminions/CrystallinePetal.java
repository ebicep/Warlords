package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.List;
import java.util.Random;

public class CrystallinePetal extends AbstractMob implements BossMinionMob {

    public CrystallinePetal(Location spawnLocation) {
        super(
                spawnLocation,
                "Crystalline Petal",
                3000,
                0.35f,
                0,
                400,
                800
        );
    }

    public CrystallinePetal(
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
    public Mob getMobRegistry() {
        return Mob.CRYSTALLINE_PETAL;
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.addKnockback(name, attacker.getLocation(), receiver, -1, 0.25);
    }
}
