package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.delta.ShieldOfSnatching;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Pillage extends BaseSet {

    private int healthThresholdPercent;
    private int healPercent;

    @Override
    public void init() {
        super.init();
        this.healthThresholdPercent = getValue("healthThresholdPercent", int.class);
        this.healPercent = getValue("healPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "pillage";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthThresholdPercent, healPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Pillage.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {

                    },
                    false
            ).addModifier(Modifier.MODIFY_OUTGOING_HEALING, (event, currentHealValue) -> {
                        long playersBelowThreshold = warlordsPlayer.getGame()
                                .warlordsPlayers()
                                .filter(p -> p.getCurrentHealth() / p.getMaxHealth() < healthThresholdPercent / 100f)
                                .count();
                        currentHealValue.addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 + (playersBelowThreshold * healPercent / 100f)
                        );
            }));
        }
    }
}
