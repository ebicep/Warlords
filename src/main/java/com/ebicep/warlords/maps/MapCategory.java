package com.ebicep.warlords.maps;

import com.ebicep.warlords.maps.option.Option;
import com.ebicep.warlords.maps.option.TextOption;
import com.ebicep.warlords.util.LocationFactory;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum MapCategory {
    CAPTURE_THE_FLAG("Capture The Flag") {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
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
                    ChatColor.GREEN + "GO!",
                    ChatColor.YELLOW + "Steal and capture the enemy flag!"
            ));
            return options;
        }
    },
    INTERCEPTION("Interception") {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
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
                    ChatColor.GREEN + "GO!",
                    ChatColor.YELLOW + "Capture the marked points!"
            ));
            return options;
        }
    },
    DEBUG("Debug Map") {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            options.add(TextOption.Type.TITLE.create(
                    ChatColor.GREEN + "GO!",
                    ChatColor.YELLOW + "Debug some issued!"
            ));
            return options;
        }
    },
    OTHER("PLACEHOLDER") {
        @Override
        public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
            List<Option> options = new ArrayList<>();
            options.add(TextOption.Type.TITLE.create(
                    ChatColor.GREEN + "GO!",
                    ChatColor.YELLOW + "PLACEHOLDER!"
            ));
            return options;
        }
    },
    DUEL("Duel") {
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

    ;

    private final String name;

    MapCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons);
}
