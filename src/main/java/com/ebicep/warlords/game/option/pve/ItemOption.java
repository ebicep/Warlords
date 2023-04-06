package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsDropRewardEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.ItemLoadout;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.menu.util.ItemMenuUtil;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemOption implements Option {

    private final HashMap<UUID, ItemPlayerConfig> itemPlayerConfigs = new HashMap<>();

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(new Listener() {

            @EventHandler
            public void onMobDrop(WarlordsDropRewardEvent event) {
                if (event.getRewardType() == WarlordsDropRewardEvent.RewardType.WEAPON) {
                    return;
                }
                WarlordsEntity player = event.getPlayer();
                ItemPlayerConfig itemPlayerConfig = itemPlayerConfigs.get(player.getUuid());
                if (itemPlayerConfig == null) {
                    return;
                }
                AtomicDouble dropRate = event.getDropRate();
                dropRate.set(dropRate.get() * itemPlayerConfig.getDropRateModifier());
            }

        });
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (!(player instanceof WarlordsPlayer)) {
            return;
        }
        PveOption pveOption = player
                .getGame()
                .getOptions()
                .stream()
                .filter(option -> option instanceof PveOption)
                .map(PveOption.class::cast)
                .findFirst().orElse(null);
        if (pveOption == null) {
            return;
        }
        WarlordsPlayer warlordsPlayer = (WarlordsPlayer) player;
        DatabaseManager.getPlayer(player.getUuid(), databasePlayer -> {
            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
            //items
            ItemsManager itemsManager = pveStats.getItemsManager();
            List<ItemLoadout> loadouts = new ArrayList<>(itemsManager.getLoadouts());
            int maxWeight = ItemsManager.getMaxWeight(databasePlayer, player.getSpecClass());
            loadouts.removeIf(itemLoadout -> itemLoadout.getDifficulty() != null && itemLoadout.getDifficulty() != pveOption.getDifficulty());
            loadouts.removeIf(itemLoadout -> itemLoadout.getSpec() != null && itemLoadout.getSpec() != player.getSpecClass());
            loadouts.removeIf(itemLoadout -> itemLoadout.getWeight(itemsManager) > maxWeight);
            if (loadouts.isEmpty()) {
                return;
            }
            ItemLoadout loadout = loadouts.get(0);
            List<AbstractItem> applied = loadout.getActualItems(itemsManager);
            itemPlayerConfigs.putIfAbsent(player.getUuid(),
                    new ItemPlayerConfig(loadout, 1 + applied
                            .stream()
                            .filter(item -> item.getType() == ItemType.GAUNTLET)
                            .mapToDouble(AbstractItem::getModifierCalculated)
                            .sum() / 100.0)
            );
            float abilityDurationModifier = (float) (1 + applied
                    .stream()
                    .filter(item -> item.getType() == ItemType.TOME)
                    .mapToDouble(AbstractItem::getModifierCalculated)
                    .sum() / 100f);
            for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
                if (ability instanceof Duration) {
                    ((Duration) ability).multiplyTickDuration(abilityDurationModifier);
                }
            }
            loadout.applyToWarlordsPlayer(itemsManager, warlordsPlayer);
            if (!applied.isEmpty() && player.getEntity() instanceof Player) {
                AbstractItem.sendItemMessage((Player) player.getEntity(),
                        new ComponentBuilder(ChatColor.GREEN + "Applied Item Loadout: ")
                                .appendHoverText(ChatColor.GOLD + loadout.getName(), String.join("\n", ItemMenuUtil.getTotalBonusLore(applied)))
                );
            }
        });
    }

    static class ItemPlayerConfig {

        private final ItemLoadout loadout;
        private final double dropRateModifier;

        public ItemPlayerConfig(ItemLoadout loadout, double dropRateModifier) {
            this.loadout = loadout;
            this.dropRateModifier = dropRateModifier;
        }

        public ItemLoadout getLoadout() {
            return loadout;
        }

        public double getDropRateModifier() {
            return dropRateModifier;
        }
    }
}
