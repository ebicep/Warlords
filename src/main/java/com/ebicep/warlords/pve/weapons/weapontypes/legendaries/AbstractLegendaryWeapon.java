package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.weapons.AbstractTierTwoWeapon;
import com.ebicep.warlords.pve.weapons.WeaponStats;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.StarPieceBonus;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Utils;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public abstract class AbstractLegendaryWeapon extends AbstractTierTwoWeapon implements StarPieceBonus {

    @Field("star_piece")
    protected StarPieces starPiece;
    @Field("star_piece_bonus")
    protected WeaponStats starPieceBonus;
    @Field("skill_boost")
    protected SkillBoosts selectedSkillBoost;
    @Field("unlocked_skill_boosts")
    protected List<SkillBoosts> unlockedSkillBoosts = new ArrayList<>();
    @Field("unlocked_titles")
    protected List<LegendaryTitles> unlockedTitles = new ArrayList<>();
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
        Specializations selectedSpec = PlayerSettings.getPlayerSettings(uuid).getSelectedSpec();
        List<SkillBoosts> skillBoosts = selectedSpec.skillBoosts;
        this.selectedSkillBoost = skillBoosts.get(Utils.generateRandomValueBetweenInclusive(0, skillBoosts.size() - 1));
        this.unlockedSkillBoosts.add(selectedSkillBoost);
        this.selectedWeaponSkin = Weapons.getRandomWeaponFromRarity(WeaponsRarity.LEGENDARY);
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
    }

    public AbstractLegendaryWeapon(AbstractLegendaryWeapon legendaryWeapon) {
        this.uuid = legendaryWeapon.getUUID();
        this.date = legendaryWeapon.getDate();
        this.selectedWeaponSkin = legendaryWeapon.getSelectedWeaponSkin();
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
        this.specialization = legendaryWeapon.getSpecializations();
        this.isBound = legendaryWeapon.isBound();

        this.selectedSkillBoost = legendaryWeapon.getSelectedSkillBoost();
        this.unlockedSkillBoosts.add(selectedSkillBoost);
        this.unlockedTitles = legendaryWeapon.getUnlockedTitles();
        generateStats();
        for (int i = 0; i < legendaryWeapon.getUpgradeLevel(); i++) {
            upgrade();
        }
    }

    public SkillBoosts getSelectedSkillBoost() {
        return selectedSkillBoost;
    }

    public void setSelectedSkillBoost(SkillBoosts selectedSkillBoost) {
        this.selectedSkillBoost = selectedSkillBoost;
    }

    public WeaponStats getStarPieceBonus() {
        return starPieceBonus;
    }

    @Override
    public WeaponsPvE getRarity() {
        return WeaponsPvE.LEGENDARY;
    }

    @Override
    public String getName() {
        if (getTitle().isEmpty()) {
            return super.getName();
        } else {
            return ChatColor.GOLD + getTitle() + " " + super.getName();
        }
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.GOLD;
    }

    public abstract String getTitle();

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);
        for (AbstractUpgradeBranch<?> upgradeBranch : player.getAbilityTree().getUpgradeBranches()) {
            if (upgradeBranch.getAbility().getClass().equals(selectedSkillBoost.ability)) {
                upgradeBranch.setFreeUpgrades(1);
                break;
            }
        }

        AbstractPlayerClass playerClass = player.getSpec();
        playerClass.setEnergyOnHit(playerClass.getEnergyOnHit() + getEnergyPerHitBonus());
        playerClass.setEnergyPerSec(playerClass.getEnergyPerSec() + getEnergyPerSecondBonus());
        for (AbstractAbility ability : playerClass.getAbilities()) {
            if (ability.getClass().equals(selectedSkillBoost.ability)) {
                if (ability.getCritChance() != -1) {
                    ability.setCritChance(ability.getCritChance() + getSkillCritChanceBonus());
                    ability.setCritMultiplier(ability.getCritMultiplier() + getSkillCritMultiplierBonus());
                }
                break;
            }
        }
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        if (speedBonus != 0) {
            lore.add(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + format(getSpeedBonus()) + "%" + getStarPieceBonusString(WeaponStats.SPEED_BONUS));
        }
        if (energyPerSecondBonus != 0) {
            lore.add(ChatColor.GRAY + "Energy per Second: " + ChatColor.GREEN + format(getEnergyPerSecondBonus()) + getStarPieceBonusString(WeaponStats.ENERGY_PER_SECOND_BONUS));
        }
        if (energyPerHitBonus != 0) {
            lore.add(ChatColor.GRAY + "Energy per Hit: " + ChatColor.GREEN + format(getEnergyPerHitBonus()) + getStarPieceBonusString(WeaponStats.ENERGY_PER_HIT_BONUS));
        }
        lore.addAll(Arrays.asList(
                "",
                ChatColor.GREEN + "Skill Boost (" + selectedSkillBoost.name + "):",
                ChatColor.GRAY + WordWrap.wrapWithNewline("1 Free Ability Upgrade", 175)
        ));
        if (skillCritChanceBonus != 0) {
            lore.add(ChatColor.GRAY + "Skill Crit Chance: " + ChatColor.GREEN + format(getSkillCritChanceBonus()) + "%" + getStarPieceBonusString(WeaponStats.SKILL_CRIT_CHANCE_BONUS));
        }
        if (skillCritMultiplierBonus != 0) {
            lore.add(ChatColor.GRAY + "Skill Crit Multiplier: " + ChatColor.GREEN + format(getSkillCritMultiplierBonus()) + "%" + getStarPieceBonusString(
                    WeaponStats.SKILL_CRIT_MULTIPLIER_BONUS));
        }
        String passiveEffect = getPassiveEffect();
        if (!passiveEffect.isEmpty()) {
            lore.addAll(Arrays.asList(
                    "",
                    ChatColor.GREEN + "Passive Effect (" + getTitle() + "):",
                    ChatColor.GRAY + WordWrap.wrapWithNewline(passiveEffect, 175)
            ));
        }

        return lore;
    }

    public String getStarPieceBonusString(WeaponStats weaponStats) {
        return starPieceBonus == weaponStats ? getStarPieceBonusString() : "";
    }

    public abstract String getPassiveEffect();

    @Override
    public float getSpeedBonus() {
        return starPieceBonus == WeaponStats.SPEED_BONUS ? speedBonus * getStarPieceBonusMultiplicativeValue() : speedBonus;
    }

    @Override
    public void upgrade() {
        super.upgrade();
        this.energyPerSecondBonus *= energyPerSecondBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier();
        this.energyPerHitBonus *= energyPerHitBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier();
        this.skillCritChanceBonus *= skillCritChanceBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier();
        this.skillCritMultiplierBonus *= skillCritMultiplierBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier();
    }

    @Override
    public List<String> getUpgradeLore() {
        List<String> upgradeLore = new ArrayList<>(super.getUpgradeLore());
        if (energyPerSecondBonus != 0) {
            upgradeLore.add(ChatColor.GRAY + "Energy per Second: " + ChatColor.GREEN + format(energyPerSecondBonus) + " > " +
                    format(energyPerSecondBonus * (energyPerSecondBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier())));
        }
        if (energyPerHitBonus != 0) {
            upgradeLore.add(ChatColor.GRAY + "Energy per Hit: " + ChatColor.GREEN + format(energyPerHitBonus) + " > " +
                    format(energyPerHitBonus * (energyPerHitBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier())));
        }
        if (skillCritChanceBonus != 0) {
            upgradeLore.add(ChatColor.GRAY + "Skill Crit Chance: " + ChatColor.GREEN + format(skillCritChanceBonus) + " > " +
                    format(skillCritChanceBonus * (skillCritChanceBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier())));
        }
        if (skillCritMultiplierBonus != 0) {
            upgradeLore.add(ChatColor.GRAY + "Skill Crit Multiplier: " + ChatColor.GREEN + format(skillCritMultiplierBonus) + " > " +
                    format(skillCritMultiplierBonus * (skillCritMultiplierBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier())));
        }
        return upgradeLore;
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

    @Override
    public List<String> getBaseStats() {
        return Arrays.asList(
                ChatColor.GRAY + "Damage: " + ChatColor.RED + NumberFormat.formatOptionalTenths(getMeleeDamageMin()) + " - " + NumberFormat.formatOptionalHundredths(
                        getMeleeDamageMax()) + getStarPieceBonusString(WeaponStats.MELEE_DAMAGE),
                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + NumberFormat.formatOptionalTenths(getCritChance()) + "%" + getStarPieceBonusString(
                        WeaponStats.CRIT_CHANCE),
                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + NumberFormat.formatOptionalTenths(getCritMultiplier()) + "%" + getStarPieceBonusString(
                        WeaponStats.CRIT_MULTIPLIER),
                "",
                ChatColor.GRAY + "Health: " + ChatColor.GREEN + format(getHealthBonus()) + getStarPieceBonusString(WeaponStats.HEALTH_BONUS)
        );
    }

    @Override
    public float getMeleeDamageMin() {
        return starPieceBonus == WeaponStats.MELEE_DAMAGE ? meleeDamage * getStarPieceBonusMultiplicativeValue() : meleeDamage;
    }

    @Override
    public float getMeleeDamageMax() {
        return starPieceBonus == WeaponStats.MELEE_DAMAGE ? (meleeDamage + getMeleeDamageRange()) * getStarPieceBonusMultiplicativeValue() : meleeDamage + getMeleeDamageRange();
    }

    @Override
    public float getCritChance() {
        return starPieceBonus == WeaponStats.CRIT_CHANCE ? critChance * getStarPieceBonusMultiplicativeValue() : critChance;
    }

    @Override
    public float getCritMultiplier() {
        return starPieceBonus == WeaponStats.CRIT_MULTIPLIER ? critMultiplier * getStarPieceBonusMultiplicativeValue() : critMultiplier;
    }

    @Override
    public float getHealthBonus() {
        return starPieceBonus == WeaponStats.HEALTH_BONUS ? healthBonus * getStarPieceBonusMultiplicativeValue() : healthBonus;
    }

    @Override
    public List<WeaponStats> getRandomStatBonus() {
        List<WeaponStats> randomStatBonus = new ArrayList<>();
        if (meleeDamage > 0) {
            randomStatBonus.add(WeaponStats.MELEE_DAMAGE);
        }
        if (critChance > 0) {
            randomStatBonus.add(WeaponStats.CRIT_CHANCE);
        }
        if (critMultiplier > 0) {
            randomStatBonus.add(WeaponStats.CRIT_MULTIPLIER);
        }
        if (healthBonus > 0) {
            randomStatBonus.add(WeaponStats.HEALTH_BONUS);
        }
        if (speedBonus > 0) {
            randomStatBonus.add(WeaponStats.SPEED_BONUS);
        }
        if (energyPerSecondBonus > 0) {
            randomStatBonus.add(WeaponStats.ENERGY_PER_SECOND_BONUS);
        }
        if (energyPerHitBonus > 0) {
            randomStatBonus.add(WeaponStats.ENERGY_PER_HIT_BONUS);
        }
        if (skillCritChanceBonus > 0) {
            randomStatBonus.add(WeaponStats.SKILL_CRIT_CHANCE_BONUS);
        }
        if (skillCritMultiplierBonus > 0) {
            randomStatBonus.add(WeaponStats.SKILL_CRIT_MULTIPLIER_BONUS);
        }
        return randomStatBonus;
    }

    @Override
    public int getStarPieceBonusValue() {
        return starPiece.starPieceBonusValue;
    }

    @Override
    public int getMaxUpgradeLevel() {
        return 4;
    }

    @Override
    public LinkedHashMap<Currencies, Long> getUpgradeCost(int tier) {
        LinkedHashMap<Currencies, Long> cost = new LinkedHashMap<>();
        switch (tier) {
            case 1:
                cost.put(Currencies.COIN, 100000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 10000L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 5000L);
                break;
            case 2:
                cost.put(Currencies.COIN, 250000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 20000L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 10000L);
                break;
            case 3:
                cost.put(Currencies.COIN, 500000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 30000L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 15000L);
                break;
            case 4:
                cost.put(Currencies.COIN, 1000000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 40000L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 20000L);
                break;
        }
        return cost;
    }

    public void setStarPiece(StarPieces starPiece, WeaponStats starPieceBonus) {
        this.starPiece = starPiece;
        this.starPieceBonus = starPieceBonus;
    }

    public List<SkillBoosts> getUnlockedSkillBoosts() {
        return unlockedSkillBoosts;
    }

    public List<LegendaryTitles> getUnlockedTitles() {
        return unlockedTitles;
    }
}
