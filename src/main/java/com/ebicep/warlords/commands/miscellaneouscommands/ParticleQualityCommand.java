package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.player.general.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("pq")
public class ParticleQualityCommand extends BaseCommand {

    @Default
    @Description("Sets your particle quality")
    public void particleQuality(Player player, Settings.ParticleQuality particleQuality) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.setParticleQuality(particleQuality);
            player.sendMessage(Component.text("Particle Quality set to " + particleQuality, NamedTextColor.GREEN));
        });
    }

}
