package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.spider.Spider;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Mithra extends AbstractZombie implements BossMob {

    private boolean flamePhaseTrigger = false;
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
    public void onSpawn(WaveDefenseOption option) {
        for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        ChatColor.LIGHT_PURPLE + "Mithra",
                        ChatColor.WHITE + "The Envoy Queen of Illusion",
                        20, 30, 20
                );
            }
        }

        for (int i = 0; i < (2 * option.getGame().warlordsPlayers().count()); i++) {
            option.spawnNewMob(new Spider(spawnLocation));
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        Location loc = warlordsNPC.getLocation();
        long playerCount = option.getGame().warlordsPlayers().count();
        int hitRadius = 15;

        if (ticksElapsed % 150 == 0) {
            EffectUtils.playSphereAnimation(loc, hitRadius, ParticleEffect.FLAME, 1);
            for (WarlordsEntity knockTarget : PlayerFilter
                    .entitiesAround(warlordsNPC, hitRadius, hitRadius, hitRadius)
                    .aliveEnemiesOf(warlordsNPC)
                    .closestFirst(warlordsNPC)
            ) {
                EffectUtils.strikeLightning(knockTarget.getLocation(), false);
                knockTarget.setVelocity(new Vector(0, 1, 0), false);
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

        if (ticksElapsed % 310 == 0 && !preventBarrage) {
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

        if (warlordsNPC.getHealth() < (warlordsNPC.getMaxHealth() * 0.5f) && !flamePhaseTrigger) {
            flamePhaseTrigger = true;
            preventBarrage = true;
            warlordsNPC.addSpeedModifier(warlordsNPC, "Mithra Slowness", -99, 200);
            for (int i = 0; i < 3; i++) {
                Utils.playGlobalSound(loc, Sound.ENDERDRAGON_GROWL, 500, 0.6f);
            }

            for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
                if (we.getEntity() instanceof Player) {
                    PacketUtils.sendTitle(
                            (Player) we.getEntity(),
                            ChatColor.RED + "PREPARE TO DIE",
                            ChatColor.LIGHT_PURPLE + "Immolation Spell",
                            20, 60, 20
                    );
                }
            }

            float damage;
            switch (option.getDifficulty()) {
                case HARD:
                    damage = 200;
                    break;
                case EASY:
                    damage = 50;
                    break;
                default:
                    damage = 150;
                    break;
            }
            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;

                @Override
                public void run() {
                    if (warlordsNPC.isDead()) {
                        this.cancel();
                        return;
                    }

                    counter++;
                    double radius = (1.5 * counter);
                    Utils.playGlobalSound(loc, Sound.ENDERDRAGON_GROWL, 500, 0.8f);
                    Utils.playGlobalSound(loc, "warrior.laststand.activation", 500, 0.6f);
                    EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), radius, ParticleEffect.FLAME, 2, counter);
                    for (WarlordsEntity flameTarget : PlayerFilter
                            .entitiesAround(warlordsNPC, radius, radius, radius)
                            .aliveEnemiesOf(warlordsNPC)
                    ) {
                        Utils.addKnockback(warlordsNPC.getLocation(), flameTarget, -1, 0.1f);
                        flameTarget.addDamageInstance(
                                warlordsNPC,
                                "Immolation",
                                damage,
                                damage,
                                0,
                                100,
                                false
                        );
                    }

                    if (counter == 50) {
                        this.cancel();
                        warlordsNPC.getSpeed().addBaseModifier(70);
                    }
                }
            }.runTaskTimer(40, 5);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
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
                counter++;
                warlordsNPC.getSpec().getRed().onActivate(warlordsNPC, null);

                if (counter == amountOfShots) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, delayBetweenShots);
    }
}
