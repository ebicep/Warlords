package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilities.SoulSwitch;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;

public class SoulSwitchBranch extends AbstractUpgradeBranch<SoulSwitch> {

    @Override
    public void runOnce() {
        ability.getCooldown().setBaseValue(22);
        ability.getEnergyCost().setBaseValue(30);
        ability.setCritChance(15);
        ability.setCritMultiplier(175);
    }

    public SoulSwitchBranch(AbilityTree abilityTree, SoulSwitch ability) {
        super(abilityTree, ability);
//        ability.getCooldown().addMultiplicativeModifierMult("Soul Switch Branch", 0.75f);
//
//        UpgradeTreeBuilder
//                .create(abilityTree, this)
//                .addUpgradeCooldown(ability, .1f)
//                .addTo(treeA);
//
//        UpgradeTreeBuilder
//                .create(abilityTree, this)
//                .addUpgradeHitBox(ability, 3f)
//                .addTo(treeB);
//
//        masterUpgrade = new Upgrade(
//                "Soul Burst",
//                "Soul Switch - Master Upgrade",
//                "Double the damage you deal with the decoy and cripple all enemies hit by the decoy's explosion for 5 seconds," +
//                        " reducing their damage dealt by 50%. Additionally, heal for 10% of your missing health when swapping.",
//                50000,
//                () -> {
//
//                }
//        );
//        masterUpgrade2 = new Upgrade(
//                "Tricky Switch",
//                "Soul Switch - Master Upgrade",
//                """
//                        For the duration the decoy is active, increase movement speed by 30%. Increase blindness duration by 1.5s and increase explosion delay by 2s.
//                        """,
//                50000,
//                () -> {
//                    ability.setBlindnessTicks(ability.getBlindnessTicks() + 30);
//                    ability.setDecoyMaxTicksLived(ability.getDecoyMaxTicksLived() + 40);
//                }
//        );
    }
}
