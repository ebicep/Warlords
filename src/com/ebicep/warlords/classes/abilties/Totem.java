package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Totem extends EntityArmorStand {

    private WarlordsPlayer owner;
    private ArmorStand totemArmorStand;
    private int secondsLeft;
    private List<WarlordsPlayer> playersHit;

    public Totem(World world, WarlordsPlayer owner, ArmorStand totemArmorStand, int secondsLeft) {
        super(world);
        this.owner = owner;
        this.totemArmorStand = totemArmorStand;
        this.secondsLeft = secondsLeft;
        playersHit = new ArrayList<>();
    }

    public WarlordsPlayer getOwner() {
        return owner;
    }

    public ArmorStand getTotemArmorStand() {
        return totemArmorStand;
    }

    public void setTotemArmorStand(ArmorStand totemArmorStand) {
        this.totemArmorStand = totemArmorStand;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public void setSecondsLeft(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    public List<WarlordsPlayer> getPlayersHit() {
        return playersHit;
    }

    public void setPlayersHit(List<WarlordsPlayer> playersHit) {
        this.playersHit = playersHit;
    }

    public static class TotemThunderlord extends AbstractAbility {

        public TotemThunderlord() {
            super("Capacitor Totem", -404, -503, 60 + 2, 20, 20, 200, "capacitor totem description");
        }

        @Override
        public void onActivate(Player player) {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

            Location standLocation = player.getLocation();
            standLocation.setYaw(0);
            standLocation.setY(standLocation.getWorld().getHighestBlockYAt(standLocation) - 1);
            ArmorStand totemStand = player.getWorld().spawn(standLocation, ArmorStand.class);
            totemStand.setVisible(false);
            totemStand.setGravity(false);
            totemStand.setHelmet(new ItemStack(Material.RED_ROSE, 1, (short) 4));

            Totem capacitorTotem = new Totem(((CraftWorld) player.getWorld()).getHandle(), warlordsPlayer, totemStand, 8);
            Warlords.totems.add(capacitorTotem);
        }
    }

    public static class TotemSpiritguard extends AbstractAbility {

        public TotemSpiritguard() {
            super("Death's Debt", 0, 0, 60 + 12, 20, -1, 100, "deaths debt description");
        }

        @Override
        public void onActivate(Player player) {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

            Location standLocation = player.getLocation();
            standLocation.setYaw(0);
            standLocation.setY(standLocation.getWorld().getHighestBlockYAt(standLocation) - 1);
            ArmorStand totemStand = player.getWorld().spawn(standLocation, ArmorStand.class);
            totemStand.setVisible(false);
            totemStand.setGravity(false);
            totemStand.setHelmet(new ItemStack(Material.JUNGLE_FENCE_GATE));

            //TODO find time based on health 6 + (something)
            Totem deathsDebtTotem = new Totem(((CraftWorld) player.getWorld()).getHandle(), warlordsPlayer, totemStand, -1);
            Warlords.totems.add(deathsDebtTotem);
        }
    }

    public static class TotemEarthwarden extends AbstractAbility {

        public TotemEarthwarden() {
            super("Healing Totem", 168, 841, 60 + 12, 60, 15, 200, "healing totem description");
            //168 - 227
            //841 - 1138
            //1.35x
        }

        @Override
        public void onActivate(Player player) {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

            Location standLocation = player.getLocation();
            standLocation.setYaw(0);
            standLocation.setY(standLocation.getWorld().getHighestBlockYAt(standLocation) - 1);
            ArmorStand totemStand = player.getWorld().spawn(standLocation, ArmorStand.class);
            totemStand.setVisible(false);
            totemStand.setGravity(false);
            totemStand.setHelmet(new ItemStack(Material.RED_ROSE, 1, (short) 7));

            Totem healingTotem = new Totem(((CraftWorld) player.getWorld()).getHandle(), warlordsPlayer, totemStand, 5);
            Warlords.totems.add(healingTotem);
        }

    }
}
