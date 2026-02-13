package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.AvengersStrike;
import com.ebicep.warlords.abilities.HolyRadianceAvenger;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;

import java.util.List;

public class MarkedForDeath implements SpecBoostManager.SpecBoost<MarkedForDeath> {

    private float avengerMarkDamage;
    private float avengerMarkSlowPercent;
    private int avengerMarkDebuffTickDuration;
    private float holyRadianceEnergyCost;
    private int strikeMarkDurationIncreaseTicks;
    private int maxStrikeMarkDurationIncreaseTicks;

    @Override
    public void init() {
        this.avengerMarkDamage = getValue("avengerMarkDamage", float.class);
        this.avengerMarkSlowPercent = getValue("avengerMarkSlowPercent", float.class);
        this.avengerMarkDebuffTickDuration = getValue("avengerMarkDebuffTickDuration", int.class);
        this.holyRadianceEnergyCost = getValue("holyRadianceEnergyCost", float.class);
        this.strikeMarkDurationIncreaseTicks = getValue("strikeMarkDurationIncreaseTicks", int.class);
        this.maxStrikeMarkDurationIncreaseTicks = getValue("maxStrikeMarkDurationIncreaseTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "markedForDeath";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                avengerMarkDamage,
                avengerMarkSlowPercent,
                avengerMarkDebuffTickDuration,
                strikeMarkDurationIncreaseTicks,
                maxStrikeMarkDurationIncreaseTicks

        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public MarkedForDeath get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(HolyRadianceAvenger.class).forEach(holyRadiance -> {
                holyRadiance.getEnergyCost().addModifier(FloatModifiable.ModifierType.OVERRIDING, "Spec Boost", holyRadianceEnergyCost);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                return;
            }
            if (!cooldown.getName().equals("Avenger's Mark") || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            regularCooldown.setTicksLeft(avengerMarkDebuffTickDuration);
            regularCooldown.setCooldownType(CooldownTypes.TRUE_DEBUFF);
            WarlordsEntity target = event.getWarlordsEntity();
            target.addInstance(InstanceBuilder
                    .damage()
                    .cause(getStringName())
                    .source(warlordsEntity)
                    .value(100)
            );
            target.addSpeedModifier(warlordsEntity, getStringName(), -avengerMarkSlowPercent, regularCooldown);
            final int[] ticksIncreased = {0};
            regularCooldown.addModifier(Modifier.ON_INCOMING_DAMAGE, (e, currentDamageValue, isCrit) -> {
                if (e.getSource().equals(warlordsEntity) && e.getAbility() instanceof AvengersStrike) {
                            if (ticksIncreased[0] >= maxStrikeMarkDurationIncreaseTicks) {
                                return;
                            }
                            regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + strikeMarkDurationIncreaseTicks);
                            ticksIncreased[0] += strikeMarkDurationIncreaseTicks;
                        }
                    }
            );

        }

    }

}
