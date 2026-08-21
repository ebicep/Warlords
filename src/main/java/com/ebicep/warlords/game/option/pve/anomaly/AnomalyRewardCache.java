package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.database.repositories.events.pojos.GameEventReward;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.NewItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AnomalyRewardCache extends GameEventReward {

    private NewItem newItem;

    public AnomalyRewardCache() {
    }

    public AnomalyRewardCache(
            LinkedHashMap<Spendable, Long> rewards,
            String from,
            long rotation,
            @Nullable NewItem newItem
    ) {
        super(rewards, from, rotation);
        this.newItem = newItem;
    }

    @Override
    public void giveToPlayer(DatabasePlayer databasePlayer) {
        super.giveToPlayer(databasePlayer);
        if (newItem != null) {
            databasePlayer.getPveStats().getNewItemsManager().addItem(new NewItem(newItem));
        }
    }

    @Override
    public List<Component> getLore() {
        List<Component> lore = new ArrayList<>(super.getLore());
        lore.add(Component.empty());
        if (newItem == null) {
            lore.add(Component.text("Item: None", NamedTextColor.GRAY));
        } else {
            lore.add(Component.text("Item: ", NamedTextColor.GRAY).append(newItem.getHoverComponent()));
        }
        return lore;
    }

    @Override
    public TextColor getNameColor() {
        return NamedTextColor.AQUA;
    }

    @Nullable
    public NewItem getNewItem() {
        return newItem;
    }
}
