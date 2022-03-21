package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHolyRadianceBase extends AbstractAbility {

    private final int radius;

    public AbstractHolyRadianceBase(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier, int radius) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
        this.radius = radius;
    }

    public abstract void chain(WarlordsPlayer wp, Player player);

    List<WarlordsPlayer> playersHit = new ArrayList<>();

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        wp.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);
        wp.subtractEnergy(energyCost);

        chain(wp, player);

        for (WarlordsPlayer radianceTarget : PlayerFilter
                .entitiesAround(player, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            wp.getGame().registerGameTask(
                    new FlyingArmorStand(
                            wp.getLocation(),
                            radianceTarget,
                            wp,
                            1.1,
                            minDamageHeal,
                            maxDamageHeal
                    ).runTaskTimer(Warlords.getInstance(), 1, 1)
            );
            playersHit.add(radianceTarget);
        }

        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
        Utils.playGlobalSound(player.getLocation(), "paladin.holyradiance.activation", 2, 1);

        Location particleLoc = player.getLocation().add(0, 1.2, 0);
        ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0.1F, 2, particleLoc, 500);
        ParticleEffect.SPELL.display(1, 1, 1, 0.06F, 12, particleLoc, 500);

        return true;
    }

    public class FlyingArmorStand extends BukkitRunnable {

        private final WarlordsPlayer target;
        private final WarlordsPlayer owner;
        private final double speed;
        private final ArmorStand armorStand;
        private final float minHeal;
        private final float maxHeal;

        public FlyingArmorStand(Location location, WarlordsPlayer target, WarlordsPlayer owner, double speed, float minHeal, float maxHeal) {
            this.armorStand = location.getWorld().spawn(location, ArmorStand.class);
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            this.target = target;
            this.speed = speed;
            this.owner = owner;
            this.minHeal = minHeal;
            this.maxHeal = maxHeal;
        }

        @Override
        public void cancel() {
            super.cancel();
            armorStand.remove();
        }

        @Override
        public void run() {
            if (!owner.getGame().isFrozen()) {

                if (this.target.isDead()) {
                    this.cancel();
                    return;
                }

                if (target.getWorld() != armorStand.getWorld()) {
                    this.cancel();
                    return;
                }

                Location targetLocation = target.getLocation();
                Location armorStandLocation = armorStand.getLocation();
                double distance = targetLocation.distanceSquared(armorStandLocation);

                if (distance < speed * speed) {
                    target.addHealingInstance(owner, name, minHeal, maxHeal, critChance, critMultiplier, false, false);
                    playersHit.add(target);
                    this.cancel();
                    return;
                }

                targetLocation.subtract(armorStandLocation);
                //System.out.println(Math.max(speed * 3.25 / targetLocation.lengthSquared() / 2, speed / 10));
                targetLocation.multiply(Math.max(speed * 3.25 / targetLocation.lengthSquared() / 2, speed / 10));

                armorStandLocation.add(targetLocation);
                this.armorStand.teleport(armorStandLocation);

                ParticleEffect.SPELL.display(0.01f, 0, 0.01f, 0.1f, 2, armorStandLocation.add(0, 1.75, 0), 500);
            }
        }
    }

    public List<WarlordsPlayer> getPlayersHit() {
        return playersHit;
    }
}
