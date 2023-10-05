package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class EventTerasDryad extends AbstractZombie implements BossMinionMob {

    public EventTerasDryad(Location spawnLocation) {
        this(spawnLocation, "Teras Dryad", 3600, 0.25f, 0, 350, 450);
    }

    public EventTerasDryad(
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
                maxMeleeDamage,
                new SpiritHealing()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_TERAS_DRYAD;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    private static class SpiritHealing extends AbstractPveAbility {


        public SpiritHealing() {
            super("Spirit Healing", 200, 200, 2, 75);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            wp.subtractEnergy(name, energyCost, false);
            //TODO animation
            PlayerFilter.playingGame(wp.getGame())
                        .aliveTeammatesOfExcludingSelf(wp)
                        .forEach(warlordsEntity -> {
                            if (warlordsEntity instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof EventCronus) {
                                return;
                            }
                            warlordsEntity.addHealingInstance(
                                    wp,
                                    name,
                                    minDamageHeal,
                                    maxDamageHeal,
                                    critChance,
                                    critMultiplier
                            );
                        });
            return true;
        }
    }
}
