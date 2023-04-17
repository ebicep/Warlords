package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilties.HammerOfLight;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HammerOfLightBranch extends AbstractUpgradeBranch<HammerOfLight> {

    int duration = ability.getTickDuration();
    float cooldown = ability.getCooldown();
    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();
    float minDamage = ability.getMinDamage();
    float maxDamage = ability.getMaxDamage();

    public HammerOfLightBranch(AbilityTree abilityTree, HammerOfLight ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+7.5% Healing\n+7.5% Damage",
                5000,
                () -> {
                    ability.setMinDamage(minDamage * 1.075f);
                    ability.setMaxDamage(maxDamage * 1.075f);
                    ability.setMinDamageHeal(minHealing * 1.075f);
                    ability.setMaxDamageHeal(maxHealing * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Healing\n+15% Damage",
                10000,
                () -> {
                    ability.setMinDamage(minDamage * 1.15f);
                    ability.setMaxDamage(maxDamage * 1.15f);
                    ability.setMinDamageHeal(minHealing * 1.15f);
                    ability.setMaxDamageHeal(maxHealing * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Healing\n+22.5% Damage",
                15000,
                () -> {
                    ability.setMinDamage(minDamage * 1.225f);
                    ability.setMaxDamage(maxDamage * 1.225f);
                    ability.setMinDamageHeal(minHealing * 1.225f);
                    ability.setMaxDamageHeal(maxHealing * 1.225f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Healing\n+30% Damage",
                20000,
                () -> {
                    ability.setMinDamage(minDamage * 1.3f);
                    ability.setMaxDamage(maxDamage * 1.3f);
                    ability.setMinDamageHeal(minHealing * 1.3f);
                    ability.setMaxDamageHeal(maxHealing * 1.3f);
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
                "-20% Cooldown reduction\n+2s Duration",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                    ability.setTickDuration(duration + 40);
                }
        ));

        masterUpgrade = new Upgrade(
                "Hammer of Illusion",
                "Hammer of Light - Master Upgrade",
                "Upon activating Crown of Light, release 4 additional light rays that deal quintuple the damage to all nearby enemies and heal allies for " +
                        "the same amount.",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}
