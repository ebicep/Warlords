package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ebicep.warlords.util.warlords.Utils.startsWithIgnoreCase;

public abstract class GameTargetCommand implements TabExecutor {

    protected abstract void doAction(CommandSender sender, Collection<GameManager.GameHolder> gameInstances);
    
    protected Collection<GameManager.GameHolder> getGames() {
        return Warlords.getGameManager().getGames();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!command.testPermissionSilent(sender)) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }
        
        Collection<GameManager.GameHolder> gameInstances;
        if (args.length == 0) {
            WarlordsPlayer wp = BaseCommand.requireWarlordsPlayer(sender);
            if (wp == null) {
                return true;
            }
            gameInstances = getGames().stream().filter(e -> e.getGame() == wp.getGame()).collect(Collectors.toList());
            if (gameInstances.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Unable to find the game that your are in!");
            }
        } else {
            Set<GameManager.GameHolder> holder = new HashSet<>();
            gameInstances = holder;
            List<GameManager.GameHolder> matched = new ArrayList<>();
            for (String arg : args) {
                matched.clear();
                for (GameManager.GameHolder h : getGames()) {
                    Game game = h.getGame();
                    if (
                            "*".equals(arg)
                                    || h.getName().equalsIgnoreCase(arg)
                                    || h.getMap().name().equalsIgnoreCase(arg)
                                    || (game != null && game.getGameMode().name().equalsIgnoreCase(arg))
                                    || (game != null && game.getGameId().toString().equalsIgnoreCase(arg))
                    ) {
                        matched.add(h);
                    }
                }
                if (matched.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "Unable to find: " + arg);
                    continue;
                }
                holder.addAll(matched);
            }
        }
        
        this.doAction(sender, gameInstances);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String label, String[] args) {
        return Stream.concat(
                Stream.of("*"),
                Warlords.getGameManager().getGames().stream()
                        .flatMap(h -> Stream.concat(
                        Stream.of(
                                Utils.toTitleCase(h.getName()),
                                Utils.toTitleCase(h.getMap().name())
                        ),
                        h.getGame() != null ? Stream.of(
                                h.getGame().getGameId().toString(),
                                Utils.toTitleCase(h.getGame().getGameMode().name())
                        ) : Stream.empty()
                )).distinct()
        )
        .filter(e -> startsWithIgnoreCase(e, args[args.length - 1]))
        .collect(Collectors.toList());
    }
}
