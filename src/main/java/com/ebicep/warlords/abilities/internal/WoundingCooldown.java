package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.PlayerNameInstance;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WoundingCooldown extends RegularCooldown<WoundingCooldown.WoundingData> {

    public static void addWoundingCooldown(
            WarlordsEntity target,
            String name,
            WarlordsEntity from,
            float amount,
            int tickDuration
    ) {
        new CooldownFilter<>(target, WoundingCooldown.class)
                .findAny()
                .ifPresentOrElse(woundingData -> {
                            woundingData.getCooldownObject().addWoundingInstance(amount, tickDuration);
                            woundingData.setTicksLeft(tickDuration);
                        }, () -> {
                            target.sendMessage(
                                    Component.text("You are ", NamedTextColor.GRAY)
                                             .append(Component.text("wounded", NamedTextColor.RED))
                                             .append(Component.text(".", NamedTextColor.GRAY))
                            );
                            target.getCooldownManager().addCooldown(new WoundingCooldown(target, name, from, amount, tickDuration));
                        }
                );
    }

    private final WarlordsEntity target;

    public WoundingCooldown(
            WarlordsEntity target,
            String name,
            WarlordsEntity from,
            float amount,
            int tickDuration
    ) {
        this(
                target, name,
                from,
                new WoundingData.WoundingInstance(amount, tickDuration)
        );
    }

    public WoundingCooldown(
            WarlordsEntity target,
            String name,
            WarlordsEntity from,
            WoundingData.WoundingInstance instance
    ) {
        super(
                name,
                "WND",
                WoundingData.class,
                new WoundingData(instance),
                from,
                CooldownTypes.DEBUFF,
                cooldownManager -> {},
                cooldownManager -> {
                    target.sendMessage(
                            Component.text("You are no longer ", NamedTextColor.GRAY)
                                     .append(Component.text("wounded", NamedTextColor.RED))
                                     .append(Component.text(".", NamedTextColor.GRAY))
                    );
                },
                instance.getTicksLeft(),
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    cooldown.getCooldownObject().tick();
                })
        );
        this.target = target;
    }

    @Override
    public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
        return currentHealValue * cooldownObject.getWoundingMultiplier();
    }

    @Override
    public PlayerNameData addSuffixFromOther() {
        return new PlayerNameInstance.PlayerNameData(
                Component.text("WND", NamedTextColor.RED),
                we -> we == from || (we.isTeammate(target) && we.getSpecClass().specType == SpecType.HEALER)
        );
    }

    @Override
    public @Nullable Component getDebugMessage() {
        List<WoundingData.WoundingInstance> instances = cooldownObject.instances;
        return Component.text(NumberFormat.formatOptionalHundredths(cooldownObject.getWoundingMultiplier()) + "=" +
                        instances.stream().map(Object::toString).collect(Collectors.joining(",")),
                NamedTextColor.DARK_RED
        );
    }

    public record WoundingData(List<WoundingInstance> instances) {

        public WoundingData(WoundingInstance instance) {
            this(new ArrayList<>(List.of(instance)));
        }

        public float getWoundingMultiplier() {
            return AbstractAbility.convertToDivisionDecimal(
                    instances.stream()
                             .map(WoundingInstance::getAmount)
                             .max(Float::compareTo)
                             .orElse(0f)
            );
        }

        public void addWoundingInstance(float amount, int ticksLeft) {
            instances.add(new WoundingInstance(amount, ticksLeft));
        }

        public void tick() {
            for (WoundingInstance instance : instances) {
                instance.setTicksLeft(instance.getTicksLeft() - 1);
            }
            instances.removeIf(instance -> instance.getTicksLeft() <= 0);
        }

        public static class WoundingInstance {

            private float amount;
            private int ticksLeft;

            public WoundingInstance(float amount, int ticksLeft) {
                this.amount = amount;
                this.ticksLeft = ticksLeft;
            }

            @Override
            public String toString() {
                return "(" + NumberFormat.formatOptionalHundredths(amount) + "%|" + ticksLeft + ")";
            }

            public float getAmount() {
                return amount;
            }

            public void setAmount(float amount) {
                this.amount = amount;
            }

            public int getTicksLeft() {
                return ticksLeft;
            }

            public void setTicksLeft(int ticksLeft) {
                this.ticksLeft = ticksLeft;
            }

        }

    }

}
