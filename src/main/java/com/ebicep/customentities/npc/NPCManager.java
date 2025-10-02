package com.ebicep.customentities.npc;

import com.ebicep.customentities.npc.traits.*;
import com.ebicep.holograms.Hologram;
import com.ebicep.holograms.HologramDataText;
import com.ebicep.holograms.HologramManager;
import com.ebicep.holograms.VisibilityType;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
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
import net.citizensnpcs.trait.*;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

public class NPCManager {

    public static final NPCRegistry NPC_REGISTRY = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());
    //https://jd.citizensnpcs.co/net/citizensnpcs/api/npc/NPC.html

    public static void createGameJoinNPCs() {
        if (!Warlords.citizensEnabled) {
            return;
        }
        ChatUtils.MessageType.GAME.sendMessage("Adding game join NPCs...");
        Warlords.newChain()
                .sync(() -> {
                    createCTFNPC();
                    createSiegeNPC();
//                    createTeamDeathmatchNPC();
//                    createInterceptionNPC();
                    createWaveDefenseNPC();
                    createOnslaughtNPC();
                    createTreasureHuntNPC();
//                    createBossRushNPC();
                })
                .execute();

        registerTrait(ReadyUpOption.ReadyUpTrait.class, "ReadyUpTrait");
    }

    private static void createCTFNPC() {
        registerTrait(CaptureTheFlagTrait.class, "GameStartTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "capture-the-flag");
        npc.addTrait(CaptureTheFlagTrait.class);
//        npc.getOrAddTrait(SkinTrait.class).setSkinName("Chessking345");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 17.5, 82, 138.5, 45, 0));
    }

    private static void createSiegeNPC() {
        registerTrait(SiegeTrait.class, "GameStartTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "siege");
        npc.addTrait(SiegeTrait.class);
//        npc.getOrAddTrait(SkinTrait.class).setSkinName("");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 20.5, 82, 140.5, 45, 0));
    }

    private static void createWaveDefenseNPC() {
        registerTrait(PvEStartTrait.class, "PveStartTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "pve-mode");
        npc.addTrait(PvEStartTrait.class);
//        npc.getOrAddTrait(SkinTrait.class).setSkinName("Plikie");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 17.5, 82, 160.5, 135, 0));
    }

    private static void createOnslaughtNPC() {
        registerTrait(OnslaughtStartTrait.class, "OnslaughtStartTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "onslaught-mode");
        npc.addTrait(OnslaughtStartTrait.class);
//        npc.getOrAddTrait(SkinTrait.class).setSkinName("Heatran");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 5.5, 82, 160.5, -135, 0));
    }

    public static void createTeamDeathmatchNPC() {
        registerTrait(TeamDeathmatchTrait.class, "TeamDeathmatchTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "team-deathmatch");
        npc.addTrait(TeamDeathmatchTrait.class);
//        npc.getOrAddTrait(SkinTrait.class).setSkinName("Richdragon123");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 2.5, 82, 140.5, -45, 0));
    }

    public static void registerTrait(Class<? extends Trait> trait, String traitName) {
        if (CitizensAPI.getTraitFactory().getTrait(traitName) != null) {
            CitizensAPI.getTraitFactory().deregisterTrait(TraitInfo.create(trait).withName(traitName));
        }
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(trait).withName(traitName));
    }

    public static void createInterceptionNPC() {
        registerTrait(InterceptionTrait.class, "InterceptionTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "interception");
        npc.addTrait(InterceptionTrait.class);
//        npc.getOrAddTrait(SkinTrait.class).setSkinName("AwesomeRaki");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 5.5, 82, 138.5, -45, 0));
    }

    private static void createTreasureHuntNPC() {
        registerTrait(TreasureHuntStartTrait.class, "TreasureHuntStartTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "treasure-hunt-mode");
        npc.addTrait(TreasureHuntStartTrait.class);
//        npc.getOrAddTrait(SkinTrait.class).setSkinName("Alexred2522");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 20.5, 82, 158.5, 135, 0));
    }

    private static void createBossRushNPC() {
        registerTrait(BossRushStartTrait.class, "BossRushStartTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "boss-rush-mode");
        npc.addTrait(BossRushStartTrait.class);
//        npc.getOrAddTrait(SkinTrait.class).setSkinName("Stratfull");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 2.5, 82, 158.5, -135, 0));
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
                    createAscendantWeaponNPC();
                    createSupplyDropFairNPC();
                    createBountyMenuNPC();
                    createStarPieceSynthesizerNPC();
//                    createMysteriousTokenNPC();
                    createItemEnyaNPC();
                    createIllusionVendorNPC();
                    createPrestigeVendorNPC();
                    createAscendantVendorNPC();
                })
                .execute();
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

        HologramDataText hologramDataText = new HologramDataText.Builder<>(ComponentBuilder.create(
                "The Weaponsmith",
                NamedTextColor.GREEN
        ).build()).setBillboard(Display.Billboard.CENTER).build();
        HologramManager.addHologram(new Hologram.Builder(
                        "theWeaponsmith",
                        location.clone().add(0, 2.1, 0),
                        player -> hologramDataText
                ).setVisibility(VisibilityType.ALL).build()
        );
    }

    public static void createLegendaryWeaponNPC() {
        registerTrait(LegendaryWeaponTrait.class, "LegendaryWeaponTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.WITCH, "legendary-weapon");
        npc.addTrait(LegendaryWeaponTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), -14.5, 145, 220.5, -180, 0));

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

        HologramDataText hologramDataText = new HologramDataText.Builder<>(ComponentBuilder.create(
                "Supply Drop Susan",
                NamedTextColor.GREEN
        ).build()).setBillboard(Display.Billboard.CENTER).build();
        HologramManager.addHologram(new Hologram.Builder(
                        "supplyDropSusan",
                        location.clone().add(0, .6, 0),
                        player -> hologramDataText
                ).setVisibility(VisibilityType.ALL).build()
        );
    }

    public static void createBountyMenuNPC() {
        registerTrait(BountyMenuTrait.class, "BountyMenuTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "bounty-menu");
        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(
                "Freminet",
                "h4f/F6TIQWBP8JZfLtour95b5+FKGiWufKtEauAgpCeFR4nOAlzlh8WpXBeGBo/6CRB4vtmqa71T5kBvSkbxZ151AnIhJ/f8ReQBmQCmlsi4AOcLXDJIzkrZ7SyUXATSbzxXomjqoJKbu4aljSccKTq/FaOVY2gMQsEdgYefF6frLrXJ3rtKUcbkRmrUG/GqkASrp0I3eZ6GCKDlkSkShHOdmu2ELd2LNOI656/DSSCVkDjeZ5sR/fNaKMJN6ubefTpwOTPxJV0NvzzUf1uxggOoKc+NyowKYPXqmI0qnvPiXCwsQ8oyTGrei2GAouwZPddVZMdioz2eAUJZfbAC7M+CdKh1FNkbKn4mNHj9g0pTLyZaeoKVMqmD89oseZ0TO6NQ3Cgpufes+/M+yj9ikktixfZg6Kl1hN01S+ZQAQ2qixkGvPILqGtnprj091L6YhKgkbejlhTihrm3Fd52PR+ChsU63EPLLMX4T3pNTJXuCOmEFAzkyHORIm8FpDCwBKUGEE22s5kRvGIoSgIhVlKCuGQJz1W+LoQl5JBpXXVK3o44+6kpGWbuERw9diUq3xF0mdjbB62B4QM/XmMdyGd9j+ZrsOM2GY9f5/uwjF1ZcP1WDo5jLM8+YlNB51i4jIv7/IhAlkj74PqVJUT/qr+W0UACpgfDeTMggG8Dc7Q=",
                "ewogICJ0aW1lc3RhbXAiIDogMTY5MTU2ODc1NjcyOSwKICAicHJvZmlsZUlkIiA6ICI0YjJlMGM1ODliZjU0ZTk1OWM1ZmJlMzg5MjQ1MzQzZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJfTmVvdHJvbl8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjAwMzhmNTU5MDBjNTgzZjJhNzE3NWE1MDFhNTU1MWE2ZjBlNjM4OGVkYzkyNzBhZjk2NDk4N2YzYjNmMTNjNSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
        );
        npc.addTrait(BountyMenuTrait.class);

        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        Location location = new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 29.5, 81, 166.5, 90, 0);
        npc.spawn(location);

        HologramDataText hologramDataText = new HologramDataText.Builder<>(ComponentBuilder.create(
                "Bounty Hunter",
                NamedTextColor.AQUA
        ).build()).setBillboard(Display.Billboard.CENTER).build();
        HologramManager.addHologram(new Hologram.Builder(
                        "bountyHunter",
                        location.clone().add(0, 2.1, 0),
                        player -> hologramDataText
                ).setVisibility(VisibilityType.ALL).build()
        );
    }

    public static void createStarPieceSynthesizerNPC() {
        registerTrait(StarPieceSynthesizerTrait.class, "StarPieceSynthesizerTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.END_CRYSTAL, "star-piece-synthesizer");
        npc.addTrait(StarPieceSynthesizerTrait.class);

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 40.5, 74, 169, 0, 0));
    }

    public static void createItemEnyaNPC() {
        registerTrait(ItemEnyaTrait.class, "ItemEnyaTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.VILLAGER, "item-enya");
        npc.addTrait(ItemEnyaTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        Location location = new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 28.5, 93, 208.5, 90, 0);
        npc.spawn(location);
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


    public static void createAscendantVendorNPC() {
        registerTrait(AscendantVendorTrait.class, "AscendantVendorTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "ascendant-vendor");
        npc.getOrAddTrait(SkinTrait.class).setTexture(
                "ewogICJ0aW1lc3RhbXAiIDogMTc1OTI0OTUxMTgwOSwKICAicHJvZmlsZUlkIiA6ICJmODJmMTUyNWE3Zjk0M2RjOTIyYzM1MWZhZTJjZmFmMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJ3aWVzeiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iMDRlYmExZTI1NjFlMGYxMzdmOGM2Mzg4OTdhYWQ2ZGE3NDYyYzg3MDI4YzRmMmZmYWU3Mzg5NDE3ODM5NmFhIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                "k20/9mBe5vNBTQj2vuwWx3Q5TW1y1EtKK504S+dUnpBNTe8U7Fu40uy6O0DAM7ZiOcSkpsxxtRPPj6j9nxTPzLKEPgu26qp+9kHX3NWTBDBNsnPUdW1p9VwyfuXRucHLMqKJTRaXqhjABKQtLmFMNVW6cea5dbdCD+rGa3U6NQdlj0GEu1nff92d2Eh3LxmZEIhdgoxVlbFIEKOAziOv9kuSIZpok46ntnIEomz7+btI43spA8nVCgjomjl6/eOiBg9/fGOFGEx8bdSMnHeC/Ck1JUMsO6STu5OurM3RmLdkKL3JCXt2LkCBggiXW900Ik24JRtxp/AUule0TLhu4rFRwDfWgxPCoLEfPAye2fQMxeYU7URxwAjiARyi4q4QChuPavWMXi6zpOCzMRTrAl4zM8+aaSzZIaKLT6M2rdO2kVU8jrv6PcjLRkxxmPQm2hi6nwwfbS92yWqD4a/SpBlVtNHZGudyX8sCnG2jFkDrpPX0DD15QjT4Fjex+T4nhPq8FDt/NHfcAFYfLvPapiG0ZRbZInFu+/yXzXzgGkutttrRzHmWEQw7sjkpQHOqu09yWBI6Q2TvHCfoKMZBni69Epl7jk1Mnnun4VO/skQysc3LwLIvb026giq7iP7ikljhZXIP38Ka0Z5NWIXC8KYzjHmq4MUCkCAjsF94qLQ="
        );
        npc.addTrait(AscendantVendorTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 20.5, 93, 208.5, -90, 0));
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
//        armorStandTrait.setMarker(true);
        Equipment equipment = npc.getOrAddTrait(Equipment.class);
        equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.BEDROCK));

        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), -2532.5, 48.5, 746.8, 90, 0));
    }


}
