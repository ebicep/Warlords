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
    //https://jd.citizensnpcs.co/net/citizensnpcs/api/npc/NPC.html

    public static void createGameJoinNPCs() {
        if (!Warlords.citizensEnabled) {
            return;
        }

        Warlords.newChain()
                .sync(() -> {
                    createGameNPC();
                    createPvENPC();
                })
                .execute();
    }

    private static void createGameNPC() {
        registerTrait(GameStartTrait.class, "GameStartTrait");

        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "capture-the-flag");
        npc.addTrait(GameStartTrait.class);
        npc.getOrAddTrait(SkinTrait.class).setSkinName("Chessking345");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2535.5, 51, 741.5, 90, 0));
    }

    private static void createPvENPC() {
        registerTrait(PvEStartTrait.class, "PveStartTrait");

        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "pve-mode");
        npc.addTrait(PvEStartTrait.class);
        npc.getOrAddTrait(SkinTrait.class).setSkinName("Plikie");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2535.5, 51, 747.5, 90, 0));
    }

    public static void registerTrait(Class<? extends Trait> trait, String traitName) {
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

        NPC npc = npcRegistry.createNPC(EntityType.ZOMBIFIED_PIGLIN, "masterworks-fair");
        npc.addTrait(MasterworksFairTrait.class);

        npc.data().set(NPC.Metadata.VILLAGER_BLOCK_TRADES, true);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.toggle();

        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2523.5, 50, 725.5, 90, 0));
    }

    public static void createWeaponsManagerNPC() {
        registerTrait(WeaponMangerTrait.class, "WeaponMangerTrait");

        NPC npc = npcRegistry.createNPC(EntityType.VILLAGER, "weapon-manager");
        npc.addTrait(WeaponMangerTrait.class);
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK");
        hologramTrait.setLine(1, ChatColor.GREEN + "The Weaponsmith");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2533.5, 50, 722.5, 45, 0));

    }

    public static void createLegendaryWeaponNPC() {
        registerTrait(LegendaryWeaponTrait.class, "LegendaryWeaponTrait");

        NPC npc = npcRegistry.createNPC(EntityType.WITCH, "legendary-weapon");
        npc.addTrait(LegendaryWeaponTrait.class);

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2515.5, 54, 766.5, 180, 0));

    }

    public static void createQuestMenuNPC() {
        registerTrait(QuestMenuTrait.class, "QuestMenuTrait");

        NPC npc = npcRegistry.createNPC(EntityType.VILLAGER, "quest-menu");
        npc.getOrAddTrait(VillagerProfession.class).setProfession(Villager.Profession.LIBRARIAN);
        npc.addTrait(QuestMenuTrait.class);
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK");
        hologramTrait.setLine(1, ChatColor.AQUA + "Quest Lord");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2574.5, 50, 758.5, -90, 0));

    }

    public static void createStarPieceSynthesizerNPC() {
        registerTrait(StarPieceSynthesizerTrait.class, "StarPieceSynthesizerTrait");

        NPC npc = npcRegistry.createNPC(EntityType.ENDER_CRYSTAL, "star-piece-synthesizer");
        npc.addTrait(StarPieceSynthesizerTrait.class);

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2515.5, 53.49, 722.5, 0, 0));
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

        NPC npc = npcRegistry.createNPC(EntityType.RABBIT, "supply-drop");
        npc.addTrait(SupplyDropTrait.class);
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK");
        hologramTrait.setLine(1, ChatColor.GREEN + "Supply Drop Susan");
        //hologramTrait.setLine(2, ChatColor.GOLD.toString() + ChatColor.MAGIC + "   " + ChatColor.GOLD + " ROLL FOR GREAT REWARDS " + ChatColor.MAGIC + "   ");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.toggle();

        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2528.5, 50, 757.5, 90, 0));
    }


}
