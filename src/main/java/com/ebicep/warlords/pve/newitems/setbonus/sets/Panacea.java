package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Panacea extends BaseSet {

    private int healingIncreasePercent;
    private int cleanseCooldownSeconds;

    @Override
    public void init() {
        super.init();
        this.healingIncreasePercent = getValue("healingIncreasePercent", int.class);
        this.cleanseCooldownSeconds = getValue("cleanseCooldownSeconds", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "panacea";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healingIncreasePercent, (float) cleanseCooldownSeconds);
    }

    public class Bonus implements SetBonus.Bonus {

        private int cleanseCooldownTicks;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Panacea.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (cleanseCooldownTicks > 0) {
                            cleanseCooldownTicks--;
                        }
                    }
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_HEALING,
                    (event, currentHealValue) -> {
                        if (event.getFlags().contains(InstanceFlags.RECURSIVE)) {
                            return;
                        }
                        WarlordsEntity healed = event.getWarlordsEntity();
                        if (!healed.isTeammate(warlordsPlayer) || !hasDebuff(healed)) {
                            return;
                        }
                        currentHealValue.addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 + healingIncreasePercent / 100f
                        );
                    }
            ).addModifier(
                    Modifier.ON_OUTGOING_HEALING,
                    (event, currentHealValue, isCrit) -> {
                        if (event.getFlags().contains(InstanceFlags.RECURSIVE) || cleanseCooldownTicks > 0 || currentHealValue <= 0) {
                            return;
                        }
                        WarlordsEntity healed = event.getWarlordsEntity();
                        if (!healed.isTeammate(warlordsPlayer) || !hasDebuff(healed)) {
                            return;
                        }
                        healed.getCooldownManager().removeDebuffCooldowns();
                        cleanseCooldownTicks = cleanseCooldownSeconds * 20;
                    }
            ));
        }

        private boolean hasDebuff(WarlordsEntity entity) {
            return entity.getCooldownManager().getCooldowns().stream().anyMatch(cooldown -> {
                CooldownTypes type = cooldown.getCooldownType();
                return type == CooldownTypes.LOW_LEVEL_DEBUFF
                        || type == CooldownTypes.HIGH_LEVEL_DEBUFF
                        || type == CooldownTypes.TRUE_DEBUFF;
            });
        }
    }

}
