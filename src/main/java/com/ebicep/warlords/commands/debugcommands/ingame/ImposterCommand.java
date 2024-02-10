package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pvp.ImposterModeOption;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;


@CommandAlias("imposter|impostor")
public class ImposterCommand extends BaseCommand {

    @Subcommand("assign")
    @CommandPermission("warlords.game.impostertoggle")
    @Description("Assign/Reassign the imposters in the game")
    public void assign(@Conditions("requireGame:withAddon=IMPOSTER_MODE") WarlordsPlayer warlordsPlayer) {
        for (Option option : warlordsPlayer.getGame().getOptions()) {
            if (option instanceof ImposterModeOption) {
                ((ImposterModeOption) option).assignImpostersWithAnimation(0);
                ChatChannels.sendDebugMessage(warlordsPlayer, Component.text("Imposters assigned", NamedTextColor.GREEN));
                return;
            }
        }
    }

    @Subcommand("vote")
    @Description("Vote to vote out a player")
    public void vote(@Conditions("requireGame:withAddon=IMPOSTER_MODE") WarlordsPlayer warlordsPlayer) {
        if (warlordsPlayer.getGame().getState(PlayingState.class)
                          .map(PlayingState::getTicksElapsed)
                          .orElse(0) < 60 * 20 * 5) {
            warlordsPlayer.sendMessage(Component.text("You cannot request to vote before 5 minutes have past!", NamedTextColor.RED));
            return;
        }
        for (Option option : warlordsPlayer.getGame().getOptions()) {
            if (!(option instanceof ImposterModeOption imposterModeOption)) {
                continue;
            }
            if (imposterModeOption.getPoll() != null) {
                warlordsPlayer.sendMessage(Component.text("There is an ongoing poll!", NamedTextColor.GREEN));
                return;
            }

            if (imposterModeOption.getImposters()
                                  .values()
                                  .stream()
                                  .flatMap(Collection::stream)
                                  .anyMatch(wp -> wp.equals(warlordsPlayer.getUuid()))
            ) {
                warlordsPlayer.sendMessage(Component.text("You cannot request to vote when you are an imposter!", NamedTextColor.RED));
                return;
            }

            if (imposterModeOption.getVoters()
                                  .values()
                                  .stream()
                                  .anyMatch(warlordsPlayers -> warlordsPlayers.contains(warlordsPlayer.getUuid()))
            ) {
                warlordsPlayer.sendMessage(Component.text("You already voted to vote!", NamedTextColor.RED));
                return;
            }
            imposterModeOption.getVoters()
                              .computeIfAbsent(warlordsPlayer.getTeam(), v -> new ArrayList<>())
                              .add(warlordsPlayer.getUuid());

            int votesNeeded = (int) (warlordsPlayer.getGame()
                                                   .warlordsPlayers()
                                                   .filter(wp -> wp.getTeam() == warlordsPlayer.getTeam())
                                                   .count() * .6);
            if (votesNeeded <= imposterModeOption.getVoters().get(warlordsPlayer.getTeam()).size()) {
                Team team = warlordsPlayer.getTeam();
                imposterModeOption.sendPoll(team);
                warlordsPlayer.getGame().addFrozenCause(Component.text(team.name, team.getTeamColor())
                                                                 .append(Component.text(" is voting!", NamedTextColor.GREEN)));
            } else {
                String votesNeededString = imposterModeOption.getVoters()
                                                             .get(warlordsPlayer.getTeam())
                                                             .size() + "/" + votesNeeded;
                TextComponent voteRequest = Component.text("A player wants to vote out someone! (" + votesNeededString + ")", NamedTextColor.GREEN);
                warlordsPlayer.getGame().forEachOnlinePlayerWithoutSpectators((p, team) -> {
                    if (team == warlordsPlayer.getTeam()) {
                        p.sendMessage(voteRequest);
                    }
                });
            }
            return;
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }
}
