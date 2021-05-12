package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.mage.specs.cryomancer.Cryomancer;
import com.ebicep.warlords.classes.mage.specs.pyromancer.Pyromancer;
import com.ebicep.warlords.classes.warrior.specs.berserker.Berserker;
import com.ebicep.warlords.classes.warrior.specs.defender.Defender;
import com.ebicep.warlords.maps.GameManager;
import com.ebicep.warlords.maps.SpawnFlag;
import com.ebicep.warlords.powerups.PowerupManager;
import com.ebicep.warlords.util.CustomScoreboard;
import com.ebicep.warlords.util.RemoveEntities;
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

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("start")) {
            System.out.println("STARTED");
            RemoveEntities removeEntities = new RemoveEntities();
            removeEntities.onRemove();
            SpawnFlag flag = new SpawnFlag();
            flag.spawnFlag(GameManager.GameMap.CROSSFIRE);
            System.out.println(Warlords.world.getEntities());
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
                Warlords.addPlayer(new WarlordsPlayer(worldPlayer, worldPlayer.getName(), worldPlayer.getUniqueId(), new Berserker(worldPlayer), false));
                worldPlayer.setMaxHealth(40);
                //player.teleport(GameManager.GameMap.RIFT.map.getBlueLobbySpawnPoint());
                blueTeam.add(worldPlayer.getName());
                System.out.println("Added " + worldPlayer.getName());

                if (i + 1 < Warlords.world.getPlayers().size()) {
                    Player worldPlayer2 = Warlords.world.getPlayers().get(i + 1);
                    Warlords.addPlayer(new WarlordsPlayer(worldPlayer2, worldPlayer2.getName(), worldPlayer2.getUniqueId(), new Defender(worldPlayer2), false));
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
            new PowerupManager(GameManager.GameMap.RIFT).runTaskTimer(Warlords.getInstance(), 0, 0);

        } else if (command.getName().equalsIgnoreCase("test")) {
            BountifulAPI.sendTitle(((Player) sender).getPlayer(), 0, 5, 0, "TEST", "");
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
