package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Consecrate extends AbstractAbility {
    public Consecrate(int minDamageHeal, int maxDamageHeal, int energyCost, int critChance, int critMultiplier) {
        super("Consecrate", minDamageHeal, maxDamageHeal, 8, energyCost, critChance, critMultiplier,
                "§7Consecrate the ground below your\n" +
                "§7feet, declaring it sacred. Enemies\n" +
                "§7standing on it will take §c%dynamic.value% §7-\n" +
                "§c%dynamic.value% §7damage per second and\n" +
                "§7take §c%dynamic.value% §7increased damage from\n" +
                "§7your paladin strikes. Lasts §65\n" +
                "§7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        DamageHealCircle damageHealCircle = new DamageHealCircle(player, player.getLocation(), 5, 5, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.spawn();
        Warlords.getPlayer(player).subtractEnergy(energyCost);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.consecrate.activation", 1, 1);
        }

        ArmorStand consecrate = player.getLocation().getWorld().spawn(player.getLocation().clone().add(0, -2, 0), ArmorStand.class);
        consecrate.setMetadata("Consecrate - " + player.getName(), new FixedMetadataValue(Warlords.getInstance(), true));

        new BukkitRunnable() {

            @Override
            public void run() {
                damageHealCircle.setDuration(damageHealCircle.getDuration() - 1);
                List<Entity> near = (List<Entity>) damageHealCircle.getLocation().getWorld().getNearbyEntities(damageHealCircle.getLocation(), 3, 3, 3);
                near = Utils.filterOutTeammates(near, player);
                for (Entity entity : near) {
                    if (entity instanceof Player) {
                        Player player = (Player) entity;
                        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
                        double distance = damageHealCircle.getLocation().distanceSquared(player.getLocation());
                        if (distance < damageHealCircle.getRadius() * damageHealCircle.getRadius()) {
                            warlordsPlayer.addHealth(Warlords.getPlayer(damageHealCircle.getPlayer()), damageHealCircle.getName(), damageHealCircle.getMinDamage(), damageHealCircle.getMaxDamage(), damageHealCircle.getCritChance(), damageHealCircle.getCritMultiplier());
                        }

                    }
                }
                if (damageHealCircle.getDuration() == 0) {
                    this.cancel();
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }
}
