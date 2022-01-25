package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.*;
import com.ebicep.warlords.party.Party;
import static com.ebicep.warlords.util.Utils.arrayGetItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;

public class GameStartCommand implements TabExecutor {

    @Nullable
    private GameManager.QueueEntryBuilder buildQueue(@Nonnull List<? extends OfflinePlayer> people, @Nonnull CommandSender sender, @Nonnull String[] args) {
        GameMap map = null;
        MapCategory category = null;
        EnumSet<GameAddon> addon = EnumSet.of(GameAddon.PRIVATE_GAME);
        List<OfflinePlayer> selectedPeople = null;

        GameMap[] maps = GameMap.values();
        MapCategory[] categories = MapCategory.values();
        GameAddon[] addons = GameAddon.values();

        boolean isValid = true;
        boolean seenMapOrCategory = false;

        for (String arg : args) {
            int indexOf = arg.indexOf(':');
            if (indexOf < 0) {
                GameMap foundMap = arrayGetItem(maps, e -> e.name().equalsIgnoreCase(arg));
                MapCategory foundCategory = arrayGetItem(categories, e -> e.name().equalsIgnoreCase(arg));
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
                    if (addon == null) {
                        addon = EnumSet.of(foundAddon);
                    } else {
                        addon.add(foundAddon);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Not sure what your mean by " + arg);
                    isValid = false;
                }
            } else {
                String argType = arg.substring(0, indexOf);
                String argData = arg.substring(indexOf + 1);
                switch (argType) {
                    case "map":
                        GameMap foundMap = arrayGetItem(maps, e -> e.name().equalsIgnoreCase(argData));
                        if (foundMap != null) {
                            map = foundMap;
                        } else if (argData.equals("null")) {
                            map = null;
                        } else {
                            sender.sendMessage(ChatColor.RED + "Map not found: " + argData);
                            isValid = false;
                        }
                        seenMapOrCategory = true;
                        break;
                    case "category":
                        MapCategory foundCategory = arrayGetItem(categories, e -> e.name().equalsIgnoreCase(argData));
                        if (foundCategory != null) {
                            category = foundCategory;
                        } else if (argData.equals("null")) {
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
                            if (addon == null) {
                                addon = EnumSet.of(foundAddon);
                            } else {
                                addon.add(foundAddon);
                            }
                        } else if (argData.equals("null")) {
                            addon = EnumSet.noneOf(GameAddon.class);
                        } else {
                            sender.sendMessage(ChatColor.RED + "Addon not found: " + argData);
                            isValid = false;
                        }
                        break;
                    case "player":
                    case "offline-player":
                        boolean allowOfflinePlayer = argType.equals("offline-player");
                        OfflinePlayer p;
                        int length = argData.length();
                        if (length <= 16) {
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
            sender.sendMessage(ChatColor.RED + "map:" + map + " is not part of category:" + category + ", valid maps: " + Arrays.toString(GameMap.values()));
            isValid = false;
        }
        if (category == null && map == null && !seenMapOrCategory) {
            sender.sendMessage(ChatColor.RED + "Creating a game with no category and map is unusual, pass category:null or map:null if you really mean this");
        }
        for (GameAddon a : addon) {
            if (!a.hasPermission(sender)) {
                sender.sendMessage("You do not have the permission to use addon:" + a);
                isValid = false;
            }
        }
        if (!isValid) {
            return null;
        }
        sender.sendMessage("Asking the system for a game with the following parameters");
        sender.sendMessage("Map category: " + category);
        sender.sendMessage("Map: " + map);
        sender.sendMessage("Requested game addons: " + addon);
        sender.sendMessage("People: " + (selectedPeople == null ? people : selectedPeople));
        return Warlords.getGameManager()
                .newEntry(selectedPeople == null ? people : selectedPeople)
                .setCategory(category)
                .setMap(map)
                .setRequestedGameAddons(addon);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("warlords.game.start")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        Optional<Party> party = Warlords.partyManager.getPartyFromAny(((Player) sender).getUniqueId());
        List<Player> people = party.map(value -> value.getAllPartyPeoplePlayerOnline()).orElseGet(() -> new ArrayList<>(Bukkit.getOnlinePlayers()));
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

        GameManager.QueueResult result = queueEntry
                .setPriority(-10)
                .queueNow();
        if (!result.isSuccess()) {
            sender.sendMessage(ChatColor.RED + "Failed to join game: " + result.toString());
        }
        return true;
    }

    public Stream<String> prefixedEnum(Enum<?>[] list, String prefix) {
        return Stream.concat(
                Stream.of(prefix + ":null"),
                Stream.concat(
                        Stream.of(GameMap.values()).map(e -> e.name().toLowerCase(Locale.ROOT)),
                        Stream.of(GameMap.values()).map(e -> prefix + ":" + e.name().toLowerCase(Locale.ROOT))
                )
        );
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return Stream.of(
                prefixedEnum(GameMap.values(), "map"),
                prefixedEnum(GameAddon.values(), "addon"),
                prefixedEnum(MapCategory.values(), "category"),
                Bukkit.getOnlinePlayers().stream().map((Player e) -> "player:" + e.getName()),
                Bukkit.getOnlinePlayers().stream().map((Player e) -> "player:" + e.getUniqueId()),
                Bukkit.getOnlinePlayers().stream().map((Player e) -> "offline-player:" + e.getUniqueId())
        ).flatMap(Function.identity())
                .filter(e -> e.startsWith(args[args.length - 1].toUpperCase(Locale.ROOT)))
                .map(e -> e.charAt(0) + e.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());
    }

    public void register(Warlords instance) {
        instance.getCommand("start").setExecutor(this);
        instance.getCommand("start").setTabCompleter(this);
    }
}
