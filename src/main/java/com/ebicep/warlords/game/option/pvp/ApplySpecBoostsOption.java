package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.specboosts.SpecBoost;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import javax.annotation.Nonnull;

public class ApplySpecBoostsOption implements Option {

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity wp) {
        Specializations newSpec = wp.getSpecClass();
        if (wp instanceof WarlordsPlayer warlordsPlayer) {
            DatabaseManager.getPlayer(wp.getUuid(), databasePlayer -> {
                SpecBoost specBoost = SpecBoost.getSpecBoosts(newSpec).get(databasePlayer.getSelectedSpecBoost(newSpec));
                specBoost.create().apply(warlordsPlayer);
            });
        }
    }

    @Override
    public void onSpecChange(@Nonnull WarlordsEntity wp, Specializations oldSpec) {
        Specializations newSpec = wp.getSpecClass();
        if (wp instanceof WarlordsPlayer warlordsPlayer) {
            DatabaseManager.getPlayer(wp.getUuid(), databasePlayer -> {
                SpecBoost oldSpecBoost = SpecBoost.getSpecBoosts(oldSpec).get(databasePlayer.getSelectedSpecBoost(oldSpec));
                oldSpecBoost.create().unapply(warlordsPlayer);
                SpecBoost specBoost = SpecBoost.getSpecBoosts(newSpec).get(databasePlayer.getSelectedSpecBoost(newSpec));
                specBoost.create().apply(warlordsPlayer);
            });
        }
    }

}
