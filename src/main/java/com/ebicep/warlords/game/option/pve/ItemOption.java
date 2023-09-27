package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyMode;
import com.ebicep.warlords.pve.items.ItemLoadout;
import com.ebicep.warlords.pve.items.ItemsManager;
import com.ebicep.warlords.pve.items.menu.util.ItemMenuUtil;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.util.bukkit.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemOption implements Option {

    @Override
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
            List<AbstractItem> applied = loadout.getActualItems(itemsManager);

            loadout.applyToWarlordsPlayer(itemsManager, warlordsPlayer, pveOption);
            if (player.getEntity() instanceof Player) {
                AbstractItem.sendItemMessage((Player) player.getEntity(),
                        Component.text("Applied Item Loadout: ", NamedTextColor.GREEN)
                                 .append(Component.text(loadout.getName(), NamedTextColor.GOLD)
                                                  .hoverEvent(HoverEvent.showText(ComponentUtils.flattenComponentWithNewLine(ItemMenuUtil.getTotalBonusLore(applied)))))
                );
            }
        });
    }

}
