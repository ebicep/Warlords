package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;

@CommandAlias("class")
@CommandPermission("warlords.game.changeclass")
public class ClassCommand extends BaseCommand {

//    @Default
//    @Description("Change your class")
//    public void changeClass(@Conditions("outsideGame") Player player, Specializations spec) {
//        PlayerSettings settings = PlayerSettings.getPlayerSettings(player.getUniqueId());
//        settings.setSelectedSpec(spec);
//        player.sendMessage(Component.text("Your selected spec: §7" + spec, NamedTextColor.BLUE));
//        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
//        databasePlayer.setLastSpec(spec);
//        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
//    }

}
