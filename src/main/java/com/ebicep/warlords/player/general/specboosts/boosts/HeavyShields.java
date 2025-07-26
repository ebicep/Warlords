package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FortifyingHex;
import com.ebicep.warlords.abilities.GuardianBeam;
import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.List;

public class HeavyShields implements SpecBoostManager.SpecBoost<HeavyShields> {

    private float guardianBeamSlowness;
    private int guardianBeamSlownessDurationTicks;
    private float fortifyingHexKnockback;
    private float fortifyingHexKnockbackY;

    @Override
    public void init() {
        this.guardianBeamSlowness = getValue("guardianBeamSlowness", float.class);
        this.guardianBeamSlownessDurationTicks = getValue("guardianBeamSlownessDurationTicks", int.class);
        this.fortifyingHexKnockback = getValue("fortifyingHexKnockback", float.class);
        this.fortifyingHexKnockbackY = getValue("fortifyingHexKnockbackY", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "heavyShields";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                guardianBeamSlowness,
                guardianBeamSlownessDurationTicks
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public HeavyShields get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
        }

        @EventHandler
        public void onDamageHealEvent(WarlordsDamageHealingEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            WarlordsEntity target = event.getWarlordsEntity();
            if (event.getAbility() instanceof FortifyingHex) {
                for (CustomInstanceFlags customFlag : event.getCustomFlags()) {
                    if (customFlag instanceof CustomInstanceFlags.ProjectileHitInstanceFlag(
                            AbstractPiercingProjectile.InternalProjectile projectile
                            )) {
                        Vector v = projectile.getStartingLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(-fortifyingHexKnockback).setY(fortifyingHexKnockbackY);
                        target.setVelocity(getStringName(), v, false);
                        return;
                    }
                }
            } else if (event.getAbility() instanceof GuardianBeam) {
                target.addSpeedModifier(warlordsEntity, getStringName(), -guardianBeamSlowness, guardianBeamSlownessDurationTicks);
            }
        }

    }

}
