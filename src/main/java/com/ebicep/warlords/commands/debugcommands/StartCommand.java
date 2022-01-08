package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.customentities.npc.traits.GameStartTrait;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameMap;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.state.PreLobbyState;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.player.ArmorManager;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.Weapons;
import com.ebicep.warlords.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class StartCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {


        if (!sender.hasPermission("warlords.game.start")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        Game game = Warlords.game; // In the future allow the user to select a game player
        GameMap map;

        if (args.length == 0) {
            map = null;
        } else if (args[0].equalsIgnoreCase("random")) {
            Random random = new Random();
            GameMap[] values = GameMap.values();
            map = values[random.nextInt(values.length)];
        } else {
            try {
                map = GameMap.valueOf(args[0].toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + args[0] + " was not found, valid maps: " + Arrays.toString(GameMap.values()));
                return true;
            }
        }

        if (!(game.getState() instanceof PreLobbyState)) {
            sender.sendMessage(ChatColor.RED + "The game has already started!");
            return true;
        }

        game.clearAllPlayers();
        if (map != null) {
            if (game.getMap() != map) {
                game.changeMap(map);
            }
            sender.sendMessage("§cDEV: §aChanging map to " + map.getMapName());
        }

        Collection<? extends Player> online = Bukkit.getOnlinePlayers();
        List<Player> people;
        Optional<Party> party = Warlords.partyManager.getPartyFromAny(((Player) sender).getUniqueId());
        people = party.map(value -> new ArrayList<>(value.getAllPartyPeoplePlayerOnline())).orElseGet(() -> new ArrayList<>(online));
        if (party.isPresent()) {
            if (!party.get().getPartyLeader().getUuid().equals(((Player) sender).getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are not the party leader");
                return true;
            } else if (!party.get().allOnlineAndNoAFKs()) {
                sender.sendMessage(ChatColor.RED + "All party members must be online or not afk");
                return true;
            } else {
                //hiding players not in party
                List<Player> playersNotInParty = Bukkit.getOnlinePlayers().stream()
                        .filter(onlinePlayer -> party.get().getPartyPlayers().stream().noneMatch(partyPlayer -> partyPlayer.getUuid().equals(onlinePlayer.getUniqueId())))
                        .collect(Collectors.toList());
                Bukkit.getOnlinePlayers().stream()
                        .filter(onlinePlayer -> party.get().getPartyPlayers().stream().anyMatch(partyPlayer -> partyPlayer.getUuid().equals(onlinePlayer.getUniqueId())))
                        .forEach(playerInParty -> playersNotInParty.forEach(playerNotInParty -> {
                            playerInParty.hidePlayer(playerNotInParty);
                        }));
            }
        }
        //private game if started using /start
        game.setPrivate(true);
        Warlords.game.clearAllPlayers();
        GameStartTrait.ctfQueue.clear();

        for (Player player : people) {
            player.getInventory().clear();

            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
            Classes selectedClass = playerSettings.getSelectedClass();
            AbstractPlayerClass apc = selectedClass.create.get();

            player.setAllowFlight(false);

            player.getInventory().setItem(5, new ItemBuilder(Material.NOTE_BLOCK)
                    .name(ChatColor.GREEN + "Team Selector " + ChatColor.GRAY + "(Right-Click)")
                    .lore(ChatColor.YELLOW + "Click to select your team!")
                    .get());
            player.getInventory().setItem(6, new ItemBuilder(Material.NETHER_STAR)
                    .name(ChatColor.AQUA + "Pre-game Menu ")
                    .lore(ChatColor.GRAY + "Allows you to change your class, select a\n" + ChatColor.GRAY + "weapon, and edit your settings.")
                    .get());
            player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon()
                    .getItem(playerSettings.getWeaponSkins()
                            .getOrDefault(selectedClass, Weapons.FELFLAME_BLADE).item))
                    .name("§aWeapon Skin Preview")
                    .lore("")
                    .get());

            Team team = Warlords.getPlayerSettings(player.getUniqueId()).getWantedTeam();
            Warlords.game.addPlayer(player, team == null ? Team.BLUE : team);
            Warlords.game.setPlayerTeam(player, team == null ? Team.BLUE : team);
            ArmorManager.resetArmor(player, Warlords.getPlayerSettings(player.getUniqueId()).getSelectedClass(), team);

            GameStartTrait.ctfQueue.remove(player.getUniqueId());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

        return Arrays
                .stream(GameMap.values())
                .map(Enum::name)
                .filter(e -> e.startsWith(args[args.length - 1].toUpperCase(Locale.ROOT)))
                .map(e -> e.charAt(0) + e.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

    }

    public void register(Warlords instance) {
        instance.getCommand("start").setExecutor(this);
        instance.getCommand("start").setTabCompleter(this);
    }
}
