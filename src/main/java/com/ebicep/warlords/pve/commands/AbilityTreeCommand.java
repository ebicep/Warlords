package com.ebicep.warlords.pve.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.general.PlayerSettings;
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
        WarlordsEntity inGameWarlordsPlayer = Warlords.getPlayer(player);
        WarlordsPlayer warlordsPlayer = inGameWarlordsPlayer instanceof WarlordsPlayer wp ?
                                        wp :
                                        new WarlordsPlayer(player, PlayerSettings.getPlayerSettings(player).getSelectedSpec(), ConfigManager.PVE_NAMESPACES);
        warlordsPlayer.getAbilityTree().openAbilityTree();
    }

    @Subcommand("reset")
    public void reset(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.resetAbilityTree();
    }


}
