package com.ebicep.warlords.pve.mobs.creeper;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.concurrent.atomic.AtomicBoolean;

public class CreepyBomber extends AbstractMob implements EliteMob {

    public CreepyBomber(Location spawnLocation) {
        super(spawnLocation, "Creepy Bomber", 1000, .5f, 0, 0, 0);
    }

    public CreepyBomber(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.CREEPY_BOMBER;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 5 == 0) {
            AtomicBoolean hit = new AtomicBoolean(false);
            PlayerFilter.entitiesAround(warlordsNPC.getLocation(), 2, 2, 2)
                        .aliveEnemiesOf(warlordsNPC)
                        .forEach(warlordsEntity -> {
                            hit.set(true);
                            warlordsEntity.addDamageInstance(
                                    warlordsNPC,
                                    "Explosion",
                                    1000,
                                    1500,
                                    0,
                                    100
                            );
                        });
            if (hit.get()) {
                EffectUtils.displayParticle(
                        Particle.EXPLOSION_LARGE,
                        warlordsNPC.getLocation().add(0, 1, 0),
                        1,
                        0,
                        0,
                        0,
                        0
                );
                warlordsNPC.die(warlordsNPC);
            }
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        super.onDamageTaken(self, attacker, event);
        if (Utils.isProjectile(event.getAbility())) {
            event.setMin(event.getMin() * .2f);
            event.setMax(event.getMax() * .2f);
        }
    }
}
