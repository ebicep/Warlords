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
import com.ebicep.warlords.maps.GameLobby;
import com.ebicep.warlords.maps.Map;
import com.ebicep.warlords.powerups.AbstractPowerUp;
import com.ebicep.warlords.powerups.DamagePowerUp;
import com.ebicep.warlords.powerups.PowerupManager;
import com.ebicep.warlords.util.CustomScoreboard;
import com.ebicep.warlords.util.RemoveEntities;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.EulerAngle;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("start")) {
            System.out.println("STARTED");
            RemoveEntities removeEntities = new RemoveEntities();
            removeEntities.onRemove();
            System.out.println(Warlords.world.getEntities());
            //Warlords.world.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
            if (args.length > 2) {
                Location location = player.getLocation();
                ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                stand.setHelmet(new ItemStack(Material.LONG_GRASS, 1, (short) 2));
                stand.setGravity(true);
                stand.setVisible(true);
                stand.setHeadPose(new EulerAngle(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2])));
            }

            List<String> blueTeam = new ArrayList<>();
            List<String> redTeam = new ArrayList<>();
            List<CustomScoreboard> customScoreboards = new ArrayList<>();

            for (int i = 0; i < Warlords.world.getPlayers().size(); i = i + 2) {
                Player worldPlayer = Warlords.world.getPlayers().get(i);
                //worldPlayer.setWalkSpeed(.2f * Float.parseFloat(args[0]));
                Warlords.addPlayer(new WarlordsPlayer(worldPlayer, worldPlayer.getName(), worldPlayer.getUniqueId(), new Pyromancer(worldPlayer), false));
                worldPlayer.setMaxHealth(40);
                blueTeam.add(worldPlayer.getName());
                System.out.println("Added " + worldPlayer.getName());

                if (i + 1 < Warlords.world.getPlayers().size()) {
                    Player worldPlayer2 = Warlords.world.getPlayers().get(i + 1);
                    Warlords.addPlayer(new WarlordsPlayer(worldPlayer2, worldPlayer2.getName(), worldPlayer2.getUniqueId(), new Pyromancer(worldPlayer2), true));
                    worldPlayer2.setMaxHealth(40);
                    redTeam.add(worldPlayer.getName());
                    System.out.println("Added2 " + worldPlayer2.getName());
                }

                worldPlayer.setLevel((int) Warlords.getPlayer(player).getMaxEnergy());
                Warlords.getPlayer(worldPlayer).assignItemLore();
            }

            System.out.println(Warlords.getPlayers().values());
            for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                System.out.println("updated scoreboard for " + value.getName());
                value.setScoreboard(new CustomScoreboard(value.getPlayer(), blueTeam, redTeam));
            }
            new PowerupManager(GameLobby.GameMap.RIFT).runTaskTimer(Warlords.getInstance(), 0, 0);

        } else if (command.getName().equalsIgnoreCase("test")) {
            new PowerupManager(GameLobby.GameMap.RIFT).runTaskTimer(Warlords.getInstance(), 0, 0);
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
