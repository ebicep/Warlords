package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.mage.specs.aquamancer.Aquamancer;
import com.ebicep.warlords.classes.mage.specs.cryomancer.Cryomancer;
import com.ebicep.warlords.classes.mage.specs.pyromancer.Pyromancer;
import com.ebicep.warlords.classes.paladin.specs.avenger.Avenger;
import com.ebicep.warlords.classes.paladin.specs.crusader.Crusader;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;
import com.ebicep.warlords.classes.shaman.specs.earthwarden.Earthwarden;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.classes.shaman.specs.thunderlord.ThunderLord;
import com.ebicep.warlords.classes.warrior.specs.berserker.Berserker;
import com.ebicep.warlords.classes.warrior.specs.defender.Defender;
import com.ebicep.warlords.classes.warrior.specs.revenant.Revenant;
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
import org.bukkit.util.EulerAngle;

public class StartGame implements CommandExecutor {

    // test comment
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("start")) {
            System.out.println("STARTED");
            if (args.length > 2) {
                Location location = player.getLocation();
                ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                stand.setHelmet(new ItemStack(Material.LONG_GRASS, 1, (short) 2));
                stand.setGravity(true);
                stand.setVisible(true);
                stand.setHeadPose(new EulerAngle(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2])));
            }
            for (int i = 0; i < Warlords.world.getPlayers().size(); i = i + 2) {
                Player worldPlayer = Warlords.world.getPlayers().get(i);
                //worldPlayer.setWalkSpeed(.2f * Float.parseFloat(args[0]));
                Warlords.addPlayer(new WarlordsPlayer(worldPlayer, worldPlayer.getName(), worldPlayer.getUniqueId(), new Spiritguard(worldPlayer)));
                worldPlayer.setMaxHealth(40);
                System.out.println("Added " + worldPlayer.getName());

                if (i + 1 < Warlords.world.getPlayers().size()) {
                    Player worldPlayer2 = Warlords.world.getPlayers().get(i + 1);
                    Warlords.addPlayer(new WarlordsPlayer(worldPlayer2, worldPlayer2.getName(), worldPlayer2.getUniqueId(), new Earthwarden(worldPlayer2)));
                    worldPlayer2.setMaxHealth(40);
                    System.out.println("Added " + worldPlayer2.getName());
                }
            }
            player.setLevel((int) Warlords.getPlayer(player).getMaxEnergy());
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

// random wip
    // player.getPlayer();

   /* int max = 120;
    int min = 0;
    int countdown;
    GameState game;

				if (player === max) {
                        state = "GAME";
                        // Teleport players to game area

                        } else if (players >= min)


                        if (countdown > 0) {
                        countdown--;
                        } else {
                        state = "GAME";
                        // Teleport players to game area
                        } else {
                        countdown = 60;

                        } else {
                        System.out.println("Starting failed.");
                        }
                        }

                        return false;
                        } */