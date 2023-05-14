package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractChainBase extends AbstractAbility {

    public int playersHit = 0;
    protected int radius;
    protected int bounceRange;
    protected int additionalBounces;


    public AbstractChainBase(
            String name,
            float minDamageHeal,
            float maxDamageHeal,
            float cooldown,
            float energyCost,
            float critChance,
            float critMultiplier,
            int radius,
            int bounceRange,
            int additionalBounces
    ) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
        this.radius = radius;
        this.bounceRange = bounceRange;
        this.additionalBounces = additionalBounces;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity warlordsPlayer, @Nonnull Player player) {
        Set<WarlordsEntity> entitiesHit = getEntitiesHitAndActivate(warlordsPlayer, player);
        int hitCounter = entitiesHit.size();
        if (hitCounter != 0) {
            playersHit += hitCounter;

            AbstractPlayerClass.sendRightClickPacket(player);
            warlordsPlayer.subtractEnergy(energyCost, false);

            onHit(warlordsPlayer, player, hitCounter);

            entitiesHit.remove(null);

            Bukkit.getPluginManager().callEvent(new WarlordsAbilityTargetEvent(warlordsPlayer, name, entitiesHit));

            return true;
        }

        return false;
    }

    protected abstract Set<WarlordsEntity> getEntitiesHitAndActivate(WarlordsEntity warlordsPlayer, Player player);

    protected abstract void onHit(WarlordsEntity warlordsPlayer, Player player, int hitCounter);

    protected void chain(Location from, Location to) {
        Location location = from.subtract(0, .5, 0);
        location.setDirection(location.toVector().subtract(to.subtract(0, .5, 0).toVector()).multiply(-1));
        spawnChain(to, location);
    }

    protected void spawnChain(Location to, Location from) {

        List<ArmorStand> chains = new ArrayList<>();
        int maxDistance = (int) Math.round(to.distance(from));
        for (int i = 0; i < maxDistance; i++) {
            ArmorStand chain = Utils.spawnArmorStand(from, armorStand -> {
                armorStand.setHeadPose(new EulerAngle(from.getDirection().getY() * -1, 0, 0));
                armorStand.setMarker(true);
                armorStand.getEquipment().setHelmet(getChainItem());
            });
            from.add(from.getDirection().multiply(1.1));
            chains.add(chain);
            if (to.distanceSquared(from) < .4) {
                break;
            }
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                if (chains.isEmpty()) {
                    this.cancel();
                }

                for (int i = 0; i < chains.size(); i++) {
                    ArmorStand armorStand = chains.get(i);
                    if (armorStand.getTicksLived() > 9) {
                        armorStand.remove();
                        chains.remove(i);
                        i--;
                    }
                }

            }

        }.runTaskTimer(Warlords.getInstance(), 0, 0);
    }

    protected abstract ItemStack getChainItem();

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getBounceRange() {
        return bounceRange;
    }

    public void setBounceRange(int bounceRange) {
        this.bounceRange = bounceRange;
    }


    public int getAdditionalBounces() {
        return additionalBounces;
    }

    public void setAdditionalBounces(int additionalBounces) {
        this.additionalBounces = additionalBounces;
    }


}
