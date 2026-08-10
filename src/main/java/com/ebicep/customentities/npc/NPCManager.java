package com.ebicep.customentities.npc;

import com.ebicep.customentities.npc.traits.*;
import com.ebicep.holograms.Hologram;
import com.ebicep.holograms.HologramDataText;
import com.ebicep.holograms.HologramManager;
import com.ebicep.holograms.VisibilityType;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.ReadyUpOption;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.ArmorStandTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import net.citizensnpcs.trait.VillagerProfession;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCManager {

    public static final NPCRegistry NPC_REGISTRY = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());

    public static void createNPCs() {
        if (!Warlords.citizensEnabled) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                ChatUtils.MessageType.GAME.sendMessage("Adding game join NPCs...");
                createCTFNPC();
                createSiegeNPC();
                createWaveDefenseNPC();
                createOnslaughtNPC();
                createTreasureHuntNPC();
                createMasterworksFairNPC();
                createWeaponsManagerNPC();
                createLegendaryWeaponNPC();
                createSupplyDropFairNPC();
                createBountyMenuNPC();
                createSupporterShopNPC();
                createStarPieceSynthesizerNPC();
                createItemCrafterNPC();
                createIllusionVendorNPC();
                createSeasonalVendorNPC();
                createAnomalyNPC();
                createRaidOneNPC();
                createPrestigeVendorNPC();
                createWeeklyItemTraderNPC();
                createTreasureHuntVendorNPC();
                createAscendantWeaponNPC();
                createTutorialGuideNPC();
                createMainLobbySetupNPC();
                registerTrait(ReadyUpOption.ReadyUpTrait.class, "ReadyUpTrait");
                ChatUtils.MessageType.GAME.sendMessage("Done adding game join NPCs");
            }
        }.runTask(Warlords.getInstance());
    }

    private static void createCTFNPC() {
        registerTrait(CaptureTheFlagTrait.class, "GameStartTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "capture-the-flag");
        npc.addTrait(CaptureTheFlagTrait.class);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 17.5, 82, 138.5, 45, 0));
    }

    private static void createSiegeNPC() {
        registerTrait(SiegeTrait.class, "SiegeStartTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "siege");
        npc.addTrait(SiegeTrait.class);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 20.5, 82, 140.5, 45, 0));
    }

    private static void createWaveDefenseNPC() {
        registerTrait(PvEStartTrait.class, "PveStartTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "pve-mode");
        npc.addTrait(PvEStartTrait.class);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 17.5, 82, 160.5, 135, 0));
    }

    private static void createOnslaughtNPC() {
        registerTrait(OnslaughtStartTrait.class, "OnslaughtStartTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "onslaught-mode");
        npc.addTrait(OnslaughtStartTrait.class);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 5.5, 82, 160.5, -135, 0));
    }

    private static void createTreasureHuntNPC() {
        registerTrait(TreasureHuntStartTrait.class, "TreasureHuntStartTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "treasure-hunt-mode");
        npc.addTrait(TreasureHuntStartTrait.class);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 20.5, 82, 158.5, 135, 0));
    }

    private static void createAnomalyNPC() {
        registerTrait(AnomalyStartTrait.class, "AnomalyStartTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "anomaly-mode");
        npc.addTrait(AnomalyStartTrait.class);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 22.5, 82, 155.5, 122, 0));
    }

    public static void createMasterworksFairNPC() {
        if (!MasterworksFairManager.enabled) {
            return;
        }
        registerTrait(MasterworksFairTrait.class, "MasterworksFairTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.ZOMBIFIED_PIGLIN, "masterworks-fair");
        npc.addTrait(MasterworksFairTrait.class);
        npc.data().set(NPC.Metadata.VILLAGER_BLOCK_TRADES, true);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 63, 81, 163, 135, 0));
    }

    public static void createWeaponsManagerNPC() {
        registerTrait(WeaponMangerTrait.class, "WeaponMangerTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.VILLAGER, "weapon-manager");
        npc.addTrait(WeaponMangerTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        Location location = new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 53.5, 81, 157.5, 180, 0);
        npc.spawn(location);
        HologramDataText hologramDataText = new HologramDataText.Builder<>(ComponentBuilder.create("The Weaponsmith", NamedTextColor.GREEN).build()).setBillboard(Display.Billboard.CENTER).build();
        HologramManager.addHologram(new Hologram.Builder("theWeaponsmith", location.clone().add(0, 2.1, 0), player -> hologramDataText).setVisibility(VisibilityType.ALL).build());
    }

    public static void createLegendaryWeaponNPC() {
        registerTrait(LegendaryWeaponTrait.class, "LegendaryWeaponTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.WITCH, "legendary-weapon");
        npc.addTrait(LegendaryWeaponTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 51.5, 81, 157.5, 180, 0));
    }

    public static void createAscendantWeaponNPC() {
        registerTrait(AscendantWeaponTrait.class, "AscendantWeaponTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "ascendant-weapon");
        npc.getOrAddTrait(SkinTrait.class).setSkinName("Plikie");
        npc.addTrait(AscendantWeaponTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 67.5, 93, 212.5, -180, 0));
    }

    public static void createSupplyDropFairNPC() {
        registerTrait(SupplyDropTrait.class, "SupplyDropTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.RABBIT, "supply-drop");
        npc.addTrait(SupplyDropTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        Location location = new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 49.5, 81, 142.5, 0, 0);
        npc.spawn(location);
        HologramDataText hologramDataText = new HologramDataText.Builder<>(ComponentBuilder.create("Supply Drop Susan", NamedTextColor.GREEN).build()).setBillboard(Display.Billboard.CENTER).build();
        HologramManager.addHologram(new Hologram.Builder("supplyDropSusan", location.clone().add(0, .6, 0), player -> hologramDataText).setVisibility(VisibilityType.ALL).build());
    }

    public static void createBountyMenuNPC() {
        registerTrait(BountyMenuTrait.class, "BountyMenuTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "bounty-menu");
        npc.addTrait(BountyMenuTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        Location location = new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 29.5, 81, 166.5, 90, 0);
        npc.spawn(location);
        HologramDataText hologramDataText = new HologramDataText.Builder<>(ComponentBuilder.create("Bounty Hunter", NamedTextColor.AQUA).build()).setBillboard(Display.Billboard.CENTER).build();
        HologramManager.addHologram(new Hologram.Builder("bountyHunter", location.clone().add(0, 2.1, 0), player -> hologramDataText).setVisibility(VisibilityType.ALL).build());
    }

    public static void createSupporterShopNPC() {
        registerTrait(SupporterShopTrait.class, "SupporterShopTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.VILLAGER, "supporter-shop");
        npc.addTrait(SupporterShopTrait.class);
        npc.getOrAddTrait(VillagerProfession.class).setProfession(Villager.Profession.CLERIC);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        Location location = new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 29.5, 81, 163.5, 90, 0);
        npc.spawn(location);
        HologramDataText hologramDataText = new HologramDataText.Builder<>(ComponentBuilder.create("Supporter Shop", NamedTextColor.GOLD).build()).setBillboard(Display.Billboard.CENTER).build();
        HologramManager.addHologram(new Hologram.Builder("supporterShop", location.clone().add(0, 2.1, 0), player -> hologramDataText).setVisibility(VisibilityType.ALL).build());
    }

    public static void createStarPieceSynthesizerNPC() {
        registerTrait(StarPieceSynthesizerTrait.class, "StarPieceSynthesizerTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.END_CRYSTAL, "star-piece-synthesizer");
        npc.addTrait(StarPieceSynthesizerTrait.class);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 40.5, 74, 169, 0, 0));
    }

    public static void createItemCrafterNPC() {
        registerTrait(ItemCrafterTrait.class, "ItemCrafterTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.BREEZE, "item-crafter");
        npc.addTrait(ItemCrafterTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 28.5, 93, 208.5, 90, 0));
    }

    public static void createIllusionVendorNPC() {
        registerTrait(IllusionVendorTrait.class, "IllusionVendorTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.IRON_GOLEM, "illusion-vendor");
        npc.addTrait(IllusionVendorTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), -9.5, 74, 97.5, -180, 0));
    }

    public static void createPrestigeVendorNPC() {
        registerTrait(PrestigeVendorTrait.class, "PrestigeVendorTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.CREAKING, "prestige-vendor");
        npc.addTrait(PrestigeVendorTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), -21.5, 88, 183.5, 90, 0));
    }

    public static void createSeasonalVendorNPC() {
        registerTrait(SeasonalTraderTrait.class, "SeasonalVendorTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.ARMADILLO, "seasonal-vendor");
        npc.addTrait(SeasonalTraderTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), -3, 81, 135, -45, 0));
    }

    public static void createAscendantVendorNPC() {
        registerTrait(AscendantVendorTrait.class, "AscendantVendorTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "ascendant-vendor");
        npc.addTrait(AscendantVendorTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 20.5, 93, 208.5, -90, 0));
    }

    public static void createRaidOneNPC() {
        registerTrait(RaidOneStartTrait.class, "RaidStartTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.END_CRYSTAL, "raid-start");
        npc.addTrait(RaidOneStartTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        Location loc = new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 28.5, 94, 262.5, -180, 0);
        npc.spawn(loc);
        new BukkitRunnable() {
            @Override
            public void run() {
                EffectUtils.displayParticle(Particle.ASH, loc.clone().add(0, 2, 0), 10, 0.5, 0.2, 0.5, 0.001);
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 30);
    }

    public static void createTreasureHuntVendorNPC() {
        registerTrait(TreasureHuntVendorTrait.class, "TreasureHuntVendorTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "treasure-vendor");
        npc.addTrait(TreasureHuntVendorTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 71.5, 93, 198.5, 90, 0));
    }

    public static void createWeeklyItemTraderNPC() {
        registerTrait(WeeklyItemTraderTrait.class, "WeeklyItemTraderTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "weekly-item-vendor");
        npc.addTrait(WeeklyItemTraderTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        Location loc = new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 65.5, 81, 146.5, 75, 0);
        npc.spawn(loc);
        new BukkitRunnable() {
            @Override
            public void run() {
                EffectUtils.displayParticle(Particle.SOUL_FIRE_FLAME, loc.clone().add(0, 1.2, 0), 3, 0.5, 0.2, 0.5, 0.001);
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 60);
    }

    public static void registerTrait(Class<? extends Trait> trait, String traitName) {
        if (CitizensAPI.getTraitFactory().getTrait(traitName) != null) {
            CitizensAPI.getTraitFactory().getRegisteredTraits().remove(TraitInfo.create(trait).withName(traitName));
        }
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(trait).withName(traitName));
    }

    public static void destroyNPCs() {
        if (!Warlords.citizensEnabled) {
            return;
        }
        NPC_REGISTRY.despawnNPCs(DespawnReason.RELOAD);
        NPC_REGISTRY.deregisterAll();
    }

    public static void createMysteriousTokenNPC() {
        registerTrait(MysteriousTokenTrait.class, "MysteriousTokenTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.ARMOR_STAND, "mysterious-token");
        npc.addTrait(MysteriousTokenTrait.class);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        ArmorStandTrait armorStandTrait = npc.getOrAddTrait(ArmorStandTrait.class);
        armorStandTrait.setVisible(false);
        armorStandTrait.setGravity(false);
        Equipment equipment = npc.getOrAddTrait(Equipment.class);
        equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.BEDROCK));
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), -2532.5, 48.5, 746.8, 90, 0));
    }

    public static void createTutorialGuideNPC() {
        registerTrait(TutorialGuideTrait.class, "TutorialGuideTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.VILLAGER, "tutorial-guide");
        npc.getOrAddTrait(VillagerProfession.class).setProfession(Villager.Profession.LIBRARIAN);
        npc.addTrait(TutorialGuideTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 11.5, 81, 155.5, 180, 0));
    }

    public static void createMainLobbySetupNPC() {
        registerTrait(MainLobbySetupTrait.class, "MainLobbySetupTrait");
        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "main-lobby-setup");
        npc.addTrait(MainLobbySetupTrait.class);
        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), -58.5, 60, 83, 113, 0));
    }
}
