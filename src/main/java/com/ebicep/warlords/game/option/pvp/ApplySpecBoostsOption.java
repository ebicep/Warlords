package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplySpecBoostsOption implements Option {

    private final Map<WarlordsEntity, SpecBoostManager.Boost> playerSpecBoosts = new HashMap<>();

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity wp) {
        Specializations newSpec = wp.getSpecClass();
        if (wp instanceof WarlordsPlayer warlordsPlayer) {
            DatabaseManager.getPlayer(wp.getUuid(), databasePlayer -> {
                List<SpecBoostManager.SpecBoost<?>> specBoosts = SpecBoostManager.getSpecBoosts(newSpec);
                if (specBoosts.isEmpty()) {
                    return;
                }
                SpecBoostManager.SpecBoost<?> specBoost = specBoosts.get(databasePlayer.getSelectedSpecBoost(newSpec));
                        applyBoost(warlordsPlayer, specBoost);
                    }
            );
        }
    }

    private void applyBoost(WarlordsPlayer warlordsPlayer, SpecBoostManager.SpecBoost<?> specBoost) {
        SpecBoostManager.Boost boost = specBoost.create();
        boost.apply(warlordsPlayer);
        warlordsPlayer.getGame().registerEvents(boost);
        playerSpecBoosts.put(warlordsPlayer, boost);
        if (warlordsPlayer.getEntity() instanceof Player player) {
            warlordsPlayer.getAbilities().forEach(abstractAbility -> abstractAbility.updateDescription(player));
        }
    }

    @Override
    public void onSpecChange(@Nonnull WarlordsEntity wp, Specializations oldSpec) {
        Specializations newSpec = wp.getSpecClass();
        if (wp instanceof WarlordsPlayer warlordsPlayer) {
            DatabaseManager.getPlayer(wp.getUuid(), databasePlayer -> {
                        SpecBoostManager.Boost oldBoost = playerSpecBoosts.get(warlordsPlayer);
                        if (oldBoost != null) {
                            HandlerList.unregisterAll(oldBoost);
                            oldBoost.unapply(warlordsPlayer);
                        }
                List<SpecBoostManager.SpecBoost<?>> specBoosts = SpecBoostManager.getSpecBoosts(newSpec);
                if (specBoosts.isEmpty()) {
                    return;
                }
                SpecBoostManager.SpecBoost<?> specBoost = specBoosts.get(databasePlayer.getSelectedSpecBoost(newSpec));
                        applyBoost(warlordsPlayer, specBoost);
                    }
            );
        }
    }

}
