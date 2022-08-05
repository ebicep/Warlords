package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilties.Vindicate;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class VindicateBranch extends AbstractUpgradeBranch<Vindicate> {

    int duration = ability.getVindicateDuration();
    int resistDuration = ability.getVindicateSelfDuration();
    float damageReduction = ability.getVindicateDamageReduction();

    public VindicateBranch(AbilityTree abilityTree, Vindicate ability) {

        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "",
                5000,
                () -> {
                    ability.setVindicateDamageReduction(damageReduction + 5);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "",
                10000,
                () -> {
                    ability.setVindicateDamageReduction(damageReduction + 10);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "",
                15000,
                () -> {
                    ability.setVindicateDamageReduction(damageReduction + 15);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "",
                20000,
                () -> {
                    ability.setVindicateDamageReduction(damageReduction + 20);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "",
                5000,
                () -> {

                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "",
                10000,
                () -> {

                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "",
                15000,
                () -> {

                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "",
                20000,
                () -> {

                }
        ));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Become immune to knockback. Additionally, enemies\nwho try to attack you from the front are pushed back\nand reflect the damage you would have taken back.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
