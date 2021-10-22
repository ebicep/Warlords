package com.ebicep.warlords.commands.debugcommands;

import co.aikar.taskchain.TaskChain;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.database.LeaderboardRanking;
import com.ebicep.warlords.maps.flags.PlayerFlagLocation;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.mongodb.client.model.Filters;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Team;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;
import java.util.Optional;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.isOp()) {
            return true;
        }
        WarlordsPlayer player = BaseCommand.requireWarlordsPlayer(sender);
        if (player != null) {

        }

//        DatabaseManager.warlordsGamesDatabase.createCollection("Games_Information_Backup");
//        for (Document document : DatabaseManager.gamesInformation.find()) {
//            DatabaseManager.warlordsGamesDatabase.getCollection("Games_Information_Backup").insertOne(document);
//        }
//        for (Document document2 : DatabaseManager.gamesInformation.find()) {
//            if(document2.get("counted") == null) {
//                Document document = new Document();
//                document.put("counted", true);
//                DatabaseManager.gamesInformation.updateOne(Filters.eq("_id", document2.get("_id")), new Document("$set", document));
//            }
//        }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("test").setExecutor(this);
        //instance.getCommand("class").setTabCompleter(this);
    }

}
