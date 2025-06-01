package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.CrusadersStrike;
import com.ebicep.warlords.abilities.HolyRadianceCrusader;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.Objects;

public class SovereignSolitude implements SpecBoostManager.SpecBoost<SovereignSolitude> {

    private int crusaderStrikeEnergyGrant;
    private int markedAllySpeedPercent;
    private int radianceCooldownReductionTicks;

    @Override
    public void init() {
        this.crusaderStrikeEnergyGrant = getValue("crusaderStrikeEnergyGrant", int.class);
        this.markedAllySpeedPercent = getValue("markedAllySpeedPercent", int.class);
        this.radianceCooldownReductionTicks = getValue("radianceCooldownReductionTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "sovereignSolitude";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(crusaderStrikeEnergyGrant, markedAllySpeedPercent, radianceCooldownReductionTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public SovereignSolitude get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;
        private WarlordsEntity lastMarked;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(CrusadersStrike.class).forEach(crusadersStrike -> {
                crusadersStrike.setEnergyGiven(crusaderStrikeEnergyGrant);
                crusadersStrike.setEnergyMaxAllies(1);
                crusadersStrike.setBlockedByArcaneShield(false);
            });
            warlordsPlayer.getAbilitiesMatching(HolyRadianceCrusader.class).forEach(holyRadiance -> {
                holyRadiance.getCooldown().addAdditiveModifier("Spec Boost", -radianceCooldownReductionTicks / 20f);
                holyRadiance.setMarkSpeed(markedAllySpeedPercent);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!Objects.equals(cooldown.getFrom(), warlordsEntity)) {
                return;
            }
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown) || !(cooldown.getCooldownClass().equals(HolyRadianceCrusader.class))) {
                return;
            }
            String cooldownName = warlordsEntity.getName() + " - " + getStringName();
            if (cooldown.getName().equals(cooldownName)) {
                return;
            }
            if (lastMarked != null) {
                lastMarked.getCooldownManager().removeCooldownByName(cooldownName);
            }
            lastMarked = event.getWarlordsEntity();
            lastMarked.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    cooldownName,
                    null,
                    HolyRadianceCrusader.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {},
                    false
            ));
        }

    }

}
