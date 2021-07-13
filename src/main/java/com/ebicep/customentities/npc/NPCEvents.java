package com.ebicep.customentities.npc;

import com.ebicep.warlords.Warlords;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class NPCEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Warlords.npcManager.getNpcList().forEach(customNPC -> {
            if (!customNPC.isNameVisible()) {
                customNPC.hideName();
            }
        });
    }

    @EventHandler
    public void onPlayerInteractEntity(NPCClickEvent e) {
        Player player = e.getClicker();
        NPC npc = e.getNPC();

    }
}

