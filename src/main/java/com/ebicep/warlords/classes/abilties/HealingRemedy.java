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

    private static final float HITBOX = 3;

    public HealingRemedy() {
        super("Healing Remedy", 536, 644, 12, 80, 25, 175, 2.5, 25, true);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Throw a short range projectile, healing\n" +
                "§7healing allies for §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7upon impact.\n" +
                "§7The projectile will form a small puddle that\n" +
                "§7heals allies for §a189 §7- §a244 §7health per second.\n" +
                "§7Lasts §64 §7seconds." +
                "\n\n" +
                "§7Has an optimal range of §e25 §7blocks.";
    }

    @Override
    protected String getActivationSound() {
        return "mage.waterbolt.activation";
    }

    @Override
    protected float getSoundPitch() {
        return 0.4f;
    }

    @Override
    protected float getSoundVolume() {
        return 2;
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
        DamageHealCircle med = new DamageHealCircle(shooter, currentLocation, 3, 2, 189, 244, critChance, critMultiplier, name);
        med.getLocation().add(0, 1, 0);

        for (Player player1 : shooter.getWorld().getPlayers()) {
            player1.playSound(currentLocation, "mage.waterbolt.impact", 2, 0.4f);
        }

        for (WarlordsPlayer nearEntity : PlayerFilter
                .entitiesAround(currentLocation, HITBOX, HITBOX, HITBOX)
                .aliveTeammatesOfExcludingSelf(shooter)
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

                            med.setDuration(med.getDuration() - 1);
                        }
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 20),
                System.currentTimeMillis()
        );
    }
}
