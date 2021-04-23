package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.paladin.specs.avenger.Avenger;
import com.ebicep.warlords.classes.shaman.earthwarden.Earthwarden;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.util.EulerAngle;

public class StartGame implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("start")) {
            if (args.length > 2) {
                Location location = player.getLocation();
                ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                stand.setHelmet(new ItemStack(Material.LONG_GRASS, 1, (short) 2));
                stand.setGravity(true);
                stand.setVisible(true);
                stand.setHeadPose(new EulerAngle(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2])));
            }
            for (Player worldPlayer : Warlords.world.getPlayers()) {
                Warlords.addPlayer(new WarlordsPlayer(worldPlayer, worldPlayer.getName(), worldPlayer.getUniqueId(), new Avenger(worldPlayer)));
                worldPlayer.setMaxHealth(40);
            }
            player.setLevel(Warlords.getPlayer(player).getMaxEnergy());
            Warlords.getPlayer(player).assignItemLore();


            Location blueFlagLocation = new Location(player.getWorld(), 0.5, 4, 0.5);
            Block block = blueFlagLocation.getWorld().getBlockAt(blueFlagLocation);
            block.setType(Material.STANDING_BANNER);


            ArmorStand blueFlag = blueFlagLocation.getWorld().spawn(blueFlagLocation, ArmorStand.class);
            blueFlag.setGravity(false);
            blueFlag.setCanPickupItems(false);
            blueFlag.setCustomName("BLU FLAG");
            blueFlag.setCustomNameVisible(true);
            blueFlag.setVisible(false);

        } else if (command.getName().equalsIgnoreCase("as")) {
            Location location = player.getLocation();
            ArmorStand as = location.getWorld().spawn(location, ArmorStand.class);

            as.setArms(true);
            as.setRightArmPose(new EulerAngle(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2])));
            as.setItemInHand(new ItemStack(Material.BROWN_MUSHROOM));

            as.setGravity(false);
            as.setVisible(true);
        }

        return true;
    }

}
