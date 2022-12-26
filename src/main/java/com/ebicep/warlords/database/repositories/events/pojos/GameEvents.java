package com.ebicep.warlords.database.repositories.events.pojos;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.customentities.npc.traits.GameEventTrait;
import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePvEEventBoltaro;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.DatabasePlayerPvEEventStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.Pair;
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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.ebicep.warlords.menu.Menu.*;

public enum GameEvents {

    BOLTARO("Fighter’s Glory",
            Currencies.EVENT_POINTS_BOLTARO,
            DatabasePlayerPvEEventStats::getBoltaroStats,
            DatabasePlayerPvEEventStats::getBoltaroEventStats,
            DatabasePlayerPvEEventStats::getBoltaroStats,
            DatabaseGamePvEEventBoltaro::new,
            new ArrayList<>() {{
                add(new EventReward(1, Currencies.TITLE_TOKEN_JUGGERNAUT, 1, 500_000));
                add(new EventReward(10, Currencies.SUPPLY_DROP_TOKEN, 20, 20_000));
                add(new EventReward(100_000, Currencies.COIN, 5, 100_000));
                add(new EventReward(500, Currencies.LEGEND_FRAGMENTS, 5, 100_000));
                add(new EventReward(200, Currencies.FAIRY_ESSENCE, 5, 50_000));
                add(new EventReward(1_000, Currencies.SYNTHETIC_SHARD, 5, 50_000));
                add(new EventReward(1, Currencies.EPIC_STAR_PIECE, 1, 500_000));
                add(new EventReward(1_000, Currencies.COIN, -1, 10_000));
            }}
    ) {
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

        @Override
        public Long coinsPerKill() {
            return 100L;
        }

        @Override
        public Pair<Long, Integer> guildCoinsPerXSec() {
            return new Pair<>(1L, 1); // 1 coin per second
        }

        @Override
        public Pair<Long, Integer> expPerXSec() {
            return new Pair<>(15L, 10); // 15 exp per 10 seconds
        }

        private void openBoltaroModeMenu(Player player, boolean privateGame) {
            Menu menu = new Menu("Fighter’s Glory Modes", 9 * 4);

            menu.setItem(2, 1,
                    new ItemBuilder(Material.IRON_FENCE)
                            .name(ChatColor.GREEN + "Boltaro’s Lair")
                            .get(),
                    (m, e) -> {
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
                                    queueEntryBuilder -> queueEntryBuilder.setMap(GameMap.ILLUSION_RIFT_EVENT).setRequestedGameAddons(GameAddon.PRIVATE_GAME)

                            );
                        } else {
                            GameStartCommand.startGamePvEEvent(player, queueEntryBuilder -> queueEntryBuilder.setMap(GameMap.ILLUSION_RIFT_EVENT));
                        }
                    }
            );

            menu.setItem(4, 3, MENU_BACK, (m, e) -> openMenu(player));
            menu.openForPlayer(player);
        }
    },

    ;

    public static final GameEvents[] VALUES = values();
    public static NPC npc;

    public final String name;
    public final Currencies currency;
    public final Function<DatabasePlayerPvEEventStats, AbstractDatabaseStatInformation> updateStatsFuntion;
    public final Function<DatabasePlayerPvEEventStats, Map<Long, ? extends EventMode>> eventsStatsFunction;
    public final Function<DatabasePlayerPvEEventStats, ? extends EventMode> generalEventFunction;
    public final TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGamePvEEvent> createDatabaseGame;
    public final List<EventReward> rewards;

    GameEvents(
            String name,
            Currencies currency,
            Function<DatabasePlayerPvEEventStats, AbstractDatabaseStatInformation> updateStatsFuntion,
            Function<DatabasePlayerPvEEventStats, Map<Long, ? extends EventMode>> eventsStatsFunction,
            Function<DatabasePlayerPvEEventStats, ? extends EventMode> generalEventFunction,
            TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGamePvEEvent> createDatabaseGame,
            List<EventReward> rewards
    ) {
        this.name = name;
        this.currency = currency;
        this.updateStatsFuntion = updateStatsFuntion;
        this.eventsStatsFunction = eventsStatsFunction;
        this.generalEventFunction = generalEventFunction;
        this.createDatabaseGame = createDatabaseGame;
        this.rewards = rewards;
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
            DatabaseGameEvent previousEvent = DatabaseGameEvent.previousGameEvents.get(this);
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
            EventMode eventMode = eventsStatsFunction.apply(eventStats).get(finalGameEvent.getStartDate().getEpochSecond());
            Map<String, Long> generalRewardsPurchased = generalEventFunction.apply(eventStats).getRewardsPurchased();

            Menu menu = new Menu(name + " Shop", 9 * 6);

            menu.setItem(4, 0,
                    new ItemBuilder(Material.CHEST)
                            .name(currency.getCostColoredName(pveStats.getCurrencyValue(currency)))
                            .get(),
                    (m, e) -> {

                    }
            );

            int x = 1;
            int y = 1;
            for (EventReward reward : rewards) {
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
                            rewardsPurchased.merge(mapName, 1L, Long::sum);
                            generalRewardsPurchased.merge(mapName, 1L, Long::sum);
                            player.sendMessage(ChatColor.GREEN + "Purchased " + rewardCurrency.getCostColoredName(rewardAmount) + ChatColor.GREEN + " for " + currency.getCostColoredName(
                                    rewardPrice) + ChatColor.GREEN + "!");
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 500, 2.5f);
                            openShopMenu(player);
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

    public Long coinsPerKill() {
        return 0L;
    }

    public Pair<Long, Integer> guildCoinsPerXSec() {
        return null;
    }

    public Pair<Long, Integer> expPerXSec() {
        return null;
    }

    static class EventReward {

        private final int amount;
        private final Currencies currency;
        private final int stock;
        private final int price;

        EventReward(int amount, Currencies currency, int stock, int price) {
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
