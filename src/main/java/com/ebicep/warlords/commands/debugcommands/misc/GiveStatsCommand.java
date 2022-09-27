package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;

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
            System.out.println(s);
            Object[] arguments = new Object[0];
            if (s.contains("(")) {
                String[] split = s.substring(s.indexOf("(") + 1, s.indexOf(")")).split(",");
                arguments = new Object[split.length];
                System.arraycopy(split, 0, arguments, 0, split.length);
            }
            System.out.println(Arrays.toString(arguments));
            Method[] methods = object.getClass().getMethods();
            for (Method method : methods) {
                System.out.println(" - " + method.getName());
            }
            String methodToFind = s;
            if (arguments.length > 0) {
                methodToFind = s.substring(0, s.indexOf("("));
            }
            System.out.println("Looking for " + methodToFind);
            for (Method method : methods) {
                if (method.getName().equalsIgnoreCase(methodToFind)) {
                    try {
                        System.out.println("Found method " + method.getName());
                        Class<?>[] methodArguments = method.getParameterTypes();
                        System.out.println("Arguments: " + Arrays.toString(methodArguments));
                        for (int i = 0; i < methodArguments.length; i++) {
                            if (methodArguments[i] == int.class) {
                                arguments[i] = Integer.parseInt((String) arguments[i]);
                            } else if (methodArguments[i] == long.class) {
                                arguments[i] = Long.parseLong((String) arguments[i]);
                            }
                        }
                        object = method.invoke(object, arguments);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
    }

}
