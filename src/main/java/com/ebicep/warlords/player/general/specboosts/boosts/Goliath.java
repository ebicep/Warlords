package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Berserk;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Goliath implements SpecBoostManager.SpecBoost<Goliath> {

    private int healthIncrease;
    private int maxEnergyIncrease;
    private int berserkCooldownIncreaseTicks;
    private int berserkActivationHeal;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
        this.maxEnergyIncrease = getValue("maxEnergyIncrease", int.class);
        this.berserkCooldownIncreaseTicks = getValue("berserkCooldownIncreaseTicks", int.class);
        this.berserkActivationHeal = getValue("berserkActivationHeal", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "goliath";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease, maxEnergyIncrease, berserkCooldownIncreaseTicks, berserkActivationHeal);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Goliath get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost (Base)", healthIncrease);
            warlordsPlayer.getEnergy().addAdditiveModifier("Spec Boost", maxEnergyIncrease);
            warlordsPlayer.getAbilitiesMatching(Berserk.class).forEach(berserk -> {
                berserk.getCooldown().addAdditiveModifier("Spec Boost", berserkCooldownIncreaseTicks / 20f);
            });
        }

        @EventHandler
        public void onWarlordsAbilityActivate(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (event.getAbility() instanceof Berserk) {
                warlordsEntity.addInstance(InstanceBuilder
                        .healing()
                        .cause(getStringName())
                        .source(warlordsEntity)
                        .value(berserkActivationHeal)
                        .flags(InstanceFlags.TRUE_HEALING)
                );
            }
        }

    }

}
