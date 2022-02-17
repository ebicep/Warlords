package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class SoulShackle extends AbstractAbility {

    private final int shackleRange = 12;
    private float shacklePool = 0;

    public SoulShackle() {
        super("Soul Shackle", 344, 468, 10, 40, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Shackle up to §e1 §7enemy and deal §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage.\n" +
                "§7Shackled enemies are silenced for §62§7-§64 §7seconds,\n" +
                "§7making them unable to use their main attack for\n" +
                "§7the duration. The silence duration increases by §61\n" +
                "§7second for every §c1000 §7damage you took in the last\n" +
                "§610 §7seconds." +
                "\n\n" +
                "§7Has an optimal range of §e" + shackleRange + " §7blocks.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        SoulShackle tempSoulShackle = new SoulShackle();

        for (WarlordsPlayer shackleTarget : PlayerFilter
                .entitiesAround(wp, shackleRange, shackleRange, shackleRange)
                .aliveEnemiesOf(wp)
                .lookingAtFirst(wp)
                .requireLineOfSight(wp)
                .limit(1)
        ) {
            wp.subtractEnergy(energyCost);
            wp.sendMessage(
                WarlordsPlayer.RECEIVE_ARROW +
                ChatColor.GRAY + " You shackled " +
                ChatColor.YELLOW + shackleTarget.getName() +
                ChatColor.GRAY + "!"
            );

            int silenceDuration = 2 + (int) (shacklePool / 1000);
            if (silenceDuration > 4) {
                silenceDuration = 4;
            }

            shackleTarget.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
            shackleTarget.getCooldownManager().addRegularCooldown(
                    "Shackle Silence",
                    "SILENCE",
                    SoulShackle.class,
                    tempSoulShackle,
                    wp,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {},
                    silenceDuration * 20
            );

            shacklePool = 0;

            for (Player player1 : player.getWorld().getPlayers()) {
                player1.playSound(player.getLocation(), "warrior.intervene.impact", 1.5f, 0.45f);
                player1.playSound(player.getLocation(), "mage.fireball.activation", 1.5f, 0.3f);
            }

            EffectUtils.playChainAnimation(wp, shackleTarget, Material.PUMPKIN, 20);

            new GameRunnable(wp.getGame()) {
                @Override
                public void run() {
                    if (shackleTarget.getCooldownManager().hasCooldown(tempSoulShackle)) {
                        Location playerLoc = shackleTarget.getLocation();
                        Location particleLoc = playerLoc.clone();
                        for (int i = 0; i < 10; i++) {
                            for (int j = 0; j < 10; j++) {
                                double angle = j / 10D * Math.PI * 2;
                                double width = 1.075;
                                particleLoc.setX(playerLoc.getX() + Math.sin(angle) * width);
                                particleLoc.setY(playerLoc.getY() + i / 5D);
                                particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * width);

                                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(25, 25, 25), particleLoc, 500);
                            }
                        }
                        Utils.playGlobalSound(wp.getLocation(), Sound.DIG_SAND, 2, 2);
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 10);

            return true;
        }

        return false;
    }

    public float getShacklePool() {
        return shacklePool;
    }

    public void addToShacklePool(float amount) {
        this.shacklePool += amount;
    }

    @Override
    public void runEverySecond() {
        if (shacklePool > 0) {
            float newPool = shacklePool - 100;
            shacklePool = Math.max(newPool, 0);
        }
    }
}
