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
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.chat.ChatChannels.sendDebugMessage;

public class GameStartCommand {

    public static void startGamePvE(Player player, GameMode gameMode, Consumer<GameManager.QueueEntryBuilder> entryEditor) {
        if (Warlords.SENT_HALF_HOUR_REMINDER.get() && !AdminCommand.DISABLE_RESTART_CHECK) {
            player.sendMessage(Component.text("You cannot start a new game 30 minutes before the server restarts.", NamedTextColor.RED));
            return;
        }
        startGame(player, false, entryEditor.andThen(queueEntryBuilder -> {
                    queueEntryBuilder
                            .setGameMode(gameMode)
                            .setPriority(0)
                            .setOnResult((result, game) -> {
                                if (game == null) {
                                    player.sendMessage(Component.text("Failed to join/create a game: " + result, NamedTextColor.RED));
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
            player.sendMessage(Component.text("Games are currently disabled.", NamedTextColor.RED));
            return;
        }
        List<Player> people;
        UUID uuid = player.getUniqueId();
        //check if player is in a party, they must be leader to join
        Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(uuid);
        if (partyPlayerPair != null) {
            Party party = partyPlayerPair.getA();
            if (!party.getPartyLeader().getUUID().equals(uuid)) {
                player.sendMessage(Component.text("You are not the party leader", NamedTextColor.RED));
                return;
            } else if (!party.allOnlineAndNoAFKs()) {
                player.sendMessage(Component.text("All party members must be online or not afk", NamedTextColor.RED));
                return;
            }
            for (PartyPlayer partyPlayer : party.getPartyPlayers()) {
                WarlordsEntity warlordsEntity = Warlords.getPlayer(partyPlayer.getUUID());
                if (warlordsEntity != null && warlordsEntity.getGame().getGameMode() != GameMode.LOBBY) {
                    player.sendMessage(Component.text("You cannot start a game with a player who is already in a game.", NamedTextColor.RED));
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
            if (people.size() == 1 && !Permissions.isAdmin(player)) {
                DatabaseManager.getPlayer(people.get(0).getUniqueId(), databasePlayer -> {
                    if (databasePlayer.getPlays() <= 10 && !databasePlayer.getPveStats().isCompletedTutorial()) {
                        entryBuilder
                                .setGameMode(GameMode.TUTORIAL)
                                .setMap(GameMap.TUTORIAL_MAP)
                                .setOnResult((result, game) -> {
                                    if (game == null) {
                                        people.get(0).sendMessage(Component.text("Unable to find a valid tutorial map. Report this.", NamedTextColor.RED));
                                    }
                                });
                    }
                });
            }
        }

        Pair<GameManager.QueueResult, Game> resultGamePair = entryBuilder.queueNow();
        //entryBuilder.getOnResult().accept(resultGamePair.getA(), resultGamePair.getB());
    }

    public static void startGamePvERaid(Player player, Consumer<GameManager.QueueEntryBuilder> entryEditor) {
        if (Warlords.SENT_HALF_HOUR_REMINDER.get() && !AdminCommand.DISABLE_RESTART_CHECK) {
            player.sendMessage(Component.text("You cannot start a new game 30 minutes before the server restarts.", NamedTextColor.RED));
            return;
        }
        startGame(player, false, entryEditor.andThen(queueEntryBuilder -> {
                    queueEntryBuilder
                            .setGameMode(GameMode.RAID)
                            .setPriority(0)
                            .setOnResult((result, game) -> {
                                if (game == null) {
                                    player.sendMessage(Component.text("Failed to join/create a game: " + result, NamedTextColor.RED));
                                }
                            });
                })
        );

    }

    public static void startGamePvEEvent(Player player, Consumer<GameManager.QueueEntryBuilder> entryEditor) {
        if (Warlords.SENT_HALF_HOUR_REMINDER.get() && !AdminCommand.DISABLE_RESTART_CHECK) {
            player.sendMessage(Component.text("You cannot start a new game 30 minutes before the server restarts.", NamedTextColor.RED));
            return;
        }
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent == null || currentGameEvent.getEndDate().isBefore(Instant.now())) {
            player.sendMessage(Component.text("The event is over!", NamedTextColor.RED));
            return;
        }
        startGame(player, false, entryEditor.andThen(queueEntryBuilder -> {
                    queueEntryBuilder
                            .setGameMode(GameMode.EVENT_WAVE_DEFENSE)
                            .setPriority(0)
                            .setOnResult((result, game) -> {
                                if (game == null) {
                                    player.sendMessage(Component.text("Failed to join/create a game: " + result, NamedTextColor.RED));
                                }
                            });
                })
        );
    }

    public static void startGamePublic(Player player, GameMode gameMode) {
        startGame(player, false, queueEntryBuilder -> {
            queueEntryBuilder
                    .setGameMode(gameMode)
                    .setExpiresTime(System.currentTimeMillis() + 60 * 1000)
                    .setPriority(0)
                    .setOnResult((result, game) -> {
                        if (game == null) {
                            player.sendMessage(Component.text("Failed to join/create a game: " + result, NamedTextColor.RED));
                        }
                    });
        });
    }

    public static void startGameFromDebugMenu(Player player, boolean excludeStarter, Consumer<GameManager.QueueEntryBuilder> entryEditor) {
        startGame(player, excludeStarter, entryEditor.andThen(queueEntryBuilder -> queueEntryBuilder.setOnResult((result, game) -> {
            if (game == null) {
                sendDebugMessage(player, Component.text("Engine failed to find a game server suitable for your request:", NamedTextColor.RED));
                sendDebugMessage(player, Component.text(result.toString(), NamedTextColor.GRAY));
            } else {
                sendDebugMessage(player,
                        Component.text("Engine " + (result == GameManager.QueueResult.READY_NEW ? "initiated" : "found") + " a game with the following parameters:",
                                NamedTextColor.GREEN
                        )
                );
                sendDebugMessage(player, Component.empty()
                                                  .append(Component.text("- Gamemode: ", NamedTextColor.GRAY))
                                                  .append(Component.text(StringUtils.toTitleHumanCase(game.getGameMode()), NamedTextColor.RED)));
                sendDebugMessage(player, Component.empty()
                                                  .append(Component.text("- Map: ", NamedTextColor.GRAY))
                                                  .append(Component.text(StringUtils.toTitleHumanCase(game.getMap().getMapName()), NamedTextColor.RED)));
                sendDebugMessage(player, Component.empty()
                                                  .append(Component.text("- Game Addons: ", NamedTextColor.GRAY))
                                                  .append(Component.text(game.getAddons()
                                                                             .stream()
                                                                             .map(e -> StringUtils.toTitleHumanCase(e.name()))
                                                                             .collect(Collectors.joining(", ")), NamedTextColor.GOLD))
                );
                sendDebugMessage(player, Component.empty()
                                                  .append(Component.text("- Min players: ", NamedTextColor.GRAY))
                                                  .append(Component.text(game.getMinPlayers(), NamedTextColor.RED))
                );

                sendDebugMessage(player, Component.empty()
                                                  .append(Component.text("- Max players: ", NamedTextColor.GRAY))
                                                  .append(Component.text(game.getMaxPlayers(), NamedTextColor.RED))
                );

                sendDebugMessage(player, Component.empty()
                                                  .append(Component.text("- Open for public: ", NamedTextColor.GRAY))
                                                  .append(Component.text(game.acceptsPeople(), NamedTextColor.RED))
                );

                sendDebugMessage(player, Component.empty()
                                                  .append(Component.text("- Game ID: ", NamedTextColor.GRAY))
                                                  .append(Component.text(game.getGameId().toString(), NamedTextColor.RED))
                );
            }
        })));
    }
}
