package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.abilities.SpawnMobAbility;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.ArachnoVeneratus;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.EggSac;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.EventEggSac;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

public class Mithra extends AbstractMob implements BossMob {

    private boolean flamePhaseTrigger = false;
    private boolean flamePhaseTriggerTwo = false;
    private boolean preventBarrage = false;

    public Mithra(Location spawnLocation) {
        this(spawnLocation, "Mithra", 20000, 0.28f, 20, 1200, 1600);
    }

    public Mithra(
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
                new FlameBurst(1000),
                new SpawnMobAbility(1000, Mob.ARACHNO_VENERATUS) {
                    @Override
                    public int getSpawnAmount() {
                        return (int) pveOption.getGame().warlordsPlayers().count();
                    }
                },
                new HibernatingEggSac()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.MITHRA;
    }

    @Override
    public Component getDescription() {
        return Component.text("The Envoy Queen of Illusion", NamedTextColor.WHITE);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.LIGHT_PURPLE;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        new GameRunnable(option.getGame()) {
            @Override
            public void run() {
                for (int i = 0; i < (2 * option.getGame().warlordsPlayers().count()); i++) {
                    option.spawnNewMob(new ArachnoVeneratus(spawnLocation));
                }
            }
        }.runTaskLater(10);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        Location loc = warlordsNPC.getLocation();
        long playerCount = option.getGame().warlordsPlayers().count();
        int hitRadius = 15;

        if (ticksElapsed % 150 == 0) {
            EffectUtils.playSphereAnimation(loc, hitRadius, Particle.FLAME, 1);
            for (WarlordsEntity knockTarget : PlayerFilter
                    .entitiesAround(warlordsNPC, hitRadius, hitRadius, hitRadius)
                    .aliveEnemiesOf(warlordsNPC)
                    .closestFirst(warlordsNPC)
            ) {
                EffectUtils.strikeLightning(knockTarget.getLocation(), false);
                knockTarget.setVelocity(name, new Vector(0, .75, 0), false);
                knockTarget.addInstance(InstanceBuilder
                        .damage()
                        .cause("Virtue Strike")
                        .source(warlordsNPC)
                        .min(400 * playerCount)
                        .max(500 * playerCount)
                );
            }
        }

        if (ticksElapsed % 200 == 0 && !preventBarrage) {
            DifficultyIndex difficulty = option.getDifficulty();
            int multiplier = difficulty == DifficultyIndex.EXTREME ? 4 : difficulty == DifficultyIndex.HARD ? 7 : 10;
            Utils.playGlobalSound(loc, "mage.inferno.activation", 500, 0.5f);
            Utils.playGlobalSound(loc, "mage.inferno.activation", 500, 0.5f);
            new GameRunnable(warlordsNPC.getGame()) {
                @Override
                public void run() {
                    if (warlordsNPC.isDead()) {
                        this.cancel();
                    }

                    warlordsNPC.addSpeedModifier(warlordsNPC, "Mithra Slowness", -99, 100);
                    flameBurstBarrage(multiplier, 8);
                }
            }.runTaskLater(40);
        }

        if (warlordsNPC.getCurrentHealth() < (warlordsNPC.getMaxHealth() * 0.7f) && !flamePhaseTrigger) {
            flamePhaseTrigger = true;
            preventBarrage = true;
            immolation(option, loc);
        }

        if (warlordsNPC.getCurrentHealth() < (warlordsNPC.getMaxHealth() * 0.35f) && !flamePhaseTriggerTwo) {
            flamePhaseTriggerTwo = true;
            preventBarrage = true;
            immolation(option, loc);
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playFirework(deathLocation, FireworkEffect.builder()
                                                              .withColor(Color.BLACK)
                                                              .withColor(Color.WHITE)
                                                              .with(FireworkEffect.Type.BALL_LARGE)
                                                              .build());
        EffectUtils.strikeLightning(deathLocation, false, 2);
    }

    private void flameBurstBarrage(int delayBetweenShots, int amountOfShots) {
        new GameRunnable(warlordsNPC.getGame()) {
            int counter = 0;

            @Override
            public void run() {
                if (warlordsNPC.isDead() || preventBarrage) {
                    this.cancel();
                    return;
                }

                counter++;
                for (FlameBurst flameBurst : warlordsNPC.getAbilitiesMatching(FlameBurst.class)) {
                    flameBurst.setCurrentCooldown(0);
                    warlordsNPC.addEnergy(warlordsNPC, "Flame Burst Barrage", flameBurst.getEnergyCostValue());
                }

                if (counter == amountOfShots) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, delayBetweenShots);
    }

    private void immolation(PveOption option, Location loc) {
        warlordsNPC.addSpeedModifier(warlordsNPC, "Mithra Slowness", -99, 250);
        for (int i = 0; i < 3; i++) {
            Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 500, 0.6f);
        }

        ChatUtils.sendTitleToGamePlayers(
                getWarlordsNPC().getGame(),
                Component.text("PREPARE TO DIE", NamedTextColor.RED),
                Component.text("Immolation Spell", NamedTextColor.LIGHT_PURPLE),
                20, 60, 20
        );

        float damage = switch (option.getDifficulty()) {
            case ENDLESS, HARD -> 200;
            case EXTREME -> 250;
            case EASY -> 50;
            default -> 100;
        };
        new GameRunnable(warlordsNPC.getGame()) {
            int counter = 0;

            @Override
            public void run() {
                if (warlordsNPC.isDead()) {
                    this.cancel();
                    return;
                }

                counter++;
                double radius = (2 * counter);
                Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 500, 0.8f);
                Utils.playGlobalSound(loc, "warrior.laststand.activation", 500, 0.6f);
                EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), radius, Particle.FLAME, 2, counter);
                for (WarlordsEntity flameTarget : PlayerFilter
                        .entitiesAround(warlordsNPC, radius, radius, radius)
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    Utils.addKnockback(name, warlordsNPC.getLocation(), flameTarget, -0.25, 0.07f);
                    flameTarget.addInstance(InstanceBuilder
                            .damage()
                            .cause("Immolation")
                            .source(warlordsNPC)
                            .value(damage)
                    );
                    warlordsNPC.addInstance(InstanceBuilder
                            .healing()
                            .cause("Immolation")
                            .source(warlordsNPC)
                            .value(damage * 0.5f)
                    );
                }

                if (counter == 40) {
                    preventBarrage = false;
                    this.cancel();
                    warlordsNPC.getSpeed().addBaseModifier(20);
                    for (SpawnMobAbility spawnMobAbility : warlordsNPC.getAbilitiesMatching(SpawnMobAbility.class)) {
                        spawnMobAbility.setCurrentCooldown(0);
                    }
                }
            }
        }.runTaskTimer(40, 5);
    }

    private static class HibernatingEggSac extends AbstractPveAbility {

        public HibernatingEggSac() {
            super("Hibernating Egg Sac", 15, 50, 7);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {

            Location loc = pveOption.getRandomSpawnLocation((WarlordsEntity) null);
            if (loc == null) {
                return false;
            }
            Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_SHOOT, 2, 1);
            EggSac eggSac = new EggSac(loc, (int) Math.pow(1500, 1 + ((pveOption.playerCount() - 1) * .04)));
            pveOption.spawnNewMob(eggSac);
            new GameRunnable(wp.getGame()) {
                final Location particleToLocation = loc.clone().add(0, -.5, 0);
                int counter = 0;

                @Override
                public void run() {
                    if (wp.isDead() || eggSac.getWarlordsNPC().isDead()) {
                        this.cancel();
                        return;
                    }
                    if (counter++ == 10) {
                        this.cancel();
                        if (!pveOption.getMobs().contains(eggSac)) {
                            return;
                        }
                        WarlordsNPC eggSacWarlordsNPC = eggSac.getWarlordsNPC();
                        eggSacWarlordsNPC.die(eggSacWarlordsNPC);
                        Location location = eggSacWarlordsNPC.getLocation();
                        if (EventEggSac.ARMOR_STAND) {
                            location.add(0, 1.31, 0);
                        }
                        for (int i = 0; i < pveOption.playerCount() * 1.5; i++) {
                            Location spawnLocation = location.clone().add(ThreadLocalRandom.current().nextDouble(), 0, ThreadLocalRandom.current().nextDouble());
                            pveOption.spawnNewMob(new ArachnoVeneratus(spawnLocation));
                        }
                        Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_DEATH, 2, 3f);
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                for (Player p : loc.getWorld().getPlayers()) {
                                    p.stopSound(Sound.ENTITY_ENDER_DRAGON_DEATH);
                                }
                            }
                        }.runTaskLater(Warlords.getInstance(), 14);
                    } else {
                        EffectUtils.playParticleLinkAnimation(
                                particleToLocation,
                                wp.getLocation(),
                                175,
                                175,
                                175,
                                2
                        );
                        for (int i = 0; i < Math.pow(1.5, counter / 2f); i++) {
                            Utils.playGlobalSound(loc, Sound.ENTITY_SPIDER_AMBIENT, 500, 2);
                        }
                    }
                }
            }.runTaskTimer(0, 20);
            return true;
        }
    }
}
