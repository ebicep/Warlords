package com.ebicep.warlords.commands.debugcommands.game;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.misc.AdminCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.game.Game;
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

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.chat.ChatChannels.sendDebugMessage;
import static com.ebicep.warlords.util.warlords.Utils.toTitleHumanCase;

public class GameStartCommand {

    public static void startGamePvE(Player player, GameMode gameMode, Consumer<GameManager.QueueEntryBuilder> entryEditor) {
        if (Warlords.SENT_HALF_HOUR_REMINDER.get() && !AdminCommand.DISABLE_RESTART_CHECK) {
            player.sendMessage(ChatColor.RED + "You cannot start a new game 30 minutes before the server restarts.");
            return;
        }
        startGame(player, false, entryEditor.andThen(queueEntryBuilder -> {
                    queueEntryBuilder
                            .setGameMode(gameMode)
                            .setPriority(0)
                            .setOnResult((result, game) -> {
                                if (game == null) {
                                    player.sendMessage(ChatColor.RED + "Failed to join/create a game: " + result);
                                }
                            });
                })
        );
    }

    public static void startGamePvERaid(Player player, Consumer<GameManager.QueueEntryBuilder> entryEditor) {
        if (Warlords.SENT_HALF_HOUR_REMINDER.get() && !AdminCommand.DISABLE_RESTART_CHECK) {
            player.sendMessage(ChatColor.RED + "You cannot start a new game 30 minutes before the server restarts.");
            return;
        }
        startGame(player, false, entryEditor.andThen(queueEntryBuilder -> {
                    queueEntryBuilder
                            .setGameMode(GameMode.RAID)
                            .setPriority(0)
                            .setOnResult((result, game) -> {
                                if (game == null) {
                                    player.sendMessage(ChatColor.RED + "Failed to join/create a game: " + result);
                                }
                            });
                })
        );

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
            Party party = partyPlayerPair.getA();
            if (!party.getPartyLeader().getUUID().equals(uuid)) {
                player.sendMessage(ChatColor.RED + "You are not the party leader");
                return;
            } else if (!party.allOnlineAndNoAFKs()) {
                player.sendMessage(ChatColor.RED + "All party members must be online or not afk");
                return;
            }
            for (PartyPlayer partyPlayer : party.getPartyPlayers()) {
                if (Warlords.getPlayer(partyPlayer.getUUID()) != null) {
                    player.sendMessage(ChatColor.RED + "You cannot start a game with a player who is already in a game.");
                    return;
                }
            }
            people = party.getAllPartyPeoplePlayerOnline();
            if (excludeStarter) {
                people.removeIf(p -> p.getUniqueId().equals(uuid));
            }
        } else {
            people = Collections.singletonList(player);
        }

        GameManager.QueueEntryBuilder entryBuilder = Warlords.getGameManager().newEntry(people);
        entryEditor.accept(entryBuilder);

        if (GameMode.isPvE(entryBuilder.getGameMode())) {
            if (people.size() == 1) {
                DatabaseManager.getPlayer(people.get(0).getUniqueId(), databasePlayer -> {
                    if (databasePlayer.getPlays() <= 10 && !databasePlayer.getPveStats().isCompletedTutorial()) {
                        entryBuilder
                                .setGameMode(GameMode.TUTORIAL)
                                .setMap(GameMap.TUTORIAL_MAP)
                                .setOnResult((result, game) -> {
                                    if (game == null) {
                                        people.get(0).sendMessage(ChatColor.RED + "Unable to find a valid tutorial map. Report this.");
                                    }
                                });
                    }
                });
            }
        }

        Pair<GameManager.QueueResult, Game> resultGamePair = entryBuilder.queueNow();
        entryBuilder.getOnResult().accept(resultGamePair.getA(), resultGamePair.getB());
    }

    public static void startGamePvEEvent(Player player, Consumer<GameManager.QueueEntryBuilder> entryEditor) {
        if (Warlords.SENT_HALF_HOUR_REMINDER.get() && !AdminCommand.DISABLE_RESTART_CHECK) {
            player.sendMessage(ChatColor.RED + "You cannot start a new game 30 minutes before the server restarts.");
            return;
        }
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent == null || currentGameEvent.getEndDate().isBefore(Instant.now())) {
            player.sendMessage(ChatColor.RED + "The event is over!");
            return;
        }
        startGame(player, false, entryEditor.andThen(queueEntryBuilder -> {
                    queueEntryBuilder
                            .setGameMode(GameMode.EVENT_WAVE_DEFENSE)
                            .setPriority(0)
                            .setOnResult((result, game) -> {
                                if (game == null) {
                                    player.sendMessage(ChatColor.RED + "Failed to join/create a game: " + result);
                                }
                            });
                })
        );
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
                sendDebugMessage(player, ChatColor.RED + "Engine failed to find a game server suitable for your request:");
                sendDebugMessage(player, ChatColor.GRAY + result.toString());
            } else {
                sendDebugMessage(player,
                        ChatColor.GREEN + "Engine " + (result == GameManager.QueueResult.READY_NEW ? "initiated" : "found") +
                                " a game with the following parameters:"
                );
                sendDebugMessage(player, ChatColor.GRAY + "- Gamemode: " + ChatColor.RED + Utils.toTitleHumanCase(game.getGameMode()));
                sendDebugMessage(player, ChatColor.GRAY + "- Map: " + ChatColor.RED + game.getMap().getMapName());
                sendDebugMessage(player,
                        ChatColor.GRAY + "- Game Addons: " + ChatColor.GOLD + game.getAddons()
                                                                                  .stream()
                                                                                  .map(e -> toTitleHumanCase(e.name()))
                                                                                  .collect(Collectors.joining(", "))
                );
                sendDebugMessage(player, ChatColor.GRAY + "- Min players: " + ChatColor.RED + game.getMinPlayers());
                sendDebugMessage(player, ChatColor.GRAY + "- Max players: " + ChatColor.RED + game.getMaxPlayers());
                sendDebugMessage(player, ChatColor.GRAY + "- Open for public: " + ChatColor.RED + game.acceptsPeople());
                sendDebugMessage(player, ChatColor.GRAY + "- Game ID: " + ChatColor.RED + game.getGameId());
            }
        })));
    }
}
