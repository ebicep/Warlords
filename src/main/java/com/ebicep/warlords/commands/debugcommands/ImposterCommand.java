package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.ImposterModeOption;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class ImposterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length < 1) {
            return true;
        }

        String input = args[0];

        if (input.equalsIgnoreCase("assign")) {
            if (!sender.hasPermission("warlords.game.impostertoggle")) {
                sender.sendMessage("§cYou do not have permission to do that.");
                return true;
            }
        }

        switch (input.toLowerCase()) {
            case "assign": {
                WarlordsPlayer warlordsPlayer = BaseCommand.requireWarlordsPlayer(sender);
                if (warlordsPlayer == null) return true;
                if (!warlordsPlayer.getGame().getAddons().contains(GameAddon.IMPOSTER_MODE)) {
                    sender.sendMessage(ChatColor.RED + "The imposter gamemode is currently disabled");
                    return true;
                }

                break;
            }
            case "vote": {
                WarlordsPlayer warlordsPlayer = BaseCommand.requireWarlordsPlayer(sender);
                if (warlordsPlayer == null) return true;
                if (!warlordsPlayer.getGame().getAddons().contains(GameAddon.IMPOSTER_MODE)) {
                    sender.sendMessage(ChatColor.RED + "The imposter gamemode is currently disabled");
                    return true;
                }

                if (warlordsPlayer.getGameState().getTicksElapsed() < 60 * 20 * 5) {
                    sender.sendMessage(ChatColor.RED + "You cannot request to vote before 5 minutes have past!");
                    return true;
                }

                ImposterModeOption imposterModeOption = (ImposterModeOption) warlordsPlayer.getGame().getOptions().stream()
                        .filter(option -> option instanceof ImposterModeOption)
                        .findFirst()
                        .get();

                if (imposterModeOption.getPoll() != null) {
                    sender.sendMessage(ChatColor.GREEN + "There is an ongoing poll!");
                    return true;
                }

                if (imposterModeOption.getVoters().values().stream().anyMatch(warlordsPlayers -> warlordsPlayers.contains(warlordsPlayer))) {
                    sender.sendMessage(ChatColor.RED + "You already voted to vote!");
                    return true;
                }

                imposterModeOption.getVoters().computeIfAbsent(warlordsPlayer.getTeam(), v -> new ArrayList<>()).add(warlordsPlayer);

                int votesNeeded = (int) (warlordsPlayer.getGame().getPlayers().entrySet().stream().filter(uuidTeamEntry -> uuidTeamEntry.getValue() == warlordsPlayer.getTeam()).count() * .75 + 1);
                if (votesNeeded >= imposterModeOption.getVoters().get(warlordsPlayer.getTeam()).size()) {
                    Team team = warlordsPlayer.getTeam();
                    imposterModeOption.sendPoll(team);
                    warlordsPlayer.getGame().addFrozenCause(team.teamColor + team.name + ChatColor.GREEN + " is voting!");
                } else {
                    warlordsPlayer.getGame().forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        if (team == warlordsPlayer.getTeam()) {
                            player.sendMessage(ChatColor.GREEN + "A player wants to vote out someone! (" + imposterModeOption.getVoters().get(warlordsPlayer.getTeam()).size() + "/" + votesNeeded + ")");
                        }
                    });
                }
                break;
            }
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("imposter").setExecutor(this);
    }

}
