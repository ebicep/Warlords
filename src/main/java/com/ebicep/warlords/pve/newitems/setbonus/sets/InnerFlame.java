package com.ebicep.warlords.pve.newitems.setbonus.sets;

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

import java.util.List;

public class InnerFlame extends BaseSet {

    private int healingDamageConversion;

    @Override
    public void init() {
        super.init();
        this.healingDamageConversion = getValue("healingDamageConversion", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "innerFlame";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healingDamageConversion);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    InnerFlame.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.ON_OUTGOING_HEALING,
                    (event, currentHealValue, isCrit) -> {
                        PlayerFilter.entitiesAround(event.getWarlordsEntity(), 4, 4, 4)
                                .aliveEnemiesOf(warlordsPlayer)
                                .forEach(entity -> {
                                    entity.addInstance(InstanceBuilder
                                            .damage()
                                            .cause(getName())
                                            .source(warlordsPlayer)
                                            .value(currentHealValue * (healingDamageConversion / 100f))
                                            .flags(InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST)
                                    );
                                }
                        );
                    }
            ));
            // Implementation for converting healing done into AoE damage 
            // for nearby enemies based on healingDamageConversion percentage.
        }

    }

}