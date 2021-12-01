package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Consecrate extends AbstractAbility {

    protected int strikeDamageBoost;
    protected float radius;

    public Consecrate(float minDamageHeal, float maxDamageHeal, int energyCost, int critChance, int critMultiplier, int strikeDamageBoost, float radius) {
        super("Consecrate", minDamageHeal, maxDamageHeal, 7.83f, energyCost, critChance, critMultiplier);
        this.strikeDamageBoost = strikeDamageBoost;
        this.radius = radius;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Consecrate the ground below your\n" +
                "§7feet, declaring it sacred. Enemies\n" +
                "§7standing on it will take §c" + format(minDamageHeal) + " §7-\n" +
                "§c" + format(maxDamageHeal) + " §7damage per second and\n" +
                "§7take §c" + strikeDamageBoost + "% §7increased damage from\n" +
                "§7your paladin strikes. Lasts §65\n" +
                "§7seconds.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player p) {
        DamageHealCircle c = new DamageHealCircle(wp, p.getLocation(), radius, 5, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        wp.subtractEnergy(energyCost);

        for (Player player1 : p.getWorld().getPlayers()) {
            player1.playSound(p.getLocation(), "paladin.consecrate.activation", 2, 1);
        }

        ArmorStand cs = p.getLocation().getWorld().spawn(p.getLocation().clone().add(0, -2, 0), ArmorStand.class);
        cs.setMetadata("Consecrate - " + p.getName(), new FixedMetadataValue(Warlords.getInstance(), true));
        cs.setGravity(false);
        cs.setVisible(false);
        cs.setMarker(true);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), c::spawn, 0, 1);
        wp.getGame().getGameTasks().put(
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        c.setDuration(c.getDuration() - 1);
                        PlayerFilter.entitiesAround(c.getLocation(), radius, 6, radius)
                                .aliveEnemiesOf(wp)
                                .forEach(warlordsPlayer -> {
                                    warlordsPlayer.damageHealth(
                                            c.getWarlordsPlayer(),
                                            c.getName(),
                                            c.getMinDamage(),
                                            c.getMaxDamage(),
                                            c.getCritChance(),
                                            c.getCritMultiplier(),
                                            false);
                                });
                        if (c.getDuration() == 0) {
                            cs.remove();
                            this.cancel();
                            task.cancel();
                        }
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 20),
                System.currentTimeMillis()
        );
    }
}
