package com.ebicep.warlords.game;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGameDuel;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvE;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGameTDM;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.tutorial.TutorialOption;
import com.ebicep.warlords.game.option.wavedefense.WinByMaxWaveClearOption;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.java.TriFunction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;
import static com.ebicep.warlords.menu.generalmenu.WarlordsShopMenu.openMainMenu;

public enum GameMode {
    CAPTURE_THE_FLAG(
            "Capture The Flag",
            "CTF",
            new ItemStack(Material.BANNER),
            DatabaseGameCTF::new,
            GamesCollections.CTF,
            16
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
            options.add(new WeaponOption());
            options.add(new ApplySkillBoostOption());
            options.add(new HorseOption());
            return options;
        }
    },
    INTERCEPTION(
            "Interception",
            "INTER",
            null,//new ItemStack(Material.WOOL),
            DatabaseGameInterception::new,
            GamesCollections.INTERCEPTION,
            16
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
                    color + "score of " + ChatColor.AQUA + ChatColor.BOLD + "1500 " + ChatColor.YELLOW + ChatColor.BOLD + "wins!",
                    ""
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    ChatColor.GREEN + "GO!",
                    ChatColor.YELLOW + "Capture the marked points!"
            ));
            options.add(new NoRespawnIfOfflineOption());
            options.add(new WeaponOption());
            options.add(new ApplySkillBoostOption());
            options.add(new HorseOption());
            return options;
        }
    },
    TEAM_DEATHMATCH(
            "Team Deathmatch",
            "TDM",
            new ItemStack(Material.DIAMOND_BARDING),
            DatabaseGameTDM::new,
            GamesCollections.TDM,
            16
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            String color = "" + ChatColor.YELLOW + ChatColor.BOLD;
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    "" + ChatColor.WHITE + ChatColor.BOLD + "Warlords",
                    "",
                    color + "Eliminate players from the enemy team to",
                    color + "gain points for your team! The first team",
                    color + "to reach " + ChatColor.AQUA + ChatColor.BOLD + "1000 " + color + "points wins the game!"
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    ChatColor.GREEN + "GO!",
                    ChatColor.YELLOW + "First team to reach 1000 points wins!"
            ));
            options.add(new PreGameItemOption(4, new ItemBuilder(Material.NETHER_STAR)
                    .name(ChatColor.AQUA + "Pre-game Menu ")
                    .lore(ChatColor.GRAY + "Allows you to change your class, select a\nweapon, and edit your settings.")
                    .get(), (g, p) -> openMainMenu(p)));

            options.add(new WeaponOption());
            options.add(new ApplySkillBoostOption());
            options.add(new HorseOption());
            return options;
        }
    },
    DUEL(
            "[WIP] Duel",
            "DUEL",
            null,//new ItemStack(Material.DIAMOND_SWORD),
            DatabaseGameDuel::new,
            GamesCollections.DUEL,
            2
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

            options.add(new WeaponOption());
            options.add(new ApplySkillBoostOption());
            options.add(new HorseOption());
            return options;
        }
    },
    SIMULATION_TRIAL(
            "[WIP] Simulation Trial",
            "SIMS",
            null,
            null,
            null,
            Integer.MAX_VALUE
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
            options.add(new WeaponOption());
            options.add(new ApplySkillBoostOption());
            options.add(new HorseOption());
            return options;
        }
    },
    WAVE_DEFENSE(
            "[WIP] USE NPC TO START PVE - Wave Defense",
            "PVE",
            new ItemStack(Material.SKULL_ITEM, 1, (short) 2),
            DatabaseGamePvE::new,
            GamesCollections.PVE,
            1
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            String color = "" + ChatColor.YELLOW + ChatColor.BOLD;
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    "" + ChatColor.WHITE + ChatColor.BOLD + "Warlords",
                    "",
                    color + "Survive against waves of",
                    color + "monsters!",
                    ""
            ));
            options.add(new PreGameItemOption(4, new ItemBuilder(Material.NETHER_STAR)
                    .name(ChatColor.AQUA + "Pre-game Menu ")
                    .lore(ChatColor.GRAY + "Allows you to change your class, select a\nweapon, and edit your settings.")
                    .get(), (g, p) -> openMainMenu(p)));
            options.add(new PreGameItemOption(
                    6,
                    (game, player) -> {
                        if (DatabaseManager.playerService != null) {
                            DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
                            List<AbstractWeapon> weapons = databasePlayer.getPveStats().getWeaponInventory();
                            Optional<AbstractWeapon> optionalWeapon = weapons.stream()
                                    .filter(AbstractWeapon::isBound)
                                    .filter(abstractWeapon -> abstractWeapon.getSpecializations() == databasePlayer.getLastSpec())
                                    .findFirst();
                            return optionalWeapon.map(AbstractWeapon::generateItemStack).orElse(null);
                        } else {
                            return null;
                        }
                    },
                    (g, p) -> WeaponManagerMenu.openWeaponInventoryFromExternal(p)
            ));
            options.add(TextOption.Type.TITLE.create(
                    5,
                    ChatColor.GREEN + "GO!",
                    ChatColor.YELLOW + "Let the wave defense commence."
            ));
            options.add(new RecordTimeElapsedOption());
            options.add(new WeaponOption(WeaponOption::showPvEWeapon, WeaponOption::showWeaponStats));
            options.add(new WinByMaxWaveClearOption());
            options.add(new WinByAllDeathOption());
            return options;
        }
    },
    DEBUG(
            "Sandbox",
            "SandBox",
            null,
            null,
            null,
            16
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            options.add(TextOption.Type.TITLE.create(
                    3,
                    ChatColor.GREEN + "GO!"
            ));
            options.add(new WeaponOption());
            options.add(new ApplySkillBoostOption());
            options.add(new HorseOption());
            return options;
        }
    },
    TUTORIAL(
            "Tutorial",
            "Tutorial",
            null,
            null,
            null,
            Integer.MAX_VALUE
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            options.add(new WeaponOption());
            options.add(new TutorialOption());

            return options;
        }
    },

    ;

    public static final GameMode[] VALUES = values();

    public final String name;
    public final String abbreviation;
    public final ItemStack itemStack;
    public final TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> createDatabaseGame;
    public final GamesCollections gamesCollections;
    public final int minPlayersToAddToDatabase;

    GameMode(
            String name,
            String abbreviation, ItemStack itemStack,
            TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> createDatabaseGame,
            GamesCollections gamesCollections,
            int minPlayersToAddToDatabase
    ) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.itemStack = itemStack;
        this.createDatabaseGame = createDatabaseGame;
        this.gamesCollections = gamesCollections;
        this.minPlayersToAddToDatabase = minPlayersToAddToDatabase;
    }

    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = new ArrayList<>(64);

        options.add(new PreGameItemOption(1, (g, p) -> {
            PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(p.getUniqueId());
            Specializations selectedSpec = playerSettings.getSelectedSpec();
            AbstractPlayerClass apc = selectedSpec.create.get();

            return new ItemBuilder(apc.getWeapon()
                    .getItem(playerSettings.getWeaponSkins()
                            .getOrDefault(selectedSpec, Weapons.FELFLAME_BLADE).getItem()))
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
                        }
                )
        );
        options.add(new PreGameItemOption(7, (g, p) -> !g.acceptsPeople() ? null : new ItemBuilder(Material.BARRIER)
                        .name(ChatColor.RED + "Leave")
                        .lore(ChatColor.GRAY + "Right-Click to leave the game.")
                        .get(),
                        (g, p) -> {
                            if (g.acceptsPeople()) {
                                g.removePlayer(p.getUniqueId());
                            }
                        }
                )
        );

        options.add(new GameFreezeOption());

        return options;
    }

    public static void openPlayerSpecInfoMenu(Game game, Player player) {
        Menu menu = new Menu("Player Specs", 9 * 4);
        int x = 3;
        for (SpecType value : SpecType.VALUES) {
            ItemBuilder itemBuilder = new ItemBuilder(value.itemStack)
                    .name(value.chatColor + value.name);
            StringBuilder lore = new StringBuilder(ChatColor.GREEN + "Total: " + ChatColor.GOLD +
                    (int) game.getPlayers().keySet().stream()
                            .map(PlayerSettings::getPlayerSettings)
                            .map(PlayerSettings::getSelectedSpec)
                            .filter(c -> c.specType == value)
                            .count() + "\n\n");
            Arrays.stream(Specializations.VALUES)
                    .filter(classes -> classes.specType == value)
                    .forEach(classes -> {
                        int playersOnSpec = (int) game.getPlayers().keySet().stream()
                                .map(PlayerSettings::getPlayerSettings)
                                .map(PlayerSettings::getSelectedSpec)
                                .filter(c -> c == classes)
                                .count();
                        lore.append(ChatColor.GREEN)
                                .append(classes.name)
                                .append(": ")
                                .append(ChatColor.YELLOW)
                                .append(playersOnSpec)
                                .append("\n");
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

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
