package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta.GardeningGloves;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Diabolical extends BaseSet {

    private int bonusDamageChancePercent;
    private int bonusDamagePercent;

    @Override
    public void init() {
        super.init();
        this.bonusDamageChancePercent = getValue("bonusDamageChancePercent", int.class);
        this.bonusDamagePercent = getValue("bonusDamagePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "diabolical";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(bonusDamageChancePercent, bonusDamagePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Diabolical.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {

                    },
                    false
            ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                        if (!event.getWarlordsEntity().equals(warlordsPlayer) && ThreadLocalRandom.current().nextDouble() <= bonusDamageChancePercent / 100.0) {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1 + bonusDamagePercent / 100f);
                        }
                    }
            ));
        }
    }
}
