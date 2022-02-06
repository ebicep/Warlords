package com.ebicep.warlords.maps;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.maps.option.*;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.Weapons;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.LocationFactory;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.bukkit.Material;

public enum MapCategory {
    CAPTURE_THE_FLAG("Capture The Flag") {
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
    INTERCEPTION("Interception") {
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
    DUEL("Duel") {
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
    TEAM_DEATHMATCH("Team Deathmatch") {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            String color = "" + ChatColor.YELLOW + ChatColor.BOLD;
            options.add(TextOption.Type.CHAT_CENTERED.create(
                    "" + ChatColor.WHITE + ChatColor.BOLD + "Warlords",
                    "",
                    color + "First player to kill their opponent",
                    color + "5 times wins the duel!",
                    ""
            ));
            options.add(TextOption.Type.TITLE.create(
                    ChatColor.GREEN + "GO!"
            ));
            return options;
        }
    },
    DEBUG("Debug Map") {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            options.add(TextOption.Type.TITLE.create(
                    10,
                    ChatColor.GREEN + "GO!",
                    ChatColor.YELLOW + "Debug some issued!"
            ));
            return options;
        }
    },
    OTHER("PLACEHOLDER") {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = super.initMap(map, loc, addons);

            options.add(TextOption.Type.TITLE.create(
                    10,
                    ChatColor.GREEN + "GO!",
                    ChatColor.YELLOW + "PLACEHOLDER!"
            ));
            return options;
        }
    },

    ;

    private final String name;

    MapCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = new ArrayList<>(64);

        options.add(new PreGameItemOption(6, new ItemBuilder(Material.NETHER_STAR)
                .name(ChatColor.AQUA + "Pre-game Menu ")
                .lore(ChatColor.GRAY + "Allows you to change your class, select a\nweapon, and edit your settings.")
                .get()));
        options.add(new PreGameItemOption(7, (g, p) -> !g.acceptsPeople() ? null : new ItemBuilder(Material.BARRIER)
                .name(ChatColor.RED + "Leave")
                .lore(ChatColor.GRAY + "Right-Click to leave the game.")
                .get(), (g, p) -> {
                        if (g.acceptsPeople()) {
                            g.removePlayer(p.getUniqueId());
                        }
                }));
        options.add(new PreGameItemOption(1, (g, p) -> {
            PlayerSettings playerSettings = Warlords.getPlayerSettings(p.getUniqueId());
            Classes selectedClass = playerSettings.getSelectedClass();
            AbstractPlayerClass apc = selectedClass.create.get();

            return new ItemBuilder(apc.getWeapon()
                    .getItem(playerSettings.getWeaponSkins()
                            .getOrDefault(selectedClass, Weapons.FELFLAME_BLADE).item))
                    .name("§aWeapon Skin Preview")
                    .lore("")
                    .get();
        }));
        options.add(new GameFreezeOption());

        return options;
    }
}
