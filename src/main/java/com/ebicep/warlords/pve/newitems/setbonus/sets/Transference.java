package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.Comparator;
import java.util.List;

public class Transference extends BaseSet {

    private int healingRedirectPercent;

    @Override
    public void init() {
        super.init();
        this.healingRedirectPercent = getValue("healingRedirectPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "transference";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healingRedirectPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Transference.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {},
                    false
            ).addModifier(
                    Modifier.MODIFY_INCOMING_HEALING,
                    (event, currentHealingValue) -> {
                        WarlordsEntity lowestHealthAlly = PlayerFilter
                                .entitiesAround(warlordsPlayer, 100, 100, 100)
                                .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                                .stream()
                                .min(Comparator.comparingDouble(ally ->
                                        ally.getCurrentHealth() / ally.getMaxHealth()
                                ))
                                .orElse(null);

                        if (lowestHealthAlly != null) {
                            float redirectedAmount = currentHealingValue.getCalculatedValue() * (healingRedirectPercent / 100f);

                            currentHealingValue.addModifier(
                                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                    getName(),
                                    1 - (healingRedirectPercent / 100f)
                            );

                            lowestHealthAlly.addInstance(InstanceBuilder
                                    .healing()
                                    .cause(getName())
                                    .source(event.getSource())
                                    .value(redirectedAmount)
                                    .flags(InstanceFlags.TRUE_HEALING)
                            );
                        }
                    }
            ));

        }

    }

}