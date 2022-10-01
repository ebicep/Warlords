package com.ebicep.warlords.pve.rewards;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.rewards.types.MasterworksFairReward;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RewardInventory {

    public static void sendRewardMessage(UUID uuid, BaseComponent... components) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            BaseComponent[] baseComponents = new BaseComponent[components.length + 1];
            baseComponents[0] = new TextComponent(ChatColor.GOLD + "Reward" + ChatColor.DARK_GRAY + " > ");
            System.arraycopy(components, 0, baseComponents, 1, components.length);
            offlinePlayer.getPlayer().spigot().sendMessage(baseComponents);
        }
    }

    public static void openRewardInventory(Player player, int page) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();
            List<MasterworksFairReward> masterworksFairRewards = databasePlayerPvE.getRewards().stream()
                    .filter(reward -> reward.getTimeClaimed() == null)
                    .collect(Collectors.toList());

            Menu menu = new Menu("Your Rewards", 9 * 6);

            if (masterworksFairRewards.isEmpty()) {
                menu.setItem(4, 2, new ItemBuilder(Material.BARRIER).name(ChatColor.RED + "You have no rewards to claim!").get(), (m, e) -> {
                });
            } else {
                for (int i = 0; i < 45; i++) {
                    int rewardNumber = ((page - 1) * 45) + i;
                    if (rewardNumber < masterworksFairRewards.size()) {
                        MasterworksFairReward masterworksFairReward = masterworksFairRewards.get(rewardNumber);
                        menu.setItem(i % 9, i / 9,
                                masterworksFairReward.getItem(),
                                (m, e) -> {
                                    masterworksFairReward.getRewards().forEach(databasePlayerPvE::addCurrency);
                                    masterworksFairReward.setTimeClaimed();
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                                    sendRewardMessage(
                                            player.getUniqueId(),
                                            new TextComponent(ChatColor.GREEN + "Claimed: "),
                                            new TextComponentBuilder(ChatColor.GREEN + masterworksFairReward.getFrom() + " Reward")
                                                    .setHoverItem(masterworksFairReward.getItemWithoutClaim())
                                                    .getTextComponent()
                                    );

                                    openRewardInventory(player, page);
                                }
                        );

                    } else {
                        break;
                    }
                }

                menu.setItem(3, 5,
                        new ItemBuilder(Material.GOLD_BLOCK)
                                .name(ChatColor.GREEN + "Click to claim all rewards!")
                                .get(),
                        (m, e) -> {
                            for (MasterworksFairReward masterworksFairReward : masterworksFairRewards) {
                                masterworksFairReward.getRewards().forEach(databasePlayerPvE::addCurrency);
                                masterworksFairReward.setTimeClaimed();

                                sendRewardMessage(
                                        player.getUniqueId(),
                                        new TextComponent(ChatColor.GREEN + "Claimed: "),
                                        new TextComponentBuilder(ChatColor.GREEN + masterworksFairReward.getFrom() + " Reward")
                                                .setHoverItem(masterworksFairReward.getItemWithoutClaim())
                                                .getTextComponent()
                                );
                            }
                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                            openRewardInventory(player, page);
                        }
                );

            }

            if (page - 1 > 0) {
                menu.setItem(0, 5,
                        new ItemBuilder(Material.ARROW)
                                .name(ChatColor.GREEN + "Previous Page")
                                .lore(ChatColor.YELLOW + "Page " + (page - 1))
                                .get(),
                        (m, e) -> {
                            openRewardInventory(player, page - 1);
                        }
                );
            }
            if (masterworksFairRewards.size() > (page * 45)) {
                menu.setItem(8, 5,
                        new ItemBuilder(Material.ARROW)
                                .name(ChatColor.GREEN + "Next Page")
                                .lore(ChatColor.YELLOW + "Page " + (page + 1))
                                .get(),
                        (m, e) -> {
                            openRewardInventory(player, page + 1);
                        }
                );
            }


            menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        });
    }

}
