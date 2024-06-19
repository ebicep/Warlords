package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
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
import org.bukkit.*;

import javax.annotation.Nonnull;
import java.util.List;

public class ZenithLegionnaire extends AbstractMob implements BossMinionMob {

    public ZenithLegionnaire(Location spawnLocation) {
        super(spawnLocation,
                "Zenith Legionnaire",
                4400,
                0.32f,
                10,
                1000,
                1500,
                new Remedy()
        );
    }

    public ZenithLegionnaire(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new Remedy()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ZENITH_LEGIONNAIRE;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false);
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.addKnockback(name, attacker.getLocation(), receiver, -1.1, 0.3);
        Utils.playGlobalSound(attacker.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 2, 0.2f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.ORANGE)
                                                                       .with(FireworkEffect.Type.BALL)
                                                                       .withTrail()
                                                                       .build());
    }

    private static class Remedy extends AbstractPveAbility implements Heals<Remedy.HealingValues> {

        public Remedy() {
            super(
                    "Remedy",
                    500,
                    500,
                    10,
                    100
            );
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            PlayerFilter.playingGame(wp.getGame())
                        .filter(we -> we.getName().equals("Zenith"))
                        .forEach(zenith -> {
                            zenith.addInstance(InstanceBuilder
                                    .healing()
                                    .ability(this)
                                    .source(wp)
                                    .value(healingValues.remedyHealing)
                            );

                            Utils.playGlobalSound(zenith.getLocation(), "shaman.earthlivingweapon.impact", 3, 1.5f);
                            EffectUtils.playParticleLinkAnimation(zenith.getLocation(), wp.getLocation(), Particle.VILLAGER_HAPPY);
                        });
            if (wp instanceof WarlordsNPC warlordsNPC) {
                warlordsNPC.getMob().removeTarget();
            }

            return true;
        }

        private final HealingValues healingValues = new HealingValues();

        public HealingValues getHealValues() {
            return healingValues;
        }

        public static class HealingValues implements Value.ValueHolder {

            private final Value.SetValue remedyHealing = new Value.SetValue(500);
            private final List<Value> values = List.of(remedyHealing);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }
}