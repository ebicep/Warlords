package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public class AbilityTree {

    private final WarlordsPlayer player;
    private final List<UpgradeBranch<?>> upgradeBranches = new ArrayList<>();

    public AbilityTree(WarlordsPlayer player) {
        this.player = player;
    }

    public void openAbilityTree() {
        Menu menu = new Menu("Upgrades", 9 * 4);

        for (int i = 0; i < upgradeBranches.size(); i++) {
            UpgradeBranch<?> upgradeBranch = upgradeBranches.get(i);
            menu.setItem(
                    i + 2,
                    1,
                    new ItemBuilder(upgradeBranch.getItemStack())
                            .name(ChatColor.GREEN + upgradeBranch.getItemName())
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

    public List<UpgradeBranch<?>> getUpgradeBranches() {
        return upgradeBranches;
    }
}
