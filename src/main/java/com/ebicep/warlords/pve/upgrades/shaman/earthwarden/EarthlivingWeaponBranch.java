package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilities.EarthlivingWeapon;
import com.ebicep.warlords.pve.upgrades.*;

public class EarthlivingWeaponBranch extends AbstractUpgradeBranch<EarthlivingWeapon> {

    int weaponDamage = ability.getWeaponDamage();
    int maxHits = ability.getMaxHits();

    public EarthlivingWeaponBranch(AbilityTree abilityTree, EarthlivingWeapon ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Weapon Damage";
                    }

                    @Override
                    public void run(float value) {
                        ability.setWeaponDamage(weaponDamage + (int) value);
                    }
                }, 15f)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
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
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Earthliving Weapon Hit";
                    }

                    @Override
                    public void run(float value) {
                        ability.setMaxHits(maxHits + (int) value);
                    }
                }, 1f, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Loamliving Weapon",
                "Earthliving Weapon - Master Upgrade",
                "Each additional Earthliving Weapon proc on an enemy stuns them for 2 seconds. After 2 seconds they will emit a shockwave that heals all " +
                        "nearby allies for 10% of their missing health and restore energy equal to the same amount.",
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Gaianic Gift",
                "Earthliving Weapon - Master Upgrade",
                """
                        +5s Duration
                                                
                        EPH is increased by 10 for the duration of Earthliving. Additionally, the first Earthliving proc on each enemy will be a guaranteed crit.
                        """,
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() + 100);
                }
        );
    }
}
