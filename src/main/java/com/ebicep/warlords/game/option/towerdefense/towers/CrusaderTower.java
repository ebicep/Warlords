package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeInstance;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrusaderTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final BuffTowers buffTowers = new BuffTowers();
    private final StrikeAttack strikeAttack = new StrikeAttack();

    public CrusaderTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(buffTowers);
        warlordsTower.getAbilities().add(strikeAttack);

        TowerUpgradeInstance.Damage upgradeDamage1 = new TowerUpgradeInstance.Damage(25);
        TowerUpgradeInstance.Damage upgradeDamage2 = new TowerUpgradeInstance.Damage(25);
        TowerUpgradeInstance.Range upgradeRange3 = new TowerUpgradeInstance.Range(5);
        TowerUpgradeInstance.Valued upgradeBuff4 = new TowerUpgradeInstance.Valued(10) {
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
        upgrades.add(new TowerUpgrade("Upgrade 2", upgradeDamage2) {
            @Override
            public void onUpgrade() {
            }
        });
        upgrades.add(new TowerUpgrade("More Range", upgradeRange3) {
            @Override
            public void onUpgrade() {
                strikeAttack.getHitBoxRadius().addAdditiveModifier("Upgrade 3", upgradeRange3.getValue());
                buffTowers.getHitBoxRadius().addAdditiveModifier("Upgrade 3", upgradeRange3.getValue());
            }
        });
        upgrades.add(new TowerUpgrade("More Buff", upgradeBuff4) {
            @Override
            protected void onUpgrade() {
                buffTowers.getBuffValue().addAdditiveModifier("Upgrade 4", upgradeBuff4.getValue());
            }
        });
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.CRUSADER_TOWER;
    }

    @Override
    public void whileActive(int ticksElapsed) {
        super.whileActive(ticksElapsed);
        if (ticksElapsed % 5 == 0) {
//            EffectUtils.displayParticle(Particle.CRIMSON_SPORE, centerLocation, 5, .5, .1, .5, 2);
        }
    }


    @Override
    public List<TowerUpgrade> getUpgrades() {
        return upgrades;
    }

    private static class BuffTowers extends AbstractAbility implements HitBox {

        private final FloatModifiable range = new FloatModifiable(30);
        private final FloatModifiable buffValue = new FloatModifiable(30); // 30% faster

        public BuffTowers() {
            super("Buff Towers", 0, 0, 20, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                AbstractTower abstractTower = warlordsTower.getTower();
                abstractTower.getTowers(range)
                             .forEach(tower -> tower.getWarlordsTower()
                                                    .getAbilities()
                                                    .forEach(ability -> ability.getCooldown().addMultiplicativeModifierAdd(
                                                            abstractTower.getTowerRegistry().name,
                                                            -buffValue.getCalculatedValue() / 100,
                                                            (int) (getCooldownValue() * 20) + 1
                                                    ))
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

    private static class StrikeAttack extends AbstractAbility implements HitBox {

        private final FloatModifiable range = new FloatModifiable(30);

        public StrikeAttack() {
            super("Strike Attack", 250, 250, 2, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                AbstractTower tower = warlordsTower.getTower();
                tower.getEnemyMobs(range).forEach(warlordsNPC -> {

                });
            }
            return true;
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

    }


}
