package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.EarthlivingWeapon;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;

import java.util.List;

public class EarthboundInfusion implements SpecBoostManager.SpecBoost<EarthboundInfusion> {

    private int healthIncrease;
    private int earthlivingExtraGuaranteedHits;
    private int earthlivingSingleHealBonus;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
        this.earthlivingExtraGuaranteedHits = getValue("earthlivingExtraGuaranteedHits", int.class);
        this.earthlivingSingleHealBonus = getValue("earthlivingSingleHealBonus", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "earthboundInfusion";
    }

    @Override
    public int getMaxDescriptionWidth() {
        return 151;
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease, earthlivingExtraGuaranteedHits, earthlivingSingleHealBonus);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public EarthboundInfusion get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost (Base)", healthIncrease);
            warlordsPlayer.getAbilitiesMatching(EarthlivingWeapon.class).forEach(earthlivingWeapon -> {
                earthlivingWeapon.setGuaranteedHits(earthlivingWeapon.getGuaranteedHits() + earthlivingExtraGuaranteedHits);
            });
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getStringName(),
                    null,
                    Boost.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {},
                    false
            ) {
                @Override
                public float modifyHealingFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                    if (event.getWarlordsEntity().equals(warlordsPlayer) && event.getSource().equals(warlordsPlayer) && event.getCause().equals("Earthliving Weapon")) {
                        List<CustomInstanceFlags> customFlags = event.getCustomFlags();
                        for (CustomInstanceFlags customFlag : customFlags) {
                            if (customFlag instanceof CustomInstanceFlags.PlayersEffectedInstanceFlag(List<WarlordsEntity> healedPlayers) && healedPlayers.isEmpty()) {
                                return currentHealValue + earthlivingSingleHealBonus;
                            }
                        }
                    }
                    return currentHealValue;
                }
            });
        }

    }

}
