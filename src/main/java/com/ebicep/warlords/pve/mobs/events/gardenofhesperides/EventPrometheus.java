package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.customentities.nms.pve.pathfindergoals.PredictTargetFutureLocationGoal;
import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.List;

public class EventPrometheus extends AbstractMob implements BossMob, LesserGod {

    private int barrageOfFlamesDelay = 0;
    private boolean healthCheck = false;

    public EventPrometheus(Location spawnLocation) {
        this(spawnLocation, "Prometheus", 30000, .33f, 10, 730, 870);
    }

    public EventPrometheus(
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
                new BurstOfFlames(),
                new Fireball(350, 450, 1000)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_PROMETHEUS;
    }

    @Override
    public Component getDescription() {
        return Component.text("God of Fire", NamedTextColor.RED);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.DARK_RED;
    }


    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        List<Location> spawnLocations = LocationUtils.getCircle(warlordsNPC.getLocation(), 3, option.playerCount() + 1);
        for (Location location : spawnLocations) {
            option.spawnNewMob(Mob.ILLUMINATION.createMob(location));
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 20 == 0) {
            boolean anyPlayer20BlockAway = PlayerFilter
                    .playingGame(option.getGame())
                    .aliveEnemiesOf(warlordsNPC)
                    .stream()
                    .anyMatch(warlordsEntity -> warlordsEntity.getLocation().distanceSquared(warlordsNPC.getLocation()) > 400);
            if (anyPlayer20BlockAway && barrageOfFlamesDelay == 0) {
                Location loc = warlordsNPC.getLocation();

                Utils.playGlobalSound(loc, "mage.inferno.activation", 500, 0.5f);
                Utils.playGlobalSound(loc, "mage.inferno.activation", 500, 0.5f);
                new GameRunnable(warlordsNPC.getGame()) {
                    @Override
                    public void run() {
                        if (warlordsNPC.isDead()) {
                            this.cancel();
                            return;
                        }

                        barrageOfFlames();
                    }
                }.runTaskLater(40);
                barrageOfFlamesDelay = 10;
            } else {
                if (barrageOfFlamesDelay > 0) {
                    barrageOfFlamesDelay--;
                }
            }
        }
    }

    private void barrageOfFlames() {
        new GameRunnable(warlordsNPC.getGame()) {
            final List<WarlordsEntity> enemies = PlayerFilter.playingGame(warlordsNPC.getGame())
                                                             .aliveEnemiesOf(warlordsNPC)
                                                             .toList();
            int counter = 0;

            @Override
            public void run() {
                if (warlordsNPC.isDead()) {
                    this.cancel();
                    return;
                }

                counter++;
                for (Fireball fireball : warlordsNPC.getAbilitiesMatching(Fireball.class)) {
                    WarlordsEntity enemy = enemies.get(counter % enemies.size());
                    Location predictedLocation = PredictTargetFutureLocationGoal.lookAtLocation(
                            warlordsNPC.getEyeLocation(),
                            PredictTargetFutureLocationGoal.predictFutureLocation(warlordsNPC, enemy).add(0, 1, 0)
                    );
                    fireball.fire(warlordsNPC, predictedLocation);
                }

                if (counter == 4 * 5) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 5);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (!healthCheck && self.getCurrentHealth() / self.getMaxHealth() <= 0.5) {
            healthCheck = true;
            List<Location> spawnLocations = LocationUtils.getCircle(warlordsNPC.getLocation(), 3, pveOption.playerCount());
            for (Location location : spawnLocations) {
                pveOption.spawnNewMob(Mob.FIRE_SPLITTER.createMob(location));
            }
        }
    }

    private static class BurstOfFlames extends AbstractPveAbility implements Damages<BurstOfFlames.DamageValues> {

        private float radius = 10;

        public BurstOfFlames() {
            super("Burst of Flames", 860, 940, 5, 100, false);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {

            EffectUtils.playSphereAnimation(
                    wp.getLocation(),
                    radius,
                    Particle.FLAME,
                    1,
                    3.5f
            );
            PlayerFilter.entitiesAround(wp, radius, radius, radius)
                        .aliveEnemiesOf(wp)
                        .forEach(warlordsEntity -> {
                            warlordsEntity.addInstance(InstanceBuilder
                                    .damage()
                                    .ability(this)
                                    .source(wp)
                                    .value(damageValues.burstOfFlamesDamage)
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

            private final Value.RangedValue burstOfFlamesDamage = new Value.RangedValue(860, 940);
            private final List<Value> values = List.of(burstOfFlamesDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }
}
