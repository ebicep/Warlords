package com.ebicep.warlords.pve.mobs.events.pharaohsrevenge;

import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.ZombieLancer;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EventNarmer extends AbstractMob implements BossMob {

    private final int earthQuakeRadius = 12;
    private final int executeRadius = 80;
    private final List<WarlordsEntity> ancestors = new ArrayList<>();
    private final List<WarlordsEntity> acolytes = new ArrayList<>();
    private int timesMegaEarthQuakeActivated = 0;
    private Listener listener;
    private int ticksUntilNewAcolyte = 0;
    private int acolyteDeathTickWindow = 0;
    private float hpDamageIncrease = 1;

    public EventNarmer(Location spawnLocation) {
        this(spawnLocation,
                "Narmer",
                16000,
                0,
                20,
                1600,
                2000
        );
    }

    public EventNarmer(
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
                new GroundShred()
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        int currentWave = option.getWaveCounter();
        if (currentWave % 5 == 0 && currentWave > 5) {
            hpDamageIncrease = 1 + .25f * (currentWave / 5f - 1);
            warlordsNPC.setMaxHealthAndHeal(warlordsNPC.getMaxBaseHealth() * hpDamageIncrease);
        }

        Location location = warlordsNPC.getLocation();
        AbstractMob ancestor;
        if (Math.random() < 0.5) {
            ancestor = new EventDjer(location);
        } else {
            ancestor = new EventDjet(location);
        }
        option.spawnNewMob(ancestor);
        ancestor.getWarlordsNPC().teleport(location);
        ancestors.add(ancestor.getWarlordsNPC());
        new GameRunnable(option.getGame()) {

            @Override
            public void run() {
                ChatUtils.sendTitleToGamePlayers(
                        getWarlordsNPC().getGame(),
                        Component.text(getWarlordsNPC().getName() + " has called", NamedTextColor.GOLD),
                        Component.text(ancestor.getName(), NamedTextColor.YELLOW),
                        20, 20, 20
                );
            }
        }.runTaskLater(20);

        int playerCount = option.playerCount();
        for (int i = 0; i < playerCount; i++) {
            EventNarmerAcolyte acolyte = new EventNarmerAcolyte(warlordsNPC.getLocation());
            option.spawnNewMob(acolyte);
            acolyte.getWarlordsNPC().teleport(warlordsNPC.getLocation());
            acolytes.add(acolyte.getWarlordsNPC());
        }

        int berserkerSpawnCount = 5;
        if (playerCount == 3) {
            berserkerSpawnCount = 7;
        } else if (playerCount == 4) {
            berserkerSpawnCount = 9;
        }
        Mob bersekerToSpawn = switch (currentWave) {
            case 5 -> Mob.BASIC_WARRIOR_BERSERKER;
            case 10, 15 -> Mob.INTERMEDIATE_WARRIOR_BERSERKER;
            case 20, 25 -> Mob.ADVANCED_WARRIOR_BERSERKER;
            default -> currentWave % 5 == 0 ? Mob.ADVANCED_WARRIOR_BERSERKER : null;
        };
        if (bersekerToSpawn != null) {
            for (int i = 0; i < berserkerSpawnCount; i++) {
                AbstractMob berserker = bersekerToSpawn.createMob(warlordsNPC.getLocation());
                option.spawnNewMob(berserker);
            }
        }

        for (int i = 0; i < 8; i++) {
            option.spawnNewMob(new ZombieLancer(location));
        }

        listener = new Listener() {

            @EventHandler
            public void onDamageHealEvent(WarlordsDamageHealingEvent event) {
                if (event.getSource().equals(warlordsNPC)) {
                    event.setMin(event.getMin() * hpDamageIncrease);
                    event.setMax(event.getMax() * hpDamageIncrease);
                } else if (event.getWarlordsEntity().equals(warlordsNPC)) {
                    Location loc = warlordsNPC.getLocation();
                    if (!ancestors.isEmpty()) {
                        warlordsNPC.getGame().forEachOnlineWarlordsEntity(we -> {
                            Utils.playGlobalSound(loc, Sound.ENTITY_BLAZE_HURT, 2, 0.2f);
                            Utils.playGlobalSound(loc, "mage.arcaneshield.activation", 0.4f, 0.5f);
                            we.sendMessage(Component.text("Narmer cannot take damage while his ancestors are still alive!", NamedTextColor.RED));
                        });
                        event.setCancelled(true);
                    }
                    float executeHealth = warlordsNPC.getMaxHealth() * 0.4f;
                    if (warlordsNPC.getCurrentHealth() < executeHealth && !acolytes.isEmpty()) {
                        warlordsNPC.setCurrentHealth(warlordsNPC.getCurrentHealth());
                        warlordsNPC.getGame().forEachOnlineWarlordsEntity(we -> {
                            Utils.playGlobalSound(loc, Sound.ENTITY_BLAZE_HURT, 2, 0.2f);
                            Utils.playGlobalSound(loc, "mage.arcaneshield.activation", 0.4f, 0.5f);
                            we.sendMessage(Component.text("Narmer cannot take more damage while his acolytes are still alive!", NamedTextColor.RED));
                        });
                        event.setCancelled(true);
                    }
                }
            }

            @EventHandler
            private void onAllyDeath(WarlordsDeathEvent event) {
                WarlordsEntity eventPlayer = event.getWarlordsEntity();
                Location location = warlordsNPC.getLocation();

                if (eventPlayer.isTeammate(warlordsNPC)) {
                    warlordsNPC.setCurrentHealth(warlordsNPC.getCurrentHealth() * 1.15f);
                }

                if (ancestors.contains(eventPlayer)) {
                    ancestors.remove(eventPlayer);
                    if (ancestors.isEmpty()) {
                        warlordsNPC.getSpeed().setBaseSpeedToWalkingSpeed(0.16f);
                    }
                }

                if (acolytes.contains(eventPlayer)) {
                    acolytes.remove(eventPlayer);
                    Utils.playGlobalSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 0.4f);
                    EffectUtils.playHelixAnimation(
                            location.add(0, 0.15, 0),
                            12,
                            Particle.SPELL,
                            3,
                            60
                    );

                    if (acolyteDeathTickWindow > 0) {
                        Utils.playGlobalSound(location, Sound.ENTITY_WITHER_DEATH, 500, 0.2f);
                        Utils.playGlobalSound(location, Sound.ENTITY_WITHER_DEATH, 500, 0.2f);
                        EffectUtils.strikeLightning(location, false, 12);
                        List<WarlordsEntity> warlordsEntities = PlayerFilter
                                .entitiesAround(warlordsNPC, executeRadius, executeRadius, executeRadius)
                                .aliveEnemiesOf(warlordsNPC)
                                .toList();
                        for (WarlordsEntity enemy : warlordsEntities) {
                            enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Death Wish")
                                    .source(warlordsNPC)
                                    .min(965 * 8)
                                    .max(1138 * 8)
                            );
                            enemy.sendMessage(Component.text("HINT: Killing Acolytes too quickly might result in an unfavourable outcome.", NamedTextColor.RED));
                        }
                        for (WarlordsEntity warlordsEntity : warlordsEntities) {
                            ChallengeAchievements.checkForAchievement(warlordsEntity, ChallengeAchievements.FISSURED_END);
                            break;
                        }
                        timesMegaEarthQuakeActivated++;
                    } else {
                        for (WarlordsEntity enemy : PlayerFilter
                                .entitiesAround(warlordsNPC, 15, 15, 15)
                                .aliveEnemiesOf(warlordsNPC)
                        ) {
                            Utils.addKnockback(name, warlordsNPC.getLocation(), enemy, -2.5, 0.25);
                            enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Acolyte Revenge")
                                    .source(warlordsNPC)
                                    .min(965)
                                    .max(1138)
                            );
                        }
                    }

                    if (acolyteDeathTickWindow <= 0) {
                        acolyteDeathTickWindow = 20;
                    }

                    ticksUntilNewAcolyte = 300;
                }
            }
        };
        warlordsNPC.getGame().registerEvents(listener);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        Location loc = warlordsNPC.getLocation();
        long playerCount = option.getGame().warlordsPlayers().count();

        if (acolytes.size() < playerCount && ticksUntilNewAcolyte <= 0) {
            EventNarmerAcolyte acolyte = new EventNarmerAcolyte(loc);
            option.spawnNewMob(acolyte);
            acolytes.add(acolyte.getWarlordsNPC());
            ticksUntilNewAcolyte = 300;
        }

        if (ticksUntilNewAcolyte > 0) {
            ticksUntilNewAcolyte--;
        }

        if (acolyteDeathTickWindow > 0) {
            acolyteDeathTickWindow--;

            ChatUtils.sendTitleToGamePlayers(
                    getWarlordsNPC().getGame(),
                    Component.text("Death Wish", NamedTextColor.RED),
                    Component.text(acolyteDeathTickWindow / 10f, NamedTextColor.YELLOW),
                    0, acolyteDeathTickWindow, 0
            );
        }

        if (ticksElapsed % 15 == 0) {
            for (WarlordsEntity acolyte : acolytes) {
                EffectUtils.playParticleLinkAnimation(loc, acolyte.getLocation(), Particle.DRIP_LAVA);
            }
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        EffectUtils.playRandomHitEffect(self.getLocation(), 255, 255, 255, 7);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, Particle.FIREWORKS_SPARK, 3, 20);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.WHITE)
                                                                       .with(FireworkEffect.Type.STAR)
                                                                       .withTrail()
                                                                       .build());

        if (timesMegaEarthQuakeActivated >= 2) {
            ChallengeAchievements.checkForAchievement(killer, ChallengeAchievements.NEAR_DEATH_EXPERIENCE);
        }

        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.RED;
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_NARMER;
    }

    @Override
    public Component getDescription() {
        return Component.text("Unifier of Worlds", NamedTextColor.YELLOW);
    }

    private static class GroundShred extends AbstractPveAbility {

        private final int earthQuakeRadius = 12;

        public GroundShred() {
            super("Ground Shred", 8, 50);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            Location loc = wp.getLocation();
            Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 0.4f);
            EffectUtils.strikeLightning(loc, false);
            EffectUtils.playSphereAnimation(loc, earthQuakeRadius, Particle.SPELL_WITCH, 2);
            EffectUtils.playHelixAnimation(loc, earthQuakeRadius, Particle.FIREWORKS_SPARK, 2, 40);
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(wp, earthQuakeRadius, earthQuakeRadius, earthQuakeRadius)
                    .aliveEnemiesOf(wp)
            ) {
                Utils.addKnockback(name, loc, enemy, -2.5, 0.25);
                enemy.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .value(damageValues.groundShredDamage)
                );
            }
            return true;
        }

        private final DamageValues damageValues = new DamageValues();

        public static class DamageValues implements Value.ValueHolder {

            private final Value.RangedValue groundShredDamage = new Value.RangedValue(920, 1080);
            private final List<Value> values = List.of(groundShredDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }

    }
}
