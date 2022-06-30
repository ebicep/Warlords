package com.ebicep.warlords.pve.upgrades.pyromancer;

import com.ebicep.warlords.abilties.Fireball;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class FireballBranch extends UpgradeBranch<Fireball> {

    public FireballBranch(AbilityTree abilityTree, Fireball ability, ItemStack itemStack, String itemName) {
        super(abilityTree, ability, itemStack, itemName);
        treeA.add(new Upgrade("Energy - Tier I", "-10 Energy cost", 5000));
        treeA.add(new Upgrade("Energy - Tier II", "-20 Energy cost", 10000));
        treeA.add(new Upgrade("Energy - Tier III", "-30 Energy cost", 20000));

        treeB.add(new Upgrade("Utility - Tier I", "+1 Projectile", 5000));
        treeB.add(new Upgrade("Utility - Tier II", "+2 Projectiles", 10000));
        treeB.add(new Upgrade("Utility - Tier III", "+3 Projectiles", 20000));

        treeC.add(new Upgrade("Speed - Tier I", "+20% Projectile speed", 5000));
        treeC.add(new Upgrade("Speed - Tier II", "+40% Projectile speed", 10000));
        treeC.add(new Upgrade("Speed - Tier III", "+80% Projectile speed", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Direct hits apply the BURN status for 5 seconds.\n\nBURN: Enemies take 20% more damage from all sources\nand burn for 1% of their max health every second",
                500000
        );
    }

    @Override
    public void a1() {
        ability.setEnergyCost(ability.getEnergyCost() - 10);
    }

    @Override
    public void a2() {
        ability.setEnergyCost(ability.getEnergyCost() - 10);
    }

    @Override
    public void a3() {
        ability.setEnergyCost(ability.getEnergyCost() - 10);
    }

    @Override
    public void b1() {
        ability.setShotsFiredAtATime(2);
    }

    @Override
    public void b2() {
        ability.setShotsFiredAtATime(3);
    }

    @Override
    public void b3() {
        ability.setShotsFiredAtATime(4);
    }

    double projectileSpeed = ability.getProjectileSpeed();
    @Override
    public void c1() {
        ability.setProjectileSpeed(projectileSpeed * 1.2);
    }

    @Override
    public void c2() {
        ability.setProjectileSpeed(projectileSpeed * 1.4);
    }

    @Override
    public void c3() {
        ability.setProjectileSpeed(projectileSpeed * 1.8);
    }

    @Override
    public void master() {
        ability.setPveUpgrade(true);
    }
}
