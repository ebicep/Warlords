package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.CrusadersStrike;
import com.ebicep.warlords.abilities.InspiringPresence;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsStrikeEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.Objects;

public class RallyingPresence implements SpecBoostManager.SpecBoost<RallyingPresence> {

    private int energyPerSecondIncrease;
    private int speedIncreasePercent;
    private float damagePerAllyHitPercent;
    private int inspiringPresenceDurationIncreasePerStrikeTicks;

    @Override
    public void init() {
        this.energyPerSecondIncrease = getValue("energyPerSecondIncrease", int.class);
        this.speedIncreasePercent = getValue("speedIncreasePercent", int.class);
        this.damagePerAllyHitPercent = getValue("damagePerAllyHitPercent", float.class);
        this.inspiringPresenceDurationIncreasePerStrikeTicks = getValue("inspiringPresenceDurationIncreasePerStrikeTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "rallyingPresence";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                energyPerSecondIncrease,
                speedIncreasePercent,
                damagePerAllyHitPercent,
                inspiringPresenceDurationIncreasePerStrikeTicks
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public RallyingPresence get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(InspiringPresence.class).forEach(inspiringPresence -> {
                inspiringPresence.setEnergyPerSecond(inspiringPresence.getEnergyPerSecond() + energyPerSecondIncrease);
                inspiringPresence.setSpeedBuff(inspiringPresence.getSpeedBuff() + speedIncreasePercent);
            });
        }

        @EventHandler
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!Objects.equals(cooldown.getFrom(), warlordsEntity)) {
                return;
            }
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                return;
            }
            if (!(regularCooldown.getCooldownObject() instanceof InspiringPresence.InspiringPresenceData data)) {
                return;
            }
            int alliesHitCount = data.getAlliesHitCount();
            if (alliesHitCount == 0) {
                return;
            }
            regularCooldown.addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (e, currentDamageValue) -> {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                                getStringName(),
                                AbstractAbility.convertToMultiplicationDecimal(alliesHitCount * damagePerAllyHitPercent)
                        );
                    }
            );
        }

        @EventHandler(ignoreCancelled = true)
        public void onWarlordsStrikeEvent(WarlordsStrikeEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (!(event.getStrikeAbility() instanceof CrusadersStrike)) {
                return;
            }
            new CooldownFilter<>(warlordsEntity, RegularCooldown.class)
                    .filterCooldownClass(InspiringPresence.InspiringPresenceData.class)
                    .filterCooldownFrom(warlordsEntity)
                    .forEach(regularCooldown -> regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + inspiringPresenceDurationIncreasePerStrikeTicks));
            warlordsEntity.getAbilitiesMatching(InspiringPresence.class).forEach(inspiringPresence -> {
                warlordsEntity.getSpeed().getModifiers().stream()
                              .filter(modifier -> modifier.getName().equals(inspiringPresence.getName()))
                              .forEach(modifier -> modifier.setTicksLeft(modifier.getTicksLeft() + inspiringPresenceDurationIncreasePerStrikeTicks));
            });
        }

    }

}
