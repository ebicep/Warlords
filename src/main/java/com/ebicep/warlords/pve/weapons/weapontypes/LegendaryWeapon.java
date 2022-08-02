package com.ebicep.warlords.pve.weapons.weapontypes;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.general.WeaponsRarity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.AbstractBetterWeapon;
import com.ebicep.warlords.pve.weapons.WeaponStats;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Utils;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LegendaryWeapon extends AbstractBetterWeapon {

    protected String title;
    @Field("skill_boost")
    protected SkillBoosts selectedSkillBoost;
    @Field("unlocked_skill_boosts")
    protected List<SkillBoosts> unlockedSkillBoosts = new ArrayList<>();
    @Field("energy_per_second_bonus")
    protected float energyPerSecondBonus;
    @Field("energy_per_hit_bonus")
    protected float energyPerHitBonus;

    public LegendaryWeapon() {
    }

    public LegendaryWeapon(UUID uuid) {
        super(uuid);
        Specializations selectedSpec = Warlords.getPlayerSettings(uuid).getSelectedSpec();
        List<SkillBoosts> skillBoosts = selectedSpec.skillBoosts;
        this.selectedSkillBoost = skillBoosts.get(Utils.generateRandomValueBetweenInclusive(0, skillBoosts.size() - 1));
        this.unlockedSkillBoosts.add(selectedSkillBoost);
        this.selectedWeaponSkin = Weapons.getRandomWeaponFromRarity(WeaponsRarity.LEGENDARY);
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);
        player.getSpec().setEnergyOnHit(player.getSpec().getEnergyOnHit() + getEnergyPerHitBonus());
        player.getSpec().setEnergyPerSec(player.getSpec().getEnergyPerSec() + getEnergyPerSecondBonus());
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.GOLD;
    }

    @Override
    public List<String> getLore() {
        List<String> lore = new ArrayList<>(super.getLore());
        lore.addAll(Arrays.asList(
                ChatColor.GRAY + "Energy per Second: " + ChatColor.GREEN + "+" + (starPieceBonus == WeaponStats.ENERGY_PER_SECOND_BONUS ? getStarPieceBonusMultiplicativeString(energyPerSecondBonus) + getStarPieceBonusString() : NumberFormat.formatOptionalHundredths(energyPerSecondBonus)) + "%",
                ChatColor.GRAY + "Energy per Hit: " + ChatColor.GREEN + "+" + (starPieceBonus == WeaponStats.ENERGY_PER_HIT_BONUS ? getStarPieceBonusMultiplicativeString(energyPerHitBonus) + getStarPieceBonusString() : NumberFormat.formatOptionalHundredths(energyPerHitBonus)) + "%",
                "",
                ChatColor.GREEN + Specializations.getClass(specialization).name + " (" + specialization.name + "):",
                ChatColor.GRAY + selectedSkillBoost.name + " - Description placeholder"
        ));
        return lore;
    }

    @Override
    public void generateStats() {

    }

    @Override
    public int getMeleeDamageRange() {
        return 0;
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
        return randomStatBonus;
    }

    @Override
    public void upgrade() {
        super.upgrade();
        this.energyPerSecondBonus *= getUpgradeMultiplier();
        this.energyPerHitBonus *= getUpgradeMultiplier();
    }

    @Override
    public List<String> getUpgradeLore() {
        List<String> upgradeLore = new ArrayList<>(super.getUpgradeLore());
        upgradeLore.add(ChatColor.GRAY + "Energy per Second: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalHundredths(energyPerSecondBonus) + " > " + NumberFormat.formatOptionalHundredths(energyPerSecondBonus * getUpgradeMultiplier()));
        upgradeLore.add(ChatColor.GRAY + "Energy per Hit: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalHundredths(energyPerHitBonus) + " > " + NumberFormat.formatOptionalHundredths(energyPerHitBonus * getUpgradeMultiplier()));
        return upgradeLore;
    }

    @Override
    public int getMaxUpgradeLevel() {
        return 4;
    }

    public int getEnergyPerHitBonus() {
        float amount = starPieceBonus == WeaponStats.ENERGY_PER_HIT_BONUS ? energyPerHitBonus * getStarPieceBonusMultiplicativeValue() : energyPerHitBonus;
        return Math.round(amount);
    }

    public int getEnergyPerSecondBonus() {
        float amount = starPieceBonus == WeaponStats.ENERGY_PER_SECOND_BONUS ? energyPerSecondBonus * getStarPieceBonusMultiplicativeValue() : energyPerSecondBonus;
        return Math.round(amount);
    }
}
