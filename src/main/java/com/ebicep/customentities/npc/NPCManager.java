package com.ebicep.customentities.npc;

import com.ebicep.customentities.npc.traits.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.trait.HologramTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import net.citizensnpcs.trait.VillagerProfession;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class NPCManager {

    public static final NPCRegistry npcRegistry = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());
    public static NPC gameStartNPC;
    public static NPC pveStartNPC;
    public static NPC masterworksFairNPC;
    public static NPC supplyDropNPC;
    public static NPC weaponManagerNPC;
    public static NPC legendaryWeaponNPC;
    public static NPC questLordNPC;
    public static NPC starPieceSynthesizerNPC;
    //https://jd.citizensnpcs.co/net/citizensnpcs/api/npc/NPC.html

    public static void createGameJoinNPCs() {
        if (!Warlords.citizensEnabled) {
            return;
        }

        Warlords.newChain()
                .sync(() -> {
                    //for reloading
                    createGameNPC();
                    createPvENPC();
                })
                .execute();
    }

    private static void createGameNPC() {
        registerTrait(GameStartTrait.class, "GameStartTrait");

        gameStartNPC = npcRegistry.createNPC(EntityType.PLAYER, "capture-the-flag");
        gameStartNPC.addTrait(GameStartTrait.class);
        gameStartNPC.getOrAddTrait(SkinTrait.class).setSkinName("Chessking345");

        gameStartNPC.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        gameStartNPC.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2535.5, 51, 741.5, 90, 0));
    }

    private static void createPvENPC() {
        registerTrait(PvEStartTrait.class, "PveStartTrait");

        pveStartNPC = npcRegistry.createNPC(EntityType.PLAYER, "pve-mode");
        pveStartNPC.addTrait(PvEStartTrait.class);
        pveStartNPC.getOrAddTrait(SkinTrait.class).setSkinName("Plikie");

        pveStartNPC.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        pveStartNPC.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2535.5, 51, 747.5, 90, 0));
    }

    private static void registerTrait(Class<? extends Trait> trait, String traitName) {
        if (CitizensAPI.getTraitFactory().getTrait(traitName) != null) {
            CitizensAPI.getTraitFactory().deregisterTrait(TraitInfo.create(trait).withName(traitName));
        }
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(trait).withName(traitName));
    }

    public static void createDatabaseRequiredNPCs() {
        if (!Warlords.citizensEnabled) {
            return;
        }

        Warlords.newChain()
                .sync(() -> {
                    createMasterworksFairNPC();
                    createWeaponsManagerNPC();
                    createLegendaryWeaponNPC();
                    createQuestMenuNPC();
                    createStarPieceSynthesizerNPC();
                })
                .execute();
    }

    public static void createMasterworksFairNPC() {
        if (!MasterworksFairManager.enabled) {
            return;
        }
        registerTrait(MasterworksFairTrait.class, "MasterworksFairTrait");

        masterworksFairNPC = npcRegistry.createNPC(EntityType.PIG_ZOMBIE, "masterworks-fair");
        masterworksFairNPC.addTrait(MasterworksFairTrait.class);

        masterworksFairNPC.data().set(NPC.VILLAGER_BLOCK_TRADES, true);
        masterworksFairNPC.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        LookClose lookClose = masterworksFairNPC.getOrAddTrait(LookClose.class);
        lookClose.toggle();

        masterworksFairNPC.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2523.5, 50, 725.5, 90, 0));
    }

    public static void createWeaponsManagerNPC() {
        registerTrait(WeaponMangerTrait.class, "WeaponMangerTrait");

        weaponManagerNPC = npcRegistry.createNPC(EntityType.VILLAGER, "weapon-manager");
        weaponManagerNPC.addTrait(WeaponMangerTrait.class);
        HologramTrait hologramTrait = weaponManagerNPC.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK");
        hologramTrait.setLine(1, ChatColor.GREEN + "The Weaponsmith");

        weaponManagerNPC.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        weaponManagerNPC.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2533.5, 50, 722.5, 45, 0));

    }

    public static void createLegendaryWeaponNPC() {
        registerTrait(LegendaryWeaponTrait.class, "LegendaryWeaponTrait");

        legendaryWeaponNPC = npcRegistry.createNPC(EntityType.WITCH, "legendary-weapon");
        legendaryWeaponNPC.addTrait(LegendaryWeaponTrait.class);

        legendaryWeaponNPC.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        legendaryWeaponNPC.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2515.5, 54, 766.5, 180, 0));

    }

    public static void createQuestMenuNPC() {
        registerTrait(QuestMenuTrait.class, "QuestMenuTrait");

        questLordNPC = npcRegistry.createNPC(EntityType.VILLAGER, "quest-menu");
        questLordNPC.getOrAddTrait(VillagerProfession.class).setProfession(Villager.Profession.LIBRARIAN);
        questLordNPC.addTrait(QuestMenuTrait.class);
        HologramTrait hologramTrait = questLordNPC.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK");
        hologramTrait.setLine(1, ChatColor.AQUA + "Quest Lord");

        questLordNPC.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        questLordNPC.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2574.5, 50, 758.5, -90, 0));

    }

    public static void destroyNPCs() {
        if (!Warlords.citizensEnabled) {
            return;
        }

        npcRegistry.despawnNPCs(DespawnReason.RELOAD);
        npcRegistry.deregisterAll();
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
        LookClose lookClose = supplyDropNPC.getOrAddTrait(LookClose.class);
        lookClose.toggle();

        supplyDropNPC.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2528.5, 50, 757.5, 90, 0));
    }

    public static void createStarPieceSynthesizerNPC() {
        registerTrait(StarPieceSynthesizerTrait.class, "StarPieceSynthesizerTrait");

        starPieceSynthesizerNPC = npcRegistry.createNPC(EntityType.ENDER_CRYSTAL, "star-piece-synthesizer");
        starPieceSynthesizerNPC.addTrait(StarPieceSynthesizerTrait.class);

        starPieceSynthesizerNPC.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        starPieceSynthesizerNPC.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2515.5, 53.49, 722.5, 0, 0));
    }


}
