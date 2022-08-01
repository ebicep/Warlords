package com.ebicep.warlords.commands.debugcommands.game;

import co.aikar.commands.BaseCommand;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.commands.miscellaneouscommands.ChatCommand.sendDebugMessage;
import static com.ebicep.warlords.util.warlords.Utils.*;

public class GameStartCommand extends BaseCommand {

    @Nullable
    private static GameManager.QueueEntryBuilder buildQueue(@Nonnull List<? extends OfflinePlayer> people, @Nonnull Player player, @Nonnull String[] args) {
        GameMap map = null;
        GameMode category = null;
        EnumSet<GameAddon> addon = EnumSet.of(GameAddon.PRIVATE_GAME);
        //ArrayList<OfflinePlayer> selectedPeople = null;
        ArrayList<OfflinePlayer> selectedPeople = new ArrayList<>(Bukkit.getOnlinePlayers());

        GameMap[] maps = GameMap.values();
        GameMode[] categories = GameMode.values();
        GameAddon[] addons = GameAddon.values();

        boolean isValid = true;
        boolean seenMapOrCategory = false;

        for (String arg : args) {
            int indexOf = arg.indexOf(':');
            if (indexOf < 0) {
                GameMap foundMap = arrayGetItem(maps, e -> e.name().equalsIgnoreCase(arg));
                GameMode foundCategory = arrayGetItem(categories, e -> e.name().equalsIgnoreCase(arg));
                GameAddon foundAddon = arrayGetItem(addons, e -> e.name().equalsIgnoreCase(arg));
                if ((foundMap == null ? 0 : 1) + (foundCategory == null ? 0 : 1) + (foundAddon == null ? 0 : 1) > 1) {
                    sendDebugMessage(player, ChatColor.RED + "Vague option: " + arg, false);
                    isValid = false;
                    if (foundMap != null) {
                        sendDebugMessage(player, ChatColor.RED + "Prepend map: to specify the map with this name", false);
                    }
                    if (foundCategory != null) {
                        sendDebugMessage(player, ChatColor.RED + "Prepend category: to specify the category with this name", false);
                    }
                    if (foundAddon != null) {
                        sendDebugMessage(player, ChatColor.RED + "Prepend addon: to specify the addon with this name", false);
                    }
                } else if (foundMap != null) {
                    map = foundMap;
                    seenMapOrCategory = true;
                } else if (foundCategory != null) {
                    category = foundCategory;
                    seenMapOrCategory = true;
                } else if (foundAddon != null) {
                    if (addon.isEmpty()) {
                        addon = EnumSet.of(foundAddon);
                    } else {
                        addon.add(foundAddon);
                    }
                } else {
                    sendDebugMessage(player, ChatColor.RED + "Invalid addon! " + arg, false);
                    isValid = false;
                }
            } else {
                String argType = arg.substring(0, indexOf);
                String argData = arg.substring(indexOf + 1);
                switch (argType.toLowerCase(Locale.ROOT)) {
                    case "map":
                        GameMap foundMap = arrayGetItem(maps, e -> e.name().equalsIgnoreCase(argData));
                        if (foundMap != null) {
                            map = foundMap;
                        } else if (argData.equalsIgnoreCase("null")) {
                            map = null;
                        } else {
                            sendDebugMessage(player, ChatColor.RED + "Map not found: " + argData, false);
                            isValid = false;
                        }
                        seenMapOrCategory = true;
                        break;
                    case "category":
                        GameMode foundCategory = arrayGetItem(categories, e -> e.name().equalsIgnoreCase(argData));
                        if (foundCategory != null) {
                            category = foundCategory;
                        } else if (argData.equalsIgnoreCase("null")) {
                            category = null;
                        } else {
                            sendDebugMessage(player, ChatColor.RED + "Category not found: " + argData, false);
                            isValid = false;
                        }
                        seenMapOrCategory = true;
                        break;
                    case "addon":
                        GameAddon foundAddon = arrayGetItem(addons, e -> e.name().equalsIgnoreCase(argData));
                        if (foundAddon != null) {
                            if (addon.isEmpty()) {
                                addon = EnumSet.of(foundAddon);
                            } else {
                                addon.add(foundAddon);
                            }
                        } else if (argData.equalsIgnoreCase("null")) {
                            addon = EnumSet.noneOf(GameAddon.class);
                        } else {
                            sendDebugMessage(player, ChatColor.RED + "Addon not found: " + argData, false);
                            isValid = false;
                        }
                        break;
                    case "player":
                    case "players":
                    case "offline-player":
                        if (!player.hasPermission("warlords.game.start.players")) {
                            sendDebugMessage(player, ChatColor.RED + "You do not have permissions to invite players outside your party: " + arg, false);
                            isValid = false;
                            continue;
                        }
                        boolean allowOfflinePlayer = argType.equals("offline-player");
                        OfflinePlayer p;
                        int length = argData.length();
                        if (argData.equals("*")) {
                            if (selectedPeople == null) {
                                selectedPeople = new ArrayList<>();
                            }
                            selectedPeople.addAll(Bukkit.getOnlinePlayers());
                            break;
                        } else if (length <= 16) {
                            p = Bukkit.getPlayer(argData);
                            if (p == null) {
                                sendDebugMessage(player, ChatColor.RED + "Player not online: " + argData + " specify offline-player:<uuid> to target offline players", false);
                                isValid = false;
                            }
                        } else if (length == 32 || length == 36) {
                            try {
                                UUID id = UUID.fromString(length == 32 ? argData.replaceFirst(
                                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                                ) : argData);
                                p = allowOfflinePlayer ? Bukkit.getOfflinePlayer(id) : Bukkit.getPlayer(id);
                            } catch (IllegalArgumentException e) {
                                p = null;
                                sendDebugMessage(player, ChatColor.RED + "Invalid UUID: " + argData, false);
                                isValid = false;
                            }
                        } else {
                            p = null;
                            sendDebugMessage(player, ChatColor.RED + "Invalid name/UUID: " + argData, false);
                            isValid = false;
                        }
                        if (p != null) {
                            if (selectedPeople == null) {
                                selectedPeople = new ArrayList<>();
                            }
                            selectedPeople.add(p);
                        }
                        break;
                    default:
                        sendDebugMessage(player, ChatColor.RED + "Unknown option type: " + argType + " in " + arg, false);
                        isValid = false;
                }
            }
        }
        if (category != null && map != null && !map.getGameModes().contains(category)) {
            sendDebugMessage(player, ChatColor.RED + "map:" + toTitleCase(map) + " is not part of category:" + toTitleCase(category) + ", valid maps: " + Arrays.toString(GameMap.values()), false);
            isValid = false;
        }
        if (category == null && map == null && !seenMapOrCategory) {
            sendDebugMessage(player, ChatColor.RED + "Creating a game with no category and map is unusual, pass category:null or map:null if you really mean this.", false);
            isValid = false;
        }

        for (GameAddon a : addon) {
            if (!a.hasPermission(player)) {
                sendDebugMessage(player, ChatColor.RED + "You do not have the permission to use addon: " + Utils.toTitleCase(a.name()), false);
                isValid = false;
            }
        }

        if (!isValid) {
            return null;
        }

        sendDebugMessage(player, ChatColor.GREEN + "Requesting a game with the following parameters:", false);
        if (category != null) {
            sendDebugMessage(player, ChatColor.GRAY + "- Category: " + ChatColor.RED + toTitleHumanCase(category.name()), false);
        }
        if (map != null) {
            sendDebugMessage(player, ChatColor.GRAY + "- Map: " + ChatColor.RED + toTitleHumanCase(map.name()), false);
        }
        if (!addon.isEmpty()) {
            sendDebugMessage(player, ChatColor.GRAY + "- Game Addons: " + ChatColor.GOLD + addon.stream().map(e -> toTitleHumanCase(e.name())).collect(Collectors.joining(", ")), false);
        }
        if (selectedPeople != null) {
            sendDebugMessage(player, ChatColor.GRAY + "- Players: " + ChatColor.RED + selectedPeople.stream().map(OfflinePlayer::getName).collect(Collectors.joining(", ")), false);
        }
        return Warlords.getGameManager()
                .newEntry(selectedPeople == null ? people : selectedPeople)
                .setGamemode(category)
                .setMap(map)
                .setRequestedGameAddons(addon);
    }

