package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.*;

public class AbilityTree {

    private final WarlordsPlayer player;
    private final List<AbstractUpgradeBranch<?>> upgradeBranches = new ArrayList<>();
    private final List<UpgradeLog> upgradeLog = new ArrayList<>();

    private int maxMasterUpgrades = 3;

    public AbilityTree(WarlordsPlayer player) {
        this.player = player;
    }

    public void openAbilityTree() {
        Menu menu = new Menu("Upgrades", 9 * 5);

        for (int i = 0; i < upgradeBranches.size(); i++) {
            AbstractUpgradeBranch<?> upgradeBranch = upgradeBranches.get(i);
            menu.setItem(
                    i + 2,
                    2,
                    new ItemBuilder(upgradeBranch.getItemStack())
                            .name(ChatColor.GOLD + upgradeBranch.getItemName())
                            .lore(
                                    ChatColor.GRAY + ">> Click to open ability upgrade tree. <<",
                                    "",
                                    ChatColor.GRAY + "Upgrades Remaining: " + ChatColor.GREEN + upgradeBranches.get(i).getMaxUpgrades(),
                                    ChatColor.GRAY + "Free Upgrades Available: " + ChatColor.GREEN + upgradeBranches.get(i).getFreeUpgrades()
                            )
                            .get(),
                    (m, e) -> upgradeBranch.openUpgradeBranchMenu()
            );
        }
        menu.setItem(4, 0,
                new ItemBuilder(Material.GOLD_INGOT)
                        .name(ChatColor.GRAY + "Insignia: " + ChatColor.GOLD + "❂ " + NumberFormat.addCommas(player.getCurrency()))
                        .lore(ChatColor.GRAY + "Master Upgrades Remaining: " + ChatColor.GOLD + maxMasterUpgrades)
                        .get(),
                ACTION_DO_NOTHING
        );
        menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);


        if (player.getEntity() instanceof Player) {
            menu.openForPlayer((Player) player.getEntity());
        }
    }

    public WarlordsPlayer getPlayer() {
        return player;
    }

    public List<AbstractUpgradeBranch<?>> getUpgradeBranches() {
        return upgradeBranches;
    }

    public List<UpgradeLog> getUpgradeLog() {
        return upgradeLog;
    }

    public int getMaxMasterUpgrades() {
        return maxMasterUpgrades;
    }

    public void setMaxMasterUpgrades(int maxMasterUpgrades) {
        this.maxMasterUpgrades = maxMasterUpgrades;
    }

    public static class UpgradeLog {

        @Field("time_elapsed")
        private int gameTimeLeft;
        @Field("name")
        private String upgradeName;
        @Field("description")
        private String upgradeDescription;

        public UpgradeLog(int gameTimeLeft, String upgradeName, String upgradeDescription) {
            this.gameTimeLeft = gameTimeLeft;
            this.upgradeName = upgradeName;
            this.upgradeDescription = upgradeDescription;
        }
    }
}
