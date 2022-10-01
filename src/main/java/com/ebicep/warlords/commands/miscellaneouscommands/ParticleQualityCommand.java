package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("pq")
public class ParticleQualityCommand extends BaseCommand {

    @Default
    @Description("Sets your particle quality")
    public void particleQuality(Player player, Settings.ParticleQuality particleQuality) {
        PlayerSettings settings = PlayerSettings.getPlayerSettings(player.getUniqueId());
        settings.setParticleQuality(particleQuality);
        player.sendMessage(ChatColor.GREEN + "Particle Quality set to " + particleQuality);
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.setParticleQuality(particleQuality);
        });
    }

}
