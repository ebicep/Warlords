package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.ImpalingStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ImpalingStrikeBranch extends AbstractUpgradeBranch<ImpalingStrike> {

    public ImpalingStrikeBranch(AbilityTree abilityTree, ImpalingStrike ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+15% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+30% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+60% Damage", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-5 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-10 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-15 Energy cost", 20000));

        treeC.add(new Upgrade("Healing - Tier I", "+2.5% Leech healing", 5000));
        treeC.add(new Upgrade("Healing - Tier II", "+5% Leech healing", 10000));
        treeC.add(new Upgrade("Healing - Tier III", "+10% Leech healing", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Impaling Strike deals damage to 2 additional enemies and inflicts leech on those targets.",
                50000
        );
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    @Override
    public void a1() {
        ability.setMinDamageHeal(minDamage * 1.15f);
        ability.setMaxDamageHeal(maxDamage * 1.15f);
    }

    @Override
    public void a2() {
        ability.setMinDamageHeal(minDamage * 1.3f);
        ability.setMaxDamageHeal(maxDamage * 1.3f);
    }

    @Override
    public void a3() {
        ability.setMinDamageHeal(minDamage * 1.6f);
        ability.setMaxDamageHeal(maxDamage * 1.6f);
    }

    int energyCost = ability.getEnergyCost();

    @Override
    public void b1() {
        ability.setEnergyCost(energyCost - 5);
    }

    @Override
    public void b2() {
        ability.setEnergyCost(energyCost - 10);
    }

    @Override
    public void b3() {
        ability.setEnergyCost(energyCost - 15);
    }

    float selfLeech = ability.getLeechSelfAmount();
    float allyLeech = ability.getLeechAllyAmount();

    @Override
    public void c1() {
        ability.setLeechSelfAmount(selfLeech + 2.5f);
        ability.setLeechAllyAmount(allyLeech + 2.5f);
    }

    @Override
    public void c2() {
        ability.setLeechSelfAmount(selfLeech + 5);
        ability.setLeechAllyAmount(allyLeech + 5);
    }

    @Override
    public void c3() {
        ability.setLeechSelfAmount(selfLeech + 10);
        ability.setLeechAllyAmount(allyLeech + 10);
    }

    @Override
    public void master() {
        ability.setPveUpgrade(true);
    }
}
