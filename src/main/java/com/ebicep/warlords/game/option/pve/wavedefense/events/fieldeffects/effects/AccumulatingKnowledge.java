package com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.effects;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffectOption;
import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AccumulatingKnowledge implements FieldEffect {
    @Override
    public String getName() {
        return "Accumulating Knowledge";
    }

    @Override
    public String getDescription() {
        return "For every 10,000 DHP a player accumulates, they will permanently earn the following:";
    }

    @Override
    public List<Component> getSubDescription() {
        return new ArrayList<>() {{
            add(Component.empty());
            add(Component.text("+1% Damage ", NamedTextColor.DARK_RED).append(Component.text("(Max 25%)")));
            add(Component.text("+1% Damage Reduction ", NamedTextColor.GOLD).append(Component.text("(Max 15%)")));
            add(Component.text("+1% Max HP ", NamedTextColor.RED).append(Component.text("(Max 25%)")));
        }};
    }

    @Override
    public void onWarlordsEntityCreated(WarlordsEntity player) {
        AtomicInteger multiplier = new AtomicInteger(1);
        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Accumulating Knowledge",
                null,
                FieldEffectOption.class,
                null,
                player,
                CooldownTypes.FIELD_EFFECT,
                cooldownManager -> {
                },
                false,
                (cooldown, ticksElapsed) -> {
                    if (ticksElapsed % 20 != 0) {
                        return;
                    }
                    PlayerStatisticsMinute.Entry total = player.getMinuteStats().total();
                    int oldMultiplier = multiplier.get();
                    int newMultiplier = (int) ((total.getDamage() + total.getHealing() + total.getAbsorbed()) / 10_000);
                    if (oldMultiplier == newMultiplier) {
                        return;
                    }
                    multiplier.set(newMultiplier);
                    player.getHealth().addMultiplicativeModifierAdd(getName() + " (Base)", Math.min(multiplier.get(), 25) / 100f);
                }
        ) {

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                int buff = Math.min(multiplier.get(), 25);
                return currentDamageValue * (1 + buff / 100f);
            }

            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                int buff = Math.min(multiplier.get(), 15);
                return currentDamageValue * (1 - buff / 100f);
            }
        });
    }
}
