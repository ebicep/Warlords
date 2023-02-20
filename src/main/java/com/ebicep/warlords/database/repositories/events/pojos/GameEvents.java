package com.ebicep.warlords.database.repositories.events.pojos;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.customentities.npc.traits.GameEventTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.events.EventLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePvEEventBoltaroLair;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabasePlayerPvEEventStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabasePlayerPvEEventBoltaroDifficultyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.DatabasePlayerPvEEventNarmerDifficultyStats;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.PreWeaponSalvageEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.wavedefense.events.modes.BoltaroBonanzaOption;
import com.ebicep.warlords.game.option.wavedefense.events.modes.BoltarosLairOption;
import com.ebicep.warlords.game.option.wavedefense.events.modes.NarmersTombOption;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.TriFunction;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;

import static com.ebicep.warlords.menu.Menu.*;

public enum GameEvents {

    BOLTARO("Fighter’s Glory",
            Currencies.EVENT_POINTS_BOLTARO,
            DatabasePlayerPvEEventStats::getBoltaroStats,
            DatabasePlayerPvEEventStats::getBoltaroEventStats,
            DatabasePlayerPvEEventStats::getBoltaroStats,
            (game, warlordsGameTriggerWinEvent, aBoolean) -> {
                for (Option option : game.getOptions()) {
                    if (option instanceof BoltarosLairOption) {
                        return new DatabaseGamePvEEventBoltaroLair(game, warlordsGameTriggerWinEvent, aBoolean);
                    } else if (option instanceof BoltaroBonanzaOption) {
                        return new DatabaseGamePvEEventBoltaroBonanza(game, warlordsGameTriggerWinEvent, aBoolean);
                    }
                }
                return null;
            },
            new ArrayList<>() {{
                add(new EventShopReward(1, Currencies.TITLE_TOKEN_JUGGERNAUT, 1, 500_000));
                add(new EventShopReward(10, Currencies.SUPPLY_DROP_TOKEN, 20, 20_000));
                add(new EventShopReward(100_000, Currencies.COIN, 5, 100_000));
                add(new EventShopReward(500, Currencies.LEGEND_FRAGMENTS, 5, 100_000));
                add(new EventShopReward(200, Currencies.FAIRY_ESSENCE, 5, 50_000));
                add(new EventShopReward(1_000, Currencies.SYNTHETIC_SHARD, 5, 50_000));
                add(new EventShopReward(1, Currencies.EPIC_STAR_PIECE, 1, 500_000));
                add(new EventShopReward(1_000, Currencies.COIN, -1, 10_000));
            }}
    ) {
        @Override
        public LinkedHashMap<Currencies, Long> getRewards(int position) {
            if (position == 1) {
                return new LinkedHashMap<>() {{
                    put(Currencies.COIN, 500_000L);
                    put(Currencies.SUPPLY_DROP_TOKEN, 500L);
                    put(Currencies.LEGEND_FRAGMENTS, 5_000L);
                    put(Currencies.FAIRY_ESSENCE, 1_000L);
                    put(Currencies.EPIC_STAR_PIECE, 3L);
                    put(Currencies.TITLE_TOKEN_JUGGERNAUT, 1L);
                }};
            }
            if (position == 2) {
                return new LinkedHashMap<>() {{
                    put(Currencies.COIN, 300_000L);
                    put(Currencies.SUPPLY_DROP_TOKEN, 300L);
                    put(Currencies.LEGEND_FRAGMENTS, 3_000L);
                    put(Currencies.FAIRY_ESSENCE, 1_000L);
                    put(Currencies.EPIC_STAR_PIECE, 2L);
                    put(Currencies.TITLE_TOKEN_JUGGERNAUT, 1L);
                }};
            }
            if (position == 3) {
                return new LinkedHashMap<>() {{
                    put(Currencies.COIN, 200_000L);
                    put(Currencies.SUPPLY_DROP_TOKEN, 200L);
                    put(Currencies.LEGEND_FRAGMENTS, 2_000L);
                    put(Currencies.FAIRY_ESSENCE, 1_000L);
                    put(Currencies.EPIC_STAR_PIECE, 1L);
                    put(Currencies.TITLE_TOKEN_JUGGERNAUT, 1L);
                }};
            }
            if (4 <= position && position <= 10) {
                return new LinkedHashMap<>() {{
                    put(Currencies.COIN, 100_000L);
                    put(Currencies.SUPPLY_DROP_TOKEN, 100L);
                    put(Currencies.LEGEND_FRAGMENTS, 1000L);
                    put(Currencies.FAIRY_ESSENCE, 500L);
                    put(Currencies.RARE_STAR_PIECE, 5L);
                }};
            }
            if (11 <= position && position <= 20) {
                return new LinkedHashMap<>() {{
                    put(Currencies.COIN, 50_000L);
                    put(Currencies.SUPPLY_DROP_TOKEN, 50L);
                    put(Currencies.LEGEND_FRAGMENTS, 500L);
                    put(Currencies.FAIRY_ESSENCE, 500L);
                    put(Currencies.RARE_STAR_PIECE, 2L);
                }};
            }
            if (21 <= position && position <= 50) {
                return new LinkedHashMap<>() {{
                    put(Currencies.COIN, 25_000L);
                    put(Currencies.SUPPLY_DROP_TOKEN, 25L);
                    put(Currencies.RARE_STAR_PIECE, 1L);
                }};
            }
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 10_000L);
                put(Currencies.SUPPLY_DROP_TOKEN, 10L);
                put(Currencies.COMMON_STAR_PIECE, 1L);
            }};
        }

        @Override
        public LinkedHashMap<String, Long> getGuildRewards(int position) {
            if (position == 1) {
                return new LinkedHashMap<>() {{
                    put("Coins", 150_000L);
                    put("Experience", 150_000L);
                }};
            }
            if (position == 2) {
                return new LinkedHashMap<>() {{
                    put("Coins", 100_000L);
                    put("Experience", 100_000L);
                }};
            }
            if (position == 3) {
                return new LinkedHashMap<>() {{
                    put("Coins", 75_000L);
                    put("Experience", 75_000L);
                }};
            }
            if (4 <= position && position <= 10) {
                return new LinkedHashMap<>() {{
                    put("Coins", 50_000L);
                    put("Experience", 50_000L);
                }};
            }
            return new LinkedHashMap<>() {{
                put("Coins", 20_000L);
                put("Experience", 20_000L);
            }};
        }

        @Override
        public void addLeaderboards(DatabaseGameEvent currentGameEvent, HashMap<EventLeaderboard, String> leaderboards) {
            long eventStart = currentGameEvent.getStartDateSecond();
            EventLeaderboard lairBoard = new EventLeaderboard(
                    eventStart,
                    "Highest Game Event Points",
                    new Location(StatsLeaderboardLocations.CENTER.getWorld(), -2539.5, 55, 751.5),
                    (databasePlayer, time) -> databasePlayer
                            .getPveStats()
                            .getEventStats()
                            .getBoltaroEventStats()
                            .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                            .getLairStats()
                            .getHighestEventPointsGame(),
                    (databasePlayer, time) -> NumberFormat.addCommaAndRound(databasePlayer
                            .getPveStats()
                            .getEventStats()
                            .getBoltaroEventStats()
                            .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                            .getLairStats()
                            .getHighestEventPointsGame())
            );
            EventLeaderboard bonanzaBoard = new EventLeaderboard(
                    eventStart,
                    "Highest Game Event Points",
                    new Location(StatsLeaderboardLocations.CENTER.getWorld(), -2539.5, 55, 757.5),
                    (databasePlayer, time) -> databasePlayer
                            .getPveStats()
                            .getEventStats()
                            .getBoltaroEventStats()
                            .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                            .getBonanzaStats()
                            .getHighestEventPointsGame(),
                    (databasePlayer, time) -> NumberFormat.addCommaAndRound(databasePlayer
                            .getPveStats()
                            .getEventStats()
                            .getBoltaroEventStats()
                            .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                            .getBonanzaStats()
                            .getHighestEventPointsGame())
            );
            EventLeaderboard totalBoard = new EventLeaderboard(
                    eventStart,
                    "Event Points",
                    new Location(StatsLeaderboardLocations.CENTER.getWorld(), -2539.5, 55, 737.5),
                    (databasePlayer, time) -> databasePlayer
                            .getPveStats()
                            .getEventStats()
                            .getBoltaroEventStats()
                            .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                            .getEventPointsCumulative(),
                    (databasePlayer, time) -> NumberFormat.addCommaAndRound(databasePlayer
                            .getPveStats()
                            .getEventStats()
                            .getBoltaroEventStats()
                            .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                            .getEventPointsCumulative())
            );
            leaderboards.put(lairBoard, "Boltaro's Lair");
            leaderboards.put(bonanzaBoard, "Boltaro Bonanza");
            leaderboards.put(totalBoard, "Total Event Points");
        }

        @Override
        public void editNPC(NPC npc) {
            Equipment equipment = npc.getOrAddTrait(Equipment.class);
            equipment.set(Equipment.EquipmentSlot.HELMET, SkullUtils.getSkullFrom(SkullID.DEMON));
            equipment.set(Equipment.EquipmentSlot.CHESTPLATE, Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0));
            equipment.set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.CHAINMAIL_LEGGINGS));
            equipment.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.CHAINMAIL_BOOTS));
            equipment.set(Equipment.EquipmentSlot.HAND, Weapons.DRAKEFANG.getItem());
        }

        @Override
        public void setMenu(Menu menu) {
            menu.setItem(2, 1,
                    new ItemBuilder(Material.BLAZE_POWDER)
                            .name(ChatColor.GREEN + "Start a private Boltaro event game")
                            .get(),
                    (m, e) -> openBoltaroModeMenu((Player) e.getWhoClicked(), true)
            );
            menu.setItem(6, 1,
                    new ItemBuilder(Material.REDSTONE_COMPARATOR)
                            .name(ChatColor.GREEN + "Join a public Boltaro event game")
                            .get(),
                    (m, e) -> openBoltaroModeMenu((Player) e.getWhoClicked(), false)
            );
        }

        private void openBoltaroModeMenu(Player player, boolean privateGame) {
            Menu menu = new Menu("Fighter’s Glory Modes", 9 * 4);

            menu.setItem(2, 1,
                    new ItemBuilder(Material.IRON_FENCE)
                            .name(ChatColor.GREEN + "Boltaro’s Lair")
                            .lore(
                                    ChatColor.YELLOW + "Do you have what it takes to be a fighter?",
                                    "",
                                    ChatColor.GRAY + "Game Duration: " + ChatColor.GREEN + "600 Seconds",
                                    ChatColor.GRAY + "Player Capacity: " + ChatColor.GREEN + "2-4 Players"
                            )
                            .get(),
                    (m, e) -> {
                        if (privateGame) {
                            GameStartCommand.startGamePvEEvent(player,
                                    queueEntryBuilder -> queueEntryBuilder.setMap(GameMap.ILLUSION_RIFT_EVENT_1).setRequestedGameAddons(GameAddon.PRIVATE_GAME)

                            );
                        } else {
                            GameStartCommand.startGamePvEEvent(player, queueEntryBuilder -> queueEntryBuilder.setMap(GameMap.ILLUSION_RIFT_EVENT_1));
                        }
                    }
            );
            menu.setItem(6, 1,
                    new ItemBuilder(SkullUtils.getSkullFrom(SkullID.DEMON))
                            .name(ChatColor.GREEN + "Boltaro Bonanza")
                            .lore(
                                    ChatColor.YELLOW + "Kill as many Boltaro as possible!",
                                    "",
                                    ChatColor.GRAY + "Game Duration: " + ChatColor.GREEN + "200 Seconds",
                                    ChatColor.GRAY + "Player Capacity: " + ChatColor.GREEN + "2-4 Players"
                            )
                            .get(),
                    (m, e) -> {
                        if (privateGame) {
                            GameStartCommand.startGamePvEEvent(player,
                                    queueEntryBuilder -> queueEntryBuilder.setMap(GameMap.ILLUSION_RIFT_EVENT_2).setRequestedGameAddons(GameAddon.PRIVATE_GAME)

                            );
                        } else {
                            GameStartCommand.startGamePvEEvent(player, queueEntryBuilder -> queueEntryBuilder.setMap(GameMap.ILLUSION_RIFT_EVENT_2));
                        }
                    }
            );

            menu.setItem(4, 3, MENU_BACK, (m, e) -> openMenu(player));
            menu.openForPlayer(player);
        }
    },
    NARMER("Pharaoh's Revenge",
            Currencies.EVENT_POINTS_NARMER,
            DatabasePlayerPvEEventStats::getNarmerStats,
            DatabasePlayerPvEEventStats::getNarmerEventStats,
            DatabasePlayerPvEEventStats::getNarmerStats,
            (game, warlordsGameTriggerWinEvent, aBoolean) -> {
                for (Option option : game.getOptions()) {
                    if (option instanceof NarmersTombOption) {
                        return new DatabaseGamePvEEventNarmersTomb(game, warlordsGameTriggerWinEvent, aBoolean);
                    }
                }
                return null;
            },
            new ArrayList<>() {{
                add(new EventShopReward(1, Currencies.TITLE_TOKEN_PHARAOHS_REVENGE, 3, 300_000));
                add(new EventShopReward(10, Currencies.SUPPLY_DROP_TOKEN, 20, 20_000));
                add(new EventShopReward(100_000, Currencies.COIN, 5, 100_000));
                add(new EventShopReward(500, Currencies.LEGEND_FRAGMENTS, 5, 150_000));
                add(new EventShopReward(200, Currencies.FAIRY_ESSENCE, 5, 50_000));
                add(new EventShopReward(1_000, Currencies.SYNTHETIC_SHARD, 5, 100_000));
                add(new EventShopReward(1, Currencies.EPIC_STAR_PIECE, 1, 500_000));
                add(new EventShopReward(1_000, Currencies.COIN, -1, 8_000));
                add(new EventShopReward(10, Currencies.SYNTHETIC_SHARD, -1, 10_000));
                add(new EventShopReward(3, Currencies.LEGEND_FRAGMENTS, -1, 10_000));
                add(new EventShopReward(3, Currencies.SKILL_BOOST_MODIFIER, 3, 75_000));
                add(new EventShopReward(1, Currencies.LIMIT_BREAKER, 1, 500_000));
            }}
    ) {
        @Override
        public void initialize() {
            super.initialize();
            Warlords.getInstance().getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onPreWeaponSalvage(PreWeaponSalvageEvent event) {
                    event.getSalvageAmount().getAndUpdate(operand -> (int) (operand * 1.25));
                }
            }, Warlords.getInstance());
        }

        @Override
        public LinkedHashMap<Currencies, Long> getRewards(int position) {
            if (position == 1) {
                return new LinkedHashMap<>() {{
                    put(Currencies.COIN, 500_000L);
                    put(Currencies.SUPPLY_DROP_TOKEN, 500L);
                    put(Currencies.LEGEND_FRAGMENTS, 5_000L);
                    put(Currencies.FAIRY_ESSENCE, 1_000L);
                    put(Currencies.EPIC_STAR_PIECE, 3L);
                    put(Currencies.LIMIT_BREAKER, 1L);
                    put(Currencies.TITLE_TOKEN_PHARAOHS_REVENGE, 5L);
                }};
            }
            if (position == 2) {
                return new LinkedHashMap<>() {{
                    put(Currencies.COIN, 300_000L);
                    put(Currencies.SUPPLY_DROP_TOKEN, 300L);
                    put(Currencies.LEGEND_FRAGMENTS, 3_000L);
                    put(Currencies.FAIRY_ESSENCE, 1_000L);
                    put(Currencies.EPIC_STAR_PIECE, 2L);
                    put(Currencies.LIMIT_BREAKER, 1L);
                    put(Currencies.TITLE_TOKEN_PHARAOHS_REVENGE, 3L);
                }};
            }
            if (position == 3) {
                return new LinkedHashMap<>() {{
                    put(Currencies.COIN, 200_000L);
                    put(Currencies.SUPPLY_DROP_TOKEN, 200L);
                    put(Currencies.LEGEND_FRAGMENTS, 2_000L);
                    put(Currencies.FAIRY_ESSENCE, 1_000L);
                    put(Currencies.EPIC_STAR_PIECE, 1L);
                    put(Currencies.LIMIT_BREAKER, 1L);
                    put(Currencies.TITLE_TOKEN_PHARAOHS_REVENGE, 2L);
                }};
            }
            if (4 <= position && position <= 10) {
                return new LinkedHashMap<>() {{
                    put(Currencies.COIN, 100_000L);
                    put(Currencies.SUPPLY_DROP_TOKEN, 100L);
                    put(Currencies.LEGEND_FRAGMENTS, 1000L);
                    put(Currencies.FAIRY_ESSENCE, 500L);
                    put(Currencies.RARE_STAR_PIECE, 5L);
                    put(Currencies.LIMIT_BREAKER, 1L);
                    put(Currencies.TITLE_TOKEN_PHARAOHS_REVENGE, 1L);
                }};
            }
            if (11 <= position && position <= 20) {
                return new LinkedHashMap<>() {{
                    put(Currencies.COIN, 50_000L);
                    put(Currencies.SUPPLY_DROP_TOKEN, 50L);
                    put(Currencies.LEGEND_FRAGMENTS, 500L);
                    put(Currencies.FAIRY_ESSENCE, 500L);
                    put(Currencies.RARE_STAR_PIECE, 2L);
                }};
            }
            if (21 <= position && position <= 50) {
                return new LinkedHashMap<>() {{
                    put(Currencies.COIN, 25_000L);
                    put(Currencies.SUPPLY_DROP_TOKEN, 25L);
                    put(Currencies.RARE_STAR_PIECE, 1L);
                }};
            }
            return new LinkedHashMap<>() {{
                put(Currencies.COIN, 10_000L);
                put(Currencies.SUPPLY_DROP_TOKEN, 10L);
                put(Currencies.COMMON_STAR_PIECE, 1L);
            }};
        }

        @Override
        public LinkedHashMap<String, Long> getGuildRewards(int position) {
            if (position == 1) {
                return new LinkedHashMap<>() {{
                    put("Coins", 150_000L);
                    put("Experience", 150_000L);
                }};
            }
            if (position == 2) {
                return new LinkedHashMap<>() {{
                    put("Coins", 100_000L);
                    put("Experience", 100_000L);
                }};
            }
            if (position == 3) {
                return new LinkedHashMap<>() {{
                    put("Coins", 75_000L);
                    put("Experience", 75_000L);
                }};
            }
            if (4 <= position && position <= 10) {
                return new LinkedHashMap<>() {{
                    put("Coins", 50_000L);
                    put("Experience", 50_000L);
                }};
            }
            return new LinkedHashMap<>() {{
                put("Coins", 20_000L);
                put("Experience", 20_000L);
            }};
        }

        @Override
        public void addLeaderboards(DatabaseGameEvent currentGameEvent, HashMap<EventLeaderboard, String> leaderboards) {
            long eventStart = currentGameEvent.getStartDateSecond();
            EventLeaderboard lairBoard = new EventLeaderboard(
                    eventStart,
                    "Highest Game Event Points",
                    new Location(StatsLeaderboardLocations.CENTER.getWorld(), -2539.5, 55, 751.5),
                    (databasePlayer, time) -> databasePlayer
                            .getPveStats()
                            .getEventStats()
                            .getNarmerEventStats()
                            .getOrDefault(eventStart, new DatabasePlayerPvEEventNarmerDifficultyStats())
                            .getTombStats()
                            .getHighestEventPointsGame(),
                    (databasePlayer, time) -> NumberFormat.addCommaAndRound(databasePlayer
                            .getPveStats()
                            .getEventStats()
                            .getNarmerEventStats()
                            .getOrDefault(eventStart, new DatabasePlayerPvEEventNarmerDifficultyStats())
                            .getTombStats()
                            .getHighestEventPointsGame())
            );
            EventLeaderboard totalBoard = new EventLeaderboard(
                    eventStart,
                    "Event Points",
                    new Location(StatsLeaderboardLocations.CENTER.getWorld(), -2539.5, 55, 737.5),
                    (databasePlayer, time) -> databasePlayer
                            .getPveStats()
                            .getEventStats()
                            .getNarmerEventStats()
                            .getOrDefault(eventStart, new DatabasePlayerPvEEventNarmerDifficultyStats())
                            .getEventPointsCumulative(),
                    (databasePlayer, time) -> NumberFormat.addCommaAndRound(databasePlayer
                            .getPveStats()
                            .getEventStats()
                            .getNarmerEventStats()
                            .getOrDefault(eventStart, new DatabasePlayerPvEEventNarmerDifficultyStats())
                            .getEventPointsCumulative())
            );
            leaderboards.put(lairBoard, "Narmer's Tomb");
            leaderboards.put(totalBoard, "Total Event Points");
        }

        @Override
        public void editNPC(NPC npc) {
            Equipment equipment = npc.getOrAddTrait(Equipment.class);
            equipment.set(Equipment.EquipmentSlot.HELMET, SkullUtils.getSkullFrom(SkullID.BURNING_WITHER_SKELETON));
            equipment.set(Equipment.EquipmentSlot.CHESTPLATE, Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160));
            equipment.set(Equipment.EquipmentSlot.LEGGINGS, ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed);
            equipment.set(Equipment.EquipmentSlot.BOOTS, Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160));
            equipment.set(Equipment.EquipmentSlot.HAND, Weapons.WALKING_STICK.getItem());
        }

        @Override
        public void setMenu(Menu menu) {
            menu.setItem(2, 1,
                    new ItemBuilder(Material.BLAZE_POWDER)
                            .name(ChatColor.GREEN + "Start a private Narmer event game")
                            .get(),
                    (m, e) -> openNarmerModeMenu((Player) e.getWhoClicked(), true)
            );
            menu.setItem(6, 1,
                    new ItemBuilder(Material.REDSTONE_COMPARATOR)
                            .name(ChatColor.GREEN + "Join a public Narmer event game")
                            .get(),
                    (m, e) -> openNarmerModeMenu((Player) e.getWhoClicked(), false)
            );
        }

        private void openNarmerModeMenu(Player player, boolean privateGame) {
            Menu menu = new Menu("Pharaoh's Revenge Modes", 9 * 4);

            menu.setItem(2, 1,
                    new ItemBuilder(Material.BONE)
                            .name(ChatColor.GREEN + "Narmer’s Tomb")
                            .lore(
                                    ChatColor.YELLOW + "Something spooky here...",
                                    "",
                                    ChatColor.GRAY + "Game Duration: " + ChatColor.GREEN + "600 Seconds",
                                    ChatColor.GRAY + "Player Capacity: " + ChatColor.GREEN + "2-4 Players"
                            )
                            .get(),
                    (m, e) -> {
                        if (privateGame) {
                            GameStartCommand.startGamePvEEvent(player,
                                    queueEntryBuilder -> queueEntryBuilder.setMap(GameMap.ILLUSION_RIFT_EVENT_3).setRequestedGameAddons(GameAddon.PRIVATE_GAME)

                            );
                        } else {
                            GameStartCommand.startGamePvEEvent(player, queueEntryBuilder -> queueEntryBuilder.setMap(GameMap.ILLUSION_RIFT_EVENT_3));
                        }
                    }
            );

            menu.setItem(4, 3, MENU_BACK, (m, e) -> openMenu(player));
            menu.openForPlayer(player);
        }
    };

    public static final GameEvents[] VALUES = values();
    public static NPC npc;

    public final String name;
    public final Currencies currency;
    public final Function<DatabasePlayerPvEEventStats, AbstractDatabaseStatInformation> updateStatsFunction;
    public final Function<DatabasePlayerPvEEventStats, Map<Long, ? extends EventMode>> eventsStatsFunction;
    public final Function<DatabasePlayerPvEEventStats, ? extends EventMode> generalEventFunction;
    public final TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGamePvEEvent> createDatabaseGame;
    public final List<EventShopReward> shopRewards;

    GameEvents(
            String name,
            Currencies currency,
            Function<DatabasePlayerPvEEventStats, AbstractDatabaseStatInformation> updateStatsFunction,
            Function<DatabasePlayerPvEEventStats, Map<Long, ? extends EventMode>> eventsStatsFunction,
            Function<DatabasePlayerPvEEventStats, ? extends EventMode> generalEventFunction,
            TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGamePvEEvent> createDatabaseGame,
            List<EventShopReward> shopRewards
    ) {
        this.name = name;
        this.currency = currency;
        this.updateStatsFunction = updateStatsFunction;
        this.eventsStatsFunction = eventsStatsFunction;
        this.generalEventFunction = generalEventFunction;
        this.createDatabaseGame = createDatabaseGame;
        this.shopRewards = shopRewards;
    }

    public abstract LinkedHashMap<Currencies, Long> getRewards(int position);

    public abstract LinkedHashMap<String, Long> getGuildRewards(int position);

    public abstract void addLeaderboards(DatabaseGameEvent currentGameEvent, HashMap<EventLeaderboard, String> leaderboards);

    public void initialize() {
        ChatUtils.MessageTypes.GAME_EVENTS.sendMessage("Initializing " + name + " event...");
    }

    public void createNPC() {
        NPCManager.registerTrait(GameEventTrait.class, "GameEventTrait");

        npc = NPCManager.npcRegistry.createNPC(EntityType.ZOMBIE, "event");
        npc.addTrait(GameEventTrait.class);
        editNPC(npc);
        npc.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, false);
        npc.spawn(new Location(StatsLeaderboardManager.SPAWN_POINT.getWorld(), -2539.5, 50, 744.5, 90, 0));
    }

    public void editNPC(NPC npc) {

    }

    public void openMenu(Player player) {
        Menu menu = new Menu(name + " Event", 9 * 6);

        setMenu(menu);

        menu.setItem(4, 3,
                new ItemBuilder(Material.ENDER_CHEST)
                        .name(ChatColor.GREEN + "Event Shop")
                        .get(),
                (m, e) -> openShopMenu(player)
        );

        //TODO previous event shop

        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public abstract void setMenu(Menu menu);

    public void openShopMenu(Player player) {
        boolean currentEvent = true;
        DatabaseGameEvent gameEvent = DatabaseGameEvent.currentGameEvent;
        if (gameEvent == null || gameEvent.getEvent() != this) {
            DatabaseGameEvent previousEvent = DatabaseGameEvent.PREVIOUS_GAME_EVENTS.get(this);
            if (previousEvent != null) {
                currentEvent = false;
                gameEvent = previousEvent;
            } else {
                player.sendMessage(ChatColor.RED + "There is no event shop for this event!");
                return;
            }
        }
        DatabaseGameEvent finalGameEvent = gameEvent;
        boolean finalCurrentEvent = currentEvent;

        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            DatabasePlayerPvEEventStats eventStats = pveStats.getEventStats();
            EventMode eventMode = eventsStatsFunction.apply(eventStats).get(finalGameEvent.getStartDateSecond());

            Menu menu = new Menu(name + " Shop", 9 * 6);

            for (int i = 0; i < 9; i++) {
                menu.setItem(i, 0,
                        new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7)
                                .name(" ")
                                .get(),
                        (m, e) -> {
                        }
                );
            }
            for (int i = 0; i < 9; i++) {
                menu.setItem(i, 5,
                        new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7)
                                .name(" ")
                                .get(),
                        (m, e) -> {
                        }
                );
            }
            for (int i = 1; i < 5; i++) {
                menu.setItem(0, i,
                        new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7)
                                .name(" ")
                                .get(),
                        (m, e) -> {
                        }
                );
            }
            for (int i = 1; i < 5; i++) {
                menu.setItem(8, i,
                        new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 7)
                                .name(" ")
                                .get(),
                        (m, e) -> {
                        }
                );
            }


            menu.setItem(4, 0,
                    new ItemBuilder(Material.CHEST)
                            .name(currency.getCostColoredName(pveStats.getCurrencyValue(currency)))
                            .get(),
                    (m, e) -> {

                    }
            );

            int x = 1;
            int y = 1;
            for (EventShopReward reward : shopRewards) {
                int rewardAmount = reward.getAmount();
                Currencies rewardCurrency = reward.getCurrency();
                int rewardPrice = reward.getPrice();
                String mapName = rewardAmount + "_" + rewardCurrency.name();

                String stock;
                if (reward.getStock() == -1) {
                    stock = "Unlimited";
                } else if (eventMode == null) {
                    stock = "" + reward.getStock();
                } else {
                    stock = "" + (reward.getStock() - eventMode.getRewardsPurchased().getOrDefault(mapName, 0L));
                }


                menu.setItem(x, y,
                        new ItemBuilder(rewardCurrency.item)
                                .name(rewardCurrency.getCostColoredName(rewardAmount))
                                .lore(
                                        ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + currency.getCostColoredName(rewardPrice),
                                        ChatColor.GRAY + "Stock: " + ChatColor.YELLOW + stock
                                )
                                .flags(ItemFlag.HIDE_POTION_EFFECTS)
                                .get(),
                        (m, e) -> {
                            if (eventMode == null || pveStats.getCurrencyValue(currency) < rewardPrice) {
                                player.sendMessage(ChatColor.RED + "You need " + currency.getCostColoredName(rewardPrice) + ChatColor.RED + " to purchase this item!");
                                return;
                            }
                            Map<String, Long> rewardsPurchased = eventMode.getRewardsPurchased();
                            if (reward.getStock() != -1 && rewardsPurchased.getOrDefault(mapName, 0L) >= reward.getStock()) {
                                player.sendMessage(ChatColor.RED + "This item is out of stock!");
                                return;
                            }
                            pveStats.subtractCurrency(currency, rewardPrice);
                            pveStats.addCurrency(rewardCurrency, rewardAmount);

                            //event
                            eventStats.getRewardsPurchased().merge(mapName, 1L, Long::sum);
                            //event mode
                            generalEventFunction.apply(eventStats).getRewardsPurchased().merge(mapName, 1L, Long::sum);
                            //event in event mode
                            rewardsPurchased.merge(mapName, 1L, Long::sum);

                            player.sendMessage(ChatColor.GREEN + "Purchased " + rewardCurrency.getCostColoredName(rewardAmount) + ChatColor.GREEN + " for " + currency.getCostColoredName(
                                    rewardPrice) + ChatColor.GREEN + "!");
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 500, 2.5f);
                            openShopMenu(player);

                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        }
                );
                x++;
                if (x == 8) {
                    x = 1;
                    y++;
                }
            }

            //TODO previous event shop
            menu.setItem(3, 5, MENU_BACK, finalCurrentEvent ? (m, e) -> openMenu(player) : (m, e) -> openMenu(player));
            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        });
    }

    static class EventShopReward {

        private final int amount;
        private final Currencies currency;
        private final int stock;
        private final int price;

        EventShopReward(int amount, Currencies currency, int stock, int price) {
            this.amount = amount;
            this.currency = currency;
            this.stock = stock;
            this.price = price;
        }

        public int getAmount() {
            return amount;
        }

        public Currencies getCurrency() {
            return currency;
        }

        public int getStock() {
            return stock;
        }

        public int getPrice() {
            return price;
        }
    }
}
