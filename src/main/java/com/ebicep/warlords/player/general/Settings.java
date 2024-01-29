package com.ebicep.warlords.player.general;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                        Component.empty(),
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
                        Component.empty(),
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

    public enum GlowingMode {

        ON(new ItemBuilder(Material.GLOW_INK_SAC)
                .name(Component.text("Glowing Mode", NamedTextColor.GREEN))
                .lore(
                        Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("On", NamedTextColor.AQUA)),
                        Component.empty(),
                        Component.text("Toggles whether or not players", NamedTextColor.GRAY),
                        Component.text("are allowed to glow", NamedTextColor.GRAY),
                        Component.empty(),
                        Component.text("Click here to disable glowing.", NamedTextColor.YELLOW)
                )
                .get()
        ),
        OFF(new ItemBuilder(Material.INK_SAC)
                .name(Component.text("Glowing Mode", NamedTextColor.GREEN))
                .lore(
                        Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("Off", NamedTextColor.YELLOW)),
                        Component.empty(),
                        Component.text("Toggles whether or not players", NamedTextColor.GRAY),
                        Component.text("are allowed to glow", NamedTextColor.GRAY),
                        Component.empty(),
                        Component.text("Click here to enable glowing.", NamedTextColor.YELLOW)
                )
                .get()
        ),

        ;

        public final ItemStack item;

        GlowingMode(ItemStack item) {
            this.item = item;
        }
    }

    public static class ChatSettings {

        private static final List<ChatMenuSetting<?>> MENU_SETTINGS = new ArrayList<>() {{
            add(new ChatMenuSetting<>(
                    ChatDamage.VALUES,
                    new ItemStack(Material.NETHER_WART),
                    "Damage Messages",
                    "Damage received and damage dealt",
                    DatabasePlayer::getChatDamageMode,
                    databasePlayer -> databasePlayer.setChatDamageMode(databasePlayer.getChatDamageMode().next())
            ));
            add(new ChatMenuSetting<>(
                    ChatHealing.VALUES,
                    new ItemStack(Material.CYAN_DYE),
                    "Healing Messages",
                    "Healing received and healing dealt",
                    DatabasePlayer::getChatHealingMode,
                    databasePlayer -> databasePlayer.setChatHealingMode(databasePlayer.getChatHealingMode().next())
            ));
            add(new ChatMenuSetting<>(
                    ChatEnergy.VALUES,
                    new ItemStack(Material.SUGAR_CANE),
                    "Energy Messages",
                    "Energy received and energy dealt",
                    DatabasePlayer::getChatEnergyMode,
                    databasePlayer -> databasePlayer.setChatEnergyMode(databasePlayer.getChatEnergyMode().next())
            ));
            add(new ChatMenuSetting<>(
                    ChatKills.VALUES,
                    new ItemStack(Material.BONE),
                    "Kill/Assist Messages",
                    "Kill and Assist messages",
                    DatabasePlayer::getChatKillsMode,
                    databasePlayer -> databasePlayer.setChatKillsMode(databasePlayer.getChatKillsMode().next())
            ));
            add(new ChatMenuSetting<>(
                    ChatInsignia.VALUES,
                    new ItemStack(Material.GOLD_NUGGET),
                    "Insignia Messages",
                    "Insignia gain messages",
                    DatabasePlayer::getChatInsigniaMode,
                    databasePlayer -> databasePlayer.setChatInsigniaMode(databasePlayer.getChatInsigniaMode().next())
            ));
            add(new ChatMenuSetting<>(
                    ChatEventPoints.VALUES,
                    new ItemStack(Material.GOLD_INGOT),
                    "Event Point Messages",
                    "Event Point messages",
                    DatabasePlayer::getChatEventPointsMode,
                    databasePlayer -> databasePlayer.setChatEventPointsMode(databasePlayer.getChatEventPointsMode().next())
            ));
            add(new ChatMenuSetting<>(
                    ChatUpgrade.VALUES,
                    new ItemStack(Material.BOOK),
                    "Upgrade Messages",
                    "Upgrade messages",
                    DatabasePlayer::getChatUpgradeMode,
                    databasePlayer -> databasePlayer.setChatUpgradeMode(databasePlayer.getChatUpgradeMode().next())
            ));
        }};

        public static void openChatSettingsMenu(Player player) {
            DatabaseManager.getPlayer(player, databasePlayer -> {
                Menu menu = new Menu("Chat Settings", 9 * 4);

                for (int i = 0; i < MENU_SETTINGS.size(); i++) {
                    ChatMenuSetting<?> menuSetting = MENU_SETTINGS.get(i);
                    ChatSetting<?> selectedSetting = menuSetting.getCurrentSetting.apply(databasePlayer);
                    menu.setItem(i % 7 + 1, i / 7 + 1,
                            new ItemBuilder(menuSetting.itemStack)
                                    .name(Component.text(menuSetting.name, NamedTextColor.GREEN))
                                    .lore(Component.text(menuSetting.description, NamedTextColor.GRAY))
                                    .addLore(Component.empty())
                                    .addLore(
                                            Arrays.stream(menuSetting.settings)
                                                  .map(chatSetting -> Component.text("🠒 " + chatSetting.getName(),
                                                          chatSetting == selectedSetting ? NamedTextColor.AQUA : NamedTextColor.GRAY
                                                  ))
                                                  .collect(Collectors.toList())
                                    )
                                    .addLore(
                                            Component.empty(),
                                            Component.text("Click to change", NamedTextColor.YELLOW)
                                    )
                                    .get(),
                            (m, e) -> {
                                DatabaseManager.updatePlayer(player, menuSetting.updateDatabasePlayerSettings);
                                openChatSettingsMenu(player);
                            }
                    );
                }

                menu.setItem(3, 3, MENU_BACK, (m, e) -> WarlordsNewHotbarMenu.SettingsMenu.openSettingsMenu(player));
                menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
                menu.openForPlayer(player);
            });
        }

        public enum ChatDamage implements ChatSetting<ChatDamage> {
            ALL("All"),
            CRITS_ONLY("Crits Only"),
            NONE("None");

            public static final ChatDamage[] VALUES = values();
            public final String name;

            ChatDamage(String name) {
                this.name = name;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public ChatDamage next() {
                return VALUES[(ordinal() + 1) % VALUES.length];
            }

            @Override
            public ChatDamage[] getValues() {
                return VALUES;
            }
        }

        public enum ChatHealing implements ChatSetting<ChatHealing> {
            ALL("All"),
            CRITS_ONLY("Crits Only"),
            NONE("None");

            public static final ChatHealing[] VALUES = values();
            public final String name;

            ChatHealing(String name) {
                this.name = name;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public ChatHealing next() {
                return VALUES[(ordinal() + 1) % VALUES.length];
            }

            @Override
            public ChatHealing[] getValues() {
                return VALUES;
            }
        }

        public enum ChatEnergy implements ChatSetting<ChatEnergy> {
            ALL("All"),
            OFF("Off");

            public static final ChatEnergy[] VALUES = values();
            public final String name;

            ChatEnergy(String name) {
                this.name = name;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public ChatEnergy next() {
                return VALUES[(ordinal() + 1) % VALUES.length];
            }

            @Override
            public ChatEnergy[] getValues() {
                return VALUES;
            }
        }

        public enum ChatKills implements ChatSetting<ChatKills> {
            ALL("All"),
            ONLY_ASSISTS("Only Assists"),
            NO_ASSISTS("No Assists"),
            OFF("Off");

            public static final ChatKills[] VALUES = values();
            public final String name;

            ChatKills(String name) {
                this.name = name;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public ChatKills next() {
                return VALUES[(ordinal() + 1) % VALUES.length];
            }

            @Override
            public ChatKills[] getValues() {
                return VALUES;
            }
        }

        public enum ChatInsignia implements ChatSetting<ChatInsignia> {
            ALL("All"),
            OFF("Off");

            public static final ChatInsignia[] VALUES = values();
            public final String name;

            ChatInsignia(String name) {
                this.name = name;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public ChatInsignia next() {
                return VALUES[(ordinal() + 1) % VALUES.length];
            }

            @Override
            public ChatInsignia[] getValues() {
                return VALUES;
            }
        }

        public enum ChatEventPoints implements ChatSetting<ChatEventPoints> {
            ALL("All"),
            OFF("Off");

            public static final ChatEventPoints[] VALUES = values();
            public final String name;

            ChatEventPoints(String name) {
                this.name = name;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public ChatEventPoints next() {
                return VALUES[(ordinal() + 1) % VALUES.length];
            }

            @Override
            public ChatEventPoints[] getValues() {
                return VALUES;
            }
        }

        public enum ChatUpgrade implements ChatSetting<ChatUpgrade> {
            ALL("All"),
            OFF("Off");

            public static final ChatUpgrade[] VALUES = values();
            public final String name;

            ChatUpgrade(String name) {
                this.name = name;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public ChatUpgrade next() {
                return VALUES[(ordinal() + 1) % VALUES.length];
            }

            @Override
            public ChatUpgrade[] getValues() {
                return VALUES;
            }
        }

        interface ChatSetting<T> {

            String getName();

            T next();

            T[] getValues();
        }

        private record ChatMenuSetting<T extends ChatSetting<T>>(
                T[] settings,
                ItemStack itemStack,
                String name,
                String description,
                Function<DatabasePlayer, T> getCurrentSetting,
                Consumer<DatabasePlayer> updateDatabasePlayerSettings
        ) {}


    }

}
