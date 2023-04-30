package com.ebicep.warlords.player.general;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.ebicep.warlords.menu.Menu.*;

public class Settings {

    public enum HotkeyMode {

        NEW_MODE(new ItemBuilder(Material.REDSTONE)
                .name(Component.text("Hotkey Mode", NamedTextColor.GREEN))
                .lore(
                        Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("NEW", NamedTextColor.AQUA)),
                        Component.empty(),
                        Component.text("Click here to enable Classic mode.", NamedTextColor.YELLOW)
                )
                .get()),
        CLASSIC_MODE(new ItemBuilder(Material.SNOWBALL)
                .name(Component.text("Hotkey Mode", NamedTextColor.GREEN))
                .lore(
                        Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("Classic", NamedTextColor.YELLOW)),
                        Component.empty(),
                        Component.text("Click here to enable NEW mode.", NamedTextColor.YELLOW)
                )
                .get()),

        ;

        public final ItemStack item;

        HotkeyMode(ItemStack item) {
            this.item = item;
        }

    }

    public enum ParticleQuality {

        LOW(new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).name(Component.text("Low Quality", NamedTextColor.GOLD)).get(),
                Component.text("Heavily reduces the amount of particles you will see.", NamedTextColor.GRAY),
                2
        ),
        MEDIUM(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name(Component.text("Medium Quality", NamedTextColor.YELLOW)).get(),
                Component.text("Reduces the amount of particles seem.", NamedTextColor.GRAY),
                4
        ),
        HIGH(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name(Component.text("High Quality", NamedTextColor.GREEN)).get(),
                Component.text("Shows all particles for the best experience.", NamedTextColor.GRAY),
                100000
        ),

        ;

        public final ItemStack item;
        public final TextComponent description;
        public final int particleReduction;

        ParticleQuality(ItemStack item, TextComponent description, int particleReduction) {
            this.item = item;
            this.description = description;
            this.particleReduction = particleReduction;
        }

    }

    public enum FlagMessageMode {

        RELATIVE(new ItemBuilder(Material.COMPASS)
                .name(Component.text("Flag Message Mode", NamedTextColor.GREEN))
                .lore(
                        Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("Relative", NamedTextColor.AQUA)),
                        Component.text("Prints out flag messages with 'YOUR/ENEMY'", NamedTextColor.GRAY),
                        Component.empty(),
                        Component.text("Click here to enable Absolute mode.", NamedTextColor.YELLOW)
                )
                .get()
        ),
        ABSOLUTE(new ItemBuilder(Material.WHITE_WOOL)
                .name(Component.text("Flag Message Mode", NamedTextColor.GREEN))
                .lore(
                        Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("Absolute", NamedTextColor.YELLOW)),
                        Component.text("Prints out flag messages with team names", NamedTextColor.GRAY),
                        Component.empty(),
                        Component.text("Click here to enable Relative mode.", NamedTextColor.YELLOW)
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
                            .name(Component.text("Damage Messages", NamedTextColor.GREEN))
                            .loreLEGACY(
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
                            .name(Component.text("Healing Messages", NamedTextColor.GREEN))
                            .loreLEGACY(
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
                            .name(Component.text("Energy Messages", NamedTextColor.GREEN))
                            .loreLEGACY(
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
            menu.setItem(4, 1,
                    new ItemBuilder(Material.BONE)
                            .name(Component.text("Kill Messages", NamedTextColor.GREEN))
                            .lore(
                                    Component.text("Currently Selected ", NamedTextColor.AQUA).append(Component.text(settings.getChatKillsMode().name, NamedTextColor.YELLOW)),
                                    WordWrap.wrapWithNewline(Component.text("Kill messages", NamedTextColor.GRAY), 150),
                                    Component.empty(),
                                    Component.text("Click to change", NamedTextColor.YELLOW)
                            )
                            .get(),
                    (m, e) -> {
                        settings.setChatKillsMode(settings.getChatKillsMode().next());
                        DatabaseManager.updatePlayer(player, databasePlayer -> databasePlayer.setChatKillsMode(settings.getChatKillsMode()));
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

        public enum ChatKills {
            ALL("All"),
            OFF("Off");

            public final String name;

            ChatKills(String name) {
                this.name = name;
            }

            public ChatKills next() {
                return values()[(ordinal() + 1) % values().length];
            }
        }


    }

}
