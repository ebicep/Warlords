package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption2;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.WarlordsMob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class ZombieLancer2 extends WarlordsMob implements BasicMob {

    public ZombieLancer2(Location spawnLocation) {
        super(
                spawnLocation,
                "Zombie Lancer",
                2800,
                0.38f,
                0,
                200,
                300
        );
    }

    public ZombieLancer2(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
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
        return Mob.ZOMBIE_LANCER;
    }

    @Override
    public void onSpawn(PveOption2 option) {
        super.onSpawn(option);

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption2 option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}
