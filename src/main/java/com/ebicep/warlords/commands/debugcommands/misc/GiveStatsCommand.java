package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@CommandAlias("givestats")
@CommandPermission("group.adminisrator")
@Conditions("database:player")
public class GiveStatsCommand extends BaseCommand {

    @Default
    @Description("Gives you stats using reflection - ex. /givestats getpvestats setwins(20)")
    public void giveStats(Player player, @Split(" ") String[] query) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        Object object = databasePlayer;
        for (String s : query) {
            player.sendMessage(ChatColor.GREEN + "Querying " + s);
            Object[] arguments = new Object[0];
            if (s.contains("(")) {
                String[] split = s.substring(s.indexOf("(") + 1, s.indexOf(")")).split(",");
                arguments = new Object[split.length];
                System.arraycopy(split, 0, arguments, 0, split.length);
            }
            player.sendMessage(ChatColor.GREEN + "Arguments: " + Arrays.toString(arguments));
            Method[] methods = object.getClass().getMethods();
            player.sendMessage(ChatColor.GREEN + "Methods: " + ChatColor.GRAY + Arrays.stream(methods)
                    .map(Method::getName)
                    .collect(Collectors.joining(", ")));
            String methodToFind = s;
            if (arguments.length > 0) {
                methodToFind = s.substring(0, s.indexOf("("));
            }
            player.sendMessage(ChatColor.GREEN + "Method to Find: " + methodToFind);
            for (Method method : methods) {
                if (method.getName().equalsIgnoreCase(methodToFind)) {
                    try {
                        player.sendMessage(ChatColor.YELLOW + "Found Method " + method.getName());
                        Class<?>[] methodArguments = method.getParameterTypes();
                        player.sendMessage(ChatColor.YELLOW + "Arguments: " + Arrays.toString(methodArguments));
                        for (int i = 0; i < methodArguments.length; i++) {
                            if (methodArguments[i] == int.class) {
                                arguments[i] = Integer.parseInt((String) arguments[i]);
                            } else if (methodArguments[i] == long.class) {
                                arguments[i] = Long.parseLong((String) arguments[i]);
                            }
                        }
                        object = method.invoke(object, arguments);
                        player.sendMessage(ChatColor.YELLOW + "Invoked Method " + method.getName());
                    } catch (Exception e) {
                        player.sendMessage("Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        ChatChannels.sendDebugMessage(player, ChatColor.DARK_GREEN + "Done: " + Arrays.toString(query), true);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
    }

}
