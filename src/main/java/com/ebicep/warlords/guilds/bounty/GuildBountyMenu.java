package com.ebicep.warlords.guilds.bounty;

import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.menu.GuildShopMenu;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;

public final class GuildBountyMenu {

    private GuildBountyMenu() {
    }

    public static void addGuildBountiesToMenu(Player player, Menu menu, int y) {
        Pair<Guild, GuildPlayer> guildPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
        if (guildPair == null) {
            menu.setItem(1, y, new ItemBuilder(Material.WRITABLE_BOOK)
                    .name(Component.text("Guild Bounties", NamedTextColor.GOLD))
                    .lore(Component.text("Join a guild to unlock weekly guild bounties.", NamedTextColor.GRAY))
                    .get(), (m, e) -> {
            });
            for (int i = 0; i < GuildBountyManager.MAX_SLOTS; i++) {
                menu.setItem(i + 2, y, getLockedItem(i, true), (m, e) -> {
                });
            }
            return;
        }

        Guild guild = guildPair.getA();
        GuildBountyData data = GuildBountyManager.getOrCreateData(guild);
        menu.setItem(1, y, new ItemBuilder(Material.WRITABLE_BOOK)
                .name(Component.text("Guild Bounties", NamedTextColor.GOLD))
                .lore(
                        Component.text("Weekly objectives completed by the entire guild.", NamedTextColor.GRAY),
                        Component.text("Unlocked Slots: ", NamedTextColor.GRAY)
                                 .append(Component.text(data.getUnlockedSlots() + "/" + GuildBountyManager.MAX_SLOTS, NamedTextColor.YELLOW))
                )
                .get(), (m, e) -> {
        });

        List<GuildBountyProgress> activeBounties = data.getActiveBounties();
        for (int i = 0; i < GuildBountyManager.MAX_SLOTS; i++) {
            if (i >= data.getUnlockedSlots()) {
                menu.setItem(i + 2, y, getLockedItem(i, false), (m, e) -> GuildShopMenu.openGuildShopMenu(guild, player));
                continue;
            }
            GuildBountyProgress progress = activeBounties.get(i);
            menu.setItem(i + 2, y, getBountyItem(progress), (m, e) -> {
            });
        }
    }

    private static ItemStack getLockedItem(int slotIndex, boolean noGuild) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.BARRIER)
                .name(Component.text("Guild Bounty Slot " + (slotIndex + 1), NamedTextColor.RED));
        if (noGuild) {
            itemBuilder.lore(Component.text("You must be in a guild to use this slot.", NamedTextColor.GRAY));
        } else {
            itemBuilder.lore(
                    Component.text("Locked", NamedTextColor.RED),
                    Component.empty(),
                    Component.text("Your guild needs to unlock this slot first.", NamedTextColor.GRAY)
            );
        }
        return itemBuilder.get();
    }

    private static ItemStack getBountyItem(GuildBountyProgress progress) {
        GuildBounty bounty = progress.getBounty();
        ItemBuilder itemBuilder = new ItemBuilder(bounty.getMaterial())
                .name(Component.text(bounty.getName(), progress.isCompleted() ? NamedTextColor.GREEN : NamedTextColor.GOLD))
                .lore(WordWrap.wrap(Component.text(bounty.getDescription(), NamedTextColor.GRAY), 160));

        itemBuilder.addLore(
                Component.empty(),
                progress.isCompleted() ? Component.text("Completed for this week!", NamedTextColor.GREEN) : getProgressComponent(progress),
                Component.empty(),
                Component.text("Rewards for every guild member:", NamedTextColor.GRAY)
        );

        LinkedHashMap<Spendable, Long> playerRewards = bounty.getPlayerRewards();
        playerRewards.forEach((spendable, amount) -> itemBuilder.addLore(
                Component.text(" +", NamedTextColor.DARK_GRAY).append(spendable.getCostColoredName(amount))
        ));
        itemBuilder.addLore(
                Component.empty(),
                Component.text("Guild Rewards:", NamedTextColor.GRAY),
                Component.text(" +" + NumberFormat.addCommas(bounty.getGuildCoins()) + " Guild Coins", NamedTextColor.YELLOW),
                Component.text(" +" + NumberFormat.addCommas(bounty.getGuildExperience()) + " Guild XP", NamedTextColor.AQUA)
        );

        if (progress.isCompleted()) {
            itemBuilder.enchant(Enchantment.RESPIRATION, 1);
        }
        return itemBuilder.get();
    }

    private static Component getProgressComponent(GuildBountyProgress progress) {
        GuildBounty bounty = progress.getBounty();
        if (bounty == GuildBounty.REACH_ONSLAUGHT_60_MINUTES) {
            return Component.text("Progress: ", NamedTextColor.GRAY)
                            .append(Component.text(formatTime(progress.getValue()), NamedTextColor.GOLD))
                            .append(Component.text("/", NamedTextColor.AQUA))
                            .append(Component.text(formatTime(bounty.getTarget()), NamedTextColor.GOLD));
        }
        return Component.text("Progress: ", NamedTextColor.GRAY)
                        .append(Component.text(NumberFormat.addCommas(progress.getValue()), NamedTextColor.GOLD))
                        .append(Component.text("/", NamedTextColor.AQUA))
                        .append(Component.text(NumberFormat.addCommas(bounty.getTarget()), NamedTextColor.GOLD));
    }

    private static String formatTime(long seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }
}
