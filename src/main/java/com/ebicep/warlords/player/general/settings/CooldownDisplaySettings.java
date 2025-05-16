package com.ebicep.warlords.player.general.settings;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.option.PlayerCooldownDisplayOption;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.exception.SignGUIVersionException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collections;

public class CooldownDisplaySettings {

    public static final ItemStack ITEM = new ItemBuilder(Material.GRAY_DYE)
            .name(Component.text("Cooldown Display Settings", NamedTextColor.GREEN))
            .lore(WordWrap.wrap(Component.text("Customize the appearance of the cooldowns above player heads.", NamedTextColor.GRAY), 150))
            .get();

    public static void openMenu(Player player, DatabasePlayer databasePlayer) {
        Menu menu = new Menu("Cooldown Display Settings", 9 * 4);
        CooldownDisplaySettings cooldownDisplaySettings = databasePlayer.getCooldownDisplaySettings();
        menu.setItem(1, 1, databasePlayer.getCooldownDisplaySettings().getCooldownDisplayMode().item, (m, e) -> {
                    player.performCommand("cooldowndisplaymode");
                    openMenu(player, databasePlayer);
                }
        );
        menu.setItem(2, 1,
                new ItemBuilder(Material.BONE_MEAL)
                        .name(Component.text("Text Scale", NamedTextColor.GREEN))
                        .lore(
                                Component.text("Current Scale: ", NamedTextColor.GRAY)
                                         .append(Component.text(NumberFormat.formatOptionalHundredths(cooldownDisplaySettings.getTextScale()), NamedTextColor.AQUA)),
                                Component.empty(),
                                Component.text("Scale how the cooldown text is shown.", NamedTextColor.GRAY)
                        )
                        .get(),
                (m, e) -> {
                    try {
                        SignGUI.builder()
                               .setLines("", "Text Scale", "0.5 <= x <= 1.5", "Current: " + NumberFormat.formatOptionalHundredths(cooldownDisplaySettings.getTextScale()))
                               .setHandler((p, lines) -> {
                                   String score = lines.getLine(0);
                                   try {
                                       float newScale = Float.parseFloat(score);
                                       if (newScale < 0.499 || 1.51 < newScale) {
                                           p.sendMessage(Component.text("Scale must be between 0.2 and 1.5", NamedTextColor.RED));
                                           return Collections.singletonList(SignGUIAction.displayNewLines(lines.getLines()));
                                       }
                                       cooldownDisplaySettings.setTextScale(newScale);
                                       p.sendMessage(Component.text("Text Scale set to " + NumberFormat.formatOptionalHundredths(newScale), NamedTextColor.GREEN));
                                       WarlordsEntity warlordsEntity = Warlords.getPlayer(p);
                                       if (warlordsEntity != null) {
                                           warlordsEntity.getGame().doOnOption(PlayerCooldownDisplayOption.class, PlayerCooldownDisplayOption::forcePacketUpdate);
                                       }
                                   } catch (Exception e1) {
                                       p.sendMessage(Component.text("Invalid Scale", NamedTextColor.GREEN));
                                   }
                                   return null;
                               })
                               .callHandlerSynchronously(Warlords.getInstance())
                               .build()
                               .open(player);
                    } catch (SignGUIVersionException ex) {
                        ChatUtils.MessageType.WARLORDS.sendErrorMessage(ex);
                    }
                }
        );
        menu.setItem(3, 1,
                new ItemBuilder(Material.ORANGE_DYE)
                        .name(Component.text("Item Scale", NamedTextColor.GREEN))
                        .lore(
                                Component.text("Current Scale: ", NamedTextColor.GRAY)
                                         .append(Component.text(NumberFormat.formatOptionalHundredths(cooldownDisplaySettings.getItemScale()), NamedTextColor.AQUA)),
                                Component.empty(),
                                Component.text("Scale how the cooldown item is shown.", NamedTextColor.GRAY)
                        )
                        .get(),
                (m, e) -> {
                    try {
                        SignGUI.builder()
                               .setLines("", "Item Scale", "0.25 <= x <= 0.6", "Current: " + NumberFormat.formatOptionalHundredths(cooldownDisplaySettings.getItemScale()))
                               .setHandler((p, lines) -> {
                                   String score = lines.getLine(0);
                                   try {
                                       float newScale = Float.parseFloat(score);
                                       if (newScale < 0.249 || 0.61 < newScale) {
                                           p.sendMessage(Component.text("Scale must be between 0.25 and 0.6", NamedTextColor.RED));
                                           return Collections.singletonList(SignGUIAction.displayNewLines(lines.getLines()));
                                       }
                                       cooldownDisplaySettings.setItemScale(newScale);
                                       p.sendMessage(Component.text("Item Scale set to " + NumberFormat.formatOptionalHundredths(newScale), NamedTextColor.GREEN));
                                       WarlordsEntity warlordsEntity = Warlords.getPlayer(p);
                                       if (warlordsEntity != null) {
                                           warlordsEntity.getGame().doOnOption(PlayerCooldownDisplayOption.class, PlayerCooldownDisplayOption::forcePacketUpdate);
                                       }
                                   } catch (Exception e1) {
                                       p.sendMessage(Component.text("Invalid Scale", NamedTextColor.GREEN));
                                   }
                                   return null;
                               })
                               .callHandlerSynchronously(Warlords.getInstance())
                               .build()
                               .open(player);
                    } catch (SignGUIVersionException ex) {
                        ChatUtils.MessageType.WARLORDS.sendErrorMessage(ex);
                    }
                }
        );

        menu.setItem(3, 3, Menu.MENU_BACK, (m, e) -> WarlordsNewHotbarMenu.SettingsMenu.openSettingsMenu(player));
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }
    @Field("cooldown_display_mode")
    private CooldownDisplaySettings.CooldownDisplayMode cooldownDisplayMode = CooldownDisplaySettings.CooldownDisplayMode.ON;
    @Field("item_scale")
    private float itemScale = 0.5f;
    @Field("text_scale")
    private float textScale = 1f;

