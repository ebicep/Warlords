package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.general.WeaponsRarity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Utils;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class AbstractLegendaryWeapon extends AbstractTierTwoWeapon {

    protected String title;
    @Field("skill_boost")
    protected SkillBoosts selectedSkillBoost;
    @Field("unlocked_skill_boosts")
    protected List<SkillBoosts> unlockedSkillBoosts = new ArrayList<>();
    @Field("energy_per_second_bonus")
    protected float energyPerSecondBonus;
    @Field("energy_per_hit_bonus")
    protected float energyPerHitBonus;
    @Field("skill_crit_chance_bonus")
    protected float skillCritChanceBonus;
    @Field("skill_crit_multiplier_bonus")
    protected float skillCritMultiplierBonus;

    public AbstractLegendaryWeapon() {
    }

    public AbstractLegendaryWeapon(UUID uuid) {
        super(uuid);
        Specializations selectedSpec = Warlords.getPlayerSettings(uuid).getSelectedSpec();
        List<SkillBoosts> skillBoosts = selectedSpec.skillBoosts;
        this.selectedSkillBoost = skillBoosts.get(Utils.generateRandomValueBetweenInclusive(0, skillBoosts.size() - 1));
        this.unlockedSkillBoosts.add(selectedSkillBoost);
        this.selectedWeaponSkin = Weapons.getRandomWeaponFromRarity(WeaponsRarity.LEGENDARY);
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
    }

    public abstract String getPassiveEffect();

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);
        AbstractPlayerClass playerClass = player.getSpec();
        playerClass.setEnergyOnHit(playerClass.getEnergyOnHit() + getEnergyPerHitBonus());
        playerClass.setEnergyPerSec(playerClass.getEnergyPerSec() + getEnergyPerSecondBonus());
        AbstractAbility weapon = playerClass.getWeapon();
        weapon.setCritChance(weapon.getCritChance() + getSkillCritChanceBonus());
        weapon.setCritMultiplier(weapon.getCritMultiplier() + getSkillCritMultiplierBonus());
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.GOLD;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>(super.getLore());
        if (energyPerSecondBonus != 0) {
            lore.add(ChatColor.GRAY + "Energy per Second: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalHundredths(getEnergyPerSecondBonus()) + getStarPieceBonusString(WeaponStats.ENERGY_PER_SECOND_BONUS));
        }
        if (energyPerHitBonus != 0) {
            lore.add(ChatColor.GRAY + "Energy per Hit: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalHundredths(getEnergyPerHitBonus()) + getStarPieceBonusString(WeaponStats.ENERGY_PER_HIT_BONUS));
        }
        if (skillCritChanceBonus != 0) {
            lore.add(ChatColor.GRAY + "Skill Crit Chance: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalHundredths(getSkillCritChanceBonus()) + "%" + getStarPieceBonusString(WeaponStats.SKILL_CRIT_CHANCE_BONUS));
        }
        if (skillCritMultiplierBonus != 0) {
            lore.add(ChatColor.GRAY + "Skill Crit Multiplier: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalHundredths(getSkillCritMultiplierBonus()) + "%" + getStarPieceBonusString(WeaponStats.SKILL_CRIT_MULTIPLIER_BONUS));
        }
        String passiveEffect = getPassiveEffect();
        if (!passiveEffect.isEmpty()) {
            lore.addAll(Arrays.asList(
                    "",
                    ChatColor.GREEN + "Passive Effect:",
                    ChatColor.GRAY + WordWrap.wrapWithNewline(passiveEffect, 175)
            ));
        }

        return lore;
    }

    @Override
    public int getStarPieceBonusValue() {
        return 50;
    }

    @Override
    public List<WeaponStats> getRandomStatBonus() {
        List<WeaponStats> randomStatBonus = new ArrayList<>(super.getRandomStatBonus());
        randomStatBonus.add(WeaponStats.ENERGY_PER_SECOND_BONUS);
        randomStatBonus.add(WeaponStats.ENERGY_PER_HIT_BONUS);
        randomStatBonus.add(WeaponStats.SKILL_CRIT_CHANCE_BONUS);
        randomStatBonus.add(WeaponStats.SKILL_CRIT_MULTIPLIER_BONUS);
        return randomStatBonus;
    }

    @Override
    public void upgrade() {
        super.upgrade();
        this.energyPerSecondBonus *= getUpgradeMultiplier();
        this.energyPerHitBonus *= getUpgradeMultiplier();
        this.skillCritChanceBonus *= getUpgradeMultiplier();
        this.skillCritMultiplierBonus *= getUpgradeMultiplier();
    }

    @Override
    public List<String> getUpgradeLore() {
        List<String> upgradeLore = new ArrayList<>(super.getUpgradeLore());
        if (energyPerSecondBonus != 0) {
            upgradeLore.add(ChatColor.GRAY + "Energy per Second: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalHundredths(energyPerSecondBonus) + " > " + NumberFormat.formatOptionalHundredths(energyPerSecondBonus * getUpgradeMultiplier()));
        }
        if (energyPerHitBonus != 0) {
            upgradeLore.add(ChatColor.GRAY + "Energy per Hit: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalHundredths(energyPerHitBonus) + " > " + NumberFormat.formatOptionalHundredths(energyPerHitBonus * getUpgradeMultiplier()));
        }
        if (skillCritChanceBonus != 0) {
            upgradeLore.add(ChatColor.GRAY + "Skill Crit Chance: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalHundredths(skillCritChanceBonus) + " > " + NumberFormat.formatOptionalHundredths(skillCritChanceBonus * getUpgradeMultiplier()));
        }
        if (skillCritMultiplierBonus != 0) {
            upgradeLore.add(ChatColor.GRAY + "Skill Crit Multiplier: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalHundredths(skillCritMultiplierBonus) + " > " + NumberFormat.formatOptionalHundredths(skillCritMultiplierBonus * getUpgradeMultiplier()));
        }
        return upgradeLore;
    }

    @Override
    public int getMaxUpgradeLevel() {
        return 4;
    }

    public float getEnergyPerHitBonus() {
        return starPieceBonus == WeaponStats.ENERGY_PER_HIT_BONUS ? energyPerHitBonus * getStarPieceBonusMultiplicativeValue() : energyPerHitBonus;
    }

    public float getEnergyPerSecondBonus() {
        return starPieceBonus == WeaponStats.ENERGY_PER_SECOND_BONUS ? energyPerSecondBonus * getStarPieceBonusMultiplicativeValue() : energyPerSecondBonus;
    }

    public float getSkillCritChanceBonus() {
        return starPieceBonus == WeaponStats.SKILL_CRIT_CHANCE_BONUS ? skillCritChanceBonus * getStarPieceBonusMultiplicativeValue() : skillCritChanceBonus;
    }

    public float getSkillCritMultiplierBonus() {
        return starPieceBonus == WeaponStats.SKILL_CRIT_MULTIPLIER_BONUS ? skillCritMultiplierBonus * getStarPieceBonusMultiplicativeValue() : skillCritMultiplierBonus;
    }
}
