package com.ebicep.warlords.pve.newitems.attributes;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.attributes.basic.*;
import com.ebicep.warlords.pve.newitems.attributes.bonus.*;

public abstract class NewItemAttribute {

    public static final NewItemAttribute HEALTH = new Health();
    public static final NewItemAttribute COOLDOWN_REDUCTION = new CooldownReduction();
    public static final NewItemAttribute HEALTH_REGEN = new HealthRegen();
    public static final NewItemAttribute KNOCKBACK_RESISTANCE = new KnockbackResistance();
    public static final NewItemAttribute SKILL_ENERGY_COST_REDUCTION = new SkillEnergyCostReduction();
    public static final NewItemAttribute CRIT_CHANCE = new CritChance();
    public static final NewItemAttribute CRIT_MULTIPLIER = new CritMultiplier();
    public static final NewItemAttribute DAMAGE = new Damage();
    public static final NewItemAttribute DAMAGE_TO_BOSS_ENEMIES = new DamageToBossEnemies();
    public static final NewItemAttribute ENERGY_PER_HIT = new EnergyPerHit();
    public static final NewItemAttribute HEALING = new Healing();
    public static final NewItemAttribute MAX_ENERGY = new MaxEnergy();
    public static final NewItemAttribute SPEED = new Speed();
    public static final NewItemAttribute THORNS = new Thorns();

    public abstract void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer);

}
