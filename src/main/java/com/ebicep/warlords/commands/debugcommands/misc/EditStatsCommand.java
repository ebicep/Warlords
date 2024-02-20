package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.commands.DatabasePlayerFuture;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabasePlayerPvEWaveDefenseDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabasePlayerWaveDefenseStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.WaveDefenseDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes.DatabaseBasePvEWaveDefense;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@CommandAlias("editstats")
@CommandPermission("group.adminisrator")
public class EditStatsCommand extends BaseCommand {

    @Default
    @Description("Edits your stats using reflection - ex. /givestats getpvestats setwins(20)")
    public void editStats(Player player, @Split(" ") String[] query) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            Object object = databasePlayer;
            for (String s : query) {
                player.sendMessage(Component.text("Querying " + s, NamedTextColor.GREEN));
                Object[] arguments = new Object[0];
                if (s.contains("(")) {
                    String[] split = s.substring(s.indexOf("(") + 1, s.indexOf(")")).split(",");
                    arguments = new Object[split.length];
                    System.arraycopy(split, 0, arguments, 0, split.length);
                }
                player.sendMessage(Component.text("Arguments: " + Arrays.toString(arguments), NamedTextColor.GREEN));
                Method[] methods = object.getClass().getMethods();
                player.sendMessage(Component.text("Methods: ", NamedTextColor.GREEN)
                                            .append(Component.text(Arrays.stream(methods)
                                                                         .map(Method::getName)
                                                                         .collect(Collectors.joining(", ")), NamedTextColor.GRAY)));
                String methodToFind = s;
                if (arguments.length > 0) {
                    methodToFind = s.substring(0, s.indexOf("("));
                }
                player.sendMessage(Component.text("Method to Find: " + methodToFind, NamedTextColor.GREEN));
                for (Method method : methods) {
                    if (method.getName().equalsIgnoreCase(methodToFind)) {
                        try {
                            player.sendMessage(Component.text("Found Method " + method.getName(), NamedTextColor.YELLOW));
                            Class<?>[] methodArguments = method.getParameterTypes();
                            player.sendMessage(Component.text("Arguments: " + Arrays.toString(methodArguments), NamedTextColor.YELLOW));
                            for (int i = 0; i < methodArguments.length; i++) {
                                if (methodArguments[i] == int.class) {
                                    arguments[i] = Integer.parseInt((String) arguments[i]);
                                } else if (methodArguments[i] == long.class) {
                                    arguments[i] = Long.parseLong((String) arguments[i]);
                                } else if (methodArguments[i] == double.class) {
                                    arguments[i] = Double.parseDouble((String) arguments[i]);
                                } else if (methodArguments[i] == float.class) {
                                    arguments[i] = Float.parseFloat((String) arguments[i]);
                                } else if (methodArguments[i] == boolean.class) {
                                    arguments[i] = Boolean.parseBoolean((String) arguments[i]);
                                }
                            }
                            object = method.invoke(object, arguments);
                            player.sendMessage(Component.text("Invoked Method " + method.getName(), NamedTextColor.YELLOW));
                            player.sendMessage(Component.text("Returned: " + object, NamedTextColor.YELLOW));
                        } catch (Exception e) {
                            player.sendMessage("Error: " + e.getMessage());
                            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
                        }
                        break;
                    }
                }
            }
            ChatChannels.sendDebugMessage(player, Component.text("Done: " + Arrays.toString(query), NamedTextColor.DARK_GREEN));
        });
    }

    @Subcommand("wipetop")
    public CompletionStage<?> wipeTopStats(Player player, DatabasePlayerFuture databasePlayerFuture) {
        return databasePlayerFuture.future().thenAccept(databasePlayer -> {
            wipeTopStats(databasePlayer);
            ChatChannels.sendDebugMessage(player, Component.text("Wiped Top Stats of " + databasePlayer.getName(), NamedTextColor.DARK_GREEN));
        });
    }

    public static void wipeTopStats(DatabasePlayer databasePlayer) {
        DatabasePlayerWaveDefenseStats pveStats = databasePlayer.getPveStats().getWaveDefenseStats();
        wipe(pveStats);
        for (Classes value : Classes.VALUES) {
            DatabaseBasePvEWaveDefense databaseBasePvE = pveStats.getClass(value);
            wipe(databaseBasePvE);
        }
        for (Specializations value : Specializations.VALUES) {
            DatabaseBasePvEWaveDefense databaseBasePvE = pveStats.getSpec(value);
            wipe(databaseBasePvE);
        }
        wipe(pveStats.getNormalStats());
        wipe(pveStats.getHardStats());
        wipe(pveStats.getExtremeStats());
        wipe(pveStats.getEndlessStats());
        DatabaseManager.playerService.update(databasePlayer, PlayersCollections.LIFETIME);
    }

    private static void wipe(DatabasePlayerPvEWaveDefenseDifficultyStats difficultyStats) {
        wipe((WaveDefenseDatabaseStatInformation) difficultyStats);
        for (Classes value : Classes.VALUES) {
            WaveDefenseDatabaseStatInformation databaseBasePvE = difficultyStats.getClass(value);
            wipe(databaseBasePvE);
        }
        for (Specializations value : Specializations.VALUES) {
            WaveDefenseDatabaseStatInformation databaseBasePvE = difficultyStats.getSpec(value);
            wipe(databaseBasePvE);
        }
        difficultyStats.getPlayerCountStats().forEach((integer, databasePlayerPvEPlayerCountStats) -> {
            wipe(databasePlayerPvEPlayerCountStats);
            for (Classes value : Classes.VALUES) {
                WaveDefenseDatabaseStatInformation databaseBasePvE = databasePlayerPvEPlayerCountStats.getClass(value);
                wipe(databaseBasePvE);
            }
            for (Specializations value : Specializations.VALUES) {
                WaveDefenseDatabaseStatInformation databaseBasePvE = databasePlayerPvEPlayerCountStats.getSpec(value);
                wipe(databaseBasePvE);
            }
        });
    }

    private static void wipe(WaveDefenseDatabaseStatInformation statInformation) {
        statInformation.setHighestWaveCleared(0);
        statInformation.setMostDamageInRound(0);
        statInformation.setMostDamageInWave(0);
        statInformation.setFastestGameFinished(0);
    }

}
