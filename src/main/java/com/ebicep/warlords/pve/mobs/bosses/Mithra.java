package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.spider.Spider;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.util.Vector;

public class Mithra extends AbstractZombie implements BossMob {

    private boolean flamePhaseTrigger = false;
    private boolean flamePhaseTriggerTwo = false;
    private boolean preventBarrage = false;

    public Mithra(Location spawnLocation) {
        super(spawnLocation,
                "Mithra",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.IRON_QUEEN),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 200, 200, 200),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 200, 200, 200),
                        Weapons.SILVER_PHANTASM_SWORD_3.getItem()
                ),
                20000,
                0.28f,
                20,
                1200,
                1600
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        for (int i = 0; i < (2 * option.getGame().warlordsPlayers().count()); i++) {
            option.spawnNewMob(new Spider(spawnLocation));
        }
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
                knockTarget.setVelocity(name, new Vector(0, 1, 0), false);
                knockTarget.addDamageInstance(
                        warlordsNPC,
                        "Virtue Strike",
                        400 * playerCount,
                        500 * playerCount,
                        0,
                        100,
                        false
                );
            }
        }

        if (ticksElapsed % 200 == 0 && !preventBarrage) {
            int multiplier = option.getDifficulty() == DifficultyIndex.HARD ? 7 : 10;
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

        if (warlordsNPC.getHealth() < (warlordsNPC.getMaxHealth() * 0.7f) && !flamePhaseTrigger) {
            flamePhaseTrigger = true;
            preventBarrage = true;
            immolation(option, loc);
        }

        if (warlordsNPC.getHealth() < (warlordsNPC.getMaxHealth() * 0.35f) && !flamePhaseTriggerTwo) {
            flamePhaseTriggerTwo = true;
            preventBarrage = true;
            immolation(option, loc);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.BLACK)
                                                                       .withColor(Color.WHITE)
                                                                       .with(FireworkEffect.Type.BALL_LARGE)
                                                                       .build());
        EffectUtils.strikeLightning(deathLocation, false, 2);
    }

    @Override
    public Component getDescription() {
        return Component.text("The Envoy Queen of Illusion", NamedTextColor.WHITE);
    }

    @Override
    public NamedTextColor getColor() {
        return NamedTextColor.LIGHT_PURPLE;
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
                warlordsNPC.getSpec().getRed().onActivate(warlordsNPC, null);

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
                    Utils.addKnockback(name, warlordsNPC.getLocation(), flameTarget, -1, 0.1f);
                    flameTarget.addDamageInstance(
                            warlordsNPC,
                            "Immolation",
                            damage,
                            damage,
                            0,
                            100,
                            false
                    );

                    warlordsNPC.addHealingInstance(
                            warlordsNPC,
                            "Immolation",
                            damage * 0.5f,
                            damage * 0.5f,
                            0,
                            100,
                            false,
                            false
                    );
                }

                if (counter == 50) {
                    preventBarrage = false;
                    this.cancel();
                    warlordsNPC.getSpeed().addBaseModifier(70);
                }
            }
        }.runTaskTimer(40, 5);
    }
}
