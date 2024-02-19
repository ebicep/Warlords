package com.ebicep.warlords.game;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGameDuel;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGameSiege;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGameTDM;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.damage.DrowningDamage;
import com.ebicep.warlords.game.option.damage.FallDamage;
import com.ebicep.warlords.game.option.damage.KillDamage;
import com.ebicep.warlords.game.option.damage.VoidDamage;
import com.ebicep.warlords.game.option.freeze.GameFreezeOption;
import com.ebicep.warlords.game.option.pve.BountyOption;
import com.ebicep.warlords.game.option.pve.tutorial.TutorialOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WinByMaxWaveClearOption;
import com.ebicep.warlords.game.option.pvp.ApplySkillBoostOption;
import com.ebicep.warlords.game.option.pvp.GameOvertimeOption;
import com.ebicep.warlords.game.option.pvp.HorseOption;
import com.ebicep.warlords.game.option.pvp.ctf.FlagOption;
import com.ebicep.warlords.game.option.pvp.interception.InterceptionOption;
import com.ebicep.warlords.game.option.pvp.interception.InterceptionRespawnOption;
import com.ebicep.warlords.game.option.respawn.DieOnLogoutOption;
import com.ebicep.warlords.game.option.respawn.NoRespawnIfOfflineOption;
import com.ebicep.warlords.game.option.respawn.RespawnProtectionOption;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.game.option.towerdefense.WinByLastStandingCastleOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.game.option.win.WinByAllDeathOption;
import com.ebicep.warlords.game.option.win.WinByPointsOption;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.PlayerHotBarItemListener;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.TriFunction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;
import static com.ebicep.warlords.menu.generalmenu.WarlordsShopMenu.openMainMenu;

