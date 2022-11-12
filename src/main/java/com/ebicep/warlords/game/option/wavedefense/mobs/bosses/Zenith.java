package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.bosses.bossminions.EnvoyLegionnaire;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
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

public class Zenith extends AbstractZombie implements BossMob {

    private final int stormRadius = 10;

    public Zenith(Location spawnLocation) {
        super(spawnLocation,
                "Zenith",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.PURPLE_ENDERMAN),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 255),
                        Weapons.VORPAL_SWORD.getItem()
                ),
                26000,
                0.36f,
                25,
                1800,
                2500
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 6);
        for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        ChatColor.DARK_PURPLE + getWarlordsNPC().getName(),
                        ChatColor.LIGHT_PURPLE + "Leader of the Illusion Vanguard",
                        20, 40, 20
                );
            }
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        long playerCount = option.getGame().warlordsPlayers().count();
        Location loc = warlordsNPC.getLocation();
        if (ticksElapsed % 240 == 0) {
            Utils.playGlobalSound(loc, "rogue.healingremedy.impact", 500, 0.85f);
            Utils.playGlobalSound(loc, "rogue.healingremedy.impact", 500, 0.85f);
            warlordsNPC.addSpeedModifier(warlordsNPC, "Armageddon Slowness", -99, 90);
            new GameRunnable(warlordsNPC.getGame()) {
                @Override
                public void run() {
                    EffectUtils.strikeLightningInCylinder(loc, stormRadius, false, 12, warlordsNPC.getGame());
                    shockwave(loc, stormRadius, 12, playerCount);
                    EffectUtils.strikeLightningInCylinder(loc, stormRadius + 5, false, 24, warlordsNPC.getGame());
                    shockwave(loc, stormRadius + 5, 24, playerCount);
                    EffectUtils.strikeLightningInCylinder(loc, stormRadius + 10, false, 36, warlordsNPC.getGame());
                    shockwave(loc, stormRadius + 10, 36, playerCount);
                    if (option.getDifficulty() == DifficultyIndex.HARD) {
                        EffectUtils.strikeLightningInCylinder(loc, stormRadius + 15, false, 48, warlordsNPC.getGame());
                        shockwave(loc, stormRadius + 15, 48, playerCount);
                        EffectUtils.strikeLightningInCylinder(loc, stormRadius + 15, false, 60, warlordsNPC.getGame());
                        shockwave(loc, stormRadius + 15, 60, playerCount);
                    }
                }
            }.runTaskLater(40);
        }

        if (ticksElapsed % 80 == 0) {
            EffectUtils.playSphereAnimation(loc, 4, ParticleEffect.SPELL_WITCH, 2);
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(loc, 4, 4, 4)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                we.addDamageInstance(warlordsNPC, "Cleanse", 300 * playerCount, 400 * playerCount, 0, 100, false);
                EffectUtils.strikeLightning(we.getLocation(), false);
            }
        }

        if (ticksElapsed % 600 == 0) {
            for (int i = 0; i < option.getGame().warlordsPlayers().count(); i++) {
                option.spawnNewMob(new EnvoyLegionnaire(loc));
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), true);
        Utils.addKnockback(attacker.getLocation(), receiver, -2, 0.3);

        if (!(event.getAbility().equals("Uppercut") || event.getAbility().equals("Armageddon") || event.getAbility().equals("Intervene"))) {
            new GameRunnable(attacker.getGame()) {
                int counter = 0;

                @Override
                public void run() {
                    counter++;
                    FireWorkEffectPlayer.playFirework(receiver.getLocation(), FireworkEffect.builder()
                            .withColor(Color.WHITE)
                            .with(FireworkEffect.Type.BURST)
                            .build());
                    receiver.addDamageInstance(attacker, "Uppercut", 250, 350, 0, 100, false);

                    if (counter == 3 || receiver.isDead()) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(8, 2);
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(self.getLocation(), Sound.BLAZE_HIT, 2, 0.2f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        for (int i = 0; i < 3; i++) {
            FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                    .withColor(Color.WHITE)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .build());
        }

        EffectUtils.strikeLightning(deathLocation, false, 5);
    }

    private void shockwave(Location loc, double radius, int tickDelay, long playerCount) {
        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                Utils.playGlobalSound(loc, Sound.ENDERDRAGON_GROWL, 10, 0.4f);
                Utils.playGlobalSound(loc, "warrior.laststand.activation", 10, 0.4f);
                for (WarlordsEntity we : PlayerFilter
                        .entitiesAround(loc, radius, radius, radius)
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    if (!we.getCooldownManager().hasCooldownFromName("Cloaked")) {
                        we.addDamageInstance(warlordsNPC, "Armageddon", 550 * playerCount, 700 * playerCount, 0, 100, false);
                        Utils.addKnockback(warlordsNPC.getLocation(), we, -2, 0.2);
                    }
                }
            }
        }.runTaskLater(tickDelay);
    }
}
