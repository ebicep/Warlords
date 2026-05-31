package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilities.Soulbinding;
import com.ebicep.warlords.pve.upgrades.*;

public class SoulbindingWeaponBranch extends AbstractUpgradeBranch<Soulbinding> {

    @Override
    public void runOnce() {
        ability.setAllyCooldownReduction(.5f);
    }

    public SoulbindingWeaponBranch(AbilityTree abilityTree, Soulbinding ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DurationUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "s Bind Duration";
                    }

                    @Override
                    public void run(float value) {
                        ability.setBindDuration(ability.getBindDuration() + (int) value);
                    }
                }, 10f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Curse Binding",
                "Soulbinding Weapon - Master Upgrade",
                """
                        Soulbinding weapon's buffs affect 2 additional allies.
                        
                        Gain 1 energy for each soulbound target hit by Fallen Souls and Spirit Link, increase your own and the allied ability cooldown reduction by 0.3s
                        """,
                50000,
                () -> {
                    ability.setSelfCooldownReduction(ability.getSelfCooldownReduction() + 0.3f);
                    ability.setAllyCooldownReduction(ability.getAllyCooldownReduction() + 0.3f);
                    ability.setMaxAlliesHit(ability.getMaxAlliesHit() + 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Chain of Custody",
                "Soulbinding Weapon - Master Upgrade",
                """
                        When a BOUND enemy dies, BOUND jumps to the nearest unbound enemy within 6 blocks.
        
                        Each jump releases a spirit wave from the fallen enemy, reducing nearby allies' cooldowns by 0.1s and slowing nearby enemies by 25% for 3s.
        
                        BOUND can jump up to 3 times per Soulbinding cast.
                        """,
                50000,
                () -> {
                }
        );
    }
}
