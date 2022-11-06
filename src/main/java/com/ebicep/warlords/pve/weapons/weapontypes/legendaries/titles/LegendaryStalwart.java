package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;

import java.util.UUID;

public class LegendaryStalwart extends AbstractLegendaryWeapon {

    public static final int MELEE_DAMAGE_MIN = 140;
    public static final int MELEE_DAMAGE_MAX = 160;
    public static final int CRIT_CHANCE = 20;
    public static final int CRIT_MULTIPLIER = 160;
    public static final int HEALTH_BONUS = 1000;
    public static final int SPEED_BONUS = 5;


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
                        System.out.println(player.getHealth() + " - " + player.getMaxHealth());
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
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.critChance = CRIT_CHANCE;
        this.critMultiplier = CRIT_MULTIPLIER;
        this.healthBonus = HEALTH_BONUS;
        this.speedBonus = SPEED_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }
}
