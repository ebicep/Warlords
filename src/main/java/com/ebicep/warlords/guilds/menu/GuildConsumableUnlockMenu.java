package com.ebicep.warlords.guilds.menu;

import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildPermissions;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.consumables.GuildConsumableManager;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.consumables.Consumable;
import com.ebicep.warlords.pve.consumables.ConsumableRegistry;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;

public final class GuildConsumableUnlockMenu {

    private GuildConsumableUnlockMenu() {
    }

    public static void open(Player player, Guild guild) {
        Menu menu = new Menu("Guild Vial Unlocks", 9 * 6);
        Optional<GuildPlayer> guildPlayer = guild.getPlayerMatchingUUID(player.getUniqueId());
        boolean canPurchase = guildPlayer.isPresent()
                && guild.playerHasPermission(guildPlayer.get(), GuildPermissions.PURCHASE_UPGRADES);

        int index = 0;
        for (Consumable consumable : ConsumableRegistry.values()) {
            boolean unlocked = GuildConsumableManager.isUnlocked(guild, consumable);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(consumable.getDescription(), NamedTextColor.GRAY));
            lore.add(Component.empty());
            lore.add(Component.text("Effect: ", NamedTextColor.GRAY).append(Component.text(consumable.getEffectDescription(), NamedTextColor.GREEN)));
            if (consumable.isTimed()) {
                lore.add(Component.text("Duration: ", NamedTextColor.GRAY)
                                  .append(Component.text(consumable.getDuration().toHours() + " hours", NamedTextColor.YELLOW)));
            }
            lore.add(Component.text("Guild Unlock Cost: ", NamedTextColor.GRAY)
                              .append(Component.text(NumberFormat.addCommas(consumable.getGuildUnlockCost()) + " Guild Coins", NamedTextColor.YELLOW)));
            lore.add(Component.empty());
            if (unlocked) {
                lore.add(Component.text("PERMANENTLY UNLOCKED", NamedTextColor.GREEN));
            } else if (canPurchase) {
                lore.add(Component.text("Click to Permanently Unlock", NamedTextColor.YELLOW));
            } else {
                lore.add(Component.text("Requires Purchase Upgrades permission", NamedTextColor.RED));
            }

            menu.setItem(index % 7 + 1, index / 7 + 1,
                    new ItemBuilder(unlocked ? consumable.getMaterial() : Material.GRAY_DYE)
                            .name(Component.text(consumable.getName(), unlocked ? NamedTextColor.GREEN : NamedTextColor.GRAY))
                            .lore(lore)
                            .get(),
                    (m, e) -> {
                        if (!GuildConsumableManager.isUnlocked(guild, consumable) && canPurchase) {
                            confirmUnlock(player, guild, consumable);
                        }
                    }
            );
            index++;
        }

        menu.setItem(4, 5, MENU_BACK, (m, e) -> GuildMenu.openGuildMenu(guild, player, 1));
        menu.openForPlayer(player);
    }

    private static void confirmUnlock(Player player, Guild guild, Consumable consumable) {
        Menu.openConfirmationMenu(
                player,
                "Unlock " + consumable.getName(),
                3,
                Component.text("Unlock for Guild", NamedTextColor.GREEN),
                List.of(
                        Component.text("Effect: ", NamedTextColor.GRAY).append(Component.text(consumable.getEffectDescription(), NamedTextColor.GREEN)),
                        Component.empty(),
                        Component.text("Cost: ", NamedTextColor.GRAY)
                                 .append(Component.text(NumberFormat.addCommas(consumable.getGuildUnlockCost()) + " Guild Coins", NamedTextColor.YELLOW)),
                        Component.empty(),
                        Component.text("This unlock is permanent for the guild.", NamedTextColor.GRAY)
                ),
                Component.text("Cancel", NamedTextColor.RED),
                Menu.GO_BACK,
                (m, e) -> unlock(player, guild, consumable),
                (m, e) -> open(player, guild),
                m -> {}
        );
    }

    private static void unlock(Player player, Guild guild, Consumable consumable) {
        Optional<GuildPlayer> guildPlayer = guild.getPlayerMatchingUUID(player.getUniqueId());
        if (guildPlayer.isEmpty() || !guild.playerHasPermission(guildPlayer.get(), GuildPermissions.PURCHASE_UPGRADES)) {
            player.sendMessage(Component.text("You do not have permission to purchase guild upgrades.", NamedTextColor.RED));
            open(player, guild);
            return;
        }
        if (GuildConsumableManager.isUnlocked(guild, consumable)) {
            open(player, guild);
            return;
        }
        if (guild.getCurrentCoins() < consumable.getGuildUnlockCost()) {
            player.sendMessage(Component.text("The guild does not have enough Guild Coins.", NamedTextColor.RED));
            open(player, guild);
            return;
        }

        guild.addCurrentCoins(-consumable.getGuildUnlockCost());
        GuildConsumableManager.unlock(guild, consumable);
        guild.queueUpdate();
        guild.sendGuildMessageToOnlinePlayers(
                Component.text(player.getName(), NamedTextColor.AQUA)
                         .append(Component.text(" permanently unlocked ", NamedTextColor.GREEN))
                         .append(Component.text(consumable.getName(), NamedTextColor.YELLOW))
                         .append(Component.text(" in the Vial Shop.", NamedTextColor.GREEN)),
                true
        );
        open(player, guild);
    }
}
