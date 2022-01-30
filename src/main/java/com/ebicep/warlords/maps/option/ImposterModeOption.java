package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.commands.debugcommands.ImposterCommand;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.WarlordsPlayer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;

public class ImposterModeOption implements Option {

    @Override
    public void register(Game game) {
        game.registerScoreboardHandler(new SimpleScoreboardHandler(30, "imposter") {
            @Override
            public List<String> computeLines(WarlordsPlayer warlordsPlayer) {
                if ((ImposterCommand.blueImposterName != null && ImposterCommand.blueImposterName.equalsIgnoreCase(warlordsPlayer.getName())) ||
                        (ImposterCommand.redImposterName != null && ImposterCommand.redImposterName.equals(warlordsPlayer.getName()))
                ) {
                    return Arrays.asList(ChatColor.WHITE + "Role: " + ChatColor.RED + "IMPOSTER");
                } else {
                    if (ImposterCommand.blueImposterName != null && ImposterCommand.redImposterName != null) {
                        return Arrays.asList(ChatColor.WHITE + "Role: " + ChatColor.GREEN + "INNOCENT");
                    } else {
                        return Collections.emptyList();
                    }
                }
            }
        });
    }
}
