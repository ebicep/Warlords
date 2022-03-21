package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
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
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        wp.subtractEnergy(energyCost);

        Utils.playGlobalSound(player.getLocation(), "paladin.consecrate.activation", 2, 1);

        Location location = player.getLocation().clone();
        ArmorStand consecrate = player.getLocation().getWorld().spawn(player.getLocation().clone().add(0, -2, 0), ArmorStand.class);
        consecrate.setMetadata("Consecrate - " + player.getName(), new FixedMetadataValue(Warlords.getInstance(), true));
        consecrate.setGravity(false);
        consecrate.setVisible(false);
        consecrate.setMarker(true);
        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location,
                radius,
                new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE),
                new DoubleLineEffect(ParticleEffect.SPELL)
        );

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), circleEffect::playEffects, 0, 1);
        new GameRunnable(wp.getGame()) {

            int timeLeft = 5;

            @Override
            public void run() {
                timeLeft--;
                PlayerFilter.entitiesAround(location, radius, 6, radius)
                        .aliveEnemiesOf(wp)
                        .forEach(warlordsPlayer -> {
                            warlordsPlayer.addDamageInstance(
                                    wp,
                                    name,
                                    minDamageHeal,
                                    maxDamageHeal,
                                    critChance,
                                    critMultiplier,
                                    false);
                        });
                if (timeLeft == 0) {
                    consecrate.remove();
                    this.cancel();
                    task.cancel();
                }
            }

        }.runTaskTimer(0, 20);

        return true;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
