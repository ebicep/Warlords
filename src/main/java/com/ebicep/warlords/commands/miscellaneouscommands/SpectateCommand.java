package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player player = BaseCommand.requirePlayerOutsideGame(sender);
        if (player != null) {
            if(Warlords.game.playersCount() == 0) {
                sender.sendMessage(ChatColor.RED + "There are no active games right now!");
                return true;
            }

            if(Warlords.game.getPlayers().containsKey(((Player) sender).getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You cannot use this command inside a game!");
                return true;
            }

            openSpectateMenu(player);

            return true;
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("spectate").setExecutor(this);
    }

    public static void openSpectateMenu(Player player) {
        Menu menu = new Menu("Current Games", 9 * 3);

        menu.setItem(
                1,
                1,
                new ItemBuilder(Material.BOOK)
                        .name(ChatColor.GREEN + "Game 1")
                        .get(),
                (n, e) -> {
                    if(Warlords.game.getSpectators().contains(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You are already spectating this game");
                    } else {
                        Warlords.game.addSpectator(player.getUniqueId());
                        player.sendMessage(ChatColor.RED + "You may leave spectator mode by typing /spectate and clicking the barrier in the menu.");
                    }
                }
        );

        if(Warlords.game.getSpectators().contains(player.getUniqueId())) {
            menu.setItem(
                    4,
                    2,
                    new ItemBuilder(Material.BARRIER)
                            .name(ChatColor.GREEN + "Return to the lobby")
                            .get(),
                    (n, e) -> Warlords.game.removeSpectator(player.getUniqueId(), true)
            );
        }

        menu.openForPlayer(player);
    }
}