    public static void startGame(Player player, String[] args, boolean excludeStarter) {
        Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(player.getUniqueId());

        List<Player> people;
        if (partyPlayerPair != null) {
            if (!partyPlayerPair.getA().getPartyLeader().getUUID().equals(player.getUniqueId())) {
                sendDebugMessage(player, ChatColor.RED + "You are not the party leader", false);
                return;
            } else if (!partyPlayerPair.getA().allOnlineAndNoAFKs()) {
                sendDebugMessage(player, ChatColor.RED + "All party members must be online or not afk", false);
                return;
            }
            people = partyPlayerPair.getA().getAllPartyPeoplePlayerOnline();
            if (excludeStarter) {
                people.removeIf(p -> p.getUniqueId().equals(player.getUniqueId()));
            }
        } else {
            people = Collections.singletonList(player);
        }


        final GameManager.QueueEntryBuilder queueEntry = buildQueue(people, player, args);
        if (queueEntry == null) {
            return;
        }

        Pair<GameManager.QueueResult, Game> result = queueEntry
                .setPriority(-10)
                .queueNow();
        Game game = result.getB();
        if (game == null) {
            sendDebugMessage(player, ChatColor.RED + "Engine failed to find a game server suitable for your request:", false);
            sendDebugMessage(player, ChatColor.GRAY + result.getA().toString(), false);
        } else {
            sendDebugMessage(player,
                    ChatColor.GREEN + "Engine " + (result.getA() == GameManager.QueueResult.READY_NEW ? "initiated" : "found") +
                            " a game with the following parameters:",
                    false);
            sendDebugMessage(player, ChatColor.GRAY + "- Gamemode: " + ChatColor.RED + Utils.toTitleHumanCase(game.getGameMode()), false);
            sendDebugMessage(player, ChatColor.GRAY + "- Map: " + ChatColor.RED + game.getMap().getMapName(), false);
            sendDebugMessage(player, ChatColor.GRAY + "- Game Addons: " + ChatColor.GOLD + game.getAddons().stream().map(e -> toTitleHumanCase(e.name())).collect(Collectors.joining(", ")), false);
            sendDebugMessage(player, ChatColor.GRAY + "- Min players: " + ChatColor.RED + game.getMinPlayers(), false);
            sendDebugMessage(player, ChatColor.GRAY + "- Max players: " + ChatColor.RED + game.getMaxPlayers(), false);
            sendDebugMessage(player, ChatColor.GRAY + "- Open for public: " + ChatColor.RED + game.acceptsPeople(), false);
            sendDebugMessage(player, ChatColor.GRAY + "- Game ID: " + ChatColor.RED + game.getGameId(), false);
        }
    }
}
