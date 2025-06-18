package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Soulbinding;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddSpeedModifierEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFlag;
import org.bukkit.event.EventHandler;

import java.util.List;

public class FarmerBrown implements SpecBoostManager.SpecBoost<FarmerBrown> {

    private float slowResistancePercent;
    private int soulbindingKnockbackResistancePercent;

    @Override
    public void init() {
        this.slowResistancePercent = getValue("slowResistancePercent", float.class);
        this.soulbindingKnockbackResistancePercent = getValue("soulbindingKnockbackResistancePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "farmerBrown";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(slowResistancePercent, soulbindingKnockbackResistancePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public FarmerBrown get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(Soulbinding.class).forEach(soulbinding -> {
                soulbinding.setKbRes(soulbinding.getKbRes() + soulbindingKnockbackResistancePercent);
            });
        }

        @EventHandler
        public void onAddSpeed(WarlordsAddSpeedModifierEvent event) {
            if (!event.getWarlordsEntity().equals(warlordsEntity)) {
                return;
            }
            if (event.getMotionModifier().getModifier() > 0) {
                return;
            }
            event.getMotionModifier().setModifier(Math.min(0, event.getMotionModifier().getModifier() + slowResistancePercent));
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!cooldown.getCooldownClass().equals(Soulbinding.SoulbindingData.class) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            cooldown.getFlags().add(CooldownFlag.CANNOT_BE_REDUCED);
        }

    }

}
