package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class OceanLight extends BaseSet {

    private float healingPerMobPercent;

    @Override
    public void init() {
        super.init();
        this.healingPerMobPercent = getValue("healingPerMobPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "oceanLight";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healingPerMobPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            Optional<PveOption> pveOption = warlordsPlayer.getGame().getOption(PveOption.class)
                    .stream()
                    .findFirst();
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    OceanLight.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_HEALING,
                    (event, currentHealValue) -> {
                        AtomicInteger mobCount = new AtomicInteger();
                        pveOption.ifPresent(option -> {
                            mobCount.set(option.mobCount());
                        });
                        currentHealValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1 + (mobCount.get() * healingPerMobPercent / 100f));
                    }
            ));
        }

    }

}