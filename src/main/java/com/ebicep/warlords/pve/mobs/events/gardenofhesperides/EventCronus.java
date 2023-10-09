package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.abilities.GroundSlamBerserker;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.CalculateSpeed;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class EventCronus extends AbstractZombie implements BossMob, LesserGod {

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
                new HeavenlyDamage()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_CRONUS;
    }

    @Override
    public Component getDescription() {
        return Component.text("Dude", NamedTextColor.LIGHT_PURPLE);
    }

    @Override
    public NamedTextColor getColor() {
        return NamedTextColor.DARK_PURPLE;
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
                        AbstractMob<?> createdMob = mob.createMob(location);
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
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (!healthCheck && self.getHealth() / self.getMaxHealth() < 0.30) {
            healthCheck = true;
            GroundSlamBerserker groundSlamBerserker = new GroundSlamBerserker();
            groundSlamBerserker.getHitBoxRadius().setCurrentValue(10);
            groundSlamBerserker.getEnergyCost().setCurrentValue(0);
            new GameRunnable(self.getGame()) {
                final float healing = self.getMaxHealth() * .0333f;
                int counter = 0;

                @Override
                public void run() {
                    if (warlordsNPC.isDead()) {
                        this.cancel();
                        return;
                    }
                    groundSlamBerserker.onActivate(self, null);

                    warlordsNPC.addHealingInstance(
                            warlordsNPC,
                            "Cronus Healing",
                            healing,
                            healing,
                            0,
                            100,
                            EnumSet.of(InstanceFlags.PIERCE)
                    );

                    if (++counter >= 6) {
                        warlordsNPC.getAbilitiesMatching(HeavenlyDamage.class).forEach(heavenlyDamage -> {
                            heavenlyDamage.setMinDamageHeal(1150);
                            heavenlyDamage.setMaxDamageHeal(1300);
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

    private static class HeavenlyDamage extends AbstractPveAbility {

        private float radius = 20;

        public HeavenlyDamage() {
            super("Heavenly Damage", 950, 1100, 10, 100, false);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            wp.subtractEnergy(name, energyCost, false);
            //TODO animation
            PlayerFilter.entitiesAround(wp, radius, radius, radius)
                        .aliveEnemiesOf(wp)
                        .forEach(warlordsEntity -> {
                            warlordsEntity.addDamageInstance(
                                    wp,
                                    name,
                                    minDamageHeal,
                                    maxDamageHeal,
                                    critChance,
                                    critMultiplier,
                                    EnumSet.of(InstanceFlags.PIERCE)
                            );
                        });
            return true;
        }
    }
}
