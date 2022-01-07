package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.Settings;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ParticleQualityCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player player = BaseCommand.requirePlayer(sender);
        if (player != null) {
            if (args.length == 0) {
                player.sendMessage(ChatColor.GREEN + "Possible Values: LOW, MEDIUM, HIGH");
                return true;
            }
            if (args[0] != null) {
                if (Arrays.stream(Settings.ParticleQuality.values()).anyMatch((t) -> t.name().equals(args[0].toUpperCase()))) {
                    Settings.ParticleQuality newParticleQuality = Settings.ParticleQuality.valueOf(args[0].toUpperCase());
                    PlayerSettings settings = Warlords.getPlayerSettings(player.getUniqueId());
                    settings.setParticleQuality(newParticleQuality);

                    DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
                    databasePlayer.setParticleQuality(newParticleQuality);
                    DatabaseManager.updatePlayerAsync(databasePlayer);

                    sender.sendMessage(ChatColor.GREEN + "Particle Quality set to " + args[0].toUpperCase());
                    return true;
                }
            }
            sender.sendMessage(ChatColor.RED + "Not found. Possible Values: LOW, MEDIUM, HIGH");
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

        return Arrays
                .stream(Settings.ParticleQuality.values())
                .map(Enum::name)
                .filter(e -> e.startsWith(args[args.length - 1].toUpperCase(Locale.ROOT)))
                .map(e -> e.charAt(0) + e.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

    }

    public void register(Warlords instance) {
        instance.getCommand("pq").setExecutor(this);
        instance.getCommand("pq").setTabCompleter(this);
    }
}
