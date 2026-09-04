package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.ImpalingStrike;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.PlayerNameInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Leech {

    public static void giveLeechCooldown(LeechInstance leechInstance) {
        WarlordsEntity target = leechInstance.target;
        WarlordsEntity from = leechInstance.from;
        float leechAmount = leechInstance.leechAmount;
        int leechTickDuration = leechInstance.leechTickDuration;

        boolean inPve = from.isInPve();
        Optional<RegularCooldown> oldLeechCooldown = new CooldownFilter<>(target, RegularCooldown.class)
                .filterCooldownClass(LeechData.class)
                .findAny();
        if (oldLeechCooldown.isPresent() && oldLeechCooldown.get().getFrom().equals(from)) {
            RegularCooldown<LeechData> leechCooldown = oldLeechCooldown.get();
            leechCooldown.setTicksLeft(Math.max(leechCooldown.getTicksLeft(), leechTickDuration));
            for (int i = 0; i < leechInstance.initialStacks; i++) {
                leechCooldown.getCooldownObject().add(leechTickDuration);
            }
        } else {
            // remove leech from other players
            oldLeechCooldown.ifPresent(abstractCooldown -> target.getCooldownManager().removeCooldown(abstractCooldown));
            LeechData data = new LeechData(target, leechAmount, leechInstance.initialStacks, leechTickDuration);
            target.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Leech Debuff",
                    "LCH",
                    LeechData.class,
                    data,
                    from,
                    CooldownTypes.LOW_LEVEL_DEBUFF,
                    cooldownManager -> {
                    },
                    leechTickDuration,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        for (int i = 0; i < data.stacks.size(); i++) {
                            Integer integer = data.stacks.get(i);
                            data.stacks.set(i, integer - 1);
                        }
                        int before = data.getStacksCount();
                        data.stacks.removeIf(integer -> integer <= 0);
                        target.getCooldownManager().markNameDisplayDirtyIfChanged(before, data.getStacksCount());
                    })
            ) {

                @Override
                protected Listener getListener() {
                    return new Listener() {
                        @EventHandler
                        public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent finalEvent) {
                            if (finalEvent.getWarlordsEntity() != target) {
                                return;
                            }
                            if (!finalEvent.isDamageInstance()) {
                                return;
                            }
                            if (finalEvent.getInstanceFlags().contains(InstanceFlags.NO_HEALING_LEECH)) {
                                return;
                            }
//                            if (inPve && data.totalHealingDone >= 1000) {
//                                setTicksLeft(0);
//                                return;
//                            }
                            float value = finalEvent.getValueBeforeAllReduction();
                            float healValue = value * AbstractAbility.convertToPercent(leechAmount * data.getStacksCount());
                            if (inPve) {
                                healValue = Math.min(300 * data.getStacksCount(), healValue);
                            }
                            finalEvent.getSource().addInstance(InstanceBuilder
                                    .healing()
                                    .cause("Leech")
                                    .source(from)
                                    .value(healValue)
                                    .flags(InstanceFlags.NO_HIT_SOUND)
                                    .customFlags(new CustomInstanceFlags.FinalEventInstanceFlag(finalEvent))
                            ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                Consumer<WarlordsDamageHealingFinalEvent> consumer = leechInstance.finalEventConsumer;
                                if (consumer != null) {
                                    consumer.accept(warlordsDamageHealingFinalEvent);
                                }
                                data.totalHealingDone += warlordsDamageHealingFinalEvent.getValue();
                                if (finalEvent.isHasFlag()) {
                                    data.addHealingDoneFromEnemyCarrier(warlordsDamageHealingFinalEvent.getValue());
                                }
                            });
                        }
                    };
                }

                @Nonnull
                @Override
                public Component getDebugMessage() {
                    return Component.text(data.getStacksCount() + ":" +
                                    data.stacks.stream().map(Object::toString).collect(Collectors.joining(",")),
                            NamedTextColor.DARK_GREEN
                    );
                }

                @Override
                public PlayerNameData addSuffixFromOther() {
                    return PlayerNameData.dynamic(
                            () -> Component.text("LCH(" + data.getStacksCount() + ")", CooldownTypes.HIGH_LEVEL_DEBUFF_COLOR),
                            we -> we.isEnemy(target) || (we.isTeammate(target) && we.getSpecClass().specType == SpecType.HEALER)
                    );
                }
            });
        }
    }

    public static class LeechInstance {

        public static LeechInstance create(WarlordsEntity from, WarlordsEntity target) {
            return new LeechInstance(from, target);
        }

        private final WarlordsEntity from;
        private final WarlordsEntity target;
        private Consumer<WarlordsDamageHealingFinalEvent> finalEventConsumer;
        private float leechAmount;
        private int leechTickDuration;
        private int initialStacks = 1;

        public LeechInstance(WarlordsEntity from, WarlordsEntity target) {
            this.from = from;
            this.target = target;
        }

        public LeechInstance withImpalingStrike(ImpalingStrike impalingStrike) {
            this.leechAmount = impalingStrike.getLeechAmount();
            this.leechTickDuration = impalingStrike.getLeechTickDuration();
            return this;
        }

        public LeechInstance withImpalingStrike() {
            for (AbstractAbility ability : from.getAbilities()) {
                if (ability instanceof ImpalingStrike impalingStrike) {
                    this.leechAmount = impalingStrike.getLeechAmount();
                    this.leechTickDuration = impalingStrike.getLeechTickDuration();
                    return this;
                }
            }
            return this;
        }

        public LeechInstance withLeechAmount(float leechAmount) {
            this.leechAmount = leechAmount;
            return this;
        }

        public LeechInstance withLeechTickDuration(int leechTickDuration) {
            this.leechTickDuration = leechTickDuration;
            return this;
        }

        public LeechInstance withInitialStacks(int initialStacks) {
            this.initialStacks = initialStacks;
            return this;
        }

        public LeechInstance withFinalEventConsumer(Consumer<WarlordsDamageHealingFinalEvent> finalEventConsumer) {
            this.finalEventConsumer = finalEventConsumer;
            return this;
        }

    }

    public static class LeechData {

        private final WarlordsEntity owner;
        private final float leechAmount;
        private final List<Integer> stacks = new ArrayList<>();
        private float healingDoneFromEnemyCarrier = 0;
        private float totalHealingDone = 0;

        public LeechData(WarlordsEntity owner, float leechAmount, int stacks, int tickDurationPerStack) {
            this.owner = owner;
            this.leechAmount = leechAmount;
            for (int i = 0; i < stacks; i++) {
                this.stacks.add(tickDurationPerStack);
            }
        }

        public void add(int stack) {
            int before = getStacksCount();
            if (stacks.size() >= 4) {
                stacks.removeFirst();
            }
            stacks.add(stack);
            owner.getCooldownManager().markNameDisplayDirtyIfChanged(before, getStacksCount());
        }

        public float getLeechAmount() {
            return leechAmount;
        }

        public int getStacksCount() {
            return stacks.size();
        }

        public void addHealingDoneFromEnemyCarrier(float amount) {
            this.healingDoneFromEnemyCarrier += amount;
        }

        public float getHealingDoneFromEnemyCarrier() {
            return healingDoneFromEnemyCarrier;
        }

    }

}
