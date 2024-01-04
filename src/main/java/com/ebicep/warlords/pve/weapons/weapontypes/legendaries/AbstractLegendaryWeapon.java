package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponStats;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.StarPieceBonus;
import com.ebicep.warlords.pve.weapons.weaponaddons.Upgradeable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

import static com.ebicep.warlords.util.java.NumberFormat.formatOptionalTenths;

public abstract class AbstractLegendaryWeapon extends AbstractWeapon implements StarPieceBonus, Upgradeable {

    private static final ItemStack ABILITY_ITEM = new ItemStack(Material.NETHER_STAR);

    @Field("skill_boost")
    protected SkillBoosts selectedSkillBoost;
    @Field("unlocked_skill_boosts")
    protected List<SkillBoosts> unlockedSkillBoosts = new ArrayList<>();
    @Field("titles")
    protected Map<LegendaryTitles, LegendaryWeaponTitleInfo> titles = new HashMap<>();
    @Field("upgrade_level")
    protected int upgradeLevel = 0;
    protected boolean ascendant = false;

    @Transient
    protected WarlordsPlayer warlordsPlayer;
    @Transient
    protected PveOption pveOption;

    public AbstractLegendaryWeapon() {
    }

    public AbstractLegendaryWeapon(UUID uuid) {
        super(uuid);
        Specializations selectedSpec = PlayerSettings.getPlayerSettings(uuid).getSelectedSpec();
        List<SkillBoosts> skillBoosts = selectedSpec.skillBoosts;
        this.specialization = selectedSpec;
        this.selectedSkillBoost = skillBoosts.get(MathUtils.generateRandomValueBetweenInclusive(0, skillBoosts.size() - 1));
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
        this.titles = new HashMap<>(legendaryWeapon.getTitles());
        this.upgradeLevel = legendaryWeapon.getUpgradeLevel();
        this.ascendant = legendaryWeapon.isAscendant();
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

    public Map<LegendaryTitles, LegendaryWeaponTitleInfo> getTitles() {
        return titles;
    }

    public boolean isAscendant() {
        return ascendant;
    }

    public List<Component> getCostLore() {
        return PvEUtils.getCostLore(getCost(), "Title Cost", true);
    }

    public LinkedHashMap<Currencies, Long> getCost() {
        return new LinkedHashMap<>() {{
            put(Currencies.COIN, 50000L);
            put(Currencies.SYNTHETIC_SHARD, 1000L);
        }};
    }

    @Override
    public void upgrade() {
        this.upgradeLevel++;
        if (this.upgradeLevel == 5) {
            this.ascendant = true;
        }
    }

    @Override
    public List<Component> getUpgradeLore() {
        float minDamageUpgradeDiff = getMeleeDamageMinValue() < 0 ? 0 : getMeleeDamageMinValue() * getUpgradeMultiplier() - getMeleeDamageMinValue();
        List<Component> upgradeLore = new ArrayList<>(Arrays.asList(
                Component.text("Damage: ", NamedTextColor.GRAY)
                         .append(Component.text(formatOptionalTenths(getMeleeDamageMin()), NamedTextColor.RED))
                         .append(Component.text(" - "))
                         .append(Component.text(formatOptionalTenths(getMeleeDamageMax()), NamedTextColor.RED))
                         .append(GREEN_ARROW)
                         .append(Component.text(formatOptionalTenths(getMeleeDamageMin() * (getMeleeDamageMin() > 0 ?
                                                                                            getUpgradeMultiplier() :
                                                                                            getUpgradeMultiplierNegative())), NamedTextColor.RED
                         ))
                         .append(Component.text(" - "))
                         .append(Component.text(formatOptionalTenths(getMeleeDamageMax() + minDamageUpgradeDiff), NamedTextColor.RED)),
                Component.text("Crit Chance: ", NamedTextColor.GRAY)
                         .append(Component.text(formatOptionalTenths(getCritChance()) + "%", NamedTextColor.RED))
                         .append(GREEN_ARROW)
                         .append(Component.text(formatOptionalTenths(getCritChance()) + "%", NamedTextColor.RED)),
                Component.text("Crit Multiplier: ", NamedTextColor.GRAY)
                         .append(Component.text(formatOptionalTenths(getCritMultiplier()) + "%", NamedTextColor.RED))
                         .append(GREEN_ARROW)
                         .append(Component.text(formatOptionalTenths(getCritMultiplier()) + "%", NamedTextColor.RED)),
                Component.empty(),
                Component.text("Health: ", NamedTextColor.GRAY)
                         .append(Component.text(format(getHealthBonus()), NamedTextColor.GREEN))
                         .append(GREEN_ARROW)
                         .append(Component.text(format(getHealthBonus() * (getHealthBonus() > 0 ?
                                                                           getUpgradeMultiplier() :
                                                                           getUpgradeMultiplierNegative())), NamedTextColor.GREEN)),
                Component.text("Speed: ", NamedTextColor.GRAY)
                         .append(Component.text(format(getSpeedBonus()) + "%", NamedTextColor.GREEN))
                         .append(GREEN_ARROW)
                         .append(Component.text(format(getSpeedBonus() * getUpgradeMultiplier()) + "%", NamedTextColor.GREEN))
        ));
        if (getEnergyPerSecondBonus() != 0) {
            upgradeLore.add(Component.text("Energy per Second: ", NamedTextColor.GRAY)
                                     .append(Component.text(format(getEnergyPerSecondBonus()), NamedTextColor.GREEN))
                                     .append(GREEN_ARROW)
                                     .append(Component.text(format(getEnergyPerSecondBonus() * (getEnergyPerSecondBonus() > 0 ?
                                                                                                getUpgradeMultiplier() :
                                                                                                getUpgradeMultiplierNegative())), NamedTextColor.GREEN)));
        }
        if (getEnergyPerHitBonus() != 0) {
            upgradeLore.add(Component.text("Energy per Hit: ", NamedTextColor.GRAY)
                                     .append(Component.text(format(getEnergyPerHitBonus()), NamedTextColor.GREEN))
                                     .append(GREEN_ARROW)
                                     .append(Component.text(format(getEnergyPerHitBonus() * (getEnergyPerHitBonus() > 0 ?
                                                                                             getUpgradeMultiplier() :
                                                                                             getUpgradeMultiplierNegative())), NamedTextColor.GREEN)));
        }

        upgradeLore.addAll(Arrays.asList(
                Component.empty(),
                Component.text("Skill Boost (" + selectedSkillBoost.name + "):", NamedTextColor.GREEN),
                Component.text((isAscendant() ? 2 : 1) + " Free Ability Upgrade" + (isAscendant() ? "s" : ""), NamedTextColor.GRAY)
        ));
        if (getSkillCritChanceBonus() != 0) {
            upgradeLore.add(Component.text("Skill Crit Chance: ", NamedTextColor.GRAY)
                                     .append(Component.text(format(getSkillCritChanceBonus()), NamedTextColor.GREEN))
                                     .append(GREEN_ARROW)
                                     .append(Component.text(format(getSkillCritChanceBonus() * (getSkillCritChanceBonus() > 0 ?
                                                                                                getUpgradeMultiplier() :
                                                                                                getUpgradeMultiplierNegative())), NamedTextColor.GREEN)));
        }
        if (getSkillCritMultiplierBonus() != 0) {
            upgradeLore.add(Component.text("Skill Crit Multiplier: ", NamedTextColor.GRAY)
                                     .append(Component.text(format(getSkillCritMultiplierBonus()), NamedTextColor.GREEN))
                                     .append(GREEN_ARROW)
                                     .append(Component.text(format(getSkillCritMultiplierBonus() * (getSkillCritMultiplierBonus() > 0 ?
                                                                                                    getUpgradeMultiplier() :
                                                                                                    getUpgradeMultiplierNegative())), NamedTextColor.GREEN)));
        }

        return upgradeLore;
    }

    @Override
    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    public void setUpgradeLevel(int upgradeLevel) {
        this.upgradeLevel = upgradeLevel;
    }

    @Override
    public int getMaxUpgradeLevel() {
        return 5;
    }

    @Override
    public LinkedHashMap<Currencies, Long> getUpgradeCost(int tier) {
        LinkedHashMap<Currencies, Long> cost = new LinkedHashMap<>();
        switch (tier) {
            case 1 -> {
                cost.put(Currencies.COIN, 100_000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 7_500L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 3_000L);
            }
            case 2 -> {
                cost.put(Currencies.COIN, 250_000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 10_000L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 6_000L);
            }
            case 3 -> {
                cost.put(Currencies.COIN, 500_000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 12_500L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 9_000L);
            }
            case 4 -> {
                cost.put(Currencies.COIN, 1_000_000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 15_000L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 12_000L);
            }
            case 5 -> {
                cost.put(Currencies.COIN, 2_000_000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 20_000L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 15_000L);
                cost.put(Currencies.ASCENDANT_SHARD, 3L);
            }
        }
        return cost;
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

    @Override
    public void generateStats() {

    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        this.warlordsPlayer = player;
        this.pveOption = pveOption;

        if (this instanceof Listener listener) {
            player.getGame().registerEvents(listener);
        }

        player.getSpeed().addBaseModifier(getSpeedBonus());

        for (AbstractUpgradeBranch<?> upgradeBranch : player.getAbilityTree().getUpgradeBranches()) {
            if (upgradeBranch.getAbility().getClass().equals(selectedSkillBoost.ability)) {
                upgradeBranch.setFreeUpgrades(isAscendant() ? 2 : 1);
                break;
            }
        }

        AbstractPlayerClass playerClass = player.getSpec();
        playerClass.setEnergyPerHit(playerClass.getEnergyPerHit() + getEnergyPerHitBonus());
        playerClass.setEnergyPerSec(playerClass.getEnergyPerSec() + getEnergyPerSecondBonus());
        for (AbstractAbility ability : playerClass.getAbilities()) {
            if (ability.getClass().equals(selectedSkillBoost.ability)) {
                if (ability.getCritChance() > 0) {
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
                        ability.subtractCurrentCooldown(.05f);
                        if (player.getEntity() instanceof Player) {
                            updateAbilityItem(player, (Player) player.getEntity());
                        }
                    }
                }
            }.runTaskTimer(20, 0);
        }
        if (this instanceof PassiveCounter && ((PassiveCounter) this).constantlyUpdate()) {
            new GameRunnable(player.getGame()) {

                @Override
                public void run() {
                    updateItemCounter(player);
                }
            }.runTaskTimer(20, 10);
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
    public Component getName() {
        if (getTitleName().isEmpty()) {
            return super.getName();
        } else {
            return Component.text(getTitleName() + " ", getTextColor())
                            .append(super.getName());
        }
    }

    @Override
    public List<Component> getBaseStats() {
        return Arrays.asList(
                Component.text("Damage: ", NamedTextColor.GRAY)
                         .append(Component.text(NumberFormat.formatOptionalTenths(getMeleeDamageMin()), NamedTextColor.RED))
                         .append(Component.text(" - "))
                         .append(Component.text(NumberFormat.formatOptionalHundredths(getMeleeDamageMax()), NamedTextColor.RED))
                         .append(getStarPieceBonusString(WeaponStats.MELEE_DAMAGE)),
                Component.text("Crit Chance: ", NamedTextColor.GRAY)
                         .append(Component.text(NumberFormat.formatOptionalTenths(getCritChance()), NamedTextColor.RED))
                         .append(Component.text("%", NamedTextColor.RED))
                         .append(getStarPieceBonusString(WeaponStats.CRIT_CHANCE)),
                Component.text("Crit Multiplier: ", NamedTextColor.GRAY)
                         .append(Component.text(NumberFormat.formatOptionalTenths(getCritMultiplier()), NamedTextColor.RED))
                         .append(Component.text("%", NamedTextColor.RED))
                         .append(getStarPieceBonusString(WeaponStats.CRIT_MULTIPLIER)),
                Component.empty(),
                Component.text("Health: ", NamedTextColor.GRAY)
                         .append(Component.text(format(getHealthBonus()), NamedTextColor.GREEN))
                         .append(getStarPieceBonusString(WeaponStats.HEALTH_BONUS))
        );
    }

    @Override
    public List<Component> getLore() {
        List<Component> lore = new ArrayList<>();
        if (getSpeedBonus() != 0) {
            lore.add(Component.text("Speed: ", NamedTextColor.GRAY)
                              .append(Component.text(format(getSpeedBonus()) + "%", NamedTextColor.GREEN))
                              .append(getStarPieceBonusString(WeaponStats.SPEED_BONUS)));
        }
        if (getEnergyPerSecondBonus() != 0) {
            lore.add(Component.text("Energy per Second: ", NamedTextColor.GRAY)
                              .append(Component.text(format(getEnergyPerSecondBonus()), NamedTextColor.GREEN))
                              .append(getStarPieceBonusString(WeaponStats.ENERGY_PER_SECOND_BONUS)));
        }
        if (getEnergyPerHitBonus() != 0) {
            lore.add(Component.text("Energy per Hit: ", NamedTextColor.GRAY)
                              .append(Component.text(format(getEnergyPerHitBonus()), NamedTextColor.GREEN))
                              .append(getStarPieceBonusString(WeaponStats.ENERGY_PER_HIT_BONUS)));
        }
        lore.addAll(Arrays.asList(
                Component.empty(),
                Component.text("Skill Boost (" + selectedSkillBoost.name + "):", NamedTextColor.GREEN)
        ));
        lore.addAll(WordWrap.wrap(Component.text((isAscendant() ? 2 : 1) + " Free Ability Upgrade" + (isAscendant() ? "s" : ""), NamedTextColor.GRAY), 175));
        if (getSkillCritChanceBonus() != 0) {
            lore.add(Component.text("Skill Crit Chance: ", NamedTextColor.GRAY)
                              .append(Component.text(format(getSkillCritChanceBonus()) + "%", NamedTextColor.GREEN))
                              .append(getStarPieceBonusString(WeaponStats.SKILL_CRIT_CHANCE_BONUS)));
        }
        if (getSkillCritMultiplierBonus() != 0) {
            lore.add(Component.text("Skill Crit Multiplier: ", NamedTextColor.GRAY)
                              .append(Component.text(format(getSkillCritMultiplierBonus()) + "%", NamedTextColor.GREEN))
                              .append(getStarPieceBonusString(WeaponStats.SKILL_CRIT_MULTIPLIER_BONUS)));
        }
        TextComponent passiveEffect = getPassiveEffect();
        if (passiveEffect != null) {
            lore.addAll(Arrays.asList(
                    Component.empty(),
                    Component.text("Passive Effect (" + getTitleName() + "):", NamedTextColor.GREEN)
            ));
            lore.addAll(WordWrap.wrap(passiveEffect, 175));
        }
        return lore;
    }

    @Override
    public List<Component> getLoreAddons() {
        List<Component> loreAddons = new ArrayList<>();
        loreAddons.add(Component.text("Upgrade Level [" + getUpgradeLevel() + "/" + getMaxUpgradeLevel() + "]", NamedTextColor.LIGHT_PURPLE));
        if (getPassiveEffect() != null) {
            loreAddons.add(Component.text("Title Level [" + getTitleLevel() + "/" + getMaxUpgradeLevel() + "]", NamedTextColor.LIGHT_PURPLE));
        }
        return loreAddons;
    }

    public abstract TextComponent getPassiveEffect();

    public int getTitleLevel() {
        return this.titles.computeIfAbsent(getTitle(), t -> new LegendaryWeaponTitleInfo()).getUpgradeLevel();
    }

    public void setTitleLevel(int level) {
        this.titles.computeIfAbsent(getTitle(), t -> new LegendaryWeaponTitleInfo()).setUpgradeLevel(level);
    }

    @Override
    public TextColor getTextColor() {
        return isAscendant() ? Currencies.ASCENDANT_SHARD.textColor : NamedTextColor.GOLD;
    }

    public String getTitleName() {
        return this.getTitle().name;
    }

    public LegendaryTitles getTitle() {
        return LegendaryTitles.NONE;
    }

    protected void updateItemCounter(WarlordsPlayer player) {
        int cooldown = ((PassiveCounter) AbstractLegendaryWeapon.this).getCounter();
        int amount = cooldown > 0 ? cooldown : 1;
        if (player.getEntity() instanceof Player) {
            ItemStack item = ((Player) player.getEntity()).getInventory().getItem(0);
            if (item != null && item.getAmount() != amount) {
                item.setAmount(amount);
            }
        }
    }

    public void upgradeTitleLevel() {
        this.titles.computeIfAbsent(getTitle(), t -> new LegendaryWeaponTitleInfo()).upgrade();
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
            warlordsPlayer.updateCustomItem(player, 8, getAbility(), ABILITY_ITEM);
        }
    }

    public Component getStarPieceBonusString(WeaponStats weaponStats) {
        return getStarPieceStat() == weaponStats ? getStarPieceBonusString() : Component.empty();
    }

    @Override
    public List<WeaponStats> getRandomStatBonus() {
        List<WeaponStats> randomStatBonus = new ArrayList<>();
        if (getMeleeDamageMinValue() > 0 && getStarPieceStat() != WeaponStats.MELEE_DAMAGE) {
            randomStatBonus.add(WeaponStats.MELEE_DAMAGE);
        }
//        if (getCritChanceValue() > 0) {
//            randomStatBonus.add(WeaponStats.CRIT_CHANCE);
//        }
//        if (getCritMultiplierValue() > 0) {
//            randomStatBonus.add(WeaponStats.CRIT_MULTIPLIER);
//        }
        if (getHealthBonusValue() > 0 && getStarPieceStat() != WeaponStats.HEALTH_BONUS) {
            randomStatBonus.add(WeaponStats.HEALTH_BONUS);
        }
        if (getSpeedBonusValue() > 0 && getStarPieceStat() != WeaponStats.SPEED_BONUS) {
            randomStatBonus.add(WeaponStats.SPEED_BONUS);
        }
        if (getEnergyPerSecondBonusValue() > 0 && getStarPieceStat() != WeaponStats.ENERGY_PER_SECOND_BONUS) {
            randomStatBonus.add(WeaponStats.ENERGY_PER_SECOND_BONUS);
        }
        if (getEnergyPerHitBonusValue() > 0 && getStarPieceStat() != WeaponStats.ENERGY_PER_HIT_BONUS) {
            randomStatBonus.add(WeaponStats.ENERGY_PER_HIT_BONUS);
        }
        if (getSkillCritChanceBonusValue() > 0 && getStarPieceStat() != WeaponStats.SKILL_CRIT_CHANCE_BONUS) {
            randomStatBonus.add(WeaponStats.SKILL_CRIT_CHANCE_BONUS);
        }
        if (getSkillCritMultiplierBonusValue() > 0 && getStarPieceStat() != WeaponStats.SKILL_CRIT_MULTIPLIER_BONUS) {
            randomStatBonus.add(WeaponStats.SKILL_CRIT_MULTIPLIER_BONUS);
        }
        return randomStatBonus;
    }

    protected abstract float getMeleeDamageMinValue();

    public WeaponStats getStarPieceStat() {
        return this.titles.computeIfAbsent(getTitle(), t -> new LegendaryWeaponTitleInfo()).getStarPieceStat();
    }

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

    protected abstract float getMeleeDamageMaxValue();

    protected abstract float getCritChanceValue();

    protected abstract float getCritMultiplierValue();

    public void setStarPiece(StarPieces starPiece, WeaponStats starPieceBonus) {
        this.titles.computeIfAbsent(getTitle(), t -> new LegendaryWeaponTitleInfo()).setStarPieceInfo(starPiece, starPieceBonus);
    }

    public ItemStack getUpgradedTitleItem() {
        Component passiveEffect = getPassiveEffect();
        if (passiveEffect == null) {
            return null;
        }
        TextComponent.Builder passiveEffectUpgraded = Component.text().style(passiveEffect.style());
        List<Component> children = new ArrayList<>(passiveEffect.children());
        children.add(0, passiveEffect.children(new ArrayList<>()));
        for (Component child : children) {
            passiveEffectUpgraded.append(child);
            for (Pair<Component, Component> upgradedComponents : getPassiveEffectUpgrade()) {
                Component oldComponent = upgradedComponents.getA();
                Component newComponent = upgradedComponents.getB();
                if (child.equals(oldComponent)) {
                    passiveEffectUpgraded.append(Component.text(" > ", NamedTextColor.GREEN))
                                         .append(newComponent);
                    break;
                }
            }
        }
        List<Component> upgradeLore = new ArrayList<>();
        upgradeLore.add(Component.text("Passive Effect (" + getTitleName() + "):", NamedTextColor.GREEN));
        upgradeLore.addAll(WordWrap.wrap(passiveEffectUpgraded.build(), 175));
        upgradeLore.add(Component.empty());
        upgradeLore.add(Component.text("Title Level [" + getTitleLevel() + "/" + getMaxUpgradeLevel() + "]", NamedTextColor.LIGHT_PURPLE)
                                 .append(Component.text(" > ", NamedTextColor.GREEN))
                                 .append(Component.text("[" + getTitleLevelUpgraded() + "/" + getMaxUpgradeLevel() + "]"))
        );
        upgradeLore.addAll(getTitleUpgradeCostLore());
        return new ItemBuilder(Material.GREEN_CONCRETE)
                .name(Component.text("Confirm", NamedTextColor.GREEN))
                .lore(upgradeLore)
                .get();

    }

    public abstract List<Pair<Component, Component>> getPassiveEffectUpgrade();

    public int getTitleLevelUpgraded() {
        return this.titles.computeIfAbsent(getTitle(), t -> new LegendaryWeaponTitleInfo()).getUpgradeLevel() + 1;
    }

    public List<Component> getTitleUpgradeCostLore() {
        LinkedHashMap<Spendable, Long> upgradeCost = getTitleUpgradeCost(getTitleLevelUpgraded());
        if (upgradeCost == null) {
            return Collections.singletonList(Component.text("Unavailable!", NamedTextColor.RED));
        } else if (upgradeCost.isEmpty()) {
            return Arrays.asList(
                    Component.empty(),
                    Component.text("Max Level!", NamedTextColor.LIGHT_PURPLE)
            );
        } else {
            return PvEUtils.getCostLore(upgradeCost, "Upgrade Cost", true);
        }
    }

    public LinkedHashMap<Spendable, Long> getTitleUpgradeCost(int tier) {
        LinkedHashMap<Spendable, Long> cost = new LinkedHashMap<>();
        switch (tier) {
            case 1 -> {
                cost.put(Currencies.COIN, 500_000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 2500L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 1000L);
                cost.put(MobDrop.ZENITH_STAR, 1L);
            }
            case 2 -> {
                cost.put(Currencies.COIN, 1_000_000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 5000L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 2000L);
                cost.put(MobDrop.ZENITH_STAR, 3L);
            }
            case 3 -> {
                cost.put(Currencies.COIN, 2_000_000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 7500L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 3000L);
                cost.put(MobDrop.ZENITH_STAR, 5L);
                cost.put(Currencies.LIMIT_BREAKER, 1L);
            }
            case 4 -> {
                cost.put(Currencies.COIN, 4_000_000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 10000L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 5000L);
                cost.put(MobDrop.ZENITH_STAR, 7L);
                cost.put(Currencies.LIMIT_BREAKER, 2L);
            }
            case 5 -> {
                cost.put(Currencies.COIN, 8_000_000L);
                cost.put(Currencies.SYNTHETIC_SHARD, 15000L);
                cost.put(Currencies.LEGEND_FRAGMENTS, 8000L);
                cost.put(MobDrop.ZENITH_STAR, 9L);
                cost.put(Currencies.LIMIT_BREAKER, 3L);
            }
        }
        return cost;
    }
}
