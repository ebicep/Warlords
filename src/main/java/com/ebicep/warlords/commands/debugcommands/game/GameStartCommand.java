package com.ebicep.warlords.commands.debugcommands.game;

import co.aikar.commands.BaseCommand;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.misc.AdminCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.chat.ChatChannels.sendDebugMessage;
import static com.ebicep.warlords.util.warlords.Utils.toTitleHumanCase;

public class GameStartCommand extends BaseCommand {

    public static void startGamePvE(Player player, GameMap map) {
        if (Warlords.SENT_HALF_HOUR_REMINDER.get() && !AdminCommand.DISABLE_RESTART_CHECK) {
            player.sendMessage(ChatColor.RED + "You cannot start a new game 30 minutes before the server restarts.");
            return;
        }
        startGame(player, false, queueEntryBuilder -> {
            queueEntryBuilder
                    .setGameMode(GameMode.WAVE_DEFENSE)
                    .setMap(map)
                    .setPriority(0)
                    .setRequestedGameAddons(GameAddon.PRIVATE_GAME, GameAddon.CUSTOM_GAME)
                    .setOnResult((result, game) -> {
                        if (game == null) {
                            player.sendMessage(ChatColor.RED + "Failed to join/create a game: " + result);
                        }
                    });
        });
    }

    public static void startGame(
            Player player,
            boolean excludeStarter,
            Consumer<GameManager.QueueEntryBuilder> entryEditor
    ) {
        if (GameManager.gameStartingDisabled) {
            player.sendMessage(ChatColor.RED + "Games are currently disabled.");
            return;
        }
        List<Player> people;
        UUID uuid = player.getUniqueId();
        //check if player is in a party, they must be leader to join
        Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(uuid);
        if (partyPlayerPair != null) {
            if (!partyPlayerPair.getA().getPartyLeader().getUUID().equals(uuid)) {
                sendDebugMessage(player, ChatColor.RED + "You are not the party leader", false);
                return;
            } else if (!partyPlayerPair.getA().allOnlineAndNoAFKs()) {
                sendDebugMessage(player, ChatColor.RED + "All party members must be online or not afk", false);
                return;
            }
            people = partyPlayerPair.getA().getAllPartyPeoplePlayerOnline();
            if (excludeStarter) {
                people.removeIf(p -> p.getUniqueId().equals(uuid));
            }
        } else {
            people = Collections.singletonList(player);
        }

        GameManager.QueueEntryBuilder entryBuilder = Warlords.getGameManager()
                .newEntry(people);
        entryEditor.accept(entryBuilder);
        entryBuilder.queueNow();
    }

    public static void startGamePublic(Player player) {
        startGame(player, false, queueEntryBuilder -> {
            queueEntryBuilder
                    .setGameMode(GameMode.CAPTURE_THE_FLAG)
                    .setExpiresTime(System.currentTimeMillis() + 60 * 1000)
                    .setPriority(0)
                    .setOnResult((result, game) -> {
                        if (game == null) {
                            player.sendMessage(ChatColor.RED + "Failed to join/create a game: " + result);
                        }
                    });
        });
    }

    public static void startGameFromDebugMenu(Player player, boolean excludeStarter, Consumer<GameManager.QueueEntryBuilder> entryEditor) {
        startGame(player, excludeStarter, entryEditor.andThen(queueEntryBuilder -> queueEntryBuilder.setOnResult((result, game) -> {
            if (game == null) {
                sendDebugMessage(player, ChatColor.RED + "Engine failed to find a game server suitable for your request:", false);
                sendDebugMessage(player, ChatColor.GRAY + result.toString(), false);
            } else {
                sendDebugMessage(player,
                        ChatColor.GREEN + "Engine " + (result == GameManager.QueueResult.READY_NEW ? "initiated" : "found") +
                                " a game with the following parameters:",
                        false
                );
                sendDebugMessage(player, ChatColor.GRAY + "- Gamemode: " + ChatColor.RED + Utils.toTitleHumanCase(game.getGameMode()), false);
                sendDebugMessage(player, ChatColor.GRAY + "- Map: " + ChatColor.RED + game.getMap().getMapName(), false);
                sendDebugMessage(player,
                        ChatColor.GRAY + "- Game Addons: " + ChatColor.GOLD + game.getAddons()
                                .stream()
                                .map(e -> toTitleHumanCase(e.name()))
                                .collect(Collectors.joining(", ")),
                        false
                );
                sendDebugMessage(player, ChatColor.GRAY + "- Min players: " + ChatColor.RED + game.getMinPlayers(), false);
                sendDebugMessage(player, ChatColor.GRAY + "- Max players: " + ChatColor.RED + game.getMaxPlayers(), false);
                sendDebugMessage(player, ChatColor.GRAY + "- Open for public: " + ChatColor.RED + game.acceptsPeople(), false);
                sendDebugMessage(player, ChatColor.GRAY + "- Game ID: " + ChatColor.RED + game.getGameId(), false);
            }
        })));
    }
}
