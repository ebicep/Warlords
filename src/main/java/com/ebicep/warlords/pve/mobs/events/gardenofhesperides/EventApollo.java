package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.abilities.ImpalingStrike;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.abilities.SpawnMobAbility;
import com.ebicep.warlords.pve.mobs.skeleton.AbstractSkeleton;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.List;

public class EventApollo extends AbstractSkeleton implements BossMinionMob {

    public EventApollo(Location spawnLocation) {
        this(spawnLocation, "Apollo", 20000, 0, 10, 450, 600);
    }

    public EventApollo(
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
                new PoisonArrow(),
                new SpawnMobAbility(10, Mob.SKELETAL_ENTROPY) {
                    @Override
                    public AbstractMob<?> createMob(@Nonnull WarlordsEntity wp) {
                        return mobToSpawn.createMob(pveOption.getRandomSpawnLocation(null));
                    }

                    @Override
                    public int getSpawnAmount() {
                        return (int) (pveOption.getGame().warlordsPlayers().count() + 2);
                    }
                }
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_APOLLO;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        new GameRunnable(option.getGame()) {

            @Override
            public void run() {
                List<Location> mesmerSpawnLocations = LocationUtils.getCircle(warlordsNPC.getLocation(), 3, option.playerCount());
                for (Location mesmerSpawnLocation : mesmerSpawnLocations) {
                    option.spawnNewMob(Mob.SKELETAL_MESMER.createMob(mesmerSpawnLocation));
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

    }

    private static class PoisonArrow extends AbstractPveAbility {

        public PoisonArrow() {
            super("Poison Arrow", 550, 750, 5, 100);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            wp.subtractEnergy(name, energyCost, false);
            PlayerFilter.playingGame(wp.getGame())
                        .aliveEnemiesOf(wp)
                        .first(warlordsEntity -> {
                            EffectUtils.playParticleLinkAnimation(warlordsEntity.getLocation(), wp.getLocation(), Particle.VILLAGER_HAPPY);
                            ImpalingStrike.giveLeechCooldown(
                                    wp,
                                    warlordsEntity,
                                    5,
                                    15,
                                    25,
                                    event -> {}
                            );
                            warlordsEntity.addDamageInstance(
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
