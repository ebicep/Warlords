package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Overheal;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class WaterBreath extends AbstractAbility {
    protected int playersHealed = 0;
    protected int debuffsRemoved = 0;

    public WaterBreath() {
        super("Water Breath", 528, 723, 6.3f, 60, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Breathe water in a cone in front of you,\n" +
                "§7knocking back enemies, cleansing all §ede-buffs\n" +
                "§7and restoring §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7health to\n" +
                "§7yourself and all allies hit." +
                "\n\n" +
                "§7Water Breath can overheal allies for up to\n" +
                "§a10% §7of their max health as bonus health\n" +
                "§7for §6" + Overheal.OVERHEAL_DURATION + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHealed));
        info.add(new Pair<>("Debuffs Removed", "" + debuffsRemoved));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost);
        Utils.playGlobalSound(player.getLocation(), "mage.waterbreath.activation", 2, 1);
        ParticleEffect.HEART.display(0.6f, 0.6f, 0.6f, 1, 2, player.getLocation().add(0, 0.7, 0), 500);

        Location playerLoc = new LocationBuilder(player.getLocation())
                .pitch(0)
                .add(0, 1.7, 0);
        new GameRunnable(wp.getGame()) {

            @Override
            public void run() {
                this.playEffect();
                this.playEffect();
            }

            int animationTimer = 0;
            final Matrix4d center = new Matrix4d(playerLoc);

            public void playEffect() {

                if (animationTimer > 12) {
                    this.cancel();
                }

                for (int i = 0; i < 4; i++) {
                    double angle = Math.toRadians(i * 90) + animationTimer * 0.15;
                    double width = animationTimer * 0.3;
                    ParticleEffect.DRIP_WATER.display(0, 0, 0, 0, 1,
                            center.translateVector(wp.getWorld(), animationTimer / 2D, Math.sin(angle) * width, Math.cos(angle) * width), 500);
                    ParticleEffect.ENCHANTMENT_TABLE.display(0, 0, 0, 0, 1,
                            center.translateVector(wp.getWorld(), animationTimer / 2D, Math.sin(angle) * width, Math.cos(angle) * width), 500);
                    ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0, 1,
                            center.translateVector(wp.getWorld(), animationTimer / 2D, Math.sin(angle) * width, Math.cos(angle) * width), 500);
                }

                animationTimer++;
            }
        }.runTaskTimer(0, 1);

        debuffsRemoved += wp.getCooldownManager().removeDebuffCooldowns();
        wp.getSpeed().removeSlownessModifiers();
        wp.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);

        Location hitbox = new LocationBuilder(player.getLocation())
                .pitch(0)
                .backward(1);
        Vector viewDirection = playerLoc.getDirection();
        for (WarlordsPlayer breathTarget : PlayerFilter
                .entitiesAroundRectangle(playerLoc, 7.5, 10, 7.5)
                .excluding(wp)
        ) {
            Vector direction = breathTarget.getLocation().subtract(hitbox).toVector().normalize();
            if (viewDirection.dot(direction) > .68) {
                if (wp.isTeammateAlive(breathTarget)) {
                    playersHealed++;
                    debuffsRemoved += breathTarget.getCooldownManager().removeDebuffCooldowns();
                    breathTarget.getSpeed().removeSlownessModifiers();
                    breathTarget.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);
                    breathTarget.getCooldownManager().removeCooldownByObject(Overheal.OVERHEAL_MARKER);
                    breathTarget.getCooldownManager().addRegularCooldown(
                            "Overheal",
                            "OVERHEAL",
                            Overheal.class,
                            Overheal.OVERHEAL_MARKER,
                            wp,
                            CooldownTypes.BUFF,
                            cooldownManager -> {
                            },
                            Overheal.OVERHEAL_DURATION * 20
                    );
                } else {
                    final Location loc = breathTarget.getLocation();
                    final Vector v = player.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-1.1).setY(0.2);
                    breathTarget.setVelocity(v, false);
                }
            }
        }

        return true;
    }
}
