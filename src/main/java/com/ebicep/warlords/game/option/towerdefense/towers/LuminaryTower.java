package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.RayOfLight;
import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeInstance;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LuminaryTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final HexAttack hexAttack = new HexAttack();
    private final BuffTowers buffTowers = new BuffTowers();
    private final MercifulHex mercifulHex = new MercifulHex();

    public LuminaryTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(hexAttack);
        warlordsTower.getAbilities().add(buffTowers);
        warlordsTower.getAbilities().add(mercifulHex);

        TowerUpgradeInstance.Damage upgradeDamage1 = new TowerUpgradeInstance.Damage(25);
        TowerUpgradeInstance.Damage upgradeDamage2 = new TowerUpgradeInstance.Damage(25);
        TowerUpgradeInstance.Valued upgradeBuff3 = new TowerUpgradeInstance.Valued(10) {
            @Override
            public String getName() {
                return "Buff";
            }
        };
        upgrades.add(new TowerUpgrade("Upgrade 1", upgradeDamage1) {
            @Override
            public void onUpgrade() {
            }
        });
        TowerUpgradeInstance.Damage upgradeDamage4 = new TowerUpgradeInstance.Damage(50);
        TowerUpgradeInstance.Healing upgradeHealing4 = new TowerUpgradeInstance.Healing(50);
        upgrades.add(new TowerUpgrade("Upgrade 2", upgradeDamage2) {
            @Override
            public void onUpgrade() {
            }
        });
        upgrades.add(new TowerUpgrade("Increased Buff Range", upgradeDamage4) {
            @Override
            public void onUpgrade() {
                buffTowers.getBuffValue().addAdditiveModifier("Upgrade 3", upgradeBuff3.getValue());
            }
        });
        upgrades.add(new TowerUpgrade("Increase Damge and Healing", upgradeDamage4) {
            @Override
            protected void onUpgrade() {
//                hexAttack.getMinDamageHeal().addAdditiveModifier("Upgrade 4", upgradeDamage4.getValue()); TODO
//                hexAttack.getMaxDamageHeal().addAdditiveModifier("Upgrade 4", upgradeDamage4.getValue());
//                mercifulHex.getMinDamageHeal().addAdditiveModifier("Upgrade 4", upgradeHealing4.getValue());
//                mercifulHex.getMaxDamageHeal().addAdditiveModifier("Upgrade 4", upgradeHealing4.getValue());
            }
        });
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.LUMINARY_TOWER;
    }

    @Override
    public void whileActive(int ticksElapsed) {
        super.whileActive(ticksElapsed);
        if (ticksElapsed % 5 == 0) {
//            EffectUtils.displayParticle(Particle.CRIMSON_SPORE, topCenterLocation, 5, .5, .1, .5, 2);
        }
    }

    @Override
    public List<TowerUpgrade> getUpgrades() {
        return upgrades;
    }

    private static class HexAttack extends AbstractAbility implements TDAbility, HitBox, Damages<HexAttack.DamageValues> {

        private final FloatModifiable range = new FloatModifiable(45);

        public HexAttack() {
            super("Hex Attack", 3, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                warlordsTower.getTower().getAllyMobs(range, 1).forEach(target -> {
                    EffectUtils.playChainAnimation(warlordsTower, target, RayOfLight.BEAM_ITEM, 3);
                    target.addInstance(InstanceBuilder
                            .damage()
                            .ability(this)
                            .source(warlordsTower)
                            .value(damageValues.hexDamage)
                            .flags(InstanceFlags.TD_MAGIC)
                    );
                });
            }
            return true;
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

        private final DamageValues damageValues = new DamageValues();

        @Override
        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.SetValue hexDamage = new Value.SetValue(100);
            private final List<Value> values = List.of(hexDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }

    private static class BuffTowers extends AbstractAbility implements TDAbility, HitBox {

        private final FloatModifiable range = new FloatModifiable(30);
        private final FloatModifiable buffValue = new FloatModifiable(30); // 30% faster

        public BuffTowers() {
            super("Buff Towers", 20, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                AbstractTower abstractTower = warlordsTower.getTower();
                abstractTower.getTowers(range)
                             .forEach(tower -> tower
                                     .getWarlordsTower()
                                     .getAbilities()
                                     .forEach(ability -> {
                                         if (ability instanceof HitBox hitBox) {
                                             hitBox.getHitBoxRadius().addMultiplicativeModifierAdd(
                                                     abstractTower.getTowerRegistry().name,
                                                     -buffValue.getCalculatedValue() / 100,
                                                     (int) (getCooldownValue() * 20) + 1
                                             );
                                         }
                                     })
                             );
            }
            return true;
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

        public FloatModifiable getBuffValue() {
            return buffValue;
        }
    }

    private static class MercifulHex extends AbstractAbility implements TDAbility, HitBox, Heals<MercifulHex.HealingValues> {

        private final FloatModifiable range = new FloatModifiable(20);

        public MercifulHex() {
            super("Merciful Hex", 3, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                warlordsTower.getTower().getAllyMobs(range).forEach(target -> {
                    target.addInstance(InstanceBuilder
                            .healing()
                            .ability(this)
                            .source(warlordsTower)
                            .value(healingValues.hexHealing)
                    );
                });
            }
            return true;
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

        private final HealingValues healingValues = new HealingValues();

        @Override
        public HealingValues getHealValues() {
            return healingValues;
        }

        public static class HealingValues implements Value.ValueHolder {

            private final Value.SetValue hexHealing = new Value.SetValue(50);
            private final List<Value> values = List.of(hexHealing);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }

}
