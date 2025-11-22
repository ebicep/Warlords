package com.ebicep.warlords.pve.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.entity.Player;

@CommandAlias("abilitytree")
@CommandPermission("group.administrator")
public class AbilityTreeCommand extends BaseCommand {

    @Default
    public void openDefault(Player player) {
        open(player);
    }

    public static void open(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        WarlordsEntity inGameWarlordsPlayer = Warlords.getPlayer(player);
        WarlordsPlayer warlordsPlayer = inGameWarlordsPlayer instanceof WarlordsPlayer wp ?
                                        wp :
                                        new WarlordsPlayer(player, databasePlayer.getLastSpec(), ConfigManager.PVE_NAMESPACES);
        warlordsPlayer.getAbilityTree().openAbilityTree();
    }

    @Subcommand("reset")
    public void reset(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.resetAbilityTree();
    }


}
