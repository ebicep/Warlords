package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.GuardianBeam;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeInstance;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.bukkit.Laser;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SentinelTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final HexAttack hexAttack = new HexAttack();
    private BeamAttack beamAttack;
    private BuffTowers buffTowers;

    public SentinelTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(hexAttack);

        TowerUpgradeInstance.Damage upgradeDamage1 = new TowerUpgradeInstance.Damage(25);
        TowerUpgradeInstance.Damage upgradeDamage2 = new TowerUpgradeInstance.Damage(25);
        TowerUpgradeInstance.Damage upgradeDamage3 = new TowerUpgradeInstance.Damage(50);

        upgrades.add(new TowerUpgrade("Upgrade 1", upgradeDamage1) {
            @Override
            public void onUpgrade() {
            }
        });
        upgrades.add(new TowerUpgrade("Upgrade 2", upgradeDamage2) {
            @Override
            public void onUpgrade() {
            }
        });
        upgrades.add(new TowerUpgrade("Beam Attack", upgradeDamage3) {
            @Override
            public void onUpgrade() {
                warlordsTower.getAbilities().add(beamAttack = new BeamAttack());
            }
        });
        upgrades.add(new TowerUpgrade("Increase Range of Nearby Towers", upgradeDamage3) {
            @Override
            protected void onUpgrade() {
                warlordsTower.getAbilities().add(buffTowers = new BuffTowers());
            }
        });
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.SENTINEL_TOWER;
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

        private final FloatModifiable range = new FloatModifiable(60);

        public HexAttack() {
            super("Hex Attack", 3, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                warlordsTower.getTower().getEnemyMobs(range, 1).forEach(target -> {
                    EffectUtils.playChainAnimation(warlordsTower, target, GuardianBeam.BEAM_ITEM, 3);
                    target.addInstance(InstanceBuilder
                            .damage()
                            .ability(this)
                            .source(warlordsTower)
                            .value(damageValues.hexDamage)
                            .flags(InstanceFlags.TD_PHYSICAL)
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

    private static class BeamAttack extends AbstractAbility implements TDAbility, HitBox, Damages<BeamAttack.DamageValues> {

        private final FloatModifiable range = new FloatModifiable(60);
        private float currentTargetDamage;
        @Nullable
        private WarlordsEntity target;
        @Nullable
        private Laser.GuardianLaser laser;

        public BeamAttack() {
            super("Beam Attack", 3, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                if (target == null || target.isDead()) {
                    warlordsTower.getTower().getEnemyMobs(range, 1).forEach(warlordsNPC -> {
                        currentTargetDamage = damageValues.beamDamage.getValue();
                        target = warlordsNPC;
                    });
                } else {
                    currentTargetDamage *= 1.5f;
                }
                if (target == null) {
                    return true;
                }
                target.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(warlordsTower)
                        .value(damageValues.beamDamage)
                        .flags(InstanceFlags.TD_MAGIC)
                );
            }
            return true;
        }

        @Override
        public void runEveryTick(@org.jetbrains.annotations.Nullable WarlordsEntity warlordsEntity) {
            super.runEveryTick(warlordsEntity);
            if (target != null && target.isAlive() && warlordsEntity instanceof WarlordsTower warlordsTower) {
                resetLaser(warlordsTower);
            }
        }

        private void resetLaser(WarlordsTower warlordsTower) {
            Location topCenterLocation = warlordsTower.getTower().getTopCenterLocation();
            if (laser == null) {
                try {
                    if (target instanceof LivingEntity livingEntity) {
                        laser = new Laser.GuardianLaser(topCenterLocation, livingEntity, 25, -1);
                    } else {
                        laser = new Laser.GuardianLaser(topCenterLocation, target.getEyeLocation(), 25, -1);
                    }
                    laser.start();
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    laser.moveStart(topCenterLocation);
                    if (target instanceof LivingEntity livingEntity) {
                        laser.attachEndEntity(livingEntity);
                    } else {
                        laser.moveEnd(target.getLocation());
                    }
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
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

            private final Value.SetValue beamDamage = new Value.SetValue(50);
            private final List<Value> values = List.of(beamDamage);

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
}
