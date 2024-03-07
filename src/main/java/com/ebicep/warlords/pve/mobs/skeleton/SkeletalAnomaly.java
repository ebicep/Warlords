package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.abilities.CripplingStrike;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;

public class SkeletalAnomaly extends AbstractMob implements EliteMob {

    public SkeletalAnomaly(Location spawnLocation) {
        super(
                spawnLocation,
                "Skeletal Anomaly",
                10000,
                0.42f,
                0,
                500,
                700
        );
    }

    public SkeletalAnomaly(
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
        return Mob.SKELETAL_ANOMALY;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.addKnockback(name, attacker.getLocation(), receiver, 1, 0.15);
        CripplingStrike.cripple(attacker, receiver, name + " Cripple", 80);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

}
