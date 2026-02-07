package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Swift extends BaseSet {

    private int movementSpeedPercent;
    private int critChanceIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.movementSpeedPercent = getValue("movementSpeedPercent", int.class);
        this.critChanceIncreasePercent = getValue("critChanceIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "swift";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(movementSpeedPercent, critChanceIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Swift.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_CRIT_CHANCE,
                    (event, currentCritChance) -> {
                        // Only apply to weapon attacks (not abilities)
                        if (!(event.getAbility() instanceof WeaponAbilityIcon)) {
                            return;
                        }

                        float currentSpeed = (long) warlordsPlayer.getSpeed().getModifiers().size();
                        float bonusCritChance = (currentSpeed / movementSpeedPercent) * critChanceIncreasePercent;

                        if (bonusCritChance > 0) {
                            currentCritChance.addModifier(FloatModifiable.ModifierType.ADDITIVE ,getName(), bonusCritChance * 100f);
                        }
                    }
            ));
        }
    }
}
