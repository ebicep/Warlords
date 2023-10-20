package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.abilities.AbstractSpawnMobAbility;
import com.ebicep.warlords.pve.mobs.abilities.SpawnMobAbility;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.NarmerAcolyte;
import com.ebicep.warlords.pve.mobs.flags.DynamicFlags;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.ZombieLancer;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Narmer extends AbstractMob implements BossMob {

    private final int executeRadius = 80;
    private final List<WarlordsEntity> acolytes = new ArrayList<>();
    private int timesMegaEarthQuakeActivated = 0;
    private Listener listener;
    private int acolyteDeathTickWindow = 0;
    private int acolyteSpawnTickWindow = 0;

    public Narmer(Location spawnLocation) {
        this(spawnLocation, "Narmer", 16000, 0.16f, 20, 1600, 2000);
    }

    public Narmer(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
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
                new FlameBurst(15),
                new GroundShred(),
                new SpawnMobAbility(6, Mob.ZOMBIE_LANCER, true) {

                    @Override
                    public AbstractMob createMob(@Nonnull WarlordsEntity wp) {
                        AbstractMob spawnedMob = super.createMob(wp);
                        spawnedMob.getDynamicFlags().add(DynamicFlags.NO_INSIGNIA);
                        return spawnedMob;
                    }

                    @Override
                    public int getSpawnAmount() {
                        return (int) pveOption.getGame().warlordsPlayers().count();
                    }
                }
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.NARMER;
    }

    @Override
    public Component getDescription() {
        return Component.text("Unifier of Worlds", NamedTextColor.YELLOW);
    }

    @Override
    public NamedTextColor getColor() {
        return NamedTextColor.RED;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        DifficultyIndex difficulty = option.getDifficulty();

        if (difficulty == DifficultyIndex.ENDLESS) {
            for (AbstractAbility ability : warlordsNPC.getAbilities()) {
                if (ability instanceof GroundShred) {
                    ability.getCooldown().addMultiplicativeModifierAdd("Narmer Endless", -0.5f);
                }
            }
        }

        SpawnNarmerAcolyteAbility spawnNarmerAcolyteAbility = new SpawnNarmerAcolyteAbility(this);
        this.playerClass.addAbility(spawnNarmerAcolyteAbility);
        if (option.playerCount() >= 3) {
            this.playerClass.addAbility(new SpawnMobAbility(7, Mob.UNDEAD_ACOLYTE, 10) {

                private WarlordsEntity customTarget;

                @Override
                public int getSpawnAmount() {
                    if (acolyteSpawnTickWindow > 0) {
                        return 0;
                    }
                    if (acolyteDeathTickWindow > 0) {
                        return 1;
                    }
//                List<Float> sortedHealths = acolytes.stream().map(WarlordsEntity::getHealth)
//                                                    .sorted(Float::compareTo)
//                                                    .toList();
                    if (acolytes.size() > 1) {
                        for (WarlordsEntity firstAcolyte : acolytes) {
                            float firstHealth = firstAcolyte.getHealth();
                            if (firstHealth > 5000) {
                                continue;
                            }
                            Location firstLocation = firstAcolyte.getLocation();
                            for (WarlordsEntity secondAcolyte : acolytes) {
                                if (firstAcolyte.equals(secondAcolyte)) {
                                    continue;
                                }
                                float secondHealth = secondAcolyte.getHealth();
                                if (secondHealth > 5000) {
                                    continue;
                                }
                                Location secondLocation = secondAcolyte.getLocation();
                                if (firstLocation.distanceSquared(secondLocation) < 4) {
                                    customTarget = firstAcolyte;
                                    return 1;
                                }
                            }
                        }
                    }
                    return 0;
                }

                @Override
                public void onMobCreate(AbstractMob mobSpawned) {
                    mobSpawned.setTarget(customTarget);
                }
            });
        }

        float multiplier = difficulty == DifficultyIndex.EXTREME ? 3 : difficulty == DifficultyIndex.HARD ? 2 : 1;

        for (int i = 0; i < (multiplier * option.playerCount()); i++) {
            spawnNarmerAcolyteAbility.spawnMob(warlordsNPC);
        }

        for (int i = 0; i < 8; i++) {
            option.spawnNewMob(new ZombieLancer(warlordsNPC.getLocation()));
        }

        listener = new Listener() {

            @EventHandler
            public void onDamageHealEvent(WarlordsDamageHealingEvent event) {
                if (event.getWarlordsEntity().equals(getWarlordsNPC())) {
                    float executeHealth = warlordsNPC.getMaxHealth() * 0.4f;
                    if (warlordsNPC.getHealth() < executeHealth && !acolytes.isEmpty()) {
                        warlordsNPC.setHealth(warlordsNPC.getHealth());
                        Location loc = warlordsNPC.getLocation();
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
                WarlordsEntity dead = event.getWarlordsEntity();
                Location location = warlordsNPC.getLocation();

                if (dead.isTeammate(warlordsNPC)) {
                    warlordsNPC.setHealth(warlordsNPC.getHealth() * 1.15f);
                }

                if (acolytes.contains(dead)) {
                    acolytes.remove(dead);
                    Utils.playGlobalSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 0.4f);
                    EffectUtils.playHelixAnimation(
                            location.add(0, 0.15, 0),
                            12,
                            Particle.SPELL,
                            3,
                            60
                    );

                    float multiplier = switch (difficulty) {
                        case EASY -> 2;
                        case HARD -> 16;
                        case EXTREME -> 32;
                        default -> 8;
                    };
                    if (acolyteDeathTickWindow > 0) {
                        Utils.playGlobalSound(location, Sound.ENTITY_WITHER_DEATH, 500, 0.2f);
                        Utils.playGlobalSound(location, Sound.ENTITY_WITHER_DEATH, 500, 0.2f);
                        EffectUtils.strikeLightning(location, false, 12);
                        List<WarlordsEntity> warlordsEntities = PlayerFilter
                                .entitiesAround(warlordsNPC, executeRadius, executeRadius, executeRadius)
                                .aliveEnemiesOf(warlordsNPC)
                                .toList();
                        for (WarlordsEntity enemy : warlordsEntities) {
                            enemy.addDamageInstance(
                                    warlordsNPC,
                                    "Death Wish",
                                    965 * multiplier,
                                    1138 * multiplier,
                                    0,
                                    100
                            );
                            enemy.sendMessage(Component.text("HINT: Killing Acolytes too quickly might result in an unfavourable outcome.",
                                    NamedTextColor.RED
                            ));
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
                            enemy.addDamageInstance(
                                    warlordsNPC,
                                    "Acolyte Revenge",
                                    965,
                                    1138,
                                    0,
                                    100
                            );
                        }
                    }

                    if (acolyteDeathTickWindow <= 0) {
                        acolyteDeathTickWindow = difficulty == DifficultyIndex.EXTREME ? 100 : difficulty == DifficultyIndex.HARD ? 60 : 20;
                    }

                    List<WarlordsEntity> selfAcolytes = spawnNarmerAcolyteAbility.getSelfAcolytes();
                    if (selfAcolytes.contains(dead)) {
                        spawnNarmerAcolyteAbility.setCurrentCooldown(spawnNarmerAcolyteAbility.getCooldownValue());
                        selfAcolytes.remove(dead);
                    }

                }
            }
        };
        warlordsNPC.getGame().registerEvents(listener);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        Location loc = warlordsNPC.getLocation();

        if (acolyteDeathTickWindow > 0) {
            acolyteDeathTickWindow--;

            ChatUtils.sendTitleToGamePlayers(
                    getWarlordsNPC().getGame(),
                    Component.text("Death Wish", NamedTextColor.RED),
                    Component.text(acolyteDeathTickWindow / 10f, NamedTextColor.YELLOW),
                    0, acolyteDeathTickWindow, 0
            );
        }
        if (acolyteSpawnTickWindow > 0) {
            acolyteSpawnTickWindow--;
        }

        if (ticksElapsed % 15 == 0) {
            for (WarlordsEntity acolyte : acolytes) {
                EffectUtils.playParticleLinkAnimation(loc, acolyte.getLocation(), Particle.DRIP_LAVA);
            }
        }

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        EffectUtils.playRandomHitEffect(self.getLocation(), 255, 255, 255, 7);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, Particle.FIREWORKS_SPARK, 3, 20);
        EffectUtils.playFirework(deathLocation, FireworkEffect.builder()
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

    public void addAcolyte(WarlordsEntity acolyte) {
        acolytes.add(acolyte);
        acolyteSpawnTickWindow = 20 * 8;
    }

    public List<WarlordsEntity> getAcolytes() {
        return acolytes;
    }

    private static class SpawnNarmerAcolyteAbility extends AbstractSpawnMobAbility {

        private final Narmer narmer;
        private final List<WarlordsEntity> selfAcolytes = new ArrayList<>(); // spawned acolytes using this ability

        public SpawnNarmerAcolyteAbility(Narmer narmer) {
            super("Narmer Acolyte", 15, false);
            this.narmer = narmer;
            this.pveOption = narmer.pveOption;
        }

        @Override
        public AbstractMob createMob(@Nonnull WarlordsEntity wp) {
            return new NarmerAcolyte(wp.getLocation());
        }

        @Override
        public int getSpawnAmount() {
            long playerCount = pveOption.getGame().warlordsPlayers().count();
            DifficultyIndex difficulty = pveOption.getDifficulty();
            float multiplier = difficulty == DifficultyIndex.EXTREME ? 3 : difficulty == DifficultyIndex.HARD ? 2 : 1;
            return 2;//narmer.getAcolytes().size() < multiplier * playerCount ? 1 : 0;
        }

        @Override
        public void onMobSpawn(WarlordsNPC warlordsNPC) {
            narmer.addAcolyte(warlordsNPC);
            selfAcolytes.add(warlordsNPC);
        }

        public List<WarlordsEntity> getSelfAcolytes() {
            return selfAcolytes;
        }
    }

    private static class GroundShred extends AbstractPveAbility {

        private final int earthQuakeRadius = 12;

        public GroundShred() {
            super(
                    "Ground Shred",
                    750,
                    900,
                    8,
                    100
            );
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            wp.subtractEnergy(name, energyCost, false);

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
                enemy.addDamageInstance(
                        wp,
                        name,
                        minDamageHeal,
                        maxDamageHeal,
                        critChance,
                        critMultiplier
                );
            }
            return true;
        }
    }
}
