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

public class BerserkersFury implements SpecBoostManager.SpecBoost<BerserkersFury> {

    private int healthIncrease;
    private float berserkCooldownIncreasePercent;
    private int berserkActivationHeal;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
        this.berserkCooldownIncreasePercent = getValue("berserkCooldownIncreasePercent", float.class);
        this.berserkActivationHeal = getValue("berserkActivationHeal", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "berserkersFury";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease, berserkCooldownIncreasePercent, berserkActivationHeal);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public BerserkersFury get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost", healthIncrease);
            warlordsPlayer.getAbilitiesMatching(Berserk.class).forEach(berserk -> {
                berserk.getCooldown().addMultiplicativeModifierAdd("Spec Boost", berserkCooldownIncreasePercent / 100);
            });
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().removeModifier("Spec Boost");
            warlordsPlayer.getAbilitiesMatching(Berserk.class).forEach(berserk -> {
                berserk.getCooldown().removeModifier("Spec Boost");
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
