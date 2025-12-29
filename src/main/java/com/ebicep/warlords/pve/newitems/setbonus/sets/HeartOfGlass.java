package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class HeartOfGlass extends BaseSet {

    private int defenseStatsPenalty;
    private int damageIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.defenseStatsPenalty = getValue("defenseStatsPenalty", int.class);
        this.damageIncreasePercent = getValue("damageIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "heartOfGlass";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(defenseStatsPenalty, damageIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1 - defenseStatsPenalty / 100f);
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Heart of Glass",
                    null,
                    HeartOfGlass.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {},
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                    (event, currentDamageValue) -> {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1 + (damageIncreasePercent / 100f));
                    }
            ).addModifier(
                    Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE,
                    (event, currentDamageValue) -> {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1 + (defenseStatsPenalty / 100f));
                    }
            ));
        }

    }

}