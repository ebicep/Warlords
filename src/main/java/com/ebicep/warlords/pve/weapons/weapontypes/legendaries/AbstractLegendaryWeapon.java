package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponStats;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.StarPieceBonus;
import com.ebicep.warlords.pve.weapons.weaponaddons.Upgradeable;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Utils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

import static com.ebicep.warlords.util.java.NumberFormat.formatOptionalTenths;

public abstract class AbstractLegendaryWeapon extends AbstractWeapon implements StarPieceBonus, Upgradeable {

    private static final ItemStack ABILITY_ITEM = new ItemStack(Material.NETHER_STAR);

    @Field("star_piece")
    protected StarPieces starPiece;
    @Field("star_piece_bonus")
    protected WeaponStats starPieceBonus;
    @Field("skill_boost")
    protected SkillBoosts selectedSkillBoost;
    @Field("unlocked_skill_boosts") //TODO REMOVE
    protected List<SkillBoosts> unlockedSkillBoosts = new ArrayList<>();
    @Field("unlocked_titles")
    protected List<LegendaryTitles> unlockedTitles = new ArrayList<>();
    @Field("titles")
    protected Map<LegendaryTitles, LegendaryWeaponTitleInfo> titles = new HashMap<>();
    @Field("upgrade_level")
    protected int upgradeLevel = 0;

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
        this.unlockedWeaponSkins = legendaryWeapon.getUnlockedWeaponSkins();
        this.specialization = legendaryWeapon.getSpecializations();
        this.isBound = legendaryWeapon.isBound();

        this.selectedSkillBoost = legendaryWeapon.getSelectedSkillBoost();
        this.unlockedSkillBoosts = legendaryWeapon.getUnlockedSkillBoosts();
        this.unlockedTitles = legendaryWeapon.getUnlockedTitles();
        this.titles = legendaryWeapon.getTitles();
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

    public List<SkillBoosts> getUnlockedSkillBoosts() {
        return unlockedSkillBoosts;
    }

    public List<LegendaryTitles> getUnlockedTitles() {
        return unlockedTitles;
    }

    public Map<LegendaryTitles, LegendaryWeaponTitleInfo> getTitles() {
        return titles;
    }

    @Override
    public void upgrade() {
        this.upgradeLevel++;
    }

    @Override
    public List<String> getUpgradeLore() {
        float minDamageUpgradeDiff = getMeleeDamageMinValue() < 0 ? 0 : getMeleeDamageMinValue() * getUpgradeMultiplier() - getMeleeDamageMinValue();
        List<String> upgradeLore = new ArrayList<>(Arrays.asList(
                ChatColor.GRAY + "Damage: " + ChatColor.RED +
                        formatOptionalTenths(getMeleeDamageMin()) + ChatColor.GRAY + " - " + ChatColor.RED + formatOptionalTenths(getMeleeDamageMax()) +
                        ChatColor.DARK_GREEN + " > " + ChatColor.RED +
                        formatOptionalTenths(getMeleeDamageMin() * (getMeleeDamageMin() > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative())) +
                        ChatColor.GRAY + " - " + ChatColor.RED + formatOptionalTenths(getMeleeDamageMax() + minDamageUpgradeDiff),
                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + formatOptionalTenths(getCritChance()) + "%" + ChatColor.DARK_GREEN + " > " +
                        ChatColor.RED + formatOptionalTenths(getCritChance()) + "%",
                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + formatOptionalTenths(getCritMultiplier()) + "%" + ChatColor.DARK_GREEN + " > " +
                        ChatColor.RED + formatOptionalTenths(getCritMultiplier()) + "%",
                "",
                ChatColor.GRAY + "Health: " + ChatColor.GREEN + format(getHealthBonus()) + ChatColor.DARK_GREEN + " > " + ChatColor.GREEN +
                        format(getHealthBonus() * (getHealthBonus() > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative())),
                ChatColor.GRAY + "Speed: " + ChatColor.GREEN + format(getSpeedBonus()) + "%" + ChatColor.DARK_GREEN + " > " + ChatColor.GREEN +
                        format(getSpeedBonus() * (getSpeedBonus() > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative())) + "%"
        ));
        if (getEnergyPerSecondBonus() != 0) {
            upgradeLore.add(ChatColor.GRAY + "Energy per Second: " + ChatColor.GREEN + format(getEnergyPerSecondBonus()) + ChatColor.DARK_GREEN + " > " + ChatColor.GREEN +
                    format(getEnergyPerSecondBonus() * (getEnergyPerSecondBonus() > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative())));
        }
        if (getEnergyPerHitBonus() != 0) {
            upgradeLore.add(ChatColor.GRAY + "Energy per Hit: " + ChatColor.GREEN + format(getEnergyPerHitBonus()) + ChatColor.DARK_GREEN + " > " + ChatColor.GREEN +
                    format(getEnergyPerHitBonus() * (getEnergyPerHitBonus() > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative())));
        }
        upgradeLore.addAll(Arrays.asList(
                "",
                ChatColor.GREEN + "Skill Boost (" + selectedSkillBoost.name + "):",
                ChatColor.GRAY + WordWrap.wrapWithNewline("1 Free Ability Upgrade", 175)
        ));
        if (getSkillCritChanceBonus() != 0) {
            upgradeLore.add(ChatColor.GRAY + "Skill Crit Chance: " + ChatColor.GREEN + format(getSkillCritChanceBonus()) + ChatColor.DARK_GREEN + " > " + ChatColor.GREEN +
                    format(getSkillCritChanceBonus() * (getSkillCritChanceBonus() > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative())));
        }
        if (getSkillCritMultiplierBonus() != 0) {
            upgradeLore.add(ChatColor.GRAY + "Skill Crit Multiplier: " + ChatColor.GREEN + format(getSkillCritMultiplierBonus()) + ChatColor.DARK_GREEN + " > " + ChatColor.GREEN +
                    format(getSkillCritMultiplierBonus() * (getSkillCritMultiplierBonus() > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative())));
        }
        String passiveEffect = getPassiveEffect();
        if (!passiveEffect.isEmpty()) {
            upgradeLore.addAll(Arrays.asList(
                    "",
                    ChatColor.GREEN + "Passive Effect (" + getTitleName() + "):",
                    ChatColor.GRAY + WordWrap.wrapWithNewline(passiveEffect, 175)
            ));
        }

        return upgradeLore;
    }

    @Override
    public int getUpgradeLevel() {
        return upgradeLevel;
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

    public StarPieces getStarPiece() {
        return starPiece;
    }

    public WeaponStats getStarPieceBonus() {
        return starPieceBonus;
    }

    public float getSpeedBonus() {
        float speedBonus = getSpeedBonusValue();
        speedBonus *= Math.pow(speedBonus > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative(), upgradeLevel);
        if (getStarPieceStat() == WeaponStats.SPEED_BONUS) {
            speedBonus *= getStarPieceBonusMultiplicativeValue();
        }
        return speedBonus;
    }

    public float getEnergyPerSecondBonus() {
        float energyPerSecondBonus = getEnergyPerSecondBonusValue();
        if (energyPerSecondBonus > 0) {
            energyPerSecondBonus *= Math.pow(getUpgradeMultiplier(), upgradeLevel);
        }
        if (getStarPieceStat() == WeaponStats.ENERGY_PER_SECOND_BONUS) {
            energyPerSecondBonus *= getStarPieceBonusMultiplicativeValue();
        }
        return energyPerSecondBonus;
    }

    public float getEnergyPerHitBonus() {
        float energyPerHitBonus = getEnergyPerHitBonusValue();
        if (energyPerHitBonus > 0) {
            energyPerHitBonus *= Math.pow(getUpgradeMultiplier(), upgradeLevel);
        }
        if (getStarPieceStat() == WeaponStats.ENERGY_PER_HIT_BONUS) {
            energyPerHitBonus *= getStarPieceBonusMultiplicativeValue();
        }
        return energyPerHitBonus;
    }

    public float getSkillCritChanceBonus() {
        float skillCritChanceBonus = getSkillCritChanceBonusValue();
        skillCritChanceBonus *= Math.pow(skillCritChanceBonus > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative(), upgradeLevel);
        if (getStarPieceStat() == WeaponStats.SKILL_CRIT_CHANCE_BONUS) {
            skillCritChanceBonus *= getStarPieceBonusMultiplicativeValue();
        }
        return skillCritChanceBonus;
    }

    public float getSkillCritMultiplierBonus() {
        float skillCritMultiplierBonus = getSkillCritMultiplierBonusValue();
        skillCritMultiplierBonus *= Math.pow(skillCritMultiplierBonus > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative(), upgradeLevel);
        if (getStarPieceStat() == WeaponStats.SKILL_CRIT_MULTIPLIER_BONUS) {
            skillCritMultiplierBonus *= getStarPieceBonusMultiplicativeValue();
        }
        return skillCritMultiplierBonus;
    }

    public abstract String getPassiveEffect();

    protected abstract float getMeleeDamageMaxValue();

    @Override
    public void generateStats() {

    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);
        player.getSpeed().addBaseModifier(getSpeedBonus());

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

        resetAbility();
        AbstractAbility ability = getAbility();
        if (ability != null) {
            ability.updateDescription(null);
            new GameRunnable(player.getGame()) {

                @Override
                public void run() {
                    if (ability.getCurrentCooldown() > 0) {
                        ability.subtractCooldown(.05f);
                        if (player.getEntity() instanceof Player) {
                            updateAbilityItem(player, (Player) player.getEntity());
                        }
                    }
                }
            }.runTaskTimer(20, 0);
        }
    }

