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
import com.ebicep.warlords.util.warlords.Utils;
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
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
                    createAnomalyNPC();
                    createRaidNPC();
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

    private static void createAnomalyNPC() {
        registerTrait(AnomalyStartTrait.class, "AnomalyStartTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "anomaly-mode");
        npc.addTrait(AnomalyStartTrait.class);
//        npc.getOrAddTrait(SkinTrait.class).setSkinName("Alexred2522");

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);
        npc.spawn(new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 22.5, 82, 155.5, 122, 0));
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
                    createTreasureHuntVendorNPC();
//                    createMysteriousTokenNPC();
                    createItemEnyaNPC();
                    createIllusionVendorNPC();
                    createPrestigeVendorNPC();
                    createAscendantVendorNPC();
                    createWeeklyItemTraderNPC();
                    createSeasonalVendorNPC();
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

    public static void createSeasonalVendorNPC() {
        registerTrait(SeasonalTraderTrait.class, "SeasonalVendorTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.SNOW_GOLEM, "seasonal-vendor");
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

    public static void createRaidNPC() {
        registerTrait(RaidStartTrait.class, "RaidStartTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "raid-start");
        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(
                "archmc-0a4f85b",
                "Ftv54gQ5wTDf66m2A3OLOLgm27ABCCf6lRlvxvEYAp4E4N8ACPXf85kHufJ8ujIDixZorkr8ZjYWGqbDMvGrw2x8zzI0K07qpcH7A7Cbcet1fSeMNGegsaaI1Dl4GvNllOoyRLcppA9O5YoWBJO7cfCFrbzH9pr8X2114ui2QLHuPIO4uh0UuDbqdhiU48tIdyrOcgHzLyMuCD5Su01x1rkrQ6TY1yl4IM9qfEuAkj6K00urt0cf8NNoxISo57olrCRZm+0WaU8qkkLyIaPiAs929cpcskD0fgDR2Pivy1D+cretD8JJ3TnqpoMgsrFWxhzLsdGBF+esH6EAiWvEXlwrnxzlJfPzKgbQLgooFUSB0UjKxmUqQpGb0qZ5cuLP5JhNx+yz3ruay0Ttg3uvEbDcRcGpgssW3l+kQ/JzD1IH8uMfU6uHDF5by1J2FxAdZfeJjewWMrjDFNAiU4ANhVaOW+19FbaLt+YOePSIOYo72lpy264bP7qG42Lc7TB4j6vjV1AXkRZNNcF7Ir/ko/2b6iWbkzUlG0hd8jWZtm4Asr7va18MZhaxuz3vXP22DO+60AZFYl5+fqp+h8tVyIsaGy1y+R1BV99qPSHO3lgYvMJfYlaYA0x2rGQT6X+xpLm3F+I7dRY3uWezspQXcNnpGZkFb+QOSKalPGmnAjc=",
                "ewogICJ0aW1lc3RhbXAiIDogMTc2MTE2NDY0NDM4NSwKICAicHJvZmlsZUlkIiA6ICJmZjQ3NzI5YmQwZDI0YWYwOThiMTFjMGE3ZTFiMGVlZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJtYXRzY2FuIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzlmNzU3Y2IzNTYwOGRiYjMyMThjYWUxNGE1MTAwM2Y3ZjhhMzdkYzYzMDIyYzJiMGQzOTU1ZmQ5ZjI1YmM0NjIiCiAgICB9CiAgfQp9"
        );
        npc.addTrait(RaidStartTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        Location loc = new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 11.5, 93, 262.5, -180, 0);
        npc.spawn(loc);

        new BukkitRunnable() {
            @Override
            public void run() {
                Utils.playGlobalSound(loc, Sound.AMBIENT_CAVE, 0.3f, 0.5f);
                EffectUtils.displayParticle(Particle.ASH, loc.clone().add(0, 2, 0), 10, 0.5, 0.2, 0.5, 0.001);
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 30);
    }

    public static void createTreasureHuntVendorNPC() {
        registerTrait(TreasureHuntVendorTrait.class, "TreasureHuntVendorTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "treasure-vendor");
        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(
                "Oral Chocolate Coyote",
                "rsVlglpqxqTzn0QwBD5vJagR2jxugEo8f4eamaoAgdZwKBhEIflOvHzgl/a575TeUML8C8S7cL6xy/eoYJIyBdvK7jQxaukxXRaYVIZeZKtI8iiMMfvyTMzLsCZyuPtATlglXNeaukB3EXztINdBXp3XBTkNAZBVUgPmesZnv+hMAMKQTDuCQDzW6f7yTYtLRb3OfhgzgF5nInTVx+DGhf0vr5z6OcHha4+c16VNf27uLPbhFrx+F6HsCYZs6iEALkJNPuNjff5v5kjMUKIeLy9+sCGOe4BwizxZnNOV9FqBbXkk4ii2qTr+4OI6JtaIXc3xcKOCmhKIctGhv0xHMGWws6xDXbcL7hNIzpZKw6lt1A/FyeH6VtCIDPWqZHoe933x325MZuFOp/pvndZHjAatAXMygCXeTCmBZ+jKzOksPHqKWvSJBnJG7AqirgFP1TuysD005kLG5oChOID247HWtN+Z89LdhasIE5RmYM7P3F8qfxahkAgoIdG1yzGzpV2jaC3qMjrO0sMFiSyaftBjrMMQELwXhrl/dRSrU3fZMNDFszHhiuWLzFPPWXvJB20mmeb9PeRlJ0BYZhZPe1DGKJb5+FKfbSa9E68NtIGRK3qbuFMoVWDZa6s16fdr4CWSCHU40z8gWuVM+4tTNVI7KKFP1MaU0SFr/zhzPE0=",
                "ewogICJ0aW1lc3RhbXAiIDogMTczMzk2MjY4MDc0OCwKICAicHJvZmlsZUlkIiA6ICJlMjc5NjliODYyNWY0NDg1YjkyNmM5NTBhMDljMWMwMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaVp6YVhQIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2JmMjk1ZjE3OTc4MjQ1ZDc3MmQ4OTZlZTFmZmZhMWQ2NWEwYWRjNDU2NzJhNmQ1MjA5OTg4OTkxZjVhNTVmNWMiCiAgICB9CiAgfQp9"
        );

        npc.addTrait(TreasureHuntVendorTrait.class);
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setPerPlayer(true);
        lookClose.toggle();

        npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false);

        Location loc = new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), -15.5, 81, 144.5, 0, 0);
        npc.spawn(loc);
    }

    public static void createWeeklyItemTraderNPC() {
        registerTrait(WeeklyItemTraderTrait.class, "WeeklyItemTraderTrait");

        NPC npc = NPC_REGISTRY.createNPC(EntityType.PLAYER, "weekly-item-vendor");
        npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(
                "evil",
                "TLWnAAA8hBVKnVmPG8CsPCqvpwbpSEzejp7+EjHFwZp7nHBlaOLGI0DOn3XApwETtaHKMmuuMRlokpF3YqY1kLxugSr5VqXjCKnA793hye6ANR3jsqHeIAktdpT+zbTxCQ2VHP3VqHGh8G6xyiMYyzk2d6eblxCwnhnZ9bjERE+A8KD/QeL6ufPlpjJaFC/qSSdrMjM/tyW641q9w4rlRCt4UGvyTE9GAt3U/LWKUQLMX4r6cBkG/VNhQpkWyev0vsRsL/19eFQQujJdJa0xUeX0J62cOwd2kc/dzJ/5ZJoim3l1Din8bjzjQfbqfspgU8h5I8v/irUsG5r600/2cqnhYZI8O/ROy+/OkPa5kk9yX5vYu6J3c3i3RIGCiQ8jNbUGrV0rfIoCZhidplsp7V2HzcqNPb3A4fenIKMjlOdb/eSL8AIIgSSccS2GTSDVdCgbL/FXItyvAkUnPCb0tn8kSukiBdqXUaUIo6mjyQ8W6IhDG8H7l5KTznUZwn8gj/ZZ0pCDkrUfFeUKpB+0Drq3AS3lLKbZbDBZPSs3K/WnqmpJ+E2/uTnkQFug69Xy4c1N/7tFPmd81Vcrvz+NUvaPa+kPczbKLRDZXbBB2OzZanFlk6pUL5cRHzDEisWz1pU2OUHUo1TJg0EqkgP5A3U3Vj9CRNYKu4FyZfCuRmw=",
                "ewogICJ0aW1lc3RhbXAiIDogMTY0NTIzODI0MjA4MSwKICAicHJvZmlsZUlkIiA6ICIxNmFkYTc5YjFjMDk0MjllOWEyOGQ5MjgwZDNjNjE5ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJMYXp1bGl0ZV9adG9uZSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mOWU4MWNlYmQ2MjBlNjcyZWJjYTRjYjZiNDg4YTIyNjE1YTU2NTlmYjNkZjdhZjU0YjhkMDc4MGZiZWYzMzQ4IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="
        );

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
