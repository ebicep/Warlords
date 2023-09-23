package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilities.WindfuryWeapon;
import com.ebicep.warlords.pve.upgrades.*;

public class WindfuryBranch extends AbstractUpgradeBranch<WindfuryWeapon> {

    float weaponDamage = ability.getWeaponDamage();

    public WindfuryBranch(AbilityTree abilityTree, WindfuryWeapon ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Weapon Damage";
                    }

                    @Override
                    public void run(float value) {
                        ability.setWeaponDamage(weaponDamage + value);
                    }
                }, 25f)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.LuckUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Proc Chance";
                    }

                    @Override
                    public void run(float value) {
                        ability.setProcChance(ability.getProcChance() + value);
                    }

                    @Override
                    public boolean autoScaleEffect() {
                        return false;
                    }
                }, 4f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Shredding Fury",
                "Windfury - Master Upgrade",
                """
                        Each hit deals 1% of the target's max health as bonus damage.

                        Hits on an enemy will permanently reduce their damage reduction by 2% for each additional Windfury proc.""",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Elemental Fury",
                "Windfury - Master Upgrade",
                """
                        +15% Proc chance
                         
                        Enemies damaged by Windfury are now SHOCKED for 3s.
                                                
                        SHOCKED: Enemies that are shocked have their movement speed slowed by 25% and their incoming healing reduced by 50%.
                        """,
                50000,
                () -> {
                    ability.setProcChance(ability.getProcChance() + 15);
                }
        );
    }

}
