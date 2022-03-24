package com.ebicep.warlords.game;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGameDuel;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGameTDM;
import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.SpecType;
import com.ebicep.warlords.player.Specializations;
import com.ebicep.warlords.player.Weapons;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.java.TriFunction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.menu.GameMenu.openMainMenu;
import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public enum GameMode {
    CAPTURE_THE_FLAG(
            "Capture The Flag",
            new ItemStack(Material.BANNER),
            DatabaseGameCTF::new,
            GamesCollections.CTF
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            String color = "" + ChatColor.YELLOW + ChatColor.BOLD;
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    "" + ChatColor.WHITE + ChatColor.BOLD + "Warlords",
                    "",
                    color + "Steal and capture the enemy team's flag to",
                    color + "earn " + ChatColor.AQUA + ChatColor.BOLD + "250 " + ChatColor.YELLOW + ChatColor.BOLD + "points! The first team with a",
                    color + "score of " + ChatColor.AQUA + ChatColor.BOLD + "1000 " + ChatColor.YELLOW + ChatColor.BOLD + "wins!",
                    ""
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    ChatColor.GREEN + "GO!",
                    ChatColor.YELLOW + "Steal and capture the enemy flag!"
            ));
            options.add(new NoRespawnIfOfflineOption());
            return options;
        }
    },
    INTERCEPTION(
            "Interception",
            null,//new ItemStack(Material.WOOL),
            DatabaseGameInterception::new,
            GamesCollections.INTERCEPTION
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            String color = "" + ChatColor.YELLOW + ChatColor.BOLD;
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    "" + ChatColor.WHITE + ChatColor.BOLD + "Warlords",
                    "",
                    color + "Capture the marked points to",
                    color + "earn points! The first team with a",
                    color + "score of " + ChatColor.AQUA + ChatColor.BOLD + "1000 " + ChatColor.YELLOW + ChatColor.BOLD + "wins!",
                    ""
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    ChatColor.GREEN + "GO!",
                    ChatColor.YELLOW + "Capture the marked points!"
            ));
            options.add(new NoRespawnIfOfflineOption());
            return options;
        }
    },
    DUEL(
            "Duel",
            new ItemStack(Material.DIAMOND_SWORD),
            DatabaseGameDuel::new,
            GamesCollections.DUEL
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);
            String color = "" + ChatColor.YELLOW + ChatColor.BOLD;
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    "" + ChatColor.WHITE + ChatColor.BOLD + "Warlords",
                    "",
                    color + "First player to kill their opponent",
                    color + "5 times wins the duel!",
                    ""
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    ChatColor.GREEN + "GO!"
            ));
            return options;
        }
    },
    TEAM_DEATHMATCH(
            "Team Deathmatch",
            new ItemStack(Material.DIAMOND_BARDING),
            DatabaseGameTDM::new,
            GamesCollections.TDM
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            String color = "" + ChatColor.YELLOW + ChatColor.BOLD;
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    "" + ChatColor.WHITE + ChatColor.BOLD + "Warlords",
                    "",
                    color + "First team to reach 1000 points wins",
                    color + "the game!",
                    ""
            ));
            options.add(TextOption.Type.TITLE.create(
                    ChatColor.GREEN + "GO!"
            ));
            return options;
        }
    },
    SIMULATION_TRIAL(
            "Simulation Trial",
            null,
            null,
            null
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            String color = "" + ChatColor.YELLOW + ChatColor.BOLD;
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    "" + ChatColor.WHITE + ChatColor.BOLD + "Simulation Trial",
                    "",
                    color + "The goal is to either defend your flag holder as long",
                    color + "as possible or return the flag as soon as possible.",
                    ""
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    ChatColor.GREEN + "GO!",
                    ChatColor.YELLOW + "Let the trials begin!"
            ));
            options.add(new NoRespawnIfOfflineOption());
            return options;
        }
    },
    DEBUG(
            "Sandbox",
            null,
            null,
            null
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            options.add(TextOption.Type.TITLE.create(
                    10,
                    ChatColor.GREEN + "GO!"
            ));
            return options;
        }
    },

    ;

    public final String name;
    public final ItemStack itemStack;
    public final TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> createDatabaseGame;
    public final GamesCollections gamesCollections;

    GameMode(String name, ItemStack itemStack, TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> createDatabaseGame, GamesCollections gamesCollections) {
        this.name = name;
        this.itemStack = itemStack;
        this.createDatabaseGame = createDatabaseGame;
        this.gamesCollections = gamesCollections;
    }

    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = new ArrayList<>(64);

        options.add(new PreGameItemOption(1, (g, p) -> {
            PlayerSettings playerSettings = Warlords.getPlayerSettings(p.getUniqueId());
            Specializations selectedSpec = playerSettings.getSelectedSpec();
            AbstractPlayerClass apc = selectedSpec.create.get();

            return new ItemBuilder(apc.getWeapon()
                    .getItem(playerSettings.getWeaponSkins()
                            .getOrDefault(selectedSpec, Weapons.FELFLAME_BLADE).item))
                    .name("§aWeapon Skin Preview")
                    .lore("")
                    .get();
        }));
        options.add(new PreGameItemOption(4, new ItemBuilder(Material.NETHER_STAR)
                .name(ChatColor.AQUA + "Pre-game Menu ")
                .lore(ChatColor.GRAY + "Allows you to change your class, select a\nweapon, and edit your settings.")
                .get(), (g, p) -> openMainMenu(p)));
        options.add(new PreGameItemOption(5, new ItemBuilder(Material.NOTE_BLOCK)
                .name(ChatColor.AQUA + "Player Spec Information")
                .lore(ChatColor.GRAY + "Displays the amount of people on each specialization.")
                .get(),
                (g, p) -> {
                    openPlayerSpecInfoMenu(g, p);
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (p.getOpenInventory().getTopInventory().getName().equals("Player Specs")) {
                                openPlayerSpecInfoMenu(g, p);
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 20, 20);
                })
        );
        options.add(new PreGameItemOption(7, (g, p) -> !g.acceptsPeople() ? null : new ItemBuilder(Material.BARRIER)
                .name(ChatColor.RED + "Leave")
                .lore(ChatColor.GRAY + "Right-Click to leave the game.")
                .get(),
                (g, p) -> {
                    if (g.acceptsPeople()) {
                        g.removePlayer(p.getUniqueId());
                    }
                })
        );

        options.add(new GameFreezeOption());

        return options;
    }

    public static void openPlayerSpecInfoMenu(Game game, Player player) {
        Menu menu = new Menu("Player Specs", 9 * 4);
        int x = 3;
        for (SpecType value : SpecType.values()) {
            ItemBuilder itemBuilder = new ItemBuilder(value.itemStack)
                    .name(value.chatColor + value.name);
            StringBuilder lore = new StringBuilder(ChatColor.GREEN + "Total: " + ChatColor.GOLD +
                    (int) game.getPlayers().keySet().stream()
                            .map(Warlords::getPlayerSettings)
                            .map(PlayerSettings::getSelectedSpec)
                            .filter(c -> c.specType == value)
                            .count() + "\n\n");
            Arrays.stream(Specializations.values())
                    .filter(classes -> classes.specType == value)
                    .forEach(classes -> {
                        int playersOnSpec = (int) game.getPlayers().keySet().stream()
                                .map(Warlords::getPlayerSettings)
                                .map(PlayerSettings::getSelectedSpec)
                                .filter(c -> c == classes)
                                .count();
                        lore.append(ChatColor.GREEN).append(classes.name).append(": ").append(ChatColor.YELLOW).append(playersOnSpec).append("\n");
                    });
            itemBuilder.lore(lore.toString());
            menu.setItem(
                    x,
                    1,
                    itemBuilder.get(),
                    (m, e) -> {
                    }
            );
            x++;
        }
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }
}
