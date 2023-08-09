package com.ebicep.warlords.pve.upgrades.arcanist.luminary;

import com.ebicep.warlords.abilities.EnergySeerLuminary;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class EnergySeerBranchLuminary extends AbstractUpgradeBranch<EnergySeerLuminary> {

    int duration = ability.getTickDuration();
    float cooldown = ability.getCooldown();

    public EnergySeerBranchLuminary(AbilityTree abilityTree, EnergySeerLuminary ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Chronos - Tier I",
                "+0.5s Duration",
                5000,
                () -> {
                    ability.setTickDuration(duration + 10);
                }
        ));
        treeA.add(new Upgrade(
                "Chronos - Tier II",
                "+1s Duration",
                10000,
                () -> {
                    ability.setTickDuration(duration + 20);
                }
        ));
        treeA.add(new Upgrade(
                "Chronos - Tier III",
                "+1.5s Duration",
                15000,
                () -> {
                    ability.setTickDuration(duration + 30);
                }
        ));
        treeA.add(new Upgrade(
                "Chronos - Tier IV",
                "+2s Duration",
                20000,
                () -> {
                    ability.setTickDuration(duration + 40);
                }
        ));

        treeB.add(new Upgrade(
                "Zeal - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Zeal - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Energizing Oracle",
                "Energy Seer - Master Upgrade",
                """
                        When your Energy Seer ends, add an additional 20% healing bonus and triple energy restored.
                        """,
                50000,
                () -> {
                    ability.setHealingIncrease(ability.getHealingIncrease() + 20);
                    ability.setEnergyRestore(ability.getEnergyRestore() * 3);
                }
        );
    }

}
