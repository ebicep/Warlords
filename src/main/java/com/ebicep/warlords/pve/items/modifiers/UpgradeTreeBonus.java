package com.ebicep.warlords.pve.items.modifiers;

import com.ebicep.warlords.abilities.internal.icon.*;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsUpgradeTreeBuilderAddUpgradeEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.atomic.AtomicReference;

public enum UpgradeTreeBonus {

    UPGRADE_WEAPON(WeaponAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return upgradeDescription("Weapon", bonusCount);
        }

        @Override
        public void applyToAbilityTree(AbilityTree abilityTree, int bonusCount) {
            upgradeApplyToAbilityTree(this, abilityTree, bonusCount);
        }
    },
    UPGRADE_RED(RedAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return upgradeDescription("Red", bonusCount);
        }

        @Override
        public void applyToAbilityTree(AbilityTree abilityTree, int bonusCount) {
            upgradeApplyToAbilityTree(this, abilityTree, bonusCount);
        }
    },
    UPGRADE_PURPLE(PurpleAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return upgradeDescription("Purple", bonusCount);
        }

        @Override
        public void applyToAbilityTree(AbilityTree abilityTree, int bonusCount) {
            upgradeApplyToAbilityTree(this, abilityTree, bonusCount);
        }
    },
    UPGRADE_BLUE(BlueAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return upgradeDescription("Blue", bonusCount);
        }

        @Override
        public void applyToAbilityTree(AbilityTree abilityTree, int bonusCount) {
            upgradeApplyToAbilityTree(this, abilityTree, bonusCount);
        }
    },
    UPGRADE_ORANGE(OrangeAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return upgradeDescription("Orange", bonusCount);
        }

        @Override
        public void applyToAbilityTree(AbilityTree abilityTree, int bonusCount) {
            upgradeApplyToAbilityTree(this, abilityTree, bonusCount);
        }
    },

    COST_WEAPON(WeaponAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return costDescription("Weapon", bonusCount);
        }

        @Override
        public void applyToAbilityTree(AbilityTree abilityTree, int bonusCount) {
            costApplyToAbilityTree(this, abilityTree, bonusCount);
        }
    },
    COST_RED(RedAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return costDescription("Red", bonusCount);
        }

        @Override
        public void applyToAbilityTree(AbilityTree abilityTree, int bonusCount) {
            costApplyToAbilityTree(this, abilityTree, bonusCount);
        }
    },
    COST_PURPLE(PurpleAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return costDescription("Purple", bonusCount);
        }

        @Override
        public void applyToAbilityTree(AbilityTree abilityTree, int bonusCount) {
            costApplyToAbilityTree(this, abilityTree, bonusCount);
        }
    },
    COST_BLUE(BlueAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return costDescription("Blue", bonusCount);
        }

        @Override
        public void applyToAbilityTree(AbilityTree abilityTree, int bonusCount) {
            costApplyToAbilityTree(this, abilityTree, bonusCount);
        }
    },
    COST_ORANGE(OrangeAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return costDescription("Orange", bonusCount);
        }

        @Override
        public void applyToAbilityTree(AbilityTree abilityTree, int bonusCount) {
            costApplyToAbilityTree(this, abilityTree, bonusCount);
        }
    },

    EFFECTIVENESS_WEAPON(WeaponAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return effectivenessDescription("Weapon", bonusCount);
        }

        @Override
        public Listener registerEvents(WarlordsPlayer warlordsPlayer, int bonusCount) {
            return UpgradeTreeBonus.effectivenessRegisterToGame(this, warlordsPlayer, bonusCount);
        }
    },
    EFFECTIVENESS_RED(RedAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return effectivenessDescription("Red", bonusCount);
        }

        @Override
        public Listener registerEvents(WarlordsPlayer warlordsPlayer, int bonusCount) {
            return UpgradeTreeBonus.effectivenessRegisterToGame(this, warlordsPlayer, bonusCount);
        }
    },
    EFFECTIVENESS_PURPLE(PurpleAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return effectivenessDescription("Purple", bonusCount);
        }

        @Override
        public Listener registerEvents(WarlordsPlayer warlordsPlayer, int bonusCount) {
            return UpgradeTreeBonus.effectivenessRegisterToGame(this, warlordsPlayer, bonusCount);
        }
    },
    EFFECTIVENESS_BLUE(BlueAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return effectivenessDescription("Blue", bonusCount);
        }

        @Override
        public Listener registerEvents(WarlordsPlayer warlordsPlayer, int bonusCount) {
            return UpgradeTreeBonus.effectivenessRegisterToGame(this, warlordsPlayer, bonusCount);
        }
    },
    EFFECTIVENESS_ORANGE(OrangeAbilityIcon.class) {
        @Override
        public String getDescription(int bonusCount) {
            return effectivenessDescription("Orange", bonusCount);
        }

        @Override
        public Listener registerEvents(WarlordsPlayer warlordsPlayer, int bonusCount) {
            return UpgradeTreeBonus.effectivenessRegisterToGame(this, warlordsPlayer, bonusCount);
        }
    },

    ;

    public static final UpgradeTreeBonus[] UPGRADES = {
            UPGRADE_WEAPON,
            UPGRADE_RED,
            UPGRADE_PURPLE,
            UPGRADE_BLUE,
            UPGRADE_ORANGE
    };

    public static final UpgradeTreeBonus[] COSTS = {
            COST_WEAPON,
            COST_RED,
            COST_PURPLE,
            COST_BLUE,
            COST_ORANGE
    };

    public static final UpgradeTreeBonus[] EFFECTIVENESS = {
            EFFECTIVENESS_WEAPON,
            EFFECTIVENESS_RED,
            EFFECTIVENESS_PURPLE,
            EFFECTIVENESS_BLUE,
            EFFECTIVENESS_ORANGE
    };

    private static String upgradeDescription(String ability, int bonusCount) {
        return "+" + bonusCount + " Free " + ability + " Ability Upgrade";
    }

    private static String costDescription(String ability, int bonusCount) {
        return "-" + NumberFormat.formatOptionalTenths(bonusCount * 7.5) + "% " + ability + " Ability Upgrade Cost";
    }

    private static String effectivenessDescription(String ability, int bonusCount) {
        return "+" + (bonusCount * 5) + "% " + ability + " Ability Upgrade Effectiveness";
    }

    private static void upgradeApplyToAbilityTree(UpgradeTreeBonus upgradeTreeBonus, AbilityTree abilityTree, int bonusCount) {
        for (AbstractUpgradeBranch<?> upgradeBranch : abilityTree.getUpgradeBranches()) {
            if (!upgradeTreeBonus.abilityIconClass.isAssignableFrom(upgradeBranch.getAbility().getClass())) {
                continue;
            }
            upgradeBranch.setFreeUpgrades(upgradeBranch.getFreeUpgrades() + bonusCount);
        }
    }

    private static void costApplyToAbilityTree(UpgradeTreeBonus upgradeTreeBonus, AbilityTree abilityTree, int bonusCount) {
        for (AbstractUpgradeBranch<?> upgradeBranch : abilityTree.getUpgradeBranches()) {
            if (!upgradeTreeBonus.abilityIconClass.isAssignableFrom(upgradeBranch.getAbility().getClass())) {
                continue;
            }
            double costReduction = 1 - bonusCount * .075;
            upgradeBranch.getTreeA().forEach(upgrade -> upgrade.setCurrencyCost((int) (upgrade.getCurrencyCost() * costReduction)));
            upgradeBranch.getTreeB().forEach(upgrade -> upgrade.setCurrencyCost((int) (upgrade.getCurrencyCost() * costReduction)));
        }
    }

    private static Listener effectivenessRegisterToGame(UpgradeTreeBonus upgradeTreeBonus, WarlordsPlayer warlordsPlayer, int bonusCount) {
        Listener listener = new Listener() {
            @EventHandler
            public void onUpgradeAdd(WarlordsUpgradeTreeBuilderAddUpgradeEvent event) {
                if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                    return;
                }
                if (!upgradeTreeBonus.abilityIconClass.isAssignableFrom(event.getBuilder().getUpgradeBranch().getAbility().getClass())) {
                    return;
                }
                AtomicReference<Float> value = event.getValue();
                value.getAndUpdate(aFloat -> aFloat * (1 + bonusCount * .05f));
            }
        };
        warlordsPlayer.getGame().registerEvents(listener);
        return listener;
    }

    private final Class<? extends AbilityIcon> abilityIconClass;

    UpgradeTreeBonus(Class<? extends AbilityIcon> abilityIconClass) {
        this.abilityIconClass = abilityIconClass;
    }

    public String getDescription(int bonusCount) {
        return "";
    }

    public void applyToAbilityTree(AbilityTree abilityTree, int bonusCount) {

    }

    public Listener registerEvents(WarlordsPlayer warlordsPlayer, int bonusCount) {
        return null;
    }
}