    @Override
    public float getHealthBonus() {
        float healthBonus = getHealthBonusValue();
        healthBonus *= Math.pow(healthBonus > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative(), upgradeLevel);
        if (getStarPieceStat() == WeaponStats.HEALTH_BONUS) {
            healthBonus *= getStarPieceBonusMultiplicativeValue();
        }
        return healthBonus;
    }

    @Override
    public WeaponsPvE getRarity() {
        return WeaponsPvE.LEGENDARY;
    }

    @Override
    public int getMeleeDamageRange() {
        return 0;
    }

    @Override
    public float getMeleeDamageMin() {
        float meleeDamageMin = getMeleeDamageMinValue();
        meleeDamageMin *= Math.pow(meleeDamageMin > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative(), upgradeLevel);
        if (getStarPieceStat() == WeaponStats.MELEE_DAMAGE) {
            meleeDamageMin *= getStarPieceBonusMultiplicativeValue();
        }
        return meleeDamageMin;
    }

    @Override
    public float getMeleeDamageMax() {
        return getMeleeDamageMaxValue() + getMeleeDamageMin() - getMeleeDamageMinValue();
    }

    @Override
    public float getCritChance() {
        float critChance = getCritChanceValue();
        //critChance *= Math.pow(critChance > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative(), upgradeLevel);
        if (getStarPieceStat() == WeaponStats.CRIT_CHANCE) {
            critChance *= getStarPieceBonusMultiplicativeValue();
        }
        return critChance;
    }

    @Override
    public float getCritMultiplier() {
        float critMultiplier = getCritMultiplierValue();
        //critMultiplier *= Math.pow(critMultiplier > 0 ? getUpgradeMultiplier() : getUpgradeMultiplierNegative(), upgradeLevel);
        if (getStarPieceStat() == WeaponStats.CRIT_MULTIPLIER) {
            critMultiplier *= getStarPieceBonusMultiplicativeValue();
        }
        return critMultiplier;
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
    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        if (getSpeedBonus() != 0) {
            lore.add(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + format(getSpeedBonus()) + "%" + getStarPieceBonusString(WeaponStats.SPEED_BONUS));
        }
        if (getEnergyPerSecondBonus() != 0) {
            lore.add(ChatColor.GRAY + "Energy per Second: " + ChatColor.GREEN + format(getEnergyPerSecondBonus()) + getStarPieceBonusString(WeaponStats.ENERGY_PER_SECOND_BONUS));
        }
        if (getEnergyPerHitBonus() != 0) {
            lore.add(ChatColor.GRAY + "Energy per Hit: " + ChatColor.GREEN + format(getEnergyPerHitBonus()) + getStarPieceBonusString(WeaponStats.ENERGY_PER_HIT_BONUS));
        }
        lore.addAll(Arrays.asList(
                "",
                ChatColor.GREEN + "Skill Boost (" + selectedSkillBoost.name + "):",
                ChatColor.GRAY + WordWrap.wrapWithNewline("1 Free Ability Upgrade", 175)
        ));
        if (getSkillCritChanceBonus() != 0) {
            lore.add(ChatColor.GRAY + "Skill Crit Chance: " + ChatColor.GREEN + format(getSkillCritChanceBonus()) + "%" + getStarPieceBonusString(WeaponStats.SKILL_CRIT_CHANCE_BONUS));
        }
        if (getSkillCritMultiplierBonus() != 0) {
            lore.add(ChatColor.GRAY + "Skill Crit Multiplier: " + ChatColor.GREEN + format(getSkillCritMultiplierBonus()) + "%" + getStarPieceBonusString(
                    WeaponStats.SKILL_CRIT_MULTIPLIER_BONUS));
        }
        String passiveEffect = getPassiveEffect();
        if (!passiveEffect.isEmpty()) {
            lore.addAll(Arrays.asList(
                    "",
                    ChatColor.GREEN + "Passive Effect (" + getTitleName() + "):",
                    ChatColor.GRAY + WordWrap.wrapWithNewline(passiveEffect, 175)
            ));
        }

        return lore;
    }

