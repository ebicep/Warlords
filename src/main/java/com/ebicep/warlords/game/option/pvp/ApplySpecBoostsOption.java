package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.WeaponDisplayMarker;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplySpecBoostsOption implements Option {

    public static String getPlayerSpecBoost(WarlordsPlayer warlordsPlayer) {
        for (ApplySpecBoostsOption applySpecBoostsOption : warlordsPlayer.getGame().getOption(ApplySpecBoostsOption.class)) {
            PlayerSpecAppliedBoost appliedBoost = applySpecBoostsOption.getPlayerSpecBoosts().get(warlordsPlayer);
            if (appliedBoost != null) {
                return appliedBoost.specBoost.getDatabaseName();
            }
        }
        return "";
    }

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
                if (specBoost.isDisabled()) {
                    // find spec boost that is not disabled
                    specBoost = specBoosts.stream()
                                          .filter(boost -> !boost.isDisabled())
                                          .findFirst()
                                          .orElse(null);
                    if (specBoost == null) {
                        return;
                    }
                }
                        applyBoost(warlordsPlayer, specBoost);
                    }
            );
        }
    }

    private final Map<WarlordsEntity, PlayerSpecAppliedBoost> playerSpecBoosts = new HashMap<>();

    @Override
    public void register(@Nonnull Game game) {
        game.registerGameMarker(WeaponDisplayMarker.class, new WeaponDisplayMarker() {
                    @Override
                    public int weaponDisplayPriority() {
                        return 1;
                    }

                    @Override
                    public @Nullable List<Component> leftClickDescription(WarlordsPlayer wp, Player player) {
                        List<Component> description = new ArrayList<>();
                        PlayerSpecAppliedBoost appliedBoost = playerSpecBoosts.get(wp);
                        if (appliedBoost != null) {
                            SpecBoostManager.SpecBoost<?> boost = appliedBoost.specBoost;
                            description.add(Component.text("Active Boost: ", NamedTextColor.GREEN).append(Component.text(boost.getStringName(), NamedTextColor.AQUA)));
                            description.addAll(boost.getDescriptionLore());
                            return description;
                        }
                        List<SpecBoostManager.SpecBoost<?>> specBoosts = SpecBoostManager.getSpecBoosts(wp.getSpecClass());
                        if (specBoosts.isEmpty()) {
                            return null;
                        }
                        SpecBoostManager.SpecBoost<?> boost = specBoosts.getFirst();
                        description.add(boost.getName());
                        description.addAll(boost.getDescriptionLore());
                        return description;
                    }
                }
        );
    }

    @Override
    public void onSpecChange(@Nonnull WarlordsEntity wp, Specializations oldSpec) {
        Specializations newSpec = wp.getSpecClass();
        if (wp instanceof WarlordsPlayer warlordsPlayer) {
            DatabaseManager.getPlayer(wp.getUuid(), databasePlayer -> {
                PlayerSpecAppliedBoost oldAppliedBoost = playerSpecBoosts.get(warlordsPlayer);
                if (oldAppliedBoost != null) {
                    HandlerList.unregisterAll(oldAppliedBoost.boost);
                    oldAppliedBoost.boost.unapply(warlordsPlayer);
                        }
                List<SpecBoostManager.SpecBoost<?>> specBoosts = SpecBoostManager.getSpecBoosts(newSpec);
                if (specBoosts.isEmpty()) {
                    return;
                }
                SpecBoostManager.SpecBoost<?> specBoost = specBoosts.get(databasePlayer.getSelectedSpecBoost(newSpec));
                if (specBoost.isDisabled()) {
                    specBoost = specBoosts.stream()
                                          .filter(boost -> !boost.isDisabled())
                                          .findFirst()
                                          .orElse(null);
                    if (specBoost == null) {
                        return;
                    }
                }
                        applyBoost(warlordsPlayer, specBoost);
                    }
            );
        }
    }

    public Map<WarlordsEntity, PlayerSpecAppliedBoost> getPlayerSpecBoosts() {
        return playerSpecBoosts;
    }

    private void applyBoost(WarlordsPlayer warlordsPlayer, SpecBoostManager.SpecBoost<?> specBoost) {
        SpecBoostManager.Boost boost = specBoost.create();
        boost.apply(warlordsPlayer);
        warlordsPlayer.getGame().registerEvents(boost);
        playerSpecBoosts.put(warlordsPlayer, new PlayerSpecAppliedBoost(specBoost, boost));
        if (warlordsPlayer.getEntity() instanceof Player player) {
            warlordsPlayer.getAbilities().forEach(abstractAbility -> abstractAbility.updateDescription(player));
        }
    }

    public record PlayerSpecAppliedBoost(SpecBoostManager.SpecBoost<?> specBoost, SpecBoostManager.Boost boost) {
    }

}
