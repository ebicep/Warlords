package com.ebicep.warlords.classes.internal;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
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

    public AbstractChainBase(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    protected abstract int getHitCounterAndActivate(WarlordsPlayer warlordsPlayer, Player player);

    protected abstract void onHit(WarlordsPlayer warlordsPlayer, Player player, int hitCounter);

    protected abstract ItemStack getChainItem();

    @Override
    public void onActivate(@Nonnull WarlordsPlayer warlordsPlayer, @Nonnull Player player) {
        int hitCounter = getHitCounterAndActivate(warlordsPlayer, player);
        if (hitCounter != 0) {
            PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);
            warlordsPlayer.subtractEnergy(energyCost);

            onHit(warlordsPlayer, player, hitCounter);
        }
    }

    protected void chain(Location from, Location to) {
        Location location = from.subtract(0, .5, 0);
        location.setDirection(location.toVector().subtract(to.subtract(0, .5, 0).toVector()).multiply(-1));
        spawnChain((int) Math.round(from.distance(to)), location);
    }

    protected void spawnChain(int distance, Location location) {

        List<ArmorStand> chains = new ArrayList<>();

        for (int i = 0; i < distance; i++) {
            ArmorStand chain = location.getWorld().spawn(location, ArmorStand.class);
            chain.setHeadPose(new EulerAngle(location.getDirection().getY() * -1, 0, 0));
            chain.setGravity(false);
            chain.setVisible(false);
            chain.setBasePlate(false);
            chain.setMarker(true);
            chain.setHelmet(getChainItem());
            location.add(location.getDirection().multiply(1.2));
            chains.add(chain);
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                if (chains.size() == 0) {
                    this.cancel();
                }

                for (int i = 0; i < chains.size(); i++) {
                    ArmorStand armorStand = chains.get(i);
                    if (armorStand.getTicksLived() > 12) {
                        armorStand.remove();
                        chains.remove(i);
                        i--;
                    }
                }

            }

        }.runTaskTimer(Warlords.getInstance(), 0, 0);
    }

}
