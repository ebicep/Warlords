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
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

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
                    createTeamDeathmatchNPC();
                    createInterceptionNPC();
                    createWaveDefenseNPC();
                    createOnslaughtNPC();
                    createTreasureHuntNPC();
                    createBossRushNPC();
                })
                .execute();
    }

    private static void createGameNPC() {
        registerTrait(CaptureTheFlagTrait.class, "GameStartTrait");

        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "capture-the-flag");
        npc.addTrait(CaptureTheFlagTrait.class);
        npc.getOrAddTrait(SkinTrait.class).setSkinName("Chessking345");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2535.5, 52, 742.5, 90, 0));
    }

    public static void createTeamDeathmatchNPC() {
        registerTrait(TeamDeathmatchTrait.class, "TeamDeathmatchTrait");

        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "team-deathmatch");
        npc.addTrait(TeamDeathmatchTrait.class);
        npc.getOrAddTrait(SkinTrait.class).setSkinName("Richdragon123");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2535.5, 50, 739.5, 90, 0));
    }

    public static void createInterceptionNPC() {
        registerTrait(InterceptionTrait.class, "InterceptionTrait");

        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "interception");
        npc.addTrait(InterceptionTrait.class);
        npc.getOrAddTrait(SkinTrait.class).setSkinName("AwesomeRaki");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2535.5, 50, 736.5, 90, 0));
    }

    private static void createWaveDefenseNPC() {
        registerTrait(PvEStartTrait.class, "PveStartTrait");

        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "pve-mode");
        npc.addTrait(PvEStartTrait.class);
        npc.getOrAddTrait(SkinTrait.class).setSkinName("Plikie");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2535.5, 52, 746.5, 90, 0));
    }

    private static void createOnslaughtNPC() {
        registerTrait(OnslaughtStartTrait.class, "OnslaughtStartTrait");

        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "onslaught-mode");
        npc.addTrait(OnslaughtStartTrait.class);
        npc.getOrAddTrait(SkinTrait.class).setSkinName("Heatran");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2535.5, 52, 749.5, 90, 0));
    }

    private static void createTreasureHuntNPC() {
        registerTrait(TreasureHuntStartTrait.class, "TreasureHuntStartTrait");

        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "treasure-hunt-mode");
        npc.addTrait(TreasureHuntStartTrait.class);
        npc.getOrAddTrait(SkinTrait.class).setSkinName("Alexred2522");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2535.5, 52, 752.5, 90, 0));
    }

    private static void createBossRushNPC() {
        registerTrait(BossRushStartTrait.class, "BossRushStartTrait");

        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "boss-rush-mode");
        npc.addTrait(BossRushStartTrait.class);
        npc.getOrAddTrait(SkinTrait.class).setSkinName("Stratfull");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2537.5, 52, 755.5, 135, 0));
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
//                    createQuestMenuNPC();
                    createBountyMenuNPC();
                    createStarPieceSynthesizerNPC();
