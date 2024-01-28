package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilities.SoulSwitch;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;
import com.ebicep.warlords.pve.upgrades.UpgradeTypes;

public class SoulSwitchBranch extends AbstractUpgradeBranch<SoulSwitch> {

    float minHealing;
    float maxHealing;

    @Override
    public void runOnce() {
        ability.getCooldown().setBaseValue(22);
        ability.getEnergyCost().setBaseValue(30);
        ability.setMinDamageHeal(300);
        ability.setMaxDamageHeal(500);
        ability.setCritChance(15);
        ability.setCritMultiplier(175);
    }

    public SoulSwitchBranch(AbilityTree abilityTree, SoulSwitch ability) {
        super(abilityTree, ability);
//        ability.getCooldown().addMultiplicativeModifierMult("Soul Switch Branch", 0.75f);

        minHealing = ability.getMinDamageHeal();
        maxHealing = ability.getMaxDamageHeal();

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability, .1f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "s Invisibility";
                    }

                    @Override
                    public void run(float value) {
                        ability.setInvisTicks(ability.getInvisTicks() + (int) (value * 20));
                    }
                }, 2f, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {
                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setMinDamageHeal(minHealing * value);
                        ability.setMaxDamageHeal(maxHealing * value);
                    }
                }, 5f)
                .addUpgradeHitBox(ability, 7, 4)
                .addTo(treeB);
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
