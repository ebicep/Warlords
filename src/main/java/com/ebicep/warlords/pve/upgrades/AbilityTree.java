package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public class AbilityTree {

    private final WarlordsPlayer player;
    private final List<AbstractUpgradeBranch<?>> upgradeBranches = new ArrayList<>();
    private final List<UpgradeLog> upgradeLog = new ArrayList<>();

    public AbilityTree(WarlordsPlayer player) {
        this.player = player;
    }

    public void openAbilityTree() {
        Menu menu = new Menu("Upgrades", 9 * 4);

        for (int i = 0; i < upgradeBranches.size(); i++) {
            AbstractUpgradeBranch<?> upgradeBranch = upgradeBranches.get(i);
            menu.setItem(
                    i + 2,
                    1,
                    new ItemBuilder(upgradeBranch.getItemStack())
                            .name(ChatColor.GOLD + upgradeBranch.getItemName())
                            .get(),
                    (n, e) -> upgradeBranch.openUpgradeBranchMenu()
            );
        }
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);

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

    public static class UpgradeLog {
        @Field("time_elapsed")
        private final int gameTimeLeft;
        @Field("name")
        private final String upgradeName;
        @Field("description")
        private final String upgradeDescription;

        public UpgradeLog(int gameTimeLeft, String upgradeName, String upgradeDescription) {
            this.gameTimeLeft = gameTimeLeft;
            this.upgradeName = upgradeName;
            this.upgradeDescription = upgradeDescription;
        }
    }
}
