package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pvp.siege.SiegeOption;
import com.ebicep.warlords.game.option.pvp.siege.SiegeStats;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Map;

@CommandAlias("printspecstats")
@CommandPermission("group.administrator")
public class PrintSpecStatsCommand extends BaseCommand {

    @Default
    @CommandCompletion("@warlordsplayers")
    @Description("Prints spec minute stats")
    public void print(CommandIssuer issuer, @Optional WarlordsPlayer target) {
        if (target == null) {
            ChatChannels.sendDebugMessage(issuer, Component.text("No target", NamedTextColor.RED));
            return;
        }
        Map<Specializations, PlayerStatisticsMinute> specMinuteStats = target.getSpecMinuteStats();
        specMinuteStats.forEach((specializations, entries) -> {
            PlayerStatisticsMinute.Entry total = entries.total();
            ChatChannels.sendDebugMessage(issuer, Component.text("Spec: " + specializations.name, NamedTextColor.GOLD));
            ChatChannels.sendDebugMessage(issuer, Component.text("  Damage: " + total.getDeaths(), NamedTextColor.RED));
            ChatChannels.sendDebugMessage(issuer, Component.text("  Healing: " + total.getHealing(), NamedTextColor.GREEN));
            ChatChannels.sendDebugMessage(issuer, Component.text("  Absorbed: " + total.getAbsorbed(), NamedTextColor.YELLOW));
            ChatChannels.sendDebugMessage(issuer, Component.text("  Kills: " + total.getKills(), NamedTextColor.GOLD));
            ChatChannels.sendDebugMessage(issuer, Component.text("  Assists: " + total.getAssists(), NamedTextColor.GOLD));
            ChatChannels.sendDebugMessage(issuer, Component.text("  Deaths: " + total.getDeaths(), NamedTextColor.GOLD));
            ChatChannels.sendDebugMessage(issuer, Component.text("  Time in combat: " + total.getTimeInCombat(), NamedTextColor.GOLD));
            ChatChannels.sendDebugMessage(issuer, Component.text("  Time in respawn: " + total.getRespawnTimeSpent(), NamedTextColor.GOLD));
        });
    }

    @Subcommand("siege")
    @CommandCompletion("@warlordsplayers")
    @Description("Prints spec minute stats")
    public void printSiege(CommandIssuer issuer, @Optional WarlordsPlayer target) {
        if (target == null) {
            ChatChannels.sendDebugMessage(issuer, Component.text("No target", NamedTextColor.RED));
            return;
        }
        for (Option option : target.getGame().getOptions()) {
            if (option instanceof SiegeOption siegeOption) {
                Map<Specializations, SiegeStats> siegeStatsMap = siegeOption.getPlayerSiegeStats().get(target.getUuid());
                if (siegeStatsMap == null) {
                    ChatChannels.sendDebugMessage(issuer, Component.text("No siege stats", NamedTextColor.RED));
                    return;
                }
                siegeStatsMap.forEach((specializations, siegeStats) -> {
                    ChatChannels.sendDebugMessage(issuer, Component.text("Spec: " + specializations.name, NamedTextColor.GOLD));
                    ChatChannels.sendDebugMessage(issuer, Component.text("  Points captured: " + siegeStats.getPointsCaptured(), NamedTextColor.GOLD));
                    ChatChannels.sendDebugMessage(issuer, Component.text("  Points captured fail: " + siegeStats.getPointsCapturedFail(), NamedTextColor.GOLD));
                    ChatChannels.sendDebugMessage(issuer, Component.text("  Time on point: " + siegeStats.getTimeOnPointTicks() / 20, NamedTextColor.GOLD));
                    ChatChannels.sendDebugMessage(issuer, Component.text("  Payloads escorted: " + siegeStats.getPayloadsEscorted(), NamedTextColor.GOLD));
                    ChatChannels.sendDebugMessage(issuer, Component.text("  Payloads escorted fail: " + siegeStats.getPayloadsEscortedFail(), NamedTextColor.GOLD));
                    ChatChannels.sendDebugMessage(issuer, Component.text("  Payloads defended: " + siegeStats.getPayloadsDefended(), NamedTextColor.GOLD));
                    ChatChannels.sendDebugMessage(issuer, Component.text("  Payloads defended fail: " + siegeStats.getPayloadsDefendedFail(), NamedTextColor.GOLD));
                    ChatChannels.sendDebugMessage(issuer, Component.text("  Time on payload escorting: " + siegeStats.getTimeOnPayloadEscortingTicks() / 20, NamedTextColor.GOLD));
                    ChatChannels.sendDebugMessage(issuer, Component.text("  Time on payload defending: " + siegeStats.getTimeOnPayloadDefendingTicks() / 20, NamedTextColor.GOLD));
                });
            }
        }
    }

}
