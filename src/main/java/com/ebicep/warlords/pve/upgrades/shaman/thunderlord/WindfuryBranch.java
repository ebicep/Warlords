package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilities.WindfuryWeapon;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.JudgementStrikeBranch;

public class WindfuryBranch extends AbstractUpgradeBranch<WindfuryWeapon> {

    float weaponDamage = ability.getWeaponDamage();

    public WindfuryBranch(AbilityTree abilityTree, WindfuryWeapon ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Windfury Damage";
                    }

                    @Override
                    public void run(float value) {
                        ability.setWeaponDamage(weaponDamage + value);
                    }
                }, 25f, 1, 2)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Windfury Damage";
                    }

                    @Override
                    public void run(float value) {
                        ability.setWeaponDamage(weaponDamage + value);
                    }
                }, 100f, 3)
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Windfury Damage";
                    }

                    @Override
                    public void run(float value) {
                        ability.setWeaponDamage(weaponDamage + value);
                    }
                }, 150f, 4)
                .addUpgradeCooldown(ability, .2f, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.LuckUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Proc Chance";
                    }

                    @Override
                    public void run(float value) {
                        ability.setProcChance(ability.getProcChance() + value);
                    }

                    @Override
                    public boolean autoScaleEffect() {
                        return false;
                    }
                }, 2f, 1, 2, 3)
                .addUpgrade(new UpgradeTypes.LuckUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Proc Chance";
                    }

                    @Override
                    public void run(float value) {
                        ability.setProcChance(ability.getProcChance() + value);
                    }

                    @Override
                    public boolean autoScaleEffect() {
                        return false;
                    }
                }, 10f, 4)
                .addUpgrade(new UpgradeTypes.LuckUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Windfury Hit";
                    }

                    @Override
                    public void run(float value) {
                        ability.setMaxHits(ability.getMaxHits() + (int) value);
                    }

                    @Override
                    public boolean autoScaleEffect() {
                        return false;
                    }
                }, 1f, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Shredding Fury",
                "Windfury - Master Upgrade",
                """
                        Each hit deals 1% of the target's max health as bonus damage.

                        Hits on an enemy will permanently reduce their damage reduction by 2% for each additional Windfury proc.""",
                50000,
                () -> {
                    abilityTree.getWarlordsPlayer().getCooldownManager().addCooldown(new PermanentCooldown<>(
                            "Shredding Fury",
                            null,
                            WindfuryBranch.class,
                            null,
                            abilityTree.getWarlordsPlayer(),
                            CooldownTypes.MASTERY,
                            cm -> {},
                            false
                    ) {
                        @Override
                        public float addDamageAfterAllModificationsBeforeShield(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            if (event.getCause().equals("Windfury Weapon")) {
                                return currentDamageValue + DamageCheck.clamp(event.getWarlordsEntity().getMaxHealth() * 0.01f);
                            }
                            return currentDamageValue;
                        }
                    });
                }
        );
        masterUpgrade2 = new Upgrade(
                "Elemental Fury",
                "Windfury - Master Upgrade",
                """
                        +15% Proc chance
                        
                        For every Windfury proc, increase movement speed by 2.5% and reduce damage taken by 2.5% for the duration of Windfury, max 25% and 15% respectively.
                        """,
                50000,
                () -> {
                    ability.setProcChance(ability.getProcChance() + 15);
                }
        );
    }

}
