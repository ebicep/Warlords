package com.ebicep.warlords.player.general;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.ebicep.warlords.menu.Menu.*;

public class Settings {

    public enum HotkeyMode {

        NEW_MODE(new ItemBuilder(Material.REDSTONE)
                .name(ChatColor.GREEN + "Hotkey Mode")
                .lore(ChatColor.AQUA + "Currently selected " + ChatColor.YELLOW + "NEW", "", ChatColor.YELLOW + "Click here to enable Classic mode.")
                .get()),
        CLASSIC_MODE(new ItemBuilder(Material.SNOWBALL)
                .name(ChatColor.GREEN + "Hotkey Mode")
                .lore(ChatColor.YELLOW + "Currently selected " + ChatColor.AQUA + "Classic", "", ChatColor.YELLOW + "Click here to enable NEW mode.")
                .get()),

        ;

        public final ItemStack item;

        HotkeyMode(ItemStack item) {
            this.item = item;
        }

    }

    public enum ParticleQuality {

        LOW(new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).name(ChatColor.GOLD + "Low Quality").get(),
                ChatColor.GRAY + "Heavily reduces the amount of\n" + ChatColor.GRAY + "particles you will see.",
                2
        ),
        MEDIUM(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name(ChatColor.YELLOW + "Medium Quality").get(),
                ChatColor.GRAY + "Reduces the amount of particles\n" + ChatColor.GRAY + "seem.",
                4
        ),
        HIGH(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name(ChatColor.GREEN + "High Quality").get(),
                ChatColor.GRAY + "Shows all particles for the best\n" + ChatColor.GRAY + "experience.",
                100000
        ),

        ;

        public final ItemStack item;
        public final String description;
        public final int particleReduction;

        ParticleQuality(ItemStack item, String description, int particleReduction) {
            this.item = item;
            this.description = description;
            this.particleReduction = particleReduction;
        }

    }

    public enum FlagMessageMode {

        RELATIVE(new ItemBuilder(Material.COMPASS)
                .name(ChatColor.GREEN + "Flag Message Mode")
                .lore(
                        ChatColor.AQUA + "Currently selected " + ChatColor.YELLOW + "Relative",
                        ChatColor.GRAY + "Prints out flag messages with 'YOUR/ENEMY'",
                        "",
                        ChatColor.YELLOW + "Click here to enable Absolute mode."
                )
                .get()
        ),
        ABSOLUTE(new ItemBuilder(Material.WHITE_WOOL)
                .name(ChatColor.GREEN + "Flag Message Mode")
                .lore(
                        ChatColor.AQUA + "Currently selected " + ChatColor.YELLOW + "Absolute",
                        ChatColor.GRAY + "Prints out flag messages with team names",
                        "",
                        ChatColor.YELLOW + "Click here to enable Relative mode."
                )
                .get()
        ),

        ;

        public final ItemStack item;

        FlagMessageMode(ItemStack item) {
            this.item = item;
        }
    }

    public static class ChatSettings {

        public static void openChatSettingsMenu(Player player) {
            PlayerSettings settings = PlayerSettings.getPlayerSettings(player);

            Menu menu = new Menu("Chat Settings", 9 * 4);

            menu.setItem(1, 1,
                    new ItemBuilder(Material.NETHER_WART)
                            .name(ChatColor.GREEN + "Damage Messages")
                            .lore(
                                    ChatColor.AQUA + "Currently Selected " + ChatColor.YELLOW + settings.getChatDamageMode().name,
                                    WordWrap.wrapWithNewline(ChatColor.GRAY + "Damage received and damage dealt", 150),
                                    "",
                                    ChatColor.YELLOW + "Click to change"
                            )
                            .get(),
                    (m, e) -> {
                        settings.setChatDamageMode(settings.getChatDamageMode().next());
                        DatabaseManager.updatePlayer(player, databasePlayer -> databasePlayer.setChatDamageMode(settings.getChatDamageMode()));
                        openChatSettingsMenu(player);
                    }
            );
            menu.setItem(2, 1,
                    new ItemBuilder(Material.CYAN_DYE)
                            .name(ChatColor.GREEN + "Healing Messages")
                            .lore(
                                    ChatColor.AQUA + "Currently Selected " + ChatColor.YELLOW + settings.getChatHealingMode().name,
                                    WordWrap.wrapWithNewline(ChatColor.GRAY + "Healing received and healing given", 150),
                                    "",
                                    ChatColor.YELLOW + "Click to change"
                            )
                            .get(),
                    (m, e) -> {
                        settings.setChatHealingMode(settings.getChatHealingMode().next());
                        DatabaseManager.updatePlayer(player, databasePlayer -> databasePlayer.setChatHealingMode(settings.getChatHealingMode()));
                        openChatSettingsMenu(player);
                    }
            );
            menu.setItem(3, 1,
                    new ItemBuilder(Material.SUGAR_CANE)
                            .name(ChatColor.GREEN + "Energy Messages")
                            .lore(
                                    ChatColor.AQUA + "Currently Selected " + ChatColor.YELLOW + settings.getChatEnergyMode().name,
                                    WordWrap.wrapWithNewline(ChatColor.GRAY + "Energy received and energy given", 150),
                                    "",
                                    ChatColor.YELLOW + "Click to change"
                            )
                            .get(),
                    (m, e) -> {
                        settings.setChatEnergyMode(settings.getChatEnergyMode().next());
                        DatabaseManager.updatePlayer(player, databasePlayer -> databasePlayer.setChatEnergyMode(settings.getChatEnergyMode()));
                        openChatSettingsMenu(player);
                    }
            );

            menu.setItem(3, 3, MENU_BACK, (m, e) -> WarlordsNewHotbarMenu.SettingsMenu.openSettingsMenu(player));
            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public enum ChatDamage {
            ALL("All"),
            CRITS_ONLY("Crits Only"),
            NONE("None");

            public final String name;

            ChatDamage(String name) {
                this.name = name;
            }

            public ChatDamage next() {
                return values()[(ordinal() + 1) % values().length];
            }
        }

        public enum ChatHealing {
            ALL("All"),
            CRITS_ONLY("Crits Only"),
            NONE("None");

            public final String name;

            ChatHealing(String name) {
                this.name = name;
            }

            public ChatHealing next() {
                return values()[(ordinal() + 1) % values().length];
            }
        }

        public enum ChatEnergy {
            ALL("All"),
            OFF("Off");

            public final String name;

            ChatEnergy(String name) {
                this.name = name;
            }

            public ChatEnergy next() {
                return values()[(ordinal() + 1) % values().length];
            }
        }


    }

}
