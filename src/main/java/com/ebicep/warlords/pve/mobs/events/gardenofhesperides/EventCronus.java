package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.abilities.GroundSlamBerserker;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.CalculateSpeed;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class EventCronus extends AbstractMob implements BossMob, LesserGod {

    private static final List<Mob> INITIAL_SPAWN = Arrays.asList(Mob.EVENT_TERAS_CYCLOPS, Mob.EVENT_TERAS_MINOTAUR, Mob.EVENT_TERAS_SIREN, Mob.EVENT_TERAS_DRYAD);
    private boolean healthCheck = false;
    private boolean rejuvenateOver = false;

    public EventCronus(Location spawnLocation) {
        this(spawnLocation, "Cronus", 42500, 0.2f, 25, 920, 1000);
    }

    public EventCronus(
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
                new HeavenlyDamage()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_CRONUS;
    }

    @Override
    public Component getDescription() {
        return Component.text("God of Time", NamedTextColor.GREEN);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.DARK_GREEN;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        new GameRunnable(option.getGame()) {

            @Override
            public void run() {
                for (Mob mob : INITIAL_SPAWN) {
                    List<Location> spawnLocations = LocationUtils.getCircle(warlordsNPC.getLocation(), 3, option.playerCount());
                    for (Location location : spawnLocations) {
                        AbstractMob createdMob = mob.createMob(location);
                        if (createdMob instanceof EventTerasSiren siren) {
                            siren.setCronus(EventCronus.this);
                        }
                        option.spawnNewMob(createdMob);
                    }
                }
            }
        }.runTaskLater(40);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (!healthCheck && self.getCurrentHealth() / self.getMaxHealth() < 0.30) {
            healthCheck = true;
            GroundSlamBerserker groundSlamBerserker = new GroundSlamBerserker();
            groundSlamBerserker.getHitBoxRadius().setBaseValue(10);
            groundSlamBerserker.getEnergyCost().setBaseValue(0);
            new GameRunnable(self.getGame()) {
                final float healing = self.getMaxHealth() * .0333f;
                int counter = 0;

                @Override
                public void run() {
                    if (warlordsNPC.isDead()) {
                        this.cancel();
                        return;
                    }
                    groundSlamBerserker.onActivate(self);

                    warlordsNPC.addInstance(InstanceBuilder
                            .healing()
                            .cause("Cronus Healing")
                            .source(warlordsNPC)
                            .value(healing)
                            .flags(InstanceFlags.PIERCE)
                    );
                    if (++counter >= 6) {
                        warlordsNPC.getAbilitiesMatching(HeavenlyDamage.class).forEach(heavenlyDamage -> {
                            Value.RangedValue damage = heavenlyDamage.getDamageValues().getHeavenlyDamage();
                            damage.min().setBaseValue(1150);
                            damage.max().setBaseValue(1300);
                        });
                        CalculateSpeed calculateSpeed = warlordsNPC.getSpeed();
                        calculateSpeed.setBaseSpeedToWalkingSpeed(0.25f);
                        calculateSpeed.setChanged(true);
                        rejuvenateOver = true;
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 10);
        }
    }

    public boolean diedDuringRejuvenate() {
        return healthCheck && !rejuvenateOver;
    }

    private static class HeavenlyDamage extends AbstractPveAbility implements Damages<HeavenlyDamage.DamageValues> {

        private float radius = 20;

        public HeavenlyDamage() {
            super("Heavenly Damage", 950, 1100, 10, 100, false);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            EffectUtils.displayParticle(
                    Particle.END_ROD,
                    wp.getLocation().add(0, 2.5, 0),
                    1000,
                    radius,
                    2,
                    radius,
                    .01
            );
            PlayerFilter.entitiesAround(wp, radius, radius, radius)
                        .aliveEnemiesOf(wp)
                        .forEach(warlordsEntity -> {
                            warlordsEntity.addInstance(InstanceBuilder
                                    .damage()
                                    .ability(this)
                                    .source(wp)
                                    .value(damageValues.heavenlyDamage)
                                    .flags(InstanceFlags.PIERCE)
                            );
                        });
            return true;
        }

        private final DamageValues damageValues = new DamageValues();

        @Override
        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.RangedValue heavenlyDamage = new Value.RangedValue(950, 1100);
            private final List<Value> values = List.of(heavenlyDamage);

            public Value.RangedValue getHeavenlyDamage() {
                return heavenlyDamage;
            }

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }
}