    public CooldownDisplayMode getCooldownDisplayMode() {
        return cooldownDisplayMode;
    }

    public void setCooldownDisplayMode(CooldownDisplayMode cooldownDisplayMode) {
        this.cooldownDisplayMode = cooldownDisplayMode;
    }

    public float getTextScale() {
        return textScale;
    }

    public void setTextScale(float textScale) {
        this.textScale = textScale;
    }

    public float getItemScale() {
        return itemScale;
    }

    public void setItemScale(float itemScale) {
        this.itemScale = itemScale;
    }

    public enum CooldownDisplayMode {

        ON(new ItemBuilder(Material.RED_DYE)
                .name(Component.text("Cooldown Display", NamedTextColor.GREEN))
                .lore(
                        Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("On", NamedTextColor.AQUA)),
                        Component.empty(),
                        Component.text("Toggles whether or not you", NamedTextColor.GRAY),
                        Component.text("can see teammates' cooldowns", NamedTextColor.GRAY),
                        Component.empty(),
                        Component.text("Click here to disable cooldown display.", NamedTextColor.YELLOW)
                )
                .get()
        ),
        OFF(new ItemBuilder(Material.GRAY_DYE)
                .name(Component.text("Cooldown Display", NamedTextColor.GREEN))
                .lore(
                        Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("Off", NamedTextColor.YELLOW)),
                        Component.empty(),
                        Component.text("Toggles whether or not you", NamedTextColor.GRAY),
                        Component.text("can see teammates' cooldowns", NamedTextColor.GRAY),
                        Component.empty(),
                        Component.text("Click here to enable cooldown display.", NamedTextColor.YELLOW)
                )
                .get()
        ),

        ;

        public final ItemStack item;

        CooldownDisplayMode(ItemStack item) {
            this.item = item;
        }
    }

}
