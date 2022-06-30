package com.ebicep.warlords.pve.upgrades.pyromancer;

import com.ebicep.warlords.abilties.ArcaneShield;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class ArcaneShieldBranch extends UpgradeBranch<ArcaneShield> {

    public ArcaneShieldBranch(AbilityTree abilityTree, ArcaneShield ability, ItemStack itemStack, String itemName) {
        super(abilityTree, ability, itemStack, itemName);
        treeA.add(new Upgrade("Cooldown - Tier I", "-15% Cooldown reduction", 5000));
        treeA.add(new Upgrade("Cooldown - Tier II", "-30% Cooldown reduction", 10000));
        treeA.add(new Upgrade("Cooldown - Tier III", "-60% Cooldown reduction", 20000));

        treeC.add(new Upgrade("Absorption - Tier I", "+15% Max shield health", 5000));
        treeC.add(new Upgrade("Absorption - Tier II", "+30% Max shield health", 10000));
        treeC.add(new Upgrade("Absorption - Tier III", "+60% Max shield health", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Increase duration by 100%",
                500000
        );
    }

    float cooldown = ability.getCooldown();

    @Override
    public void a1() {
        ability.setCooldown(cooldown * 0.85f);
    }

    @Override
    public void a2() {
        ability.setCooldown(cooldown * 0.7f);
    }

    @Override
    public void a3() {
        ability.setCooldown(cooldown * 0.4f);
    }

    @Override
    public void b1() {

    }

    @Override
    public void b2() {

    }

    @Override
    public void b3() {

    }

    @Override
    public void c1() {
        ability.setShieldPercentage(65);
        ability.updateDescription(null);
    }

    @Override
    public void c2() {
        ability.setShieldPercentage(80);
        ability.updateDescription(null);
    }

    @Override
    public void c3() {
        ability.setShieldPercentage(110);
        ability.updateDescription(null);
    }

    @Override
    public void master() {
        ability.setDuration(ability.getDuration() * 2);
    }
}