public enum GameMode {
    LOBBY(
            "MainLobby",
            "MainLobby",
            null,
            null,
            null,
            Integer.MAX_VALUE,
            true
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            options.add(new WeaponOption());
            options.add(new ApplySkillBoostOption());

            return options;
        }
    },
    CAPTURE_THE_FLAG(
            "Capture The Flag",
            "CTF",
            new ItemStack(Material.BLACK_BANNER),
            DatabaseGameCTF::new,
            GamesCollections.CTF,
            16,
            false
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    base.append(Component.text("Steal and capture the enemy team's flag to")),
                    base.append(Component.text("earn "))
                        .append(Component.text("250 ", NamedTextColor.AQUA, TextDecoration.BOLD))
                        .append(base.append(Component.text("points! The first team with a"))),
                    base.append(Component.text("score of "))
                        .append(Component.text("1000 ", NamedTextColor.AQUA, TextDecoration.BOLD))
                        .append(base.append(Component.text("wins!"))),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Steal and capture the enemy flag!", NamedTextColor.YELLOW)
            ));
            options.add(new FlagOption());
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
            16,
            true
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);
            int points = 1500;

            Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    base.append(Component.text("Capture the marked points to")),
                    base.append(Component.text("earn points! The first team with a")),
                    base.append(Component.text("score of "))
                        .append(Component.text(points, NamedTextColor.AQUA, TextDecoration.BOLD))
                        .append(base.append(Component.text(" wins!"))),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Capture the marked points!", NamedTextColor.YELLOW)
            ));

            options.add(new InterceptionOption());

            options.add(new NoRespawnIfOfflineOption());
            options.add(new WeaponOption());
            options.add(new ApplySkillBoostOption());
            options.add(new HorseOption());

            options.add(new AbstractScoreOnEventOption.OnInterceptionCapture(25));
            AbstractScoreOnEventOption.OnInterceptionTimer scoreOnEventOption = new AbstractScoreOnEventOption.OnInterceptionTimer(1);
            options.add(scoreOnEventOption);
            options.add(new WinByPointsOption(points) {
                @Override
                protected Component modifyScoreboardLine(Team team, Component component) {
                    Map<Team, Integer> cachedTeamScoreIncrease = scoreOnEventOption.getCachedTeamScoreIncrease();
                    Integer increase = cachedTeamScoreIncrease.get(team);
                    if (increase != null) {
                        return component.append(Component.text(" +" + increase, NamedTextColor.AQUA)
                                                         .append(Component.text("/s", NamedTextColor.GOLD)));
                    }
                    return component;
                }
            });
            if (addons.contains(GameAddon.DOUBLE_TIME)) {
                options.add(new WinAfterTimeoutOption(2400));
            } else {
                options.add(new WinAfterTimeoutOption(1200));
            }
            options.add(new GameOvertimeOption(100, 90));
            options.add(new RespawnWaveOption(0, 17, 8));
            options.add(new RespawnProtectionOption());
            options.add(new InterceptionRespawnOption());
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());
            return options;
        }
    },
    TEAM_DEATHMATCH(
            "Team Deathmatch",
            "TDM",
            new ItemStack(Material.DIAMOND_HORSE_ARMOR),
            DatabaseGameTDM::new,
            GamesCollections.TDM,
            16,
            true
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    base.append(Component.text("Eliminate players from the enemy team to")),
                    base.append(Component.text("gain points for your team! The first team")),
                    base.append(Component.text("to reach "))
                        .append(Component.text("1000 ", NamedTextColor.AQUA, TextDecoration.BOLD))
                        .append(base.append(Component.text("points wins the game!"))),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("First team to reach 1000 points wins!", NamedTextColor.YELLOW)
            ));
            options.add(new NoRespawnIfOfflineOption());
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
            2,
            true
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);
            Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    base.append(Component.text("First player to kill their opponent")),
                    base.append(Component.text("5 times wins the duel!")),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN)
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
            Integer.MAX_VALUE,
            true
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Simulation Trial", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    base.append(Component.text("The goal is to either defend your flag holder as long")),
                    base.append(Component.text("as possible or return the flag as soon as possible.")),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Let the trials begin!", NamedTextColor.YELLOW)
            ));
            options.add(new NoRespawnIfOfflineOption());
            options.add(new WeaponOption());
            options.add(new ApplySkillBoostOption());
            options.add(new HorseOption());
            return options;
        }
    },
    WAVE_DEFENSE(
            "Wave Defense",
            "PVE",
            new ItemStack(Material.ZOMBIE_HEAD),
            DatabaseGamePvEWaveDefense::new,
            GamesCollections.PVE,
            1,
            true
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    base.append(Component.text("Survive against waves of")),
                    base.append(Component.text("monsters!")),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Let the wave defense commence.", NamedTextColor.YELLOW)
            ));
            options.add(new PreGameItemOption(4, PlayerHotBarItemListener.SELECTION_MENU, (g, p) -> WarlordsNewHotbarMenu.SelectionMenu.openWarlordsMenu(p)));
            options.add(new RecordTimeElapsedOption());
            options.add(new WeaponOption(WeaponOption::showPvEWeapon, WeaponOption::showWeaponStats));
            options.add(new WinByMaxWaveClearOption());
            options.add(new NoRespawnIfOfflineOption());
            options.add(new WinByAllDeathOption(Team.BLUE));
            options.add(new DieOnLogoutOption());
            options.add(new GameFreezeOption());
            options.add(new BountyOption());
            return options;
        }
    },
    ONSLAUGHT(
            "Onslaught",
            "PVE",
            new ItemStack(Material.ZOMBIE_HEAD),
            DatabaseGamePvEOnslaught::new,
            GamesCollections.PVE,
            1,
            true
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    base.append(Component.text("Survive as long as possible while also killing")),
                    base.append(Component.text("as many monsters as possible! Every 5 minutes you will")),
                    base.append(Component.text("gain special reward pouches.")),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Let the onslaught begin!", NamedTextColor.YELLOW)
            ));
            options.add(new PreGameItemOption(4, PlayerHotBarItemListener.SELECTION_MENU, (g, p) -> WarlordsNewHotbarMenu.SelectionMenu.openWarlordsMenu(p)));
            options.add(new RecordTimeElapsedOption());
            options.add(new WeaponOption(WeaponOption::showPvEWeapon, WeaponOption::showWeaponStats));
            options.add(new NoRespawnIfOfflineOption());
            options.add(new WinByAllDeathOption(Team.BLUE));
            options.add(new DieOnLogoutOption());
            options.add(new GameFreezeOption());
            options.add(new BasicScoreboardOption());
            options.add(new BountyOption());

            return options;
        }

        @Override
        public float getDropModifier() {
            return .1f;
        }
    },
    BOSS_RUSH(
            "Boss Rush",
            "PVE",
            null,//new ItemStack(Material.ZOMBIE_HEAD),
            null,
            GamesCollections.PVE,
            1,
            true
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    base.append(Component.text("Survive against waves of")),
                    base.append(Component.text("monsters!")),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Kill all bosses in order to win!", NamedTextColor.YELLOW)
            ));
            options.add(new PreGameItemOption(4, PlayerHotBarItemListener.SELECTION_MENU, (g, p) -> WarlordsNewHotbarMenu.SelectionMenu.openWarlordsMenu(p)));
            options.add(new RecordTimeElapsedOption());
            options.add(new WeaponOption(WeaponOption::showPvEWeapon, WeaponOption::showWeaponStats));
            options.add(new NoRespawnIfOfflineOption());
            options.add(new WinByAllDeathOption(Team.BLUE));
            options.add(new DieOnLogoutOption());
            options.add(new GameFreezeOption());
            options.add(new BasicScoreboardOption());

            return options;
        }
    },
    TREASURE_HUNT(
            "Cryptic Conquest",
            "PVE",
            null,//new ItemStack(Material.SKULL_ITEM, 1, (short) 2),
            null,
            GamesCollections.PVE,
            1,
            true
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    base.append(Component.text("Survive against waves of")),
                    base.append(Component.text("monsters!")),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Let the onslaught begin!", NamedTextColor.YELLOW)
            ));
            options.add(new PreGameItemOption(4, PlayerHotBarItemListener.SELECTION_MENU, (g, p) -> WarlordsNewHotbarMenu.SelectionMenu.openWarlordsMenu(p)));
            options.add(new RecordTimeElapsedOption());
            options.add(new WeaponOption(WeaponOption::showPvEWeapon, WeaponOption::showWeaponStats));
            options.add(new NoRespawnIfOfflineOption());
            options.add(new WinByAllDeathOption(Team.BLUE));
            options.add(new DieOnLogoutOption());
            options.add(new GameFreezeOption());

            return options;
        }
    },
    RAID(
            "Raid",
            "RAID",
            new ItemStack(Material.ZOMBIE_HEAD),
            null,
            null,
            4,
            true
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    base.append(Component.text("Face the ultimate challenge in")),
                    base.append(Component.text("the raid trials!")),
                    Component.empty()
            ));
            options.add(new PreGameItemOption(4, PlayerHotBarItemListener.SELECTION_MENU, (g, p) -> WarlordsNewHotbarMenu.SelectionMenu.openWarlordsMenu(p)));
            options.add(new WeaponOption(WeaponOption::showPvEWeapon, WeaponOption::showWeaponStats));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Let the raid trials begin.", NamedTextColor.YELLOW)
            ));
            options.add(new RecordTimeElapsedOption());
            options.add(new WeaponOption(WeaponOption::showPvEWeapon, WeaponOption::showWeaponStats));
            options.add(new NoRespawnIfOfflineOption());
            options.add(new WinByAllDeathOption(Team.BLUE));
            options.add(new DieOnLogoutOption());

            return options;
        }
    },
    DEBUG(
            "Sandbox",
            "SandBox",
            null,
            null,
            null,
            16,
            false
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            options.add(TextOption.Type.TITLE.create(
                    3,
                    Component.text("GO!", NamedTextColor.GREEN)
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
            Integer.MAX_VALUE,
            false
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            options.add(new WeaponOption());
            options.add(new TutorialOption());

            return options;
        }
    },
    EVENT_WAVE_DEFENSE(
            "Event Wave Defense",
            "PVE",
            null,//new ItemStack(Material.ZOMBIE_HEAD),
            (game, warlordsGameTriggerWinEvent, aBoolean) -> {
                if (DatabaseGameEvent.currentGameEvent == null || !DatabaseGameEvent.currentGameEvent.isActive()) {
                    return null;
                }
                return DatabaseGameEvent.currentGameEvent.getEvent().createDatabaseGame.apply(game, warlordsGameTriggerWinEvent, aBoolean);
            },
            GamesCollections.EVENT_PVE,
            1,
            true
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();

            options.add(new PreGameItemOption(4, PlayerHotBarItemListener.SELECTION_MENU, (g, p) -> WarlordsNewHotbarMenu.SelectionMenu.openWarlordsMenu(p)));
            options.add(new RecordTimeElapsedOption(true));
            options.add(new WeaponOption(WeaponOption::showPvEWeapon, WeaponOption::showWeaponStats));
            //options.add(new WinByMaxWaveClearOption());
            options.add(new NoRespawnIfOfflineOption());
            options.add(new WinByAllDeathOption(Team.BLUE));
            options.add(new DieOnLogoutOption());
            options.add(new GameFreezeOption());
            options.add(new BountyOption());

            return options;
        }
    },
    PAYLOAD(
            "Payload",
            "Payload",
            null,//new ItemStack(Material.ZOMBIE_HEAD),
            null,
            null,
            1,
            true
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();

            Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    base.append(Component.text("Something about payload here!")),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Payload!", NamedTextColor.YELLOW)
            ));

            options.add(new WinAfterTimeoutOption(600, Team.RED));
            options.add(new GameFreezeOption());
            options.add(new NoRespawnIfOfflineOption());
            options.add(new WeaponOption());
            options.add(new ApplySkillBoostOption());
            options.add(new HorseOption());

            return options;
        }
    },
    SIEGE(
            "Siege",
            "Siege",
            new ItemStack(Material.SCULK),
            DatabaseGameSiege::new,
            GamesCollections.SIEGE,
            6,
            false
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    base.append(Component.text("Gain a point by either")),
                    base.append(Component.text("capturing the point, escorting the payload,")),
                    base.append(Component.text("or defending the payload!")),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("Siege!", NamedTextColor.YELLOW)
            ));

            options.add(new WinByPointsOption(4));

            options.add(new GameFreezeOption());
            options.add(new NoRespawnIfOfflineOption());
            options.add(new WeaponOption());
            options.add(new ApplySkillBoostOption());
            options.add(new HorseOption());

            options.add(new RespawnWaveOption()); // timers handled by siegeoption
            options.add(new RespawnProtectionOption(5, 10, false));
            options.add(new GraveOption());

            options.add(new BasicScoreboardOption());

            options.add(new GlowingTeamOption());
            options.add(new SwapSpecOption());

            return options;
        }
    },
    TOWER_DEFENSE(
            "Tower Defense",
            "TD",
            new ItemStack(Material.OAK_PLANKS),
            null,
            null,
            100,
            false
    ) {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                    Component.empty(),
                    Component.empty()
            ));
            options.add(TextOption.Type.TITLE.create(
                    10,
                    Component.text("GO!", NamedTextColor.GREEN),
                    Component.text("", NamedTextColor.YELLOW)
            ));

            options.add(new GameFreezeOption());
            options.add(new NoRespawnIfOfflineOption());
            options.add(new WeaponOption());
            options.add(new RecordTimeElapsedOption());

            options.add(new WinByLastStandingCastleOption());

            for (Option option : options) {
                if (option instanceof FlyOption flyOption) {
                    flyOption.setFlyEnabled(true);
                    break;
                }
            }


            return options;
        }
    },

    ;

    public static final GameMode[] VALUES = values();

    public static boolean isWaveDefense(GameMode mode) {
        return mode == WAVE_DEFENSE || mode == EVENT_WAVE_DEFENSE;
    }

    public static boolean isPvE(GameMode mode) {
        return mode == WAVE_DEFENSE || mode == EVENT_WAVE_DEFENSE || mode == ONSLAUGHT || mode == TREASURE_HUNT || mode == TOWER_DEFENSE;
    }

    public final String name;
    public final String abbreviation;
    public final ItemStack itemStack;
    public final TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> createDatabaseGame;
    public final GamesCollections gamesCollections;
    public final int minPlayersToAddToDatabase;
    private final boolean isHiddenInMenu;

    GameMode(
            String name,
            String abbreviation, ItemStack itemStack,
            TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> createDatabaseGame,
            GamesCollections gamesCollections,
            int minPlayersToAddToDatabase,
            boolean isHiddenInMenu
    ) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.itemStack = itemStack;
        this.createDatabaseGame = createDatabaseGame;
        this.gamesCollections = gamesCollections;
        this.minPlayersToAddToDatabase = minPlayersToAddToDatabase;
        this.isHiddenInMenu = isHiddenInMenu;
    }

    public float getDropModifier() {
        return 1;
    }

    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = new ArrayList<>(64);

        options.add(new PreGameItemOption(1, (g, p) -> {
            PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(p.getUniqueId());
            Specializations selectedSpec = playerSettings.getSelectedSpec();
            AbstractPlayerClass apc = selectedSpec.create.get();

            ItemStack weaponSkin = playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.STEEL_SWORD).getItem();
            return new ItemBuilder(apc.getWeapon().getItem(weaponSkin))
                    .name(Component.text("Weapon Skin Preview", NamedTextColor.GREEN))
                    .noLore()
                    .get();
        }));
        options.add(new PreGameItemOption(4, new ItemBuilder(Material.NETHER_STAR)
                .name(Component.text("Pre-game Menu ", NamedTextColor.AQUA))
                .lore(WordWrap.wrap(Component.text("Allows you to change your class, select a weapon, and edit your settings.", NamedTextColor.GRAY), 150))
                .get(), (g, p) -> openMainMenu(p)));
        options.add(new PreGameItemOption(5, new ItemBuilder(Material.NOTE_BLOCK)
                .name(Component.text("Player Spec Information", NamedTextColor.AQUA))
                .lore(Component.text("Displays the amount of people on each specialization.", NamedTextColor.GRAY))
                        .get(),
                        (g, p) -> {
                            openPlayerSpecInfoMenu(g, p);
                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    if (PlainTextComponentSerializer.plainText().serialize(p.getOpenInventory().title()).equals("Player Specs")) {
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
                        .name(Component.text("Leave", NamedTextColor.RED))
                        .lore(Component.text("Right-Click to leave the game.", NamedTextColor.GRAY))
                        .get(),
                        (g, p) -> {
                            if (g.acceptsPeople()) {
                                g.removePlayer(p.getUniqueId());
                            }
                        }
                )
        );

        options.add(new GameFreezeOption());
        options.add(new DrowningDamage());
        options.add(new FallDamage());
        options.add(new KillDamage());
        options.add(new VoidDamage());
        options.add(new FlyOption());

        return options;
    }

    public static void openPlayerSpecInfoMenu(Game game, Player player) {
        Menu menu = new Menu("Player Specs", 9 * 4);
        int x = 3;
        for (SpecType value : SpecType.VALUES) {
            ItemBuilder itemBuilder = new ItemBuilder(value.itemStack)
                    .name(Component.text(value.name, value.getTextColor()));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Total: ", NamedTextColor.GREEN)
                              .append(Component.text((int) game.getPlayers().keySet().stream()
                                                               .map(PlayerSettings::getPlayerSettings)
                                                               .map(PlayerSettings::getSelectedSpec)
                                                               .filter(c -> c.specType == value)
                                                               .count(), NamedTextColor.GOLD)));
            lore.add(Component.empty());
            Arrays.stream(Specializations.VALUES)
                  .filter(classes -> classes.specType == value)
                  .forEach(classes -> {
                      int playersOnSpec = (int) game.getPlayers().keySet().stream()
                                                    .map(PlayerSettings::getPlayerSettings)
                                                    .map(PlayerSettings::getSelectedSpec)
                                                    .filter(c -> c == classes)
                                                    .count();
                      lore.add(Component.text(classes.name + " : ").append(Component.text(playersOnSpec, NamedTextColor.YELLOW)));
                  });
            itemBuilder.lore(lore);
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

    public boolean isHiddenInMenu() {
        return isHiddenInMenu;
    }
}
