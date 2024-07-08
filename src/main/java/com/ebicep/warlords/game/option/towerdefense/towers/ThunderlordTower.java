package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.ChainLightning;
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
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ThunderlordTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final BoltAttack boltAttack = new BoltAttack();

    public ThunderlordTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(boltAttack);

        TowerUpgradeInstance.Damage upgradeDamage1 = new TowerUpgradeInstance.Damage(25);
        TowerUpgradeInstance.Damage upgradeDamage2 = new TowerUpgradeInstance.Damage(25);
        TowerUpgradeInstance.Damage upgradeDamage3 = new TowerUpgradeInstance.Damage(50);

        upgrades.add(new TowerUpgrade("Upgrade 1", upgradeDamage1) {
            @Override
            public void onUpgrade() {
//                flameDamage.addAdditiveModifier(name, upgradeDamage1.getValue());
            }
        });
        upgrades.add(new TowerUpgrade("Upgrade 2", upgradeDamage2) {
            @Override
            public void onUpgrade() {
//                flameDamage.addAdditiveModifier(name, upgradeDamage2.getValue());
            }
        });
        upgrades.add(new TowerUpgrade("Stun", upgradeDamage3) {
            @Override
            public void onUpgrade() {
                boltAttack.setPveMasterUpgrade(true);
            }
        });
        upgrades.add(new TowerUpgrade("Diminishing Chain Damage", upgradeDamage3) {});
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.THUNDERLORD_TOWER;
    }

    @Override
    public void whileActive(int ticksElapsed) {
        super.whileActive(ticksElapsed);
        if (ticksElapsed % 5 == 0) {
            EffectUtils.displayParticle(Particle.CRIMSON_SPORE, topCenterLocation, 5, .5, .1, .5, 2);
        }
    }

    @Override
    public List<TowerUpgrade> getUpgrades() {
        return upgrades;
    }

    private static class BoltAttack extends AbstractAbility implements TDAbility, HitBox, Damages<BoltAttack.DamageValues> {

        private static final int STUN_TICKS = 20;
        private final FloatModifiable range = new FloatModifiable(30);

        public BoltAttack() {
            super("Bolt Attack", 3, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                warlordsTower.getTower().getEnemyMobs(EnemyTargetPriority.FIRST, range, 1)
                             .forEach(target -> attack(warlordsTower, target));
            }
            return true;
        }

        private void attack(WarlordsTower warlordsTower, WarlordsEntity target) {
            EffectUtils.playChainAnimation(warlordsTower, target, ChainLightning.CHAIN_ITEM, 3);
            target.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(warlordsTower)
                    .value(damageValues.boltDamage)
                    .flags(InstanceFlags.TD_PHYSICAL)
            );
            if (pveMasterUpgrade) {
                if (target instanceof WarlordsPlayer warlordsPlayer) {
                    warlordsPlayer.stun();
                    new GameRunnable(warlordsPlayer.getGame()) {
                        @Override
                        public void run() {
                            warlordsPlayer.unstun();
                        }
                    }.runTaskLater(STUN_TICKS);
                } else if (target instanceof WarlordsNPC warlordsNPC) {
                    warlordsNPC.setStunTicks(STUN_TICKS);
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

            private final Value.SetValue boltDamage = new Value.SetValue(200);
            private final List<Value> values = List.of(boltDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }
}
