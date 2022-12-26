package com.ebicep.warlords.database.repositories.events.pojos;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.customentities.npc.traits.GameEventTrait;
import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePvEEventBoltaro;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Function;

import static com.ebicep.warlords.menu.Menu.*;

public enum GameEvents {

    BOLTARO("Boltaro",
            Currencies.EVENT_POINTS_BOLTARO,
            DatabasePlayerPvEEventStats::getBoltaroStats,
            DatabasePlayerPvEEventStats::getBoltaroEventStats,
            DatabaseGamePvEEventBoltaro::new
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
        public void openShopMenu(Player player) {

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
            Menu menu = new Menu("Boltaro Modes", 9 * 4);

            menu.setItem(2, 1,
                    new ItemBuilder(Material.IRON_FENCE)
                            .name(ChatColor.GREEN + "Boltaro")
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
    public final Function<DatabasePlayerPvEEventStats, Map<Long, ? extends EventMode>> eventModeFunction;
    public final TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGamePvEEvent> createDatabaseGame;

    GameEvents(
            String name,
            Currencies currency,
            Function<DatabasePlayerPvEEventStats, AbstractDatabaseStatInformation> updateStatsFuntion,
            Function<DatabasePlayerPvEEventStats, Map<Long, ? extends EventMode>> eventModeFunction,
            TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGamePvEEvent> createDatabaseGame
    ) {
        this.name = name;
        this.currency = currency;
        this.updateStatsFuntion = updateStatsFuntion;
        this.eventModeFunction = eventModeFunction;
        this.createDatabaseGame = createDatabaseGame;
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
                (m, e) -> {

                }
        );

        //TODO previous event shop

        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public abstract void setMenu(Menu menu);

    public abstract void openShopMenu(Player player);

    public Long coinsPerKill() {
        return 0L;
    }

    public Pair<Long, Integer> guildCoinsPerXSec() {
        return null;
    }

    public Pair<Long, Integer> expPerXSec() {
        return null;
    }
}
