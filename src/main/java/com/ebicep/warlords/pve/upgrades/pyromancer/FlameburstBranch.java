package com.ebicep.warlords.pve.upgrades.pyromancer;

import com.ebicep.warlords.abilties.FlameBurst;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class FlameburstBranch extends UpgradeBranch<FlameBurst> {

    public FlameburstBranch(AbilityTree abilityTree, FlameBurst ability, ItemStack itemStack, String itemName) {
        super(abilityTree, ability, itemStack, itemName);
        treeA.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeA.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeA.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        treeB.add(new Upgrade("Utility - Tier I", "+1 Block splash radius", 5000));
        treeB.add(new Upgrade("Utility - Tier II", "+2 Block splash radius", 10000));
        treeB.add(new Upgrade("Utility - Tier III", "+3 Block splash radius", 20000));

        treeC.add(new Upgrade("Damage - Tier I", "+15% Damage", 5000));
        treeC.add(new Upgrade("Damage - Tier II", "+30% Damage", 10000));
        treeC.add(new Upgrade("Damage - Tier III", "+60% Damage", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Remove energy cost\n\nFlame Burst gains 0.5% Crit Chance and\n1% Crit Multiplier for each block it travels.",
                500000
        );
    }

    float cooldown = ability.getCooldown();

    @Override
    public void a1() {
        ability.setCooldown(cooldown * 0.9f);
    }

    @Override
    public void a2() {
        ability.setCooldown(cooldown * 0.8f);
    }

    @Override
    public void a3() {
        ability.setCooldown(cooldown * 0.6f);
    }

    @Override
    public void b1() {
        ability.setHitbox(6);
    }

    @Override
    public void b2() {
        ability.setHitbox(7);
    }

    @Override
    public void b3() {
        ability.setHitbox(8);
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    @Override
    public void c1() {
        ability.setMinDamageHeal(minDamage * 1.15f);
        ability.setMaxDamageHeal(maxDamage * 1.15f);
    }

    @Override
    public void c2() {
        ability.setMinDamageHeal(minDamage * 1.3f);
        ability.setMaxDamageHeal(maxDamage * 1.3f);
    }

    @Override
    public void c3() {
        ability.setMinDamageHeal(minDamage * 1.6f);
        ability.setMaxDamageHeal(maxDamage * 1.6f);
    }

    @Override
    public void master() {
        ability.setPveUpgrade(true);
    }
}
