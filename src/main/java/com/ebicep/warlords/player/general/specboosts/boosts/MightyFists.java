package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;

import java.util.List;

public class MightyFists implements SpecBoostManager.SpecBoost<MightyFists> {

    private float meleeIncreasePercent;
    private float baseMeleePercent;
    private float consecutiveHitIncreasePercent;
    private float maxIncreasePercent;

    @Override
    public void init() {
        this.baseMeleePercent = getValue("baseMeleePercent", float.class);
        this.consecutiveHitIncreasePercent = getValue("consecutiveHitIncreasePercent", float.class);
        this.maxIncreasePercent = getValue("maxIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "mightyFists";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(baseMeleePercent,
                consecutiveHitIncreasePercent,
                maxIncreasePercent
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public MightyFists get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            meleeIncreasePercent = baseMeleePercent;
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getStringName(),
                    null,
                    MightyFists.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {

                    },
                    false
            ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                        if (event.getCause().isEmpty()) {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, getConfigFieldName(), meleeIncreasePercent / 100);
                        }
                    }
            ));
        }
        @EventHandler
        public void onWarlordsAbilityActivateEventPost(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (!(event.getAbility() instanceof WoundingStrikeBerserker ||
                    event.getAbility() instanceof SeismicWaveBerserker ||
                    event.getAbility() instanceof GroundSlamBerserker ||
                    event.getAbility() instanceof BloodLust ||
                    event.getAbility() instanceof Berserk)) {
                return;
            }
            meleeIncreasePercent = baseMeleePercent;
        }

        @EventHandler
        public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!event.getInstanceFlags().contains(InstanceFlags.NO_HIT_SOUND)){
                return;
            }
            meleeIncreasePercent = Math.min(meleeIncreasePercent + consecutiveHitIncreasePercent, maxIncreasePercent);
        }
    }

}

