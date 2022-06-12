package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.WarlordsEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractChainBase extends AbstractAbility {

    protected int playersHit = 0;

    public AbstractChainBase(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    protected abstract int getHitCounterAndActivate(WarlordsEntity warlordsPlayer, Player player);

    protected abstract void onHit(WarlordsEntity warlordsPlayer, Player player, int hitCounter);

    protected abstract ItemStack getChainItem();

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity warlordsPlayer, @Nonnull Player player) {
        int hitCounter = getHitCounterAndActivate(warlordsPlayer, player);
        if (hitCounter != 0) {
            playersHit += hitCounter;

            AbstractPlayerClass.sendRightClickPacket(player);
            warlordsPlayer.subtractEnergy(energyCost);

            onHit(warlordsPlayer, player, hitCounter);

            return true;
        }

        return false;
    }

    protected void chain(Location from, Location to) {
        Location location = from.subtract(0, .5, 0);
        location.setDirection(location.toVector().subtract(to.subtract(0, .5, 0).toVector()).multiply(-1));
        spawnChain(to, location);
    }

    protected void spawnChain(Location to, Location from) {

        List<ArmorStand> chains = new ArrayList<>();
        int maxDistance = (int) Math.round(to.distance(from));
        for (int i = 0; i < maxDistance; i++) {
            ArmorStand chain = from.getWorld().spawn(from, ArmorStand.class);
            chain.setHeadPose(new EulerAngle(from.getDirection().getY() * -1, 0, 0));
            chain.setGravity(false);
            chain.setVisible(false);
            chain.setBasePlate(false);
            chain.setMarker(true);
            chain.setHelmet(getChainItem());
            from.add(from.getDirection().multiply(1.1));
            chains.add(chain);
            if(to.distanceSquared(from) < .4) {
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

}
