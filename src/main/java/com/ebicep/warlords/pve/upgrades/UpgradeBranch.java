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
    protected List<Upgrade> upgrades = new ArrayList<>();
    protected ItemStack itemStack;

    public UpgradeBranch(AbilityTree abilityTree, T ability, ItemStack itemStack) {
        this.abilityTree = abilityTree;
        this.ability = ability;
        this.itemStack = itemStack;
    }

    public void openUpgradeBranchMenu() {
        WarlordsPlayer player = abilityTree.getPlayer();
        Menu menu = new Menu("Upgrades", 9 * 4);

        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade upgrade = upgrades.get(i);
            int finalI = i;
            menu.setItem(
                    i + 2,
                    1,
                    new ItemBuilder(upgrade.isUnlocked() ? new ItemStack(Material.WOOL, 1, (short) 5) : new ItemStack(Material.WOOL))
                            .name(ChatColor.GOLD + upgrade.getName())
                            .lore((upgrade.isUnlocked() ? ChatColor.GREEN : ChatColor.GRAY) + upgrade.getDescription())
                            .get(),
                    (n, e) -> {
                        if (upgrade.isUnlocked()) {
                            player.sendMessage(ChatColor.RED + "You already unlocked this upgrade.");
                            return;
                        }
                        if (finalI != 0) {
                            if (!upgrades.get(finalI - 1).isUnlocked()) {
                                player.sendMessage(ChatColor.RED + "You need to unlock the previous upgrade first!");
                                return;
                            }
                        }
                        switch (finalI) {
                            case 0:
                                tierOneUpgrade();
                                player.weaponRightClick();
                                ability.updateDescription((Player) player.getEntity());
                                break;
                            case 1:
                                tierTwoUpgrade();
                                player.updateRedItem();
                                ability.updateDescription((Player) player.getEntity());
                                break;
                            case 2:
                                tierThreeUpgrade();
                                player.updatePurpleItem();
                                ability.updateDescription((Player) player.getEntity());
                                break;
                            case 3:
                                tierFourUpgrade();
                                player.updateBlueItem();
                                ability.updateDescription((Player) player.getEntity());
                                break;
                            case 4:
                                tierFiveUpgrade();
                                player.updateOrangeItem();
                                ability.updateDescription((Player) player.getEntity());
                                break;
                        }
                        upgrade.setUnlocked(true);
                        ability.updateDescription((Player) player.getEntity());
                        openUpgradeBranchMenu();
                    }
            );
        }

        menu.setItem(3, 3, MENU_BACK, (m, e) -> abilityTree.openAbilityTree());
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        if (player.getEntity() instanceof Player) {
            menu.openForPlayer((Player) player.getEntity());
        }
    }

    public abstract void tierOneUpgrade();

    public abstract void tierTwoUpgrade();

    public abstract void tierThreeUpgrade();

    public abstract void tierFourUpgrade();

    public abstract void tierFiveUpgrade();

    public ItemStack getItemStack() {
        return itemStack;
    }
}
