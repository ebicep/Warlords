package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ebicep.warlords.util.warlords.Utils.*;

public class GameStartCommand implements TabExecutor {

    @Nullable
    private GameManager.QueueEntryBuilder buildQueue(@Nonnull List<? extends OfflinePlayer> people, @Nonnull CommandSender sender, @Nonnull String[] args) {
        GameMap map = null;
        GameMode category = null;
        EnumSet<GameAddon> addon = EnumSet.of(GameAddon.PRIVATE_GAME);
        ArrayList<OfflinePlayer> selectedPeople = null;

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
                    sender.sendMessage(ChatColor.RED + "Vague option: " + arg);
                    isValid = false;
                    if (foundMap != null) {
                        sender.sendMessage("Prepend map: to specify the map with this name");
                    }
                    if (foundCategory != null) {
                        sender.sendMessage("Prepend category: to specify the category with this name");
                    }
                    if (foundAddon != null) {
                        sender.sendMessage("Prepend addon: to specify the addon with this name");
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
                    sender.sendMessage(ChatColor.RED + "Invalid addon! " + arg);
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
                            sender.sendMessage(ChatColor.RED + "Map not found: " + argData);
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
                            sender.sendMessage(ChatColor.RED + "Category not found: " + argData);
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
                            sender.sendMessage(ChatColor.RED + "Addon not found: " + argData);
                            isValid = false;
                        }
                        break;
                    case "player":
                    case "players":
                    case "offline-player":
                        if (!sender.hasPermission("warlords.game.start.players")) {
                            sender.sendMessage("You do not have permissions to invite players outside your party: " + arg);
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
                                sender.sendMessage(ChatColor.RED + "Player not online: " + argData + " specify offline-player:<uuid> to target offline players");
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
                                sender.sendMessage(ChatColor.RED + "Invalid UUID: " + argData);
                                isValid = false;
                            }
                        } else {
                            p = null;
                            sender.sendMessage(ChatColor.RED + "Invalid name/UUID: " + argData);
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
                        sender.sendMessage(ChatColor.RED + "Unknown option type: " + argType + " in " + arg);
                        isValid = false;
                }
            }
        }
        if (category != null && map != null && !map.getCategories().contains(category)) {
            sender.sendMessage(ChatColor.RED + "map:" + toTitleCase(map) + " is not part of category:" + toTitleCase(category) + ", valid maps: " + Arrays.toString(GameMap.values()));
            isValid = false;
        }
        if (category == null && map == null && !seenMapOrCategory) {
            sender.sendMessage(ChatColor.RED + "Creating a game with no category and map is unusual, pass category:null or map:null if you really mean this.");
            isValid = false;
        }

        for (GameAddon a : addon) {
            if (!a.hasPermission(sender)) {
                sender.sendMessage(ChatColor.RED + "You do not have the permission to use addon: " + Utils.toTitleCase(a.name()));
                isValid = false;
            }
        }

        if (!isValid) {
            return null;
        }

        sender.sendMessage(ChatColor.RED + "DEV:" + ChatColor.GRAY + " Requesting a game with the following parameters:");
        if (category != null) {
            sender.sendMessage(ChatColor.GRAY + "- Category: " + ChatColor.RED + toTitleHumanCase(category.name()));
        }
        if (map != null) {
            sender.sendMessage(ChatColor.GRAY + "- Map: " + ChatColor.RED + toTitleHumanCase(map.name()));
        }
        if (!addon.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "- Game Addons: " + ChatColor.GOLD + addon.stream().map(e -> toTitleHumanCase(e.name())).collect(Collectors.joining(", ")));
        }
        if (selectedPeople != null) {
            sender.sendMessage(ChatColor.GRAY + "- Players: " + ChatColor.RED + selectedPeople.stream().map(OfflinePlayer::getName).collect(Collectors.joining(", ")));
        }
        return Warlords.getGameManager()
                .newEntry(selectedPeople == null ? people : selectedPeople)
                .setCategory(category)
                .setMap(map)
                .setRequestedGameAddons(addon);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Optional<Party> party = Warlords.partyManager.getPartyFromAny(((Player) sender).getUniqueId());
        
        List<Player> people = party
                .map(Party::getAllPartyPeoplePlayerOnline)
                .orElseGet(() -> sender instanceof Player ? Collections.singletonList((Player)sender) : new ArrayList<>(Bukkit.getOnlinePlayers()));
        if (party.isPresent()) {
            if (!party.get().getPartyLeader().getUuid().equals(((Player) sender).getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are not the party leader");
                return true;
            } else if (!party.get().allOnlineAndNoAFKs()) {
                sender.sendMessage(ChatColor.RED + "All party members must be online or not afk");
                return true;
            }
        }
        final GameManager.QueueEntryBuilder queueEntry = this.buildQueue(people, sender, args);
        if (queueEntry == null) {
            return true;
        }

        Pair<GameManager.QueueResult, Game> result = queueEntry
                .setPriority(-10)
                .queueNow();
        Game game = result.getB();
        if (game == null) {
            sender.sendMessage(ChatColor.RED + "DEV:" + ChatColor.GRAY + " Engine failed to find a game server suiteable for your request:");
            sender.sendMessage(ChatColor.GRAY + result.getA().toString());
        } else {
            sender.sendMessage(
                    ChatColor.RED + "DEV:" +
                            ChatColor.GRAY + " Engine " + (result.getA() == GameManager.QueueResult.READY_NEW ? "initiated" : "found") +
                            " a game with the following parameters:"
            );
            sender.sendMessage(ChatColor.GRAY + "- Category: " + ChatColor.RED + Utils.toTitleHumanCase(game.getGameMode()));
            sender.sendMessage(ChatColor.GRAY + "- Map: " + ChatColor.RED + toTitleHumanCase(game.getMap()));
            sender.sendMessage(ChatColor.GRAY + "- Game Addons: " + ChatColor.GOLD + game.getAddons().stream().map(e -> toTitleHumanCase(e.name())).collect(Collectors.joining(", ")));
            sender.sendMessage(ChatColor.GRAY + "- Min players: " + ChatColor.RED + game.getMinPlayers());
            sender.sendMessage(ChatColor.GRAY + "- Max players: " + ChatColor.RED + game.getMaxPlayers());
            sender.sendMessage(ChatColor.GRAY + "- Open for public: " + ChatColor.RED + game.acceptsPeople());
            sender.sendMessage(ChatColor.GRAY + "- Game ID: " + ChatColor.RED + game.getGameId());
        }
        return true;
    }

    public Stream<String> prefixedEnum(Enum<?>[] list, String prefix) {
        return Stream.concat(
                Stream.of(prefix + ":NULL"),
                Stream.concat(
                        Stream.of(list).map(e -> toTitleCase(e.name())),
                        Stream.of(list).map(e -> prefix + ":" + toTitleCase(e.name()))
                )
        );
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return Stream.of(
                        prefixedEnum(GameMap.values(), "map"),
                        prefixedEnum(GameAddon.values(), "addon"),
                        prefixedEnum(GameMode.values(), "category"),
                        Bukkit.getOnlinePlayers().stream().map((Player e) -> "player:" + e.getName()),
                        Bukkit.getOnlinePlayers().stream().map((Player e) -> "player:" + e.getUniqueId()),
                        Bukkit.getOnlinePlayers().stream().map((Player e) -> "offline-player:" + e.getUniqueId()),
                        Stream.of("players:*")
                ).flatMap(Function.identity())
                .filter(e -> startsWithIgnoreCase(e, args[args.length - 1]))
                .collect(Collectors.toList());
    }

    public void register(Warlords instance) {
        instance.getCommand("start").setExecutor(this);
        instance.getCommand("start").setTabCompleter(this);
    }
}
