package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;

import java.util.UUID;

public class LegendaryStalwart extends AbstractLegendaryWeapon {

    public LegendaryStalwart() {
    }

    public LegendaryStalwart(UUID uuid) {
        super(uuid);
    }

    public LegendaryStalwart(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getTitle() {
        return "Stalwart";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        player.getSpec().setDamageResistance(player.getSpec().getDamageResistance() + 15);

        player.getCooldownManager().addCooldown(
                new PermanentCooldown<>(
                        "Stalwart",
                        null,
                        LegendaryStalwart.class,
                        null,
                        player,
                        CooldownTypes.BUFF,
                        cooldownManager -> {

                        },
                        false
                ) {
                    @Override
                    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        if (player.getHealth() >= player.getMaxHealth() * .7) {
                            return currentDamageValue;
                        }
                        if (player.getHealth() <= player.getMaxHealth() * .4) {
                            return currentDamageValue * .7f;
                        }
                        float percentBelowMax = 1 - player.getHealth() / player.getMaxHealth();
                        float reduction = (float) (((int) (percentBelowMax * 10) - 2) * .075);
                        return currentDamageValue * (1 - reduction);
                    }
                }
        );

    }

    @Override
    public String getPassiveEffect() {
        return "For every 10% of HP under 80%, gain an additional 7.5% damage reduction. Maximum 30% Damage Reduction";
    }

    @Override
    protected float getSpeedBonusValue() {
        return 7;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 140;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 160;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 160;
    }

    @Override
    protected float getHealthBonusValue() {
        return 1000;
    }
}
