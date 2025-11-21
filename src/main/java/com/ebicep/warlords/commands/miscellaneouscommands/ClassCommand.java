package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("class")
@CommandPermission("warlords.game.changeclass")
public class ClassCommand extends BaseCommand {

    @Default
    @Description("Change your class")
    public void changeClass(@Conditions("outsideGame") Player player, Specializations spec) {
        PlayerSettings settings = PlayerSettings.getPlayerSettings(player.getUniqueId());
        settings.setSelectedSpec(spec);
        player.sendMessage(Component.text("Your selected spec: §7" + spec, NamedTextColor.BLUE));
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        databasePlayer.setLastSpec(spec);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
    }

}
