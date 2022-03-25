package com.ebicep.customentities.npc.traits;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.party.Party;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GameStartTrait extends Trait {
    public GameStartTrait() {
        super("GameStartTrait");
    }

    @Override
    public void run() {
        // we do a little trolling
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + Warlords.getGameManager().getPlayerCount() + " Chesskings in Game");
        hologramTrait.setLine(1, ChatColor.GRAY.toString() + Warlords.getGameManager().getPlayerCountInLobby() + " in Lobby to throw for Chessking");
        hologramTrait.setLine(2, ChatColor.GRAY.toString() + Warlords.getGameManager().getQueueSize() + " in Queue to deafen Chessking");
        hologramTrait.setLine(3, ChatColor.AQUA + "UH HELLLO? WHAT ARE YOU GUYS DOING HELLO");
        hologramTrait.setLine(4, ChatColor.YELLOW + ChatColor.BOLD.toString() + "I CANT DO MORE");
        hologramTrait.setLine(5, ChatColor.YELLOW + ChatColor.BOLD.toString() + "I FUCKING HATE MY TEAM FOR THROWING GAMES");


        /*hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + Warlords.getGameManager().getPlayerCount() + " Players");
        hologramTrait.setLine(1, ChatColor.GRAY.toString() + Warlords.getGameManager().getPlayerCountInLobby() + " in Lobby");
        hologramTrait.setLine(2, ChatColor.GRAY.toString() + Warlords.getGameManager().getQueueSize() + " in Queue");
        hologramTrait.setLine(3, ChatColor.AQUA + "WARLORDS 2 PUBLIC QUEUE");
        hologramTrait.setLine(4, ChatColor.YELLOW + ChatColor.BOLD.toString() + "CLICK TO PLAY");
        hologramTrait.setLine(5, ChatColor.YELLOW + ChatColor.BOLD.toString() + "[RANDOM MAP MODE]");*/
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (this.getNPC() == event.getNPC()) {
            if (!Warlords.getInstance().isEnabled()) {
                // Fix old NPC standing around on Windows + plugin reload after new deployment
                this.getNPC().destroy();
                return;
            }
            tryToJoinQueue(event.getClicker());
        }
    }

    @EventHandler
    public void onLeftClick(NPCLeftClickEvent event) {
        if (this.getNPC() == event.getNPC()) {
            if (!Warlords.getInstance().isEnabled()) {
                // Fix old NPC standing around on Windows + plugin reload after new deployment
                this.getNPC().destroy();
                return;
            }
            tryToJoinQueue(event.getClicker());
        }
    }

    private void tryToJoinQueue(Player player) {

        //check if player is in a party, they must be leader to join
        Optional<Party> party = Warlords.partyManager.getPartyFromAny(player.getUniqueId());
        List<Player> people = party.map(Party::getAllPartyPeoplePlayerOnline).orElseGet(() -> Collections.singletonList(player));
        if (party.isPresent()) {
            if (!party.get().getPartyLeader().getUuid().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You are not the party leader");
                return;
            } else if (!party.get().allOnlineAndNoAFKs()) {
                player.sendMessage(ChatColor.RED + "All party members must be online or not afk");
                return;
            }
        }

        Warlords.getGameManager()
                .newEntry(people)
                .setCategory(GameMode.CAPTURE_THE_FLAG)
                .setMap(null)
                .setPriority(0)
                .setExpiresTime(System.currentTimeMillis() + 60 * 1000)
                .setOnResult((result, game) -> {
                    if (game == null) {
                        player.sendMessage(ChatColor.RED + "Failed to join/create a game: " + result);
                    }
                }).queue();
    }
    //sendMessageToQueue(ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has quit!");
}
