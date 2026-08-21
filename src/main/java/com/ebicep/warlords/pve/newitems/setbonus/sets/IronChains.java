package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifier;
import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.MotionAddon;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class IronChains extends BaseSet {

    private int damageReductionIncreasePercent;
    private int movementSpeedPenaltyPercent;

    @Override
    public void init() {
        super.init();
        this.damageReductionIncreasePercent = getValue("damageReductionIncreasePercent", int.class);
        this.movementSpeedPenaltyPercent = getValue("movementSpeedPenaltyPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "ironChains";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damageReductionIncreasePercent, movementSpeedPenaltyPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getSpeed().addBaseModifier(-movementSpeedPenaltyPercent);
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    IronChains.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE,
                    (event, currentDamageValue) -> {
                        currentDamageValue.addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 - (damageReductionIncreasePercent / 100f)
                        );
                    }
            ));
        }

    }

}