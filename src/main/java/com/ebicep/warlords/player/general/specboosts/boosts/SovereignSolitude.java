package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.CrusadersStrike;
import com.ebicep.warlords.abilities.HolyRadianceCrusader;
import com.ebicep.warlords.events.game.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.flags.PlayerFlagLocation;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.Objects;

public class SovereignSolitude implements SpecBoostManager.SpecBoost<SovereignSolitude> {

    private int markedAllySpeedPercent;
    private int radianceCooldownReductionTicks;
    private int flagHealthIncrease;

    @Override
    public void init() {
        this.markedAllySpeedPercent = getValue("markedAllySpeedPercent", int.class);
        this.radianceCooldownReductionTicks = getValue("radianceCooldownReductionTicks", int.class);
        this.flagHealthIncrease = getValue("flagHealthIncrease", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "sovereignSolitude";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(markedAllySpeedPercent, flagHealthIncrease, radianceCooldownReductionTicks);
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
            warlordsPlayer.getAbilitiesMatching(CrusadersStrike.class).forEach(crusadersStrike ->
                    crusadersStrike.setGrantEnergyToLinkedAllyRegardlessOfRange(true)
            );
            warlordsPlayer.getAbilitiesMatching(HolyRadianceCrusader.class).forEach(holyRadiance -> {
                holyRadiance.getCooldown().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", -radianceCooldownReductionTicks / 20f);
                holyRadiance.setMarkSpeed(markedAllySpeedPercent);
            });
        }

        private void breakLink() {
            if (lastMarked != null) {
                lastMarked.getCooldownManager().removeCooldownByName(warlordsEntity.getName() + " - " + getStringName());
                lastMarked = null;
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!Objects.equals(cooldown.getFrom(), warlordsEntity)) {
                return;
            }
            if (!(cooldown instanceof RegularCooldown<?>) || !(cooldown.getCooldownClass().equals(HolyRadianceCrusader.class))) {
                return;
            }
            String cooldownName = warlordsEntity.getName() + " - " + getStringName();
            if (cooldown.getName().equals(cooldownName)) {
                return;
            }
            breakLink();
            lastMarked = event.getWarlordsEntity();
            lastMarked.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    cooldownName,
                    null,
                    HolyRadianceCrusader.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {},
                    true
            ));
        }

        @EventHandler(ignoreCancelled = true)
        public void onDeath(WarlordsDeathEvent event) {
            if (event.getWarlordsEntity().equals(warlordsEntity) || event.getWarlordsEntity().equals(lastMarked)) {
                breakLink();
            }
        }

        @EventHandler
        public void onWarlordsFlagUpdated(WarlordsFlagUpdatedEvent event) {
            if (event.getNew() instanceof PlayerFlagLocation playerFlagLocation && playerFlagLocation.getPlayer().equals(warlordsEntity)) {
                warlordsEntity.getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE, getStringName(), flagHealthIncrease);
            } else if (event.getOld() instanceof PlayerFlagLocation playerFlagLocation && playerFlagLocation.getPlayer().equals(warlordsEntity)) {
                warlordsEntity.getHealth().removeModifier(getStringName());
            }
        }

    }

}
