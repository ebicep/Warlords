package com.ebicep.warlords.commands.debugcommands.game;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.java.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.chat.ChatChannels.sendDebugMessage;

@CommandAlias("compgame")
@CommandPermission("group.administrator")
public class CompGameCommand extends BaseCommand {

    private static final List<GameMap> MAP_ROTATION = new ArrayList<>(List.of(GameMap.RIFT, GameMap.CROSSFIRE, GameMap.APERTURE));
    private static int currentIndex = 0;

    @Subcommand("start")
    @Description("Starts new comp ctf game with auto map rotation")
    public void start(Player player) {
        GameMap selectedGameMap = MAP_ROTATION.get(currentIndex);
        GameStartCommand.startGame(player, false, queueEntryBuilder -> {
            queueEntryBuilder
                    .setMap(selectedGameMap)
                    .setGameMode(GameMode.CAPTURE_THE_FLAG)
                    .setRequestedGameAddons(GameAddon.PRIVATE_GAME, GameAddon.FREEZE_GAME)
                    .setOnResult((result, game) -> {
                        if (game == null) {
                            sendDebugMessage(player, Component.text("Engine failed to find a game server suitable for your request:", NamedTextColor.RED));
                            sendDebugMessage(player, Component.text(result.toString(), NamedTextColor.GRAY));
                            return;
                        }
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

                        nextMap();

                    });
        });
    }

    private void nextMap() {
        currentIndex = (currentIndex + 1) % MAP_ROTATION.size();
    }

    @Subcommand("currentmap")
    @Description("Prints current comp ctf map")
    public void currentMap(CommandIssuer issuer) {
        ChatChannels.sendDebugMessage(issuer, getRotationMessage());
    }

    private Component getRotationMessage() {
        GameMap current = MAP_ROTATION.get(currentIndex);
        TextComponent.Builder component = Component.text().color(NamedTextColor.DARK_GRAY);
        for (int i = 0; i < MAP_ROTATION.size(); i++) {
            GameMap gameMap = MAP_ROTATION.get(i);
            component.append(Component.text(gameMap.getMapName(), gameMap == current ? NamedTextColor.GREEN : NamedTextColor.GRAY));
            if (i != MAP_ROTATION.size() - 1) {
                component.append(Component.text(", "));
            }
        }
        return component.build();
    }

    @Subcommand("setrotationindex")
    @Description("Sets the current map index")
    public void setRotationIndex(CommandIssuer issuer, int index) {
        currentIndex = index;
        ChatChannels.sendDebugMessage(issuer, Component.text("Set current map index to " + index, NamedTextColor.GREEN));
        ChatChannels.sendDebugMessage(issuer, getRotationMessage());
    }

    @Subcommand("setmap")
    @Description("Sets map at given index")
    public void setRotationIndex(CommandIssuer issuer, GameMap gameMap, int index) {
        if (index < 0 || index > MAP_ROTATION.size()) {
            ChatChannels.sendDebugMessage(issuer, Component.text("Index out of bounds", NamedTextColor.RED));
            return;
        }
        if (MAP_ROTATION.contains(gameMap)) {
            ChatChannels.sendDebugMessage(issuer, Component.text("Map already in rotation", NamedTextColor.RED));
            return;
        }
        if (index == MAP_ROTATION.size()) {
            MAP_ROTATION.add(gameMap);
            ChatChannels.sendDebugMessage(issuer, Component.text("Added map " + gameMap.getMapName() + " to rotation", NamedTextColor.GREEN));
            ChatChannels.sendDebugMessage(issuer, getRotationMessage());
        } else {
            MAP_ROTATION.set(index, gameMap);
            ChatChannels.sendDebugMessage(issuer, Component.text("Set map at index " + index + " to " + gameMap.getMapName(), NamedTextColor.GREEN));
            ChatChannels.sendDebugMessage(issuer, getRotationMessage());
        }
    }

    @Subcommand("removemap")
    @Description("Removes map from rotation")
    public void removeMap(CommandIssuer issuer, int index) {
        if (index < 0 || index >= MAP_ROTATION.size()) {
            ChatChannels.sendDebugMessage(issuer, Component.text("Index out of bounds", NamedTextColor.RED));
            return;
        }
        GameMap gameMap = MAP_ROTATION.get(index);
        if (!MAP_ROTATION.contains(gameMap)) {
            ChatChannels.sendDebugMessage(issuer, Component.text("Map not in rotation", NamedTextColor.RED));
            return;
        }
        MAP_ROTATION.remove(gameMap);
        ChatChannels.sendDebugMessage(issuer, Component.text("Removed map " + gameMap.getMapName() + " from rotation", NamedTextColor.GREEN));
        ChatChannels.sendDebugMessage(issuer, getRotationMessage());
    }


}
