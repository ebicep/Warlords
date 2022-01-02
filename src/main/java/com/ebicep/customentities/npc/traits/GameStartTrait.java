package com.ebicep.customentities.npc.traits;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.state.PreLobbyState;
import com.ebicep.warlords.player.ArmorManager;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.Weapons;
import com.ebicep.warlords.util.ItemBuilder;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ebicep.warlords.Warlords.game;

public class GameStartTrait extends Trait {

    public static List<UUID> ctfQueue = new ArrayList<>();

    public GameStartTrait() {
        super("GameStartTrait");
    }

    @Override
    public void run() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + game.playersCount() + " Players");
        hologramTrait.setLine(1, ChatColor.GRAY.toString() + ctfQueue.size() + " in Queue");
        hologramTrait.setLine(2, ChatColor.AQUA + "WARLORDS 2 PUBLIC QUEUE [BETA]");
        hologramTrait.setLine(3, ChatColor.YELLOW + ChatColor.BOLD.toString() + "CLICK TO PLAY");
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (this.getNPC() == event.getNPC()) {
            joinQueue(event.getClicker());
        }
    }

    @EventHandler
    public void onLeftClick(NPCLeftClickEvent event) {
        if (this.getNPC() == event.getNPC()) {
            joinQueue(event.getClicker());
        }
    }

    private void joinQueue(Player player) {
        if (ctfQueue.contains(player.getUniqueId())) return;
        if (!(game.getState() instanceof PreLobbyState) || game.isPrivate()) {
            player.sendMessage(ChatColor.RED + "Unable to join because there is an ongoing game. Use " + ChatColor.GRAY + "/spectate" + ChatColor.RED + " if you feel like spectating!");
            return;
        }
        ctfQueue.add(player.getUniqueId());
        sendMessageToQueue(ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has joined (" + ChatColor.AQUA + ctfQueue.size() + ChatColor.YELLOW + "/" + ChatColor.AQUA + game.getMap().getMaxPlayers() + ChatColor.YELLOW + ")!");

        int bluePlayers = (int) game.getPlayers().values().stream().filter(team -> team == Team.BLUE).count();
        int redPlayers = (int) game.getPlayers().values().stream().filter(team -> team == Team.RED).count();
        Team team = bluePlayers > redPlayers ? Team.RED : Team.BLUE;

        player.getInventory().clear();

        PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
        Classes selectedClass = playerSettings.getSelectedClass();
        AbstractPlayerClass apc = selectedClass.create.get();

        player.setAllowFlight(false);


        player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon()
                .getItem(playerSettings.getWeaponSkins()
                        .getOrDefault(selectedClass, Weapons.FELFLAME_BLADE).item))
                .name("§aWeapon Skin Preview")
                .lore("")
                .get());
        player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR)
                .name(ChatColor.AQUA + "Pre-game Menu ")
                .lore(ChatColor.GRAY + "Allows you to change your class, select a\n" + ChatColor.GRAY + "weapon, and edit your settings.")
                .get());
        player.getInventory().setItem(7, new ItemBuilder(Material.BARRIER)
                .name(ChatColor.RED + "Leave")
                .lore(ChatColor.GRAY + "Right-Click to leave the game.")
                .get());

        Warlords.game.addPlayer(player, team);
        Warlords.getPlayerSettings(player.getUniqueId()).setWantedTeam(team);
        ArmorManager.resetArmor(player, Warlords.getPlayerSettings(player.getUniqueId()).getSelectedClass(), team);
    }

    private void sendMessageToQueue(String message) {
        ctfQueue.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> player.sendMessage(message));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (ctfQueue.contains(player.getUniqueId())) {
            ctfQueue.remove(player.getUniqueId());
            game.removePlayer(player.getUniqueId());
            sendMessageToQueue(ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has quit!");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (event.hasItem()) {
            ItemStack itemStack = event.getItem();
            //if a player leaves the server, they get removed from the queue and game
            if (ctfQueue.contains(player.getUniqueId()) && itemStack.getType() == Material.BARRIER && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
                ctfQueue.remove(player.getUniqueId());
                game.removePlayer(player.getUniqueId());
                sendMessageToQueue(ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has quit!");
            }
        }
    }


}
