package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom.ItemAdditiveCooldown;
import com.ebicep.warlords.pve.DifficultyMode;
import com.ebicep.warlords.pve.items.ItemLoadout;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.addons.ItemAddonSpecBonus;
import com.ebicep.warlords.pve.items.menu.util.ItemMenuUtil;
import com.ebicep.warlords.pve.items.modifiers.UpgradeTreeBonus;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AbstractSpecialItem;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.pve.mobs.Aspect;
import com.ebicep.warlords.util.bukkit.ComponentUtils;
import com.ebicep.warlords.util.java.Priority;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemOption implements Option {

    @Override
    @Priority(-10)
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (!(player instanceof WarlordsPlayer warlordsPlayer)) {
            return;
        }
        Game game = player.getGame();
        PveOption pveOption = game
                .getOptions()
                .stream()
                .filter(option -> option instanceof PveOption)
                .map(PveOption.class::cast)
                .findFirst().orElse(null);
        if (pveOption == null) {
            return;
        }
        DatabaseManager.getPlayer(player.getUuid(), databasePlayer -> {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            //items
            ItemsManager itemsManager = pveStats.getItemsManager();
            List<ItemLoadout> loadouts = new ArrayList<>(itemsManager.getLoadouts());
            loadouts.removeIf(itemLoadout -> itemLoadout.getItems().isEmpty());
            int nonEmptyLoadouts = loadouts.size();
            loadouts.removeIf(itemLoadout -> {
                DifficultyMode difficultyMode = itemLoadout.getDifficultyMode();
                return difficultyMode != null && !difficultyMode.validGameMode(game.getGameMode()) && !difficultyMode.validDifficulty(pveOption.getDifficulty());
            });
            loadouts.removeIf(itemLoadout -> itemLoadout.getSpec() != null && itemLoadout.getSpec() != player.getSpecClass());

            if (loadouts.isEmpty()) {
                if (nonEmptyLoadouts > 0 && player.getEntity() instanceof Player) {
                    AbstractItem.sendItemMessage((Player) player.getEntity(),
                            Component.text("No item loadout applied. Make sure your loadout is not overweight or unbinded.", NamedTextColor.RED)
                    );
                }
                return;
            }

            ItemLoadout loadout = loadouts.get(0);
            List<AbstractItem> appliedItems = loadout.getActualItems(itemsManager);

            // aspect bonuses
            Map<Aspect, Map<ItemType, Integer>> aspectBonuses = new HashMap<>();
            for (AbstractItem equippedItem : appliedItems) {
                ItemType type = equippedItem.getType();
                Aspect aspectModifier1 = equippedItem.getAspectModifier1();
                Aspect aspectModifier2 = equippedItem.getAspectModifier2();
                if (aspectModifier1 != null) {
                    ItemTier tier = equippedItem.getTier();
                    if (aspectModifier2 != null) {
                        aspectBonuses.computeIfAbsent(aspectModifier1, k -> new HashMap<>())
                                     .merge(type, tier.aspectModifierValues.dualModifier1(), Integer::sum);
                        aspectBonuses.computeIfAbsent(aspectModifier2, k -> new HashMap<>())
                                     .merge(type, tier.aspectModifierValues.dualModifier2(), Integer::sum);
                    } else {
                        aspectBonuses.computeIfAbsent(aspectModifier1, k -> new HashMap<>())
                                     .merge(type, tier.aspectModifierValues.singleModifier(), Integer::sum);
                    }
                }
            }
            aspectBonuses.forEach((aspect, itemTypeBonuses) -> {
                float damageMultiplier = 1 + itemTypeBonuses.getOrDefault(ItemType.GAUNTLET, 0) / 100f;
                int effectNegationTicks = itemTypeBonuses.getOrDefault(ItemType.TOME, 0) * 2; // div 10 to get .1s, mult by 20 to get ticks = times 2
                float damageReductionMultiplier = 1 - itemTypeBonuses.getOrDefault(ItemType.BUCKLER, 0) / 100f;
                ItemAdditiveCooldown.giveCooldown(warlordsPlayer,
                        itemAdditiveCooldown -> itemAdditiveCooldown.addAspectModifier(aspect,
                                new ItemAdditiveCooldown.AspectModifier(damageMultiplier, effectNegationTicks, damageReductionMultiplier)
                        )
                );
            });
            // stat/special bonuses
            loadout.applyToWarlordsPlayer(itemsManager, warlordsPlayer, pveOption);

            // gamma upgrade bonsues
            Map<UpgradeTreeBonus, Integer> upgradeTreeBonuses = new HashMap<>();
            for (AbstractItem appliedItem : appliedItems) {
                if (appliedItem instanceof AbstractSpecialItem specialItem) {
                    UpgradeTreeBonus upgradeTreeBonus = specialItem.getUpgradeTreeBonus();
                    if (upgradeTreeBonus == null) {
                        continue;
                    }
                    if (appliedItem instanceof ItemAddonSpecBonus specBonus && specBonus.getSpec() != warlordsPlayer.getSpecClass()) {
                        continue;
                    }
                    upgradeTreeBonuses.merge(upgradeTreeBonus, 1, Integer::sum);
                }
            }
            //TODO find other way than listener + resetting tree garbage
            List<Listener> listeners = new ArrayList<>();
            upgradeTreeBonuses.forEach((upgradeTreeBonus, integer) -> {
                Listener listener = upgradeTreeBonus.registerEvents(warlordsPlayer, integer);
                if (listener != null) {
                    listeners.add(listener);
                }
            });
            warlordsPlayer.resetAbilityTree();
            upgradeTreeBonuses.forEach((upgradeTreeBonus, integer) -> upgradeTreeBonus.applyToAbilityTree(warlordsPlayer.getAbilityTree(), integer));

            listeners.forEach(HandlerList::unregisterAll);

            if (player.getEntity() instanceof Player) {
                AbstractItem.sendItemMessage((Player) player.getEntity(),
                        Component.text("Applied Item Loadout: ", NamedTextColor.GREEN)
                                 .append(Component.text(loadout.getName(), NamedTextColor.GOLD)
                                                  .hoverEvent(HoverEvent.showText(ComponentUtils.flattenComponentWithNewLine(ItemMenuUtil.getTotalBonusLore(appliedItems)))))
                );
            }
        });
    }

}
