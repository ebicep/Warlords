package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.internal.AbstractProjectileBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;

public class HealingRemedy extends AbstractProjectileBase {

    public HealingRemedy() {
        super("Healing Remedy", 536, 644, 12, 80, 25, 175, 2, 20, true);
    }

    @Override
    public void updateDescription(Player player) {

    }

    @Override
    protected String getActivationSound() {
        return "mage.waterbolt.activation";
    }

    @Override
    protected void playEffect(Location currentLocation, int animationTimer) {
        ParticleEffect.ENCHANTMENT_TABLE.display(0, 0, 0, 0.1F, 5, currentLocation, 500);
        ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0.1F, 5, currentLocation, 500);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        this.playEffect(projectile);
    }

    @Override
    protected void onHit(WarlordsPlayer shooter, Location currentLocation, Location startingLocation, WarlordsPlayer victim) {
        DamageHealCircle med = new DamageHealCircle(shooter, currentLocation, 3, 3, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        med.getLocation().add(0, 1, 0);
        med.spawn();

        for (Player player1 : shooter.getWorld().getPlayers()) {
            player1.playSound(currentLocation, "mage.waterbolt.impact", 2, 0.4f);
        }

        for (WarlordsPlayer nearEntity : PlayerFilter
                .entitiesAround(currentLocation, 3, 3, 3)
                .excluding(shooter)
                .aliveTeammatesOf(shooter)
        ) {
            nearEntity.addHealingInstance(
                    shooter,
                    name,
                    minDamageHeal,
                    maxDamageHeal,
                    critChance,
                    critMultiplier,
                    false,
                    false);
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), med::spawn, 0, 1);
        shooter.getGame().getGameTasks().put(task, System.currentTimeMillis());

        shooter.getGame().getGameTasks().put(

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (!shooter.getGame().isGameFreeze()) {
                            PlayerFilter.entitiesAround(med.getLocation(), med.getRadius(), med.getRadius(), med.getRadius())
                                    .aliveTeammatesOf(shooter)
                                    .forEach((ally) -> ally.addHealingInstance(
                                            med.getWarlordsPlayer(),
                                            med.getName(),
                                            med.getMinDamage(),
                                            med.getMaxDamage(),
                                            med.getCritChance(),
                                            med.getCritMultiplier(),
                                            false,
                                            false));

                            if (med.getDuration() < 0) {
                                this.cancel();
                                task.cancel();
                            }
                        }
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 20),
                System.currentTimeMillis()
        );

        shooter.getGame().getGameTasks().put(

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (!shooter.getGame().isGameFreeze()) {
                            med.setDuration(med.getDuration() - 1);
                        }
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 20),
                System.currentTimeMillis()
        );
    }
}
