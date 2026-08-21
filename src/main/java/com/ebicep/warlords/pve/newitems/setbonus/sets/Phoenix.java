package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.omega.CrescentBulwark;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;
import java.util.Optional;

public class Phoenix extends BaseSet {

    private float damagePerMobPercent;

    @Override
    public void init() {
        super.init();
        this.damagePerMobPercent = getValue("damagePerMobPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "phoenix";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(damagePerMobPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            PveOption pveOption = warlordsPlayer.getGame().getOption(PveOption.class)
                    .stream()
                    .findFirst()
                    .get();
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    CrescentBulwark.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                    (event, currentDamageValue) -> {
                        int mobCount = pveOption.mobCount();
                        currentDamageValue.addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 + (mobCount * damagePerMobPercent / 100f)
                        );
                    }
            ));
        }

    }

}