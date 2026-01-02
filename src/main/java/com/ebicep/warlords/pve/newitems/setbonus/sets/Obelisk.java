package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Obelisk extends BaseSet {

    private int bossDamageIncreasePercent;
    private int bossDamageTakenReductionPercent;

    @Override
    public void init() {
        super.init();
        this.bossDamageIncreasePercent = getValue("bossDamageIncreasePercent", int.class);
        this.bossDamageTakenReductionPercent = getValue("bossDamageTakenReductionPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "obelisk";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(bossDamageIncreasePercent, bossDamageTakenReductionPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Obelisk.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                    (event, currentHealValue) -> {
                        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof BossLike) {
                            currentHealValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1 + (bossDamageIncreasePercent / 100f));
                        }
                    }
            ).addModifier(
                    Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE,
                    (event, currentHealValue) -> {
                        if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof BossLike) {
                            currentHealValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getName(), 1 - (bossDamageTakenReductionPercent / 100f));
                        }
                    }
            ));
        }

    }

}