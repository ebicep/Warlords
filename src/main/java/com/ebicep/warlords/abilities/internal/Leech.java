package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.ImpalingStrike;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Consumer;

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
            leechCooldown.setTicksLeft(leechTickDuration);
            leechCooldown.getCooldownObject().stacks++;
        } else {
            // remove leech from other players
            oldLeechCooldown.ifPresent(abstractCooldown -> target.getCooldownManager().removeCooldown(abstractCooldown));
            LeechData data = new LeechData(leechAmount, leechInstance.initialStacks);
            target.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Leech Debuff",
                    "LCH",
                    LeechData.class,
                    data,
                    from,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    leechTickDuration
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
                            if (inPve && data.totalHealingDone >= 1000) {
                                setTicksLeft(0);
                                return;
                            }
                            float value = finalEvent.getValueBeforeAllReduction();
                            float healValue = value * AbstractAbility.convertToPercent(leechAmount * data.stacks);
                            if (inPve) {
                                healValue = Math.min(500, healValue);
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

                @Override
                public TextColor customActionBarColor() {
                    return AbstractCooldown.PSEUDO_DEBUFF_COLOR;
                }

                @Nonnull
                @Override
                public Component getDebugMessage() {
                    return Component.text(data.stacks).color(NamedTextColor.DARK_GREEN);
                }

                @Override
                public PlayerNameData addSuffixFromOther() {
                    return new PlayerNameData(Component.text("LCH(" + data.stacks + ")", AbstractCooldown.PSEUDO_DEBUFF_COLOR),
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

        private final float leechAmount;
        private int stacks;
        private float healingDoneFromEnemyCarrier = 0;
        private float totalHealingDone = 0;

        public LeechData(float leechAmount, int stacks) {
            this.leechAmount = leechAmount;
            this.stacks = stacks;
        }

        public float getLeechAmount() {
            return leechAmount;
        }

        public int getStacks() {
            return stacks;
        }

        public void setStacks(int stacks) {
            this.stacks = stacks;
        }

        public float getTotalHealingDone() {
            return totalHealingDone;
        }

        public void setTotalHealingDone(float totalHealingDone) {
            this.totalHealingDone = totalHealingDone;
        }

        public void addHealingDoneFromEnemyCarrier(float amount) {
            this.healingDoneFromEnemyCarrier += amount;
        }

        public float getHealingDoneFromEnemyCarrier() {
            return healingDoneFromEnemyCarrier;
        }

    }

}
