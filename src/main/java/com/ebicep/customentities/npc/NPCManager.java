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
import net.citizensnpcs.trait.ArmorStandTrait;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import net.citizensnpcs.trait.VillagerProfession;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCManager {

    public static final NPCRegistry NPC_REGISTRY = CitizensAPI.createAnonymousNPCRegistry(new MemoryNPCDataStore());
    //https://jd.citizensnpcs.co/net/citizensnpcs/api/npc/NPC.html

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
                createStarPieceSynthesizerNPC();
                createItemEnyaNPC();
                createIllusionVendorNPC();
                createSeasonalVendorNPC();
                createAnomalyNPC();
                createRaidOneNPC();
                createPrestigeVendorNPC();
                createAscendantVendorNPC();
                createWeeklyItemTraderNPC();
                createTreasureHuntVendorNPC();
                createAscendantWeaponNPC();
                createTutorialGuideNPC();
                registerTrait(ReadyUpOption.ReadyUpTrait.class, "ReadyUpTrait");
                ChatUtils.MessageType.GAME.sendMessage("Done adding game join NPCs");
            }
        }.runTask(Warlords.getInstance());
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
        registerTrait(SiegeTrait.class, "SiegeStartTrait");

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

        NPC npc = NPC_REGISTRY.createNPC(EntityType.BREEZE, "item-enya");
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
//        armorStandTrait.setMarker(true);
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

        Location location = new Location(StatsLeaderboardManager.MAIN_LOBBY_SPAWN.getWorld(), 11.5, 82, 155.5, 180, 0);
        npc.spawn(location);
    }
}
