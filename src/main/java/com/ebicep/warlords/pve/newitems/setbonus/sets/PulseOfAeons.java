package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;

public class PulseOfAeons extends BaseSet {

    private int healsPerPulse;
    private int pulseHealAmount;

    @Override
    public void init() {
        super.init();
        this.healsPerPulse = getValue("healsPerPulse", int.class);
        this.pulseHealAmount = getValue("pulseHealAmount", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "pulseOfAeons";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healsPerPulse, pulseHealAmount);
    }

    public class Bonus implements SetBonus.Bonus {

        int stacks = 0;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    PulseOfAeons.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {},
                    false,
                    (cooldown, ticksElapsed) -> {

                    }
            ).addModifier(Modifier.ON_OUTGOING_HEALING, (event, currentHealValue, isCrit) -> {
                if (event.getFlags().contains(InstanceFlags.DOT)) {
                    return;
                }

                stacks++;

                if (stacks == healsPerPulse) {
                    stacks = 0;
                    Utils.playGlobalSound(warlordsPlayer.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 2, 2f);
                    PlayerFilter.playingGame(warlordsPlayer.getGame())
                            .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                            .forEach(entity -> {
                                entity.addInstance(InstanceBuilder
                                        .healing()
                                        .cause(getName())
                                        .source(warlordsPlayer)
                                        .value(pulseHealAmount)
                                        .flags(InstanceFlags.TRUE_HEALING)
                                );
                            }
                    );
                }
            }));
        }

    }

}