package com.ebicep.customentities.npc;

import com.ebicep.customentities.npc.traits.GameStartTrait;
import com.ebicep.customentities.npc.traits.MasterworksFairTrait;
import com.ebicep.customentities.npc.traits.PveStartTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.leaderboards.LeaderboardManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class NPCManager {

    public static final NPCRegistry npcRegistry = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());
    public static NPC gameStartNPC;
    public static NPC pveStartNPC;
    public static NPC masterworksFairNPC;
    //https://jd.citizensnpcs.co/net/citizensnpcs/api/npc/NPC.html

    public static void createNPCs() {
        if (!Warlords.citizensEnabled) return;

        Warlords.newChain()
                .sync(() -> {
                    //for reloading
                    createGameNPC();
                    createPvENPC();
                    createMasterworksFairNPC();
                })
                .execute();

    }

    public static void destroyNPCs() {
        if (!Warlords.citizensEnabled) return;

        gameStartNPC.destroy();
        pveStartNPC.destroy();
        masterworksFairNPC.destroy();
    }

    private static void registerTrait(Class<? extends Trait> trait, String traitName) {
        if (CitizensAPI.getTraitFactory().getTrait(traitName) != null) {
            CitizensAPI.getTraitFactory().deregisterTrait(TraitInfo.create(trait).withName(traitName));
        }
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(trait).withName(traitName));
    }

    private static void createGameNPC() {
        registerTrait(GameStartTrait.class, "GameStartTrait");

        gameStartNPC = npcRegistry.createNPC(EntityType.PLAYER, "capture-the-flag");
        gameStartNPC.addTrait(GameStartTrait.class);
        gameStartNPC.getOrAddTrait(SkinTrait.class).setSkinName("Chessking345");

        gameStartNPC.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        gameStartNPC.spawn(new Location(LeaderboardManager.spawnPoint.getWorld(), -2535.5, 51, 741.5, 90, 0));
    }

    private static void createPvENPC() {
        registerTrait(PveStartTrait.class, "PveStartTrait");

        pveStartNPC = npcRegistry.createNPC(EntityType.PLAYER, "pve-mode");
        pveStartNPC.addTrait(PveStartTrait.class);
        pveStartNPC.getOrAddTrait(SkinTrait.class).setSkinName("Plikie");

        pveStartNPC.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        pveStartNPC.spawn(new Location(LeaderboardManager.spawnPoint.getWorld(), -2535.5, 51, 747.5, 90, 0));
    }

    private static void createMasterworksFairNPC() {
        registerTrait(MasterworksFairTrait.class, "MasterworksFairTrait");

        masterworksFairNPC = npcRegistry.createNPC(EntityType.VILLAGER, "masterworks-fair");
        masterworksFairNPC.addTrait(MasterworksFairTrait.class);

        masterworksFairNPC.data().set(NPC.VILLAGER_BLOCK_TRADES, true);
        masterworksFairNPC.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);

        masterworksFairNPC.spawn(new Location(LeaderboardManager.spawnPoint.getWorld(), -2542, 50, 754.5, 135, 0));
    }


}
