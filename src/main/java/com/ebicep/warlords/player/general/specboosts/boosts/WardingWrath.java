package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.AvengersWrath;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.events.player.ingame.WarlordsStrikeEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.event.EventHandler;

import java.util.List;

public class WardingWrath implements SpecBoostManager.SpecBoost<WardingWrath> {

    private int shieldPerStrike;
    private int wrathShieldPerStrike;
    private float avengerWrathRadiusIncrease;

    @Override
    public void init() {
        this.shieldPerStrike = getValue("shieldPerStrike", int.class);
        this.wrathShieldPerStrike = getValue("wrathShieldPerStrike", int.class);
        this.avengerWrathRadiusIncrease = getValue("avengerWrathRadiusIncrease", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "wardingWrath";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(shieldPerStrike, avengerWrathRadiusIncrease, wrathShieldPerStrike);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public WardingWrath get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(AvengersWrath.class).forEach(avengerWrath -> {
                avengerWrath.setHitRadius(avengerWrath.getHitRadius() + avengerWrathRadiusIncrease);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onWarlordsStrikeEvent(WarlordsStrikeEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            boolean wrathActive = warlordsEntity.getCooldownManager().hasCooldown(AvengersWrath.AvengersWrathData.class);
            giveShield(wrathActive ? wrathShieldPerStrike : shieldPerStrike);
        }

        private void giveShield(int shieldAmount) {
            CooldownManager cooldownManager = warlordsEntity.getCooldownManager();
            new CooldownFilter<>(cooldownManager, RegularCooldown.class)
                    .filterCooldownName(getStringName())
                    .filterCooldownFrom(warlordsEntity)
                    .filter(cooldown -> cooldown.getCooldownObject() instanceof Shield shield && shield.getName().equals(getStringName()))
                    .findFirst()
                    .ifPresentOrElse(cooldown -> {
                                Shield shield = (Shield) cooldown.getCooldownObject();
                                shield.setMaxShieldHealth(shield.getMaxShieldHealth() + shieldAmount);
                                shield.setShieldHealth(shield.getShieldHealth() + shieldAmount);
                                Shield.updateAbsorption(warlordsEntity);
                            }, () -> cooldownManager.addCooldown(new RegularCooldown<>(
                                    getStringName(),
                                    null,
                                    Shield.class,
                                    new Shield(getStringName(), shieldAmount),
                                    warlordsEntity,
                                    CooldownTypes.SPEC_BOOST,
                                    cm -> {},
                                    Integer.MAX_VALUE
                            ))
                    );
        }

    }

}
