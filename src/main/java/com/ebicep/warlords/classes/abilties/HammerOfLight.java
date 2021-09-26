package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HammerOfLight extends AbstractAbility {

    private final static int radius = 6;
    private final int duration = 8;

    public HammerOfLight() {
        super("Hammer of Light", 178, 244, 62.64f, 30, 25, 175
        );
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Throw down a Hammer of Light on\n" +
                "§7the ground, dealing §c" + format(minDamageHeal) + " §7-\n" +
                "§c" + format(maxDamageHeal) + " §7damage every second to\n" +
                "§7nearby enemies and healing nearby\n" +
                "§7allies for §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7every\n" +
                "§7second in a §e" + radius + " §7block radius. Your Protector\n" +
                "§7Strike pierces shields and defenses of enemies\n" +
                "§7standing on top of the Hammer of Light.\n" +
                "§7Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7Each enemy standing in your hammer increases\n" +
                "§7your overall healing and damage by §f3%\n" +
                "§7(max 12%)";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {

        if (player.getTargetBlock((HashSet<Byte>) null, 25).getType() == Material.AIR) return;
        DamageHealCircle damageHealCircle = new DamageHealCircle(wp, player.getTargetBlock((HashSet<Byte>) null, 25).getLocation().add(1, 0, 1), radius, duration, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.spawnHammer();
        damageHealCircle.getLocation().add(0, 1, 0);
        wp.subtractEnergy(energyCost);
        wp.getSpec().getOrange().setCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));
        wp.getCooldownManager().addCooldown(name, this.getClass(), new HammerOfLight(), "HAMMER", duration, wp, CooldownTypes.ABILITY);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.hammeroflight.impact", 2, 0.85f);
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), damageHealCircle::spawn, 0, 1);

        new BukkitRunnable() {

            @Override
            public void run() {
                damageHealCircle.setDuration(damageHealCircle.getDuration() - 1);
                for (WarlordsPlayer warlordsPlayer : PlayerFilter
                    .entitiesAround(damageHealCircle.getLocation(), radius, radius, radius)
                    .isAlive()
                ) {
                    if (damageHealCircle.getWarlordsPlayer().isTeammateAlive(warlordsPlayer)) {
                        warlordsPlayer.addHealth(
                                damageHealCircle.getWarlordsPlayer(),
                                damageHealCircle.getName(),
                                damageHealCircle.getMinDamage(),
                                damageHealCircle.getMaxDamage(),
                                damageHealCircle.getCritChance(),
                                damageHealCircle.getCritMultiplier(),
                                false);
                    } else {
                        warlordsPlayer.addHealth(
                                damageHealCircle.getWarlordsPlayer(),
                                damageHealCircle.getName(),
                                -damageHealCircle.getMinDamage(),
                                -damageHealCircle.getMaxDamage(),
                                damageHealCircle.getCritChance(),
                                damageHealCircle.getCritMultiplier(),
                                false);
                    }
                }

                if (damageHealCircle.getDuration() <= 0) {
                    damageHealCircle.removeHammer();
                    this.cancel();
                    task.cancel();
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }

    public static boolean standingInHammer(WarlordsPlayer owner, Entity standing) {
        if (!(owner.getSpec() instanceof Protector)) return false;
        for (Entity entity : owner.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Hammer of Light - " + owner.getName())) {
                if (entity.getLocation().clone().add(0, 2, 0).distanceSquared(standing.getLocation()) < 5 * 5.25) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public static List<WarlordsPlayer> getStandingInHammer(WarlordsPlayer owner) {
        List<WarlordsPlayer> playersInHammer = new ArrayList<>();
        for (Entity entity : owner.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Hammer of Light - " + owner.getName())) {
                for (WarlordsPlayer enemy : PlayerFilter
                        .entitiesAround(entity, radius, 4, radius)
                        .enemiesOf(owner)
                        .isAlive()) {
                    playersInHammer.add(enemy);
                }
                break;
            }
        }
        return playersInHammer;
    }
}