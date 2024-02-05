package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilities.SoulSwitch;
import com.ebicep.warlords.pve.upgrades.*;

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

        masterUpgrade = new Upgrade(
                "Soul Burst",
                "Soul Switch - Master Upgrade",
                """
                        While swapping and upon landing, gain 50% damage reduction and become invisible for 5s.
                        Additionally, at the start and end locations of the swap, increase movement speed by 25% for self and allies within a 3-block radius for 3s (The Animus is not considered an ally in this instance).
                        """,
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Tricky Switch",
                "Soul Switch - Master Upgrade",
                """
                        While the Animus is active, increase crit chance by 15%. For every kill the Animus earns, gain 10 energy and 10% of the damage dealt on the killing hit as self healing.
                        """,
                50000,
                () -> {
                    ability.setBlindnessTicks(ability.getBlindnessTicks() + 30);
                    ability.setDecoyMaxTicksLived(ability.getDecoyMaxTicksLived() + 40);
                }
        );
    }
}
