package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.EffectUtils;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class SoulShackle extends AbstractAbility {

    private final int shackleRange = 12;
    private float absorbPool = 0;

    public SoulShackle() {
        super("Soul Shackle", 327, 443, 8, 40, 20, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Shackle up to §e1 §7enemy and deal §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage.\n" +
                "§7Shackled enemies are silenced for §62 §7seconds,\n" +
                "§7making them unable to use their main attack for\n" +
                "§7the duration.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        SoulShackle tempSoulShackle = new SoulShackle();

        for (WarlordsPlayer shackleTarget : PlayerFilter
                .entitiesAround(player, shackleRange, shackleRange, shackleRange)
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

            shackleTarget.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
            shackleTarget.getCooldownManager().addRegularCooldown(
                    "Shackle Silence",
                    "SILENCE",
                    SoulShackle.class,
                    tempSoulShackle,
                    wp,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {},
                    2 * 20
            );

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
                        for (Player player1 : player.getWorld().getPlayers()) {
                            player1.playSound(playerLoc, Sound.DIG_SAND, 2, 2);
                        }
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 10);

            return true;
        }

        return false;
    }

    public float getAbsorbPool() {
        return absorbPool;
    }

    public void addToAbsorbPool(float amount) {
        this.absorbPool += amount;
    }

    public void setAbsorbPool(float pool) {
        this.absorbPool = pool;
    }

}
