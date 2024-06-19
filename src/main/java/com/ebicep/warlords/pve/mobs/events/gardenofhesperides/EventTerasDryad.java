package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.List;

public class EventTerasDryad extends AbstractMob implements BossMinionMob, Teras {

    public EventTerasDryad(Location spawnLocation) {
        this(spawnLocation, "Teras Dryad", 3600, 0.25f, 0, 350, 450);
    }

    public EventTerasDryad(
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
            super("Spirit Healing", 200, 200, 2, 75, false);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {

            Utils.playGlobalSound(wp.getLocation(), "mage.waterbolt.impact", 2, 1.5f);
            EffectUtils.displayParticle(
                    Particle.HEART,
                    wp.getLocation().add(0, 2, 0),
                    10,
                    .5,
                    .1,
                    .5,
                    0
            );
            PlayerFilter.playingGame(wp.getGame())
                        .aliveTeammatesOfExcludingSelf(wp)
                        .forEach(warlordsEntity -> {
                            if (warlordsEntity instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof EventCronus) {
                                return;
                            }
                            EffectUtils.displayParticle(
                                    Particle.VILLAGER_HAPPY,
                                    warlordsEntity.getLocation().add(0, 1.25, 0),
                                    10,
                                    .5,
                                    .5,
                                    .5,
                                    0
                            );
                            warlordsEntity.addInstance(InstanceBuilder
                                    .healing()
                                    .ability(this)
                                    .source(wp)
                                    .value(healingValues.spiritHealing)
                            );
                        });
            return true;
        }

        private final HealingValues healingValues = new HealingValues();

        public HealingValues getHealValues() {
            return healingValues;
        }

        public static class HealingValues implements Value.ValueHolder {

            private final Value.SetValue spiritHealing = new Value.SetValue(200);
            private final List<Value> values = List.of(spiritHealing);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }
}
