package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.ingame.pve.drops.AbstractWarlordsDropRewardEvent;
import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropMobDropEvent;
import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropNewItemEvent;
import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropWeaponEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Spelunker extends BaseSet {

    private int rareLootChanceIncreasePercent;
    private int spelunkerChestDropChancePercent;

    @Override
    public void init() {
        super.init();
        this.rareLootChanceIncreasePercent = getValue("rareLootChanceIncreasePercent", int.class);
        this.spelunkerChestDropChancePercent = getValue("spelunkerChestDropChancePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "spelunker";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(rareLootChanceIncreasePercent, spelunkerChestDropChancePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getGame().registerEvents(new Listener() {
                @EventHandler
                public void onWeaponDrop(WarlordsDropWeaponEvent event) {
                    if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                        return;
                    }
                    increaseRareLootChance(event);
                    if (ThreadLocalRandom.current().nextDouble(100) < spelunkerChestDropChancePercent) {
                        giveSpelunkerChest(warlordsPlayer);
                    }
                }

                @EventHandler
                public void onItemDrop(WarlordsDropNewItemEvent event) {
                    if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                        return;
                    }
                    increaseRareLootChance(event);
                }

                @EventHandler
                public void onMobDrop(WarlordsDropMobDropEvent event) {
                    if (!event.getWarlordsEntity().equals(warlordsPlayer) || event.getMobDrop() != MobDrop.ZENITH_STAR) {
                        return;
                    }
                    increaseRareLootChance(event);
                }
            });
        }

        private void increaseRareLootChance(AbstractWarlordsDropRewardEvent event) {
            event.setModifier(event.getModifier() * (1 + rareLootChanceIncreasePercent / 100d));
        }

        private void giveSpelunkerChest(WarlordsPlayer warlordsPlayer) {
            double roll = ThreadLocalRandom.current().nextDouble(100);
            Currencies currency;
            long amount;
            if (roll < 50) {
                currency = Currencies.SYNTHETIC_SHARD;
                amount = 20;
            } else if (roll < 75) {
                currency = Currencies.LEGEND_FRAGMENTS;
                amount = 15;
            } else if (roll < 90) {
                currency = Currencies.SUPPLY_DROP_TOKEN;
                amount = 2;
            } else if (roll < 95) {
                currency = Currencies.ETHEREUM_CRYSTAL;
                amount = 1;
            } else {
                currency = Currencies.ASCENDANT_SHARD;
                amount = 1;
            }

            DatabasePlayer databasePlayer = warlordsPlayer.getDatabasePlayer();
            currency.addToPlayer(databasePlayer, amount);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

            NewItemsUtils.sendItemMessage(
                    warlordsPlayer,
                    Component.text("You found a Spelunker Chest containing ", NamedTextColor.GOLD)
                             .append(Component.text(amount + " "))
                             .append(currency.getColoredName())
                             .append(Component.text(amount == 1 ? "!" : "s!"))
            );
        }

    }

}
