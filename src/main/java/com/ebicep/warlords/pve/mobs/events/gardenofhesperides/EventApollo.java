package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.abilities.ImpalingStrike;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.abilities.SpawnMobAbility;
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
import java.util.List;

public class EventApollo extends AbstractMob implements BossMob, LesserGod {

    public EventApollo(Location spawnLocation) {
        this(spawnLocation, "Apollo", 20000, 0, 10, 450, 600);
    }

    public EventApollo(
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
                new PoisonArrow(),
                new SpawnMobAbility(10, Mob.SKELETAL_ENTROPY, 10) {
                    @Override
                    public AbstractMob createMob(@Nonnull WarlordsEntity wp) {
                        return mobToSpawn.createMob(pveOption.getRandomSpawnLocation((WarlordsEntity) null));
                    }

                    @Override
                    public int getSpawnAmount() {
                        return (int) (pveOption.getGame().warlordsPlayers().count());
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
                List<Location> spawnLocations = LocationUtils.getCircle(warlordsNPC.getLocation(), 3, option.playerCount());
                for (Location location : spawnLocations) {
                    option.spawnNewMob(Mob.SKELETAL_MESMER.createMob(location));
                }
            }
        }.runTaskLater(40);

    }

    private static class PoisonArrow extends AbstractPveAbility {

        public PoisonArrow() {
            super("Poison Arrow", 550, 750, 5, 100, false);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {

            PlayerFilter.playingGame(wp.getGame())
                        .aliveEnemiesOf(wp)
                        .first(warlordsEntity -> {
                            EffectUtils.playParticleLinkAnimation(warlordsEntity.getLocation(), wp.getLocation(), Particle.VILLAGER_HAPPY);
                            ImpalingStrike.giveLeechCooldown(
                                    wp,
                                    warlordsEntity,
                                    3,
                                    .20f,
                                    .35f,
                                    event -> {}
                            );
                            warlordsEntity.addInstance(InstanceBuilder
                                    .damage()
                                    .ability(this)
                                    .source(wp)
                                    .value(damageValues.poisonArrowDamage)
                            );
                        });
            return true;
        }

        private final DamageValues damageValues = new DamageValues();

        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.RangedValue poisonArrowDamage = new Value.RangedValue(550, 750);
            private final List<Value> values = List.of(poisonArrowDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }

    @Override
    public Component getDescription() {
        return Component.text("God of Everything..?", TextColor.color(255, 188, 54));
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.GOLD;
    }

}
