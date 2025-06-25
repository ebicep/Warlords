package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.function.BiConsumer;

public class OneManArmy implements SpecBoostManager.SpecBoost<OneManArmy> {

    private int undyingArmyTickDurationIncrease;
    private int undyingArmyCooldownIncreaseTicks;

    @Override
    public void init() {
        this.undyingArmyTickDurationIncrease = getValue("undyingArmyTickDurationIncrease", int.class);
        this.undyingArmyCooldownIncreaseTicks = getValue("undyingArmyCooldownIncreaseTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "oneManArmy";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(undyingArmyTickDurationIncrease, undyingArmyCooldownIncreaseTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public OneManArmy get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(UndyingArmy.class).forEach(undyingArmy -> {
                undyingArmy.setTickDuration(undyingArmy.getTickDuration() + undyingArmyTickDurationIncrease);
                undyingArmy.getCooldown().addAdditiveModifier("Spec Boost", undyingArmyCooldownIncreaseTicks / 20f);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                return;
            }
            if (!(cooldown.getCooldownObject() instanceof UndyingArmy.UndyingArmyData data) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            BiConsumer<RegularCooldown<UndyingArmy.UndyingArmyData>, WarlordsEntity> oldOnPop = data.getOnPop();
            data.setOnPop((cd, we) -> {
                if (!we.equals(warlordsEntity)) {
                    oldOnPop.accept(cd, we);
                } else {
                    cd.setTicksLeft(1);
                }
            });
            regularCooldown.getConsumers().clear();
        }

    }

}
