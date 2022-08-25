package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilties.SoulShackle;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SoulShackleBranch extends AbstractUpgradeBranch<SoulShackle> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    int minTicks = ability.getMinSilenceDurationInTicks();
    int maxTicks = ability.getMaxSilenceDurationInTicks();
    float cooldown = ability.getCooldown();

    public SoulShackleBranch(AbilityTree abilityTree, SoulShackle ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+2.5% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.025f);
                    ability.setMaxDamageHeal(maxDamage * 1.025f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+5% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.05f);
                    ability.setMaxDamageHeal(maxDamage * 1.05f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+7.5% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.075f);
                    ability.setMaxDamageHeal(maxDamage * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+10% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
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
                "Conscience Crush",
                "Soul Shackle - Master Upgrade",
                "Soul Shackle now hits up to 5 enemies in a cone.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