    @Override
    public List<String> getLoreAddons() {
        return Collections.singletonList(ChatColor.LIGHT_PURPLE + "Upgrade Level [" + getUpgradeLevel() + "/" + getMaxUpgradeLevel() + "]");
    }

    @Override
    public String getName() {
        if (getTitleName().isEmpty()) {
            return super.getName();
        } else {
            return ChatColor.GOLD + getTitleName() + " " + super.getName();
        }
    }

    public String getTitleName() {
        return this.getTitle().name;
    }

    public LegendaryTitles getTitle() {
        return LegendaryTitles.NONE;
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.GOLD;
    }

    public void activateAbility(WarlordsPlayer wp, Player player, boolean hotkeyMode) {
        if (getAbility() == null) {
            return;
        }
        if (!wp.isActive()) {
            return;
        }

        if (wp.isDead()) {
            return;
        }
        if (!wp.getGame().isFrozen()) {
            wp.getSpec().onRightClickAbility(getAbility(), wp, player);
        }
        if (hotkeyMode) {
            player.getInventory().setHeldItemSlot(0);
        }
    }

    public AbstractAbility getAbility() {
        return null;
    }

    public void resetAbility() {

    }

    public void updateAbilityItem(WarlordsPlayer warlordsPlayer, Player player) {
        if (getAbility() != null) {
            warlordsPlayer.updateItem(player, 8, getAbility(), ABILITY_ITEM);
        }
    }

    public String getStarPieceBonusString(WeaponStats weaponStats) {
        return getStarPieceStat() == weaponStats ? getStarPieceBonusString() : "";
    }

    @Override
    public List<WeaponStats> getRandomStatBonus() {
        List<WeaponStats> randomStatBonus = new ArrayList<>();
        if (getMeleeDamageMinValue() > 0) {
            randomStatBonus.add(WeaponStats.MELEE_DAMAGE);
        }
        if (getCritChanceValue() > 0) {
            randomStatBonus.add(WeaponStats.CRIT_CHANCE);
        }
        if (getCritMultiplierValue() > 0) {
            randomStatBonus.add(WeaponStats.CRIT_MULTIPLIER);
        }
        if (getHealthBonusValue() > 0) {
            randomStatBonus.add(WeaponStats.HEALTH_BONUS);
        }
        if (getSpeedBonusValue() > 0) {
            randomStatBonus.add(WeaponStats.SPEED_BONUS);
        }
        if (getEnergyPerSecondBonusValue() > 0) {
            randomStatBonus.add(WeaponStats.ENERGY_PER_SECOND_BONUS);
        }
        if (getEnergyPerHitBonusValue() > 0) {
            randomStatBonus.add(WeaponStats.ENERGY_PER_HIT_BONUS);
        }
        if (getSkillCritChanceBonusValue() > 0) {
            randomStatBonus.add(WeaponStats.SKILL_CRIT_CHANCE_BONUS);
        }
        if (getSkillCritMultiplierBonusValue() > 0) {
            randomStatBonus.add(WeaponStats.SKILL_CRIT_MULTIPLIER_BONUS);
        }
        return randomStatBonus;
    }

    protected abstract float getMeleeDamageMinValue();

    protected abstract float getCritChanceValue();

    protected abstract float getCritMultiplierValue();

    protected abstract float getHealthBonusValue();

    protected float getSpeedBonusValue() {
        return 0;
    }

    protected float getEnergyPerSecondBonusValue() {
        return 0;
    }

    protected float getEnergyPerHitBonusValue() {
        return 0;
    }

    protected float getSkillCritChanceBonusValue() {
        return 0;
    }

    protected float getSkillCritMultiplierBonusValue() {
        return 0;
    }

    @Override
    public int getStarPieceBonusValue() {
        return this.titles.computeIfAbsent(getTitle(), t -> new LegendaryWeaponTitleInfo()).getStarPiece().starPieceBonusValue;
    }

    public WeaponStats getStarPieceStat() {
        return this.titles.computeIfAbsent(getTitle(), t -> new LegendaryWeaponTitleInfo()).getStarPieceStat();
    }

    public void setStarPiece(StarPieces starPiece, WeaponStats starPieceBonus) {
        this.titles.computeIfAbsent(getTitle(), t -> new LegendaryWeaponTitleInfo()).setStarPieceInfo(starPiece, starPieceBonus);
    }
}
