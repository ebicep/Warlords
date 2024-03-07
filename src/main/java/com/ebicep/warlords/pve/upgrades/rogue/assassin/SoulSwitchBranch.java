package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilities.SoulSwitch;
import com.ebicep.warlords.pve.upgrades.*;

public class SoulSwitchBranch extends AbstractUpgradeBranch<SoulSwitch> {

    @Override
    public void runOnce() {
        ability.getCooldown().setBaseValue(22);
        ability.getEnergyCost().setBaseValue(30);
        ability.getMinDamageHeal().setBaseValue(300);
        ability.getMaxDamageHeal().setBaseValue(500);
        ability.setCritChance(15);
        ability.setCritMultiplier(175);
    }

    public SoulSwitchBranch(AbilityTree abilityTree, SoulSwitch ability) {
        super(abilityTree, ability);

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
                .addUpgradeHealing(ability, 5f)
                .addUpgradeHitBox(ability, 7, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Soul Burst",
                "Soul Switch - Master Upgrade",
                """
                        While swapping and upon landing, gain 50% damage reduction and become invisible for 5s.
                        Additionally, at the start and end locations of the swap, increase movement speed by 25% for self and allies within a 3-block radius for 3s and for every 1% speed bonus granted to the Animus, increase the damage of its Judgment Strike by 1%.
                        """,
                50000,
                () -> {

                }
        );
        masterUpgrade2 = new Upgrade(
                "Tricky Switch",
                "Soul Switch - Master Upgrade",
                """
                        While the Animus is active, increase crit chance by 15%. For every Judgment Strike dealt by the Animus, gain 10 energy and self heal for 10% of the damage dealt.
                        """,
                50000,
                () -> {
                    ability.setBlindnessTicks(ability.getBlindnessTicks() + 30);
                    ability.setDecoyMaxTicksLived(ability.getDecoyMaxTicksLived() + 40);
                }
        );
    }
}
