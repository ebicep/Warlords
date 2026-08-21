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

import java.util.List;

public class Soulflame extends BaseSet {

    private boolean healAlliesOnDamage;
    private boolean selfHealthDegeneration;

    @Override
    public void init() {
        super.init();
        this.healAlliesOnDamage = getValue("healAlliesOnDamage", boolean.class);
        this.selfHealthDegeneration = getValue("selfHealthDegeneration", boolean.class);
    }

    @Override
    public String getConfigFieldName() {
        return "soulflame";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        // No numeric placeholders in current description, 
        // but variables are available for future logic.
        return List.of();
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Soulflame.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {

                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (ticksElapsed % 20 == 0) {
                            warlordsPlayer.addInstance(InstanceBuilder.damage()
                                    .value(warlordsPlayer.getMaxHealth() * 0.05f)
                                    .cause("Soulflame")
                                    .source(warlordsPlayer)
                                    .flags(InstanceFlags.NO_MESSAGE, InstanceFlags.NO_HIT_SOUND)
                            );
                        }
                    }
            ).addModifier(Modifier.ON_OUTGOING_DAMAGE, (event, currentDamageValue, isCrit) -> {
                if (!healAlliesOnDamage) {
                    return;
                }
                if (event.getCause().isEmpty()) {
                    return;
                }
                if (event.getFlags().contains(InstanceFlags.DOT)) {
                    return;
                }
                PlayerFilter.entitiesAround(warlordsPlayer, 8, 8, 8)
                        .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                        .forEach(ally -> {
                            ally.addInstance(InstanceBuilder
                                    .healing()
                                    .value(currentDamageValue * 0.01f)
                                    .cause("Soulflame")
                                    .source(warlordsPlayer)
                            );
                        }
                );
            }));
        }

    }

}