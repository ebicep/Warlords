package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Oathkeeper extends BaseSet {

    private int meleeDamageIncreasePercent;
    private int meleeAttackTwiceChancePercent;

    @Override
    public void init() {
        super.init();
        this.meleeDamageIncreasePercent = getValue("meleeDamageIncreasePercent", int.class);
        this.meleeAttackTwiceChancePercent = getValue("meleeAttackTwiceChancePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "oathkeeper";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(meleeDamageIncreasePercent, meleeAttackTwiceChancePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Oathkeeper.class,
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
                        currentDamageValue.addModifier(
                                FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getName(),
                                1 + (meleeDamageIncreasePercent / 100f)
                        );
                        new GameRunnable(warlordsPlayer.getGame()) {
                            @Override
                            public void run() {
                                event.getWarlordsEntity().addInstance(InstanceBuilder
                                        .damage()
                                        .cause(event.getCause())
                                        .source(event.getSource())
                                        .min(event.getMin().getCalculatedValue())
                                        .max(event.getMax().getCalculatedValue())
                                        .critChance(event.getCritChance().getCalculatedValue())
                                        .critMultiplier(event.getCritMultiplier().getCalculatedValue())
                                );
                            }
                        }.runTaskLater(3);
                    }
            ));
        }

    }

}
