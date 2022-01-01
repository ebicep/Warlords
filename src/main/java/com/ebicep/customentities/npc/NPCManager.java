package com.ebicep.customentities.npc;

import com.ebicep.customentities.npc.traits.GameStartTrait;
import com.ebicep.warlords.database.leaderboards.LeaderboardManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class NPCManager {

    public static void createGameNPC() {
        //for reloading
        if (CitizensAPI.getTraitFactory().getTrait("GameStartTrait") != null) {
            CitizensAPI.getTraitFactory().deregisterTrait(TraitInfo.create(GameStartTrait.class).withName("GameStartTrait"));
        }
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(GameStartTrait.class).withName("GameStartTrait"));

        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        NPC npc = registry.createNPC(EntityType.PLAYER, "capture-the-flag");
        npc.addTrait(GameStartTrait.class);
        npc.getOrAddTrait(SkinTrait.class).setSkinName("sumSmash");
        npc.data().set("nameplate-visible", false);
        npc.spawn(new Location(LeaderboardManager.spawnPoint.getWorld(), -2535.5, 51, 744.5, 90, 0));
    }
}
