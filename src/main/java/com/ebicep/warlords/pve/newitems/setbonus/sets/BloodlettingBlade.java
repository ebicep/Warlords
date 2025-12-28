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

public class BloodlettingBlade extends BaseSet {

    private int critMultiplierIncreasePercent;
    private int selfDamageOnCritPercentMaxHealth;

    @Override
    public void init() {
        super.init();
        this.critMultiplierIncreasePercent = getValue("critMultiplierIncreasePercent", int.class);
        this.selfDamageOnCritPercentMaxHealth = getValue("selfDamageOnCritPercentMaxHealth", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "bloodlettingBlade";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(critMultiplierIncreasePercent, selfDamageOnCritPercentMaxHealth);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Bloodletting Blade",
                    null,
                    BloodlettingBlade.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {},
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_CRIT_MULTIPLIER,
                    (event, currentCritMultiplier) -> {
                        if (event.getCause().isEmpty() || event.getCause().equals("Time Warp")) {
                            return;
                        }
                        currentCritMultiplier.addModifier(FloatModifiable.ModifierType.ADDITIVE, getName(), 100);
                    }
            ).addModifier(
                    Modifier.ON_OUTGOING_DAMAGE,
                    (event, currentDamageValue, isCrit) -> {
                        if (isCrit) {
                            warlordsPlayer.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Bloodletting Blade")
                                    .source(warlordsPlayer)
                                    .value(warlordsPlayer.getMaxHealth() * (selfDamageOnCritPercentMaxHealth / 100f))
                                    .flags(InstanceFlags.TRUE_DAMAGE, InstanceFlags.NO_MESSAGE)
                            );
                        }
                    }
            ));
        }

    }

}