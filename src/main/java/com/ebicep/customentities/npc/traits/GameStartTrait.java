package com.ebicep.customentities.npc.traits;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.util.java.Pair;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Collections;
import java.util.List;

public class GameStartTrait extends Trait {
    public GameStartTrait() {
        super("GameStartTrait");
    }

    @Override
    public void run() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + Warlords.getGameManager().getPlayerCount() + " Players");
        hologramTrait.setLine(1, ChatColor.GRAY.toString() + Warlords.getGameManager().getPlayerCountInLobby() + " in Lobby");
        hologramTrait.setLine(2, ChatColor.AQUA + "WARLORDS 2 PUBLIC QUEUE");
        hologramTrait.setLine(3, ChatColor.YELLOW + ChatColor.BOLD.toString() + "CLICK TO PLAY");
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
        if (GameManager.gameStartingDisabled) {
            player.sendMessage(ChatColor.RED + "Games are currently disabled.");
            return;
        }

        //check if player is in a party, they must be leader to join
        Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(player.getUniqueId());
        if (partyPlayerPair != null) {
            if (!partyPlayerPair.getA().getPartyLeader().getUUID().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You are not the party leader");
                return;
            } else if (!partyPlayerPair.getA().allOnlineAndNoAFKs()) {
                player.sendMessage(ChatColor.RED + "All party members must be online or not afk");
                return;
            }
        }
        List<Player> people = partyPlayerPair != null ? partyPlayerPair.getA().getAllPartyPeoplePlayerOnline() : Collections.singletonList(player);
        Warlords.getGameManager()
                .newEntry(people)
                .setGamemode(GameMode.CAPTURE_THE_FLAG)
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
