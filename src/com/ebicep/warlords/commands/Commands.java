package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.warrior.specs.berserker.Berserker;
import com.ebicep.warlords.classes.warrior.specs.defender.Defender;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameMap;
import com.ebicep.warlords.maps.SpawnFlag;
import com.ebicep.warlords.powerups.PowerupManager;
import com.ebicep.warlords.util.CustomScoreboard;
import com.ebicep.warlords.util.RemoveEntities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.ebicep.warlords.maps.Game.State.GAME;
import static com.ebicep.warlords.maps.Game.State.PRE_GAME;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (command.getName().equalsIgnoreCase("start")) {

            if (Warlords.getInstance().game.getState() != PRE_GAME) {
                sender.sendMessage(ChatColor.RED + "Game has already started!");
                return true;
            }

            if (args.length > 0) {
                if (!Warlords.getInstance().game.canChangeMap()) {
                    sender.sendMessage(ChatColor.RED + "Game already started, cannot change map.");

                    return true;
                }

                if (args[0].equalsIgnoreCase("random")) {
                    Random random = new Random();
                    GameMap[] values = GameMap.values();
                    int randomIndex = random.nextInt(values.length);
                    GameMap newMap = values[randomIndex];
                    Warlords.getInstance().game.changeMap(newMap);

                } else {
                    try {
                        GameMap newMap = GameMap.valueOf(args[0].toUpperCase(Locale.ROOT));
                        Warlords.getInstance().game.changeMap(newMap);
                    } catch (IllegalArgumentException ex) {
                        sender.sendMessage(ChatColor.RED + "Error: " + ex);

                        return true;
                    }
                }
            }


            boolean teamBlue = true;

            for (Player player : Bukkit.getOnlinePlayers()) {
                Warlords.getInstance().game.addPlayer(player, teamBlue);
                teamBlue = !teamBlue;
            }

        } else if (command.getName().equalsIgnoreCase("endgame")) {
            if (Warlords.getInstance().game.getState() != GAME) {
                sender.sendMessage(ChatColor.RED + "There are no games currently running!");
                return true;
            }

            Warlords.getInstance().game.forceDraw();
            sender.sendMessage(ChatColor.RED + "Game has been terminated.");
        }

//            Location location = player.getLocation();
//            ArmorStand as = location.getWorld().spawn(location, ArmorStand.class);
//
//            as.setArms(true);
//            as.setRightArmPose(new EulerAngle(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2])));
//            as.setItemInHand(new ItemStack(Material.BROWN_MUSHROOM));
//
//            as.setGravity(false);
//            as.setVisible(true);

        return true;
    }

}
