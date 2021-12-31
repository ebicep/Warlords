package com.ebicep.customentities.npc.traits;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.maps.Team;
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
        hologramTrait.setLine(2, ChatColor.AQUA + "Capture The Flag");
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
        if (ctfQueue.contains(player.getUniqueId())) {
            ctfQueue.remove(player.getUniqueId());
            sendMessageToQueue(ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has quit!");
        } else {
            ctfQueue.add(player.getUniqueId());
            sendMessageToQueue(ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has joined (" + ChatColor.AQUA + ctfQueue.size() + ChatColor.YELLOW + "/" + ChatColor.AQUA + game.getMap().getMaxPlayers() + ChatColor.YELLOW + ")!");
            //start game a game isnt running and if there is enough players
            if (game.playersCount() == 0 && ctfQueue.size() >= game.getMap().getMinPlayers()) {
                Collections.shuffle(ctfQueue);
                AtomicBoolean blue = new AtomicBoolean(true);
                ctfQueue.stream()
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .forEach(p -> {
                            p.getInventory().clear();

                            PlayerSettings playerSettings = Warlords.getPlayerSettings(p.getUniqueId());
                            Classes selectedClass = playerSettings.getSelectedClass();
                            AbstractPlayerClass apc = selectedClass.create.get();

                            p.setAllowFlight(false);

                            p.getInventory().setItem(5, new ItemBuilder(Material.NOTE_BLOCK)
                                    .name(ChatColor.GREEN + "Team Selector " + ChatColor.GRAY + "(Right-Click)")
                                    .lore(ChatColor.YELLOW + "Click to select your team!")
                                    .get());
                            p.getInventory().setItem(6, new ItemBuilder(Material.NETHER_STAR)
                                    .name(ChatColor.AQUA + "Pre-game Menu ")
                                    .lore(ChatColor.GRAY + "Allows you to change your class, select a\n" + ChatColor.GRAY + "weapon, and edit your settings.")
                                    .get());
                            p.getInventory().setItem(1, new ItemBuilder(apc.getWeapon()
                                    .getItem(playerSettings.getWeaponSkins()
                                            .getOrDefault(selectedClass, Weapons.FELFLAME_BLADE).item))
                                    .name("§aWeapon Skin Preview")
                                    .lore("")
                                    .get());

                            Team team = Warlords.getPlayerSettings(p.getUniqueId()).getWantedTeam();
                            Warlords.game.addPlayer(p, blue.get() ? Team.BLUE : Team.RED);
                            Warlords.game.setPlayerTeam(p, blue.get() ? Team.BLUE : Team.RED);
                            ArmorManager.resetArmor(p, Warlords.getPlayerSettings(p.getUniqueId()).getSelectedClass(), team);
                            blue.set(false);
                        });
                ctfQueue.clear();
            }
        }
    }

    private void sendMessageToQueue(String message) {
        ctfQueue.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> player.sendMessage(message));
    }
}
