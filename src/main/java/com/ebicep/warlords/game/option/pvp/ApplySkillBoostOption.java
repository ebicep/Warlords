package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.WeaponDisplayMarker;
import com.ebicep.warlords.player.general.AbstractPlayerClass;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplySkillBoostOption implements Option {

    private final Map<WarlordsEntity, SkillBoosts> playerSkillBoosts = new HashMap<>();


    @Override
    public void register(@Nonnull Game game) {
        game.registerGameMarker(WeaponDisplayMarker.class, new WeaponDisplayMarker() {
                    @Override
                    public int weaponDisplayPriority() {
                        return 0;
                    }

            @Nonnull
                    @Override
            public List<Component> leftClickDescription(WarlordsPlayer wp, Player player) {
                        AbstractPlayerClass spec = wp.getSpec();
                        List<Component> description = new ArrayList<>();
                        description.add(Component.text(spec.getClassName() + " (" + spec.getClass().getSimpleName() + "):", NamedTextColor.GREEN));
                        description.addAll(WordWrap.wrap(playerSkillBoosts.getOrDefault(wp, wp.getSpecClass().skillBoosts.getFirst()).getSelectedDescription(), 150));
                        return description;
                    }
                }
        );
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity wp) {
        if (wp instanceof WarlordsPlayer warlordsPlayer) {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(wp.getUuid());
            Specializations specClass = wp.getSpecClass();
            SkillBoosts skillBoost = databasePlayer.getSpec(specClass).getSkillBoost();
            playerSkillBoosts.put(wp, skillBoost == null ? specClass.skillBoosts.getFirst() : skillBoost);
            if (wp.getEntity() instanceof Player player) {
                applySkillBoost(warlordsPlayer, player);
            } else {
                applySkillBoost(warlordsPlayer, null);
            }
        }
    }

    @Override
    public void onSpecChange(@Nonnull WarlordsEntity wp, Specializations oldSpec) {
        if (wp instanceof WarlordsPlayer warlordsPlayer) {
            if (wp.getEntity() instanceof Player player) {
                applySkillBoost(warlordsPlayer, player);
            } else {
                applySkillBoost(warlordsPlayer, null);
            }
        }
    }

    public void applySkillBoost(WarlordsPlayer warlordsPlayer, Player player) {
        SkillBoosts skillBoost = playerSkillBoosts.computeIfAbsent(warlordsPlayer, k -> k.getSpecClass().skillBoosts.getFirst());
        for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
            if (ability.getClass() != skillBoost.ability) {
                continue;
            }
            ability.boostSkill(skillBoost, warlordsPlayer);
            ability.updateDescription(player);
            ability.queueUpdateItem();
        }
    }

    public Map<WarlordsEntity, SkillBoosts> getPlayerSkillBoosts() {
        return playerSkillBoosts;
    }

}
