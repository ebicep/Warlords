package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilties.HealingTotem;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HealingTotemBranch extends AbstractUpgradeBranch<HealingTotem> {

    public HealingTotemBranch(AbilityTree abilityTree, HealingTotem ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Healing - Tier I", "+10% Healing", 5000));
        treeA.add(new Upgrade("Healing - Tier II", "+20% Healing", 10000));
        treeA.add(new Upgrade("Healing - Tier III", "+40% Healing", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+50% Duration\n\nAll enemies within the radius of Healing Totem are\nperpetually crippled, reducing their damage dealt\nby 25%",
                50000
        );
    }

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    float cooldown = ability.getCooldown();

}
