package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilities.ProtectorsStrike;
import com.ebicep.warlords.pve.upgrades.*;

public class ProtectorStrikeBranch extends AbstractUpgradeBranch<ProtectorsStrike> {

    float minDamage;
    float maxDamage;

    @Override
    public void runOnce() {
        ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.3f);
        ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.3f);
    }

    public ProtectorStrikeBranch(AbilityTree abilityTree, ProtectorsStrike ability) {
        super(abilityTree, ability);
        minDamage = ability.getMinDamageHeal();
        maxDamage = ability.getMaxDamageHeal();
        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * value);
                        ability.setMaxDamageHeal(maxDamage * value);
                    }
                }, 7.5f)
                .addUpgradeHitBox(ability, 1, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Alleviating Strike",
                "Protector's Strike - Master Upgrade",
                "Increase the healing of Protector's Strike on the lowest health allies and you by 50%. Additionally, double the healing range and increase the ally limit by 2.",
                50000,
                () -> {
                    ability.setStrikeRadius(ability.getStrikeRadius() * 2);
                    ability.setMaxAllies(ability.getMaxAllies() + 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Protecting Strike",
                "Protector's Strike - Master Upgrade",
                """
                        Increase the damage dealt by strike by 20% and strike crit chance by 15%. Additionally, double the healing range and increase the ally limit by 1.
                        """,
                50000,
                () -> {
                    ability.multiplyMinMax(1.2f);
                    ability.setCritChance(ability.getCritChance() + 15);
                    ability.setStrikeRadius(ability.getStrikeRadius() * 2);
                    ability.setMaxAllies(ability.getMaxAllies() + 1);
                }
        );
    }
}
