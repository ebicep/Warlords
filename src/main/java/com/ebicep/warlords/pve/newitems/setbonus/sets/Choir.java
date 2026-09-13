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

import java.util.List;

public class Choir extends BaseSet {

    private int radius;
    private int splashHealingPercent;

    @Override
    public void init() {
        super.init();
        this.radius = getValue("radius", int.class);
        this.splashHealingPercent = getValue("splashHealingPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "choir";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(radius, splashHealingPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Choir.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.ON_OUTGOING_HEALING,
                    (event, currentHealValue, isCrit) -> {
                        if (event.getFlags().contains(InstanceFlags.RECURSIVE) || currentHealValue <= 0) {
                            return;
                        }
                        WarlordsEntity healed = event.getWarlordsEntity();
                        if (healed.equals(warlordsPlayer) || !healed.isTeammate(warlordsPlayer)) {
                            return;
                        }
                        float splashHeal = currentHealValue * (splashHealingPercent / 100f);
                        if (splashHeal <= 0) {
                            return;
                        }
                        PlayerFilter.entitiesAround(healed, radius, radius, radius)
                                .aliveTeammatesOf(warlordsPlayer)
                                .excluding(healed)
                                .forEach(ally -> ally.addInstance(InstanceBuilder
                                        .healing()
                                        .cause(getName())
                                        .source(warlordsPlayer)
                                        .value(splashHeal)
                                        .flags(
                                                InstanceFlags.RECURSIVE,
                                                InstanceFlags.TRUE_HEALING,
                                                InstanceFlags.NO_HEALING_ORBS,
                                                InstanceFlags.NO_HEALING_LEECH
                                        )
                                ));
                    }
            ));
        }
    }

}
