package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.LightningRod;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightningRodBranch extends AbstractUpgradeBranch<LightningRod> {

    int healthRestore = ability.getHealthRestore();
    int energyRestore = ability.getEnergyRestore();
    float cooldown = ability.getCooldown();

    public LightningRodBranch(AbilityTree abilityTree, LightningRod ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "+10 Energy given\n-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setEnergyRestore(energyRestore + 10);
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "+20 Energy given\n-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setEnergyRestore(energyRestore + 20);
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "+30 Energy given\n-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setEnergyRestore(energyRestore + 30);
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "+40 Energy given\n-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setEnergyRestore(energyRestore + 40);
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        treeB.add(new Upgrade(
                "Alleviate - Tier I",
                "+6% Healing",
                5000,
                () -> {
                    ability.setHealthRestore(healthRestore + 6);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier II",
                "+12% Healing",
                10000,
                () -> {
                    ability.setHealthRestore(healthRestore + 12);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier III",
                "+18% Healing",
                15000,
                () -> {
                    ability.setHealthRestore(healthRestore + 18);
                }
        ));
        treeB.add(new Upgrade(
                "Alleviate - Tier IV",
                "+24% Healing",
                20000,
                () -> {
                    ability.setHealthRestore(healthRestore + 24);
                }
        ));

        masterUpgrade = new Upgrade(
                "Thunderbolt",
                "Lightning Rod - Master Upgrade",
                "Lightning Rod increases damage dealt by 40% for 12 seconds after initial cast.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }


}
