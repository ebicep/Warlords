package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Bellicose extends BaseSet {

    private int maxHealthRequirement;
    private int meleeDamageIncrease;

    @Override
    public void init() {
        super.init();
        this.maxHealthRequirement = getValue("maxHealthRequirement", int.class);
        this.meleeDamageIncrease = getValue("meleeDamageIncrease", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "bellicose";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(maxHealthRequirement, meleeDamageIncrease);
    }

    public class Bonus implements SetBonus.Bonus {
        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Bellicose.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                    (event, currentDamageValue) -> {
                        if (!event.getCause().isEmpty()) {
                            return;
                        }

                        int stacks = (int) (warlordsPlayer.getMaxHealth() / maxHealthRequirement);
                        if (stacks <= 0) {
                            return;
                        }

                        float perStackMultiplier = meleeDamageIncrease / 100f;
                        currentDamageValue.addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 + (stacks * perStackMultiplier)
                        );
                    }
            ));

        }

    }

}
