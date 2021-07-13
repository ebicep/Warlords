package com.ebicep.customentities.npc;

import com.ebicep.warlords.Warlords;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCManager {

    private List<CustomNPC> npcList = new ArrayList<>();

    public List<CustomNPC> getNpcList() {
        return npcList;
    }

    public void createNPC(Location location, UUID uuid, String npcName, boolean nameVisible, List<String> info) {
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        NPC npc = registry.createNPC(EntityType.PLAYER, uuid, npcList.size(), npcName);
        npc.spawn(location);

        Hologram npcInfo = HologramsAPI.createHologram(Warlords.getInstance(), location.clone().add(0, info.size() / 3.5f + 2, 0));
        for (String s : info) {
            npcInfo.appendTextLine(s);
        }

        CustomNPC customNPC = new CustomNPC(npc, location, nameVisible, info);
        if (!nameVisible) {
            customNPC.hideName();
        }

        npcList.add(customNPC);
    }

}
