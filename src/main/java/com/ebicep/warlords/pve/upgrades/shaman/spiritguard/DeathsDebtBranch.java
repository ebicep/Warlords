package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilties.DeathsDebt;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class DeathsDebtBranch extends AbstractUpgradeBranch<DeathsDebt> {

    int radius = ability.getDebtRadius();
    int spiteRadius = ability.getRespiteRadius();
    float cooldown = ability.getCooldown();

    public DeathsDebtBranch(AbilityTree abilityTree, DeathsDebt ability) {
        super(abilityTree, ability);
        ability.setSelfDamageInPercentPerSecond(ability.getSelfDamageInPercentPerSecond() * 0.5f);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+4 Blocks debt radius",
                5000,
                () -> {
                    ability.setDebtRadius(radius + 4);
                    ability.setRespiteRadius(spiteRadius + 4);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+6 Blocks debt radius",
                10000,
                () -> {
                    ability.setDebtRadius(radius + 6);
                    ability.setRespiteRadius(spiteRadius + 6);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+8 Blocks debt radius",
                15000,
                () -> {
                    ability.setDebtRadius(radius + 8);
                    ability.setRespiteRadius(spiteRadius + 8);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+10 Blocks debt radius",
                20000,
                () -> {
                    ability.setDebtRadius(radius + 10);
                    ability.setRespiteRadius(spiteRadius + 10);
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
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Ghoul’s Gamble",
                "Death's Debt - Master Upgrade",
                "Double the damage dealt based on damage taken after Death's Debt ends. Additionally, " +
                        "gain 50% knockback resistance while Spirit's Respite is active and reduce damage taken by an additional 40%",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                    ability.setDamagePercent(ability.getDamagePercent() * 2);
                    ability.setSelfDamageInPercentPerSecond(ability.getSelfDamageInPercentPerSecond() * 0.2f);
                }
        );
    }
}
