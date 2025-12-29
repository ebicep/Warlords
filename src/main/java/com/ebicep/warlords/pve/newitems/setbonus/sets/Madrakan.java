package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Madrakan extends BaseSet {

    private int damageIncreasePercent;
    private boolean disableCriticalHits;

    @Override
    public void init() {
        super.init();
        this.damageIncreasePercent = getValue("damageIncreasePercent", int.class);
        this.disableCriticalHits = getValue("disableCriticalHits", boolean.class);
    }

    @Override
    public String getConfigFieldName() {
        return "madrakan";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damageIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Madrakan",
                    null,
                    Madrakan.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {},
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_CRIT_CHANCE,
                    (event, currentCritChance) -> {
                        if (disableCriticalHits) {
                            currentCritChance.addModifier(FloatModifiable.ModifierType.OVERRIDING, getName(), 0);
                        }
                    }
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                    (event, currentDamageValue) -> {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1 + (damageIncreasePercent / 100f));
                    }
            ));
            // Implementation for:
            // 1. Increasing the player's base damage by damageIncreasePercent.
            // 2. Setting the player's Crit Chance or Crit Multiplier to 0
            //    (or intercepting damage events to cancel crits) if disableCriticalHits is true.
        }

    }

}