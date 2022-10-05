package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;

import java.util.UUID;

public class LegendaryGale extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 150;
    public static final int MELEE_DAMAGE_MAX = 170;
    public static final int CRIT_CHANCE = 20;
    public static final int CRIT_MULTIPLIER = 185;
    public static final int HEALTH_BONUS = 500;
    public static final int SPEED_BONUS = 18;

    public LegendaryGale() {
    }

    public LegendaryGale(UUID uuid) {
        super(uuid);
    }

    public LegendaryGale(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getTitle() {
        return "Gale";
    }

    @Override
    public String getPassiveEffect() {
        return "Increase movement speed by 10% and decrease energy consumption of all abilities by 5.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        player.getSpeed().addBaseModifier(10);
        for (AbstractAbility ability : player.getSpec().getAbilities()) {
            if (ability.getEnergyCost() > 0) {
                ability.setEnergyCost(ability.getEnergyCost() - 5);
            }
        }

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
