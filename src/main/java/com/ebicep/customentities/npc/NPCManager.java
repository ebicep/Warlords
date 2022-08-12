package com.ebicep.customentities.npc;

import com.ebicep.customentities.npc.traits.GameStartTrait;
import com.ebicep.customentities.npc.traits.MasterworksFairTrait;
import com.ebicep.customentities.npc.traits.PveStartTrait;
import com.ebicep.customentities.npc.traits.SupplyDropTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.leaderboards.stats.LeaderboardManager;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class NPCManager {

    public static final NPCRegistry npcRegistry = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());
    public static NPC gameStartNPC;
    public static NPC pveStartNPC;
    public static NPC masterworksFairNPC;
    public static NPC supplyDropNPC;
    //https://jd.citizensnpcs.co/net/citizensnpcs/api/npc/NPC.html

    public static void createGameJoinNPCs() {
        if (!Warlords.citizensEnabled) return;

        Warlords.newChain()
                .sync(() -> {
                    //for reloading
                    createGameNPC();
                    createPvENPC();
                })
                .execute();
    }

    public static void createDatabaseRequiredNPCs() {
        if (!Warlords.citizensEnabled) return;

        Warlords.newChain()
                .sync(() -> {
                    createMasterworksFairNPC();
                })
                .execute();
    }

    public static void destroyNPCs() {
        if (!Warlords.citizensEnabled) return;

        npcRegistry.deregisterAll();
        npcRegistry.despawnNPCs(DespawnReason.RELOAD);

        if (gameStartNPC != null) {
            gameStartNPC.destroy();
        }
        if (gameStartNPC != null) {
            gameStartNPC.destroy();
        }
        if (pveStartNPC != null) {
            pveStartNPC.destroy();
        }
        if (masterworksFairNPC != null) {
            masterworksFairNPC.destroy();
        }
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
        gameStartNPC.spawn(new Location(LeaderboardManager.SPAWN_POINT.getWorld(), -2535.5, 51, 741.5, 90, 0));
    }

    private static void createPvENPC() {
        registerTrait(PveStartTrait.class, "PveStartTrait");

        pveStartNPC = npcRegistry.createNPC(EntityType.PLAYER, "pve-mode");
        pveStartNPC.addTrait(PveStartTrait.class);
        pveStartNPC.getOrAddTrait(SkinTrait.class).setSkinName("Plikie");

        pveStartNPC.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        pveStartNPC.spawn(new Location(LeaderboardManager.SPAWN_POINT.getWorld(), -2535.5, 51, 747.5, 90, 0));
    }

    public static void createMasterworksFairNPC() {
        if (!MasterworksFairManager.enabled) return;
        registerTrait(MasterworksFairTrait.class, "MasterworksFairTrait");

        masterworksFairNPC = npcRegistry.createNPC(EntityType.VILLAGER, "masterworks-fair");
        masterworksFairNPC.addTrait(MasterworksFairTrait.class);

        masterworksFairNPC.data().set(NPC.VILLAGER_BLOCK_TRADES, true);
        masterworksFairNPC.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);

        masterworksFairNPC.spawn(new Location(LeaderboardManager.SPAWN_POINT.getWorld(), -2542, 50, 754.5, 135, 0));
    }

    public static void createSupplyDropFairNPC() {
        registerTrait(SupplyDropTrait.class, "SupplyDropTrait");

        supplyDropNPC = npcRegistry.createNPC(EntityType.RABBIT, "supply-drop");
        supplyDropNPC.addTrait(SupplyDropTrait.class);
        HologramTrait hologramTrait = supplyDropNPC.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK");
        hologramTrait.setLine(1, ChatColor.GREEN + "Supply Drop Susan");
        //hologramTrait.setLine(2, ChatColor.GOLD.toString() + ChatColor.MAGIC + "   " + ChatColor.GOLD + " ROLL FOR GREAT REWARDS " + ChatColor.MAGIC + "   ");

        supplyDropNPC.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);

        supplyDropNPC.spawn(new Location(LeaderboardManager.SPAWN_POINT.getWorld(), -2538, 50, 751, 135, 0));
    }


}
