package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyMode;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.newitems.NewItemLoadout;
import com.ebicep.warlords.pve.newitems.NewItemsManager;
import com.ebicep.warlords.util.java.Priority;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class NewItemOption implements Option {

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
                .filter(PveOption.class::isInstance)
                .map(PveOption.class::cast)
                .findFirst().orElse(null);
        if (pveOption == null) {
            return;
        }
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player.getUuid());
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        NewItemsManager itemsManager = pveStats.getNewItemsManager();
        List<NewItemLoadout> loadouts = new ArrayList<>(itemsManager.getLoadouts());
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

        NewItemLoadout loadout = loadouts.get(0);
        List<NewItem> appliedItems = loadout.getActualItems(itemsManager);

        loadout.apply(itemsManager, warlordsPlayer);
    }

}
