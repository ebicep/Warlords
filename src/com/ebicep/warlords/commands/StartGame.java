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
import com.ebicep.warlords.util.CustomScoreboard;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StartGame implements CommandExecutor {

    private int blueKills = 0;
    private int redKills = 0;

    public int getBlueKills() {
        return blueKills;
    }

    public void setBlueKills(int blueKills) {
        this.blueKills = blueKills;
    }

    public int getRedKills() {
        return redKills;
    }

    public void setRedKills(int redKills) {
        this.redKills = redKills;
    }

    public void spawnScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = format.format(new Date());
        Objective objective = board.registerNewObjective(dateString, "");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§e§lWARLORDS");
        objective.getScore(ChatColor.GRAY + dateString).setScore(15);
        objective.getScore(" ").setScore(14);
        objective.getScore(ChatColor.BLUE + "BLU: " + ChatColor.AQUA + blueKills * 5 + ChatColor.GOLD + "/1000").setScore(13);
        objective.getScore(ChatColor.RED + "RED: " + ChatColor.AQUA + redKills * 5 + ChatColor.GOLD + "/1000").setScore(12);
        objective.getScore("  ").setScore(11);
        objective.getScore(ChatColor.BLUE + "BLU " + ChatColor.GOLD + "Wins in: " + ChatColor.GREEN + "10:00").setScore(10);
        objective.getScore("   ").setScore(9);
        objective.getScore(ChatColor.RED + "RED Flag: " + ChatColor.GREEN + "Safe").setScore(8);
        objective.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.GREEN + "Safe").setScore(7);
        objective.getScore("    ").setScore(6);
        objective.getScore("     ").setScore(4);
        objective.getScore("      ").setScore(2);
        objective.getScore(ChatColor.YELLOW + "localhost").setScore(1);


        for (Player player : Warlords.getPlayers().keySet()) {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
            objective.getScore(ChatColor.GOLD + "Lv90 " + ChatColor.GREEN + warlordsPlayer.getSpec().getClass().getSimpleName()).setScore(5);
            objective.getScore("" + ChatColor.GREEN + warlordsPlayer.getKills() + ChatColor.RESET + "Kills " + ChatColor.GREEN + warlordsPlayer.getAssists() + ChatColor.RESET + "Assists").setScore(3);

            player.setScoreboard(board);
        }
    }

    public void updateScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getMainScoreboard();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = format.format(new Date());
        Objective objective = board.getObjective(dateString);


        objective.getScore(ChatColor.GRAY + dateString).setScore(15);
        objective.getScore(" ").setScore(14);
        objective.getScore(ChatColor.BLUE + "BLU: " + ChatColor.AQUA + blueKills * 5 + ChatColor.GOLD + "/1000").setScore(13);
        objective.getScore(ChatColor.RED + "RED: " + ChatColor.AQUA + redKills * 5 + ChatColor.GOLD + "/1000").setScore(12);
        objective.getScore("  ").setScore(11);
        objective.getScore(ChatColor.BLUE + "BLU " + ChatColor.GOLD + "Wins in: " + ChatColor.GREEN + "10:00").setScore(10);
        objective.getScore("   ").setScore(9);
        objective.getScore(ChatColor.RED + "RED Flag: " + ChatColor.GREEN + "Safe").setScore(8);
        objective.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.GREEN + "Safe").setScore(7);
        objective.getScore("    ").setScore(6);
        objective.getScore("     ").setScore(4);
        objective.getScore("      ").setScore(2);
        objective.getScore(ChatColor.YELLOW + "localhost").setScore(1);


        for (Player player : Warlords.getPlayers().keySet()) {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
            objective.getScore(ChatColor.GOLD + "Lv90 " + ChatColor.GREEN + warlordsPlayer.getSpec().getClass().getSimpleName()).setScore(5);
            objective.getScore("" + ChatColor.GREEN + warlordsPlayer.getKills() + ChatColor.RESET + "Kills " + ChatColor.GREEN + warlordsPlayer.getAssists() + ChatColor.RESET + "Assists").setScore(3);

            player.setScoreboard(board);
        }
    }

    // test comment
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("start")) {
            System.out.println("STARTED");
            Warlords.world.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
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
                Warlords.addPlayer(new WarlordsPlayer(worldPlayer, worldPlayer.getName(), worldPlayer.getUniqueId(), new Earthwarden(worldPlayer)));
                worldPlayer.setMaxHealth(40);
                blueTeam.add(worldPlayer.getName());
                System.out.println("Added " + worldPlayer.getName());

                if (i + 1 < Warlords.world.getPlayers().size()) {
                    Player worldPlayer2 = Warlords.world.getPlayers().get(i + 1);
                    Warlords.addPlayer(new WarlordsPlayer(worldPlayer2, worldPlayer2.getName(), worldPlayer2.getUniqueId(), new Cryomancer(worldPlayer2)));
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

//            Location blueFlagLocation = new Location(player.getWorld(), 0.5, 4, 0.5);
//            Block block = blueFlagLocation.getWorld().getBlockAt(blueFlagLocation);
//            block.setType(Material.STANDING_BANNER);
//
//
//            ArmorStand blueFlag = blueFlagLocation.getWorld().spawn(blueFlagLocation, ArmorStand.class);
//            blueFlag.setGravity(false);
//            blueFlag.setCanPickupItems(false);
//            blueFlag.setCustomName("BLU FLAG");
//            blueFlag.setCustomNameVisible(true);
//            blueFlag.setVisible(false);

        } else if (command.getName().equalsIgnoreCase("test")) {
            for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                value.getScoreboard().updateKills();
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
        }

        return true;
    }

}
