package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.EnvoyLegionnaire;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;

import java.util.HashMap;

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
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 6);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        long playerCount = option.getGame().warlordsPlayers().count();
        Location loc = warlordsNPC.getLocation();
        float multiplier = switch (option.getDifficulty()) {
            case EASY -> 0.5f;
            case HARD -> 1;
            case EXTREME -> 1.25f;
            default -> 0.75f;
        };
        if (ticksElapsed % 240 == 0) {
            Utils.playGlobalSound(loc, "rogue.healingremedy.impact", 500, 0.85f);
            Utils.playGlobalSound(loc, "rogue.healingremedy.impact", 500, 0.85f);
            warlordsNPC.addSpeedModifier(warlordsNPC, "Armageddon Slowness", -99, 90);
            new GameRunnable(warlordsNPC.getGame()) {
                @Override
                public void run() {
                    if (warlordsNPC.isDead()) {
                        this.cancel();
                        return;
                    }

                    EffectUtils.strikeLightningInCylinder(loc, stormRadius, false, 12, warlordsNPC.getGame());
                    shockwave(loc, stormRadius, 12, playerCount, multiplier);
                    EffectUtils.strikeLightningInCylinder(loc, stormRadius + 5, false, 24, warlordsNPC.getGame());
                    shockwave(loc, stormRadius + 5, 24, playerCount, multiplier);
                    EffectUtils.strikeLightningInCylinder(loc, stormRadius + 10, false, 36, warlordsNPC.getGame());
                    shockwave(loc, stormRadius + 10, 36, playerCount, multiplier);
                    if (difficulty == DifficultyIndex.HARD || difficulty == DifficultyIndex.EXTREME || difficulty == DifficultyIndex.ENDLESS) {
                        EffectUtils.strikeLightningInCylinder(loc, stormRadius + 15, false, 48, warlordsNPC.getGame());
                        shockwave(loc, stormRadius + 15, 48, playerCount, multiplier);
                        EffectUtils.strikeLightningInCylinder(loc, stormRadius + 15, false, 60, warlordsNPC.getGame());
                        shockwave(loc, stormRadius + 15, 60, playerCount, multiplier);
                    }
                }
            }.runTaskLater(40);
        }

        if (ticksElapsed % 80 == 0) {
            EffectUtils.playSphereAnimation(loc, 4, Particle.SPELL_WITCH, 2);
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(loc, 4, 4, 4)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                Utils.addKnockback(name, warlordsNPC.getLocation(), we, -1.5, 0.3);
                we.addDamageInstance(warlordsNPC, "Cleanse", (300 * playerCount) * multiplier, (400 * playerCount) * multiplier, 0, 100, false);
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

        if (!(event.getAbility().equals("Uppercut") || event.getAbility().equals("Armageddon") || event.getAbility().equals("Intervene"))) {
            new GameRunnable(attacker.getGame()) {
                int counter = 0;

                @Override
                public void run() {
                    if (warlordsNPC.isDead()) {
                        this.cancel();
                    }

                    counter++;
                    FireWorkEffectPlayer.playFirework(
                            receiver.getLocation(),
                            FireworkEffect.builder()
                                          .withColor(Color.WHITE)
                                          .with(FireworkEffect.Type.BURST)
                                          .build()
                    );
                    Utils.addKnockback(name, attacker.getLocation(), receiver, -1, 0.3);
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
        Utils.playGlobalSound(self.getLocation(), Sound.ENTITY_BLAZE_HURT, 2, 0.2f);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        for (int i = 0; i < 3; i++) {
            FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                           .withColor(Color.WHITE)
                                                                           .with(FireworkEffect.Type.BALL_LARGE)
                                                                           .build());
        }

        EffectUtils.strikeLightning(deathLocation, false, 5);
    }

    private void shockwave(Location loc, double radius, int tickDelay, long playerCount, float damageMultiplier) {
        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                if (warlordsNPC.isDead()) {
                    this.cancel();
                }

                Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 10, 0.4f);
                Utils.playGlobalSound(loc, "warrior.laststand.activation", 10, 0.4f);
                for (WarlordsEntity we : PlayerFilter
                        .entitiesAround(loc, radius, radius, radius)
                        .aliveEnemiesOf(warlordsNPC)
                ) {
                    if (!we.getCooldownManager().hasCooldownFromName("Cloaked")) {
                        we.addDamageInstance(warlordsNPC,
                                "Armageddon",
                                (550 * playerCount) * damageMultiplier,
                                (700 * playerCount) * damageMultiplier,
                                0,
                                100,
                                false
                        );
                        Utils.addKnockback(name, warlordsNPC.getLocation(), we, -2, 0.2);
                    }
                }
            }
        }.runTaskLater(tickDelay);
    }

    @Override
    public HashMap<MobDrops, HashMap<DifficultyIndex, Double>> mobDrops() {
        return new HashMap<>() {{
            put(MobDrops.ZENITH_STAR, new HashMap<>() {{
                put(DifficultyIndex.EASY, .015);
                put(DifficultyIndex.NORMAL, .025);
                put(DifficultyIndex.HARD, .05);
                put(DifficultyIndex.EXTREME, .10);
                put(DifficultyIndex.ENDLESS, .05);
            }});
        }};
    }

    @Override
    public NamedTextColor getColor() {
        return NamedTextColor.DARK_PURPLE;
    }

    @Override
    public Component getDescription() {
        return Component.text("Leader of the Illusion Vanguard", NamedTextColor.LIGHT_PURPLE);
    }
}
