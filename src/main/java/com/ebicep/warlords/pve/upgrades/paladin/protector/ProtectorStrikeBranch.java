package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilities.ProtectorsStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class ProtectorStrikeBranch extends AbstractUpgradeBranch<ProtectorsStrike> {

    @Override
    public void runOnce() {
        ability.getMinDamageHeal().addMultiplicativeModifierAdd("PvE", .3f);
        ability.getMaxDamageHeal().addMultiplicativeModifierAdd("PvE", .3f);
    }

    public ProtectorStrikeBranch(AbilityTree abilityTree, ProtectorsStrike ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability, 7.5f)
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
                    ability.getMinDamageHeal().addMultiplicativeModifierAdd("Master Upgrade Branch", .2f);
                    ability.getMaxDamageHeal().addMultiplicativeModifierAdd("Master Upgrade Branch", .2f);
                    ability.setCritChance(ability.getCritChance() + 15);
                    ability.setStrikeRadius(ability.getStrikeRadius() * 2);
                    ability.setMaxAllies(ability.getMaxAllies() + 1);
                }
        );
    }
}
