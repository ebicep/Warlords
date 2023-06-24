package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilties.GuardianBeam;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class GuardianBeamBranch extends AbstractUpgradeBranch<GuardianBeam> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float cooldown = ability.getCooldown();
    double maxDistance = ability.getMaxDistance();

    public GuardianBeamBranch(AbilityTree abilityTree, GuardianBeam ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+5% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.05f);
                    ability.setMaxDamageHeal(maxDamage * 1.05f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+10% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+15% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+20% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.2f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20% Cooldown reduction\n+15 Block range",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                    ability.setMaxDistance(maxDistance + 15);
                }
        ));

        masterUpgrade = new Upgrade(
                "Electrifying Storm",
                "Healing Rain - Master Upgrade",
                """
                        """,
                50000,
                () -> {

                }
        );
    }

}
