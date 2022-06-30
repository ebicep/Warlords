package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.*;

public abstract class UpgradeBranch<T extends AbstractAbility> {

    protected AbilityTree abilityTree;
    protected T ability;
    protected ItemStack itemStack;
    protected String itemName;

    protected List<Upgrade> treeA = new ArrayList<>();
    protected List<Upgrade> treeB = new ArrayList<>();
    protected List<Upgrade> treeC = new ArrayList<>();
    protected Upgrade masterUpgrade;

    public UpgradeBranch(AbilityTree abilityTree, T ability, ItemStack itemStack, String itemName) {
        this.abilityTree = abilityTree;
        this.ability = ability;
        this.itemStack = itemStack;
        this.itemName = itemName;
    }

    public void openUpgradeBranchMenu() {
        WarlordsPlayer player = abilityTree.getPlayer();
        Menu menu = new Menu("Upgrades", 9 * 6);

        for (int i = 0; i < treeA.size(); i++) {
            Upgrade upgrade = treeA.get(i);
            int finalI = i;
            menu.setItem(
                    2,
                    4 - i,
                    new ItemBuilder(upgrade.isUnlocked() ? new ItemStack(Material.WOOL, 1, (short) 5) : new ItemStack(Material.WOOL))
                            .name(ChatColor.GOLD + upgrade.getName())
                            .lore((upgrade.isUnlocked() ? ChatColor.GREEN : ChatColor.GRAY) + upgrade.getDescription() +
                                    "\n\n" + ChatColor.GRAY + "Cost: " + ChatColor.GOLD + upgrade.getCurrencyCost())
                            .get(),
                    (n, e) -> {
                        if (upgrade.isUnlocked()) {
                            player.sendMessage(ChatColor.RED + "You already unlocked this upgrade.");
                            return;
                        }
                        if (finalI != 0) {
                            if (!treeA.get(finalI - 1).isUnlocked()) {
                                player.sendMessage(ChatColor.RED + "You need to unlock the previous upgrade first!");
                                return;
                            }
                        }
                        switch (finalI) {
                            case 0:
                                a1();
                                break;
                            case 1:
                                a2();
                                break;
                            case 2:
                                a3();
                                break;
                        }
                        upgrade.setUnlocked(true);
                        updateInventory(player);
                        openUpgradeBranchMenu();
                    }
            );
        }

        for (int i = 0; i < treeB.size(); i++) {
            Upgrade upgrade = treeB.get(i);
            int finalI = i;
            menu.setItem(
                    4,
                    4 - i,
                    new ItemBuilder(upgrade.isUnlocked() ? new ItemStack(Material.WOOL, 1, (short) 5) : new ItemStack(Material.WOOL))
                            .name(ChatColor.GOLD + upgrade.getName())
                            .lore((upgrade.isUnlocked() ? ChatColor.GREEN : ChatColor.GRAY) + upgrade.getDescription() +
                                    "\n\n" + ChatColor.GRAY + "Cost: " + ChatColor.GOLD + upgrade.getCurrencyCost())
                            .get(),
                    (n, e) -> {
                        if (upgrade.isUnlocked()) {
                            player.sendMessage(ChatColor.RED + "You already unlocked this upgrade.");
                            return;
                        }
                        if (finalI != 0) {
                            if (!treeB.get(finalI - 1).isUnlocked()) {
                                player.sendMessage(ChatColor.RED + "You need to unlock the previous upgrade first!");
                                return;
                            }
                        }
                        switch (finalI) {
                            case 0:
                                if (player.getCurrency() <= upgrade.getCurrencyCost()) {
                                    player.sendMessage(ChatColor.RED + "You do not have enough currency to buy this upgrade!");
                                    return;
                                }

                                b1();
                                break;
                            case 1:
                                b2();
                                break;
                            case 2:
                                b3();
                                break;
                        }
                        upgrade.setUnlocked(true);
                        updateInventory(player);
                        openUpgradeBranchMenu();
                    }
            );
        }

        for (int i = 0; i < treeC.size(); i++) {
            Upgrade upgrade = treeC.get(i);
            int finalI = i;
            menu.setItem(
                    6,
                    4 - i,
                    new ItemBuilder(upgrade.isUnlocked() ? new ItemStack(Material.WOOL, 1, (short) 5) : new ItemStack(Material.WOOL))
                            .name(ChatColor.GOLD + upgrade.getName())
                            .lore((upgrade.isUnlocked() ? ChatColor.GREEN : ChatColor.GRAY) + upgrade.getDescription() +
                                    "\n\n" + ChatColor.GRAY + "Cost: " + ChatColor.GOLD + upgrade.getCurrencyCost())
                            .get(),
                    (n, e) -> {
                        if (upgrade.isUnlocked()) {
                            player.sendMessage(ChatColor.RED + "You already unlocked this upgrade.");
                            return;
                        }
                        if (finalI != 0) {
                            if (!treeC.get(finalI - 1).isUnlocked()) {
                                player.sendMessage(ChatColor.RED + "You need to unlock the previous upgrade first!");
                                return;
                            }
                        }
                        switch (finalI) {
                            case 0:
                                c1();
                                break;
                            case 1:
                                c2();
                                break;
                            case 2:
                                c3();
                                break;
                        }
                        upgrade.setUnlocked(true);
                        updateInventory(player);
                        openUpgradeBranchMenu();
                    }
            );
        }

        menu.setItem(
                4,
                0,
                new ItemBuilder(masterUpgrade.isUnlocked() ? new ItemStack(Material.WOOL, 1, (short) 5) : new ItemStack(Material.WOOL))
                        .name(ChatColor.GOLD + ChatColor.BOLD.toString() + masterUpgrade.getName())
                        .lore((masterUpgrade.isUnlocked() ? ChatColor.GREEN : ChatColor.GRAY) + masterUpgrade.getDescription() + "\n\n" + ChatColor.GRAY + "Cost: " + ChatColor.GOLD + masterUpgrade.getCurrencyCost())
                        .get(), (m, e) -> {
                    master();
                    masterUpgrade.setUnlocked(true);
                    ability.updateDescription((Player) player.getEntity());
                    openUpgradeBranchMenu();
                });

        menu.setItem(3, 5, MENU_BACK, (m, e) -> abilityTree.openAbilityTree());
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        if (player.getEntity() instanceof Player) {
            menu.openForPlayer((Player) player.getEntity());
        }
    }

    public abstract void a1();

    public abstract void a2();

    public abstract void a3();

    public abstract void b1();

    public abstract void b2();

    public abstract void b3();

    public abstract void c1();

    public abstract void c2();

    public abstract void c3();

    public abstract void master();

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getItemName() {
        return itemName;
    }

    public void updateInventory(WarlordsPlayer wp) {
        wp.weaponRightClick();
        wp.updateRedItem();
        wp.updatePurpleItem();
        wp.updateBlueItem();
        wp.updateOrangeItem();
        ability.updateDescription((Player) wp.getEntity());
    }
}