//                    createMysteriousTokenNPC();
//                    createItemMichaelNPC();
                    createItemEnyaNPC();
                    createIllusionVendorNPC();
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

    public static void createBountyMenuNPC() {
        registerTrait(BountyMenuTrait.class, "BountyMenuTrait");

        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, "bounty-menu");
        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(
                "Freminet",
                "h4f/F6TIQWBP8JZfLtour95b5+FKGiWufKtEauAgpCeFR4nOAlzlh8WpXBeGBo/6CRB4vtmqa71T5kBvSkbxZ151AnIhJ/f8ReQBmQCmlsi4AOcLXDJIzkrZ7SyUXATSbzxXomjqoJKbu4aljSccKTq/FaOVY2gMQsEdgYefF6frLrXJ3rtKUcbkRmrUG/GqkASrp0I3eZ6GCKDlkSkShHOdmu2ELd2LNOI656/DSSCVkDjeZ5sR/fNaKMJN6ubefTpwOTPxJV0NvzzUf1uxggOoKc+NyowKYPXqmI0qnvPiXCwsQ8oyTGrei2GAouwZPddVZMdioz2eAUJZfbAC7M+CdKh1FNkbKn4mNHj9g0pTLyZaeoKVMqmD89oseZ0TO6NQ3Cgpufes+/M+yj9ikktixfZg6Kl1hN01S+ZQAQ2qixkGvPILqGtnprj091L6YhKgkbejlhTihrm3Fd52PR+ChsU63EPLLMX4T3pNTJXuCOmEFAzkyHORIm8FpDCwBKUGEE22s5kRvGIoSgIhVlKCuGQJz1W+LoQl5JBpXXVK3o44+6kpGWbuERw9diUq3xF0mdjbB62B4QM/XmMdyGd9j+ZrsOM2GY9f5/uwjF1ZcP1WDo5jLM8+YlNB51i4jIv7/IhAlkj74PqVJUT/qr+W0UACpgfDeTMggG8Dc7Q=",
                "ewogICJ0aW1lc3RhbXAiIDogMTY5MTU2ODc1NjcyOSwKICAicHJvZmlsZUlkIiA6ICI0YjJlMGM1ODliZjU0ZTk1OWM1ZmJlMzg5MjQ1MzQzZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJfTmVvdHJvbl8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjAwMzhmNTU5MDBjNTgzZjJhNzE3NWE1MDFhNTU1MWE2ZjBlNjM4OGVkYzkyNzBhZjk2NDk4N2YzYjNmMTNjNSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
        );
        npc.addTrait(BountyMenuTrait.class);
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK");
        hologramTrait.setLine(1, ChatColor.AQUA + "Bounty Hunter");

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

    public static void createItemMichaelNPC() {
        registerTrait(ItemMichaelTrait.class, "ItemMichaelTrait");

        NPC npc = npcRegistry.createNPC(EntityType.VILLAGER, "item-michael");
        npc.getOrAddTrait(VillagerProfession.class).setProfession(Villager.Profession.TOOLSMITH);
        npc.addTrait(ItemMichaelTrait.class);
        npc.getOrAddTrait(LookClose.class)
           .toggle();
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK");
        hologramTrait.setLine(1, ChatColor.GREEN + "Mysterious Michael");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2528, 50, 770, 125, 0));
    }

    public static void createItemEnyaNPC() {
        registerTrait(ItemEnyaTrait.class, "ItemEnyaTrait");

        NPC npc = npcRegistry.createNPC(EntityType.BLAZE, "item-enya");
        npc.data().set("swim", false);
        npc.addTrait(ItemEnyaTrait.class);
        npc.getOrAddTrait(LookClose.class)
           .toggle();
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK");
        hologramTrait.setLine(1, ChatColor.GREEN + "Ethical Enya");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2523.5, 50, 764, 90, 0));
    }

    public static void createIllusionVendorNPC() {
        registerTrait(IllusionVendorTrait.class, "IllusionVendorTrait");

        NPC npc = npcRegistry.createNPC(EntityType.IRON_GOLEM, "illusion-vendor");
        npc.addTrait(IllusionVendorTrait.class);

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2557.5, 50, 769.5, 180, 0));
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
        npc.getOrAddTrait(LookClose.class)
           .toggle();

        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2528.5, 50, 757.5, 90, 0));
    }

    public static void createMysteriousTokenNPC() {
        registerTrait(MysteriousTokenTrait.class, "MysteriousTokenTrait");

        NPC npc = npcRegistry.createNPC(EntityType.ARMOR_STAND, "mysterious-token");
        npc.addTrait(MysteriousTokenTrait.class);

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        ArmorStandTrait armorStandTrait = npc.getOrAddTrait(ArmorStandTrait.class);
        armorStandTrait.setVisible(false);
        armorStandTrait.setGravity(false);
//        armorStandTrait.setMarker(true);
        Equipment equipment = npc.getOrAddTrait(Equipment.class);
        equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.BEDROCK));

        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2532.5, 48.5, 746.8, 90, 0));
    }


}
