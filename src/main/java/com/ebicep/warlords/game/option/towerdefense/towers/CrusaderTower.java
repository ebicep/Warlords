
package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.AttackSpeed;
import com.ebicep.warlords.game.option.towerdefense.attributes.Damage;
import com.ebicep.warlords.game.option.towerdefense.attributes.Range;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrusaderTower extends AbstractTower implements Damage, Range, AttackSpeed, Upgradeable.Path2 {

    private final List<FloatModifiable> damages = new ArrayList<>();
    private final List<FloatModifiable> ranges = new ArrayList<>();
    private final List<FloatModifiable> attackSpeeds = new ArrayList<>();
    private final List<TowerUpgrade> upgrades = new ArrayList<>();

    private final FloatModifiable strikeDamage = new FloatModifiable(250);
    private final FloatModifiable strikeRange = new FloatModifiable(10);
    private final FloatModifiable strikeAttackSpeed = new FloatModifiable(2 * 20); // 5 seconds

    private final FloatModifiable buffValue = new FloatModifiable(-.3f); // 30% faster
    private final FloatModifiable buffRange = new FloatModifiable(15);


    public CrusaderTower(Game game, UUID owner, Location location) {
        super(game, owner, location);
        damages.add(strikeDamage);
        ranges.add(strikeRange);
        attackSpeeds.add(strikeAttackSpeed);

        ranges.add(buffRange);
//        TowerUpgradeInstance.DamageUpgradeInstance upgradeDamage1 = new TowerUpgradeInstance.DamageUpgradeInstance(25);
//        TowerUpgradeInstance.DamageUpgradeInstance upgradeDamage2 = new TowerUpgradeInstance.DamageUpgradeInstance(25);
//
//        upgrades.add(new TowerUpgrade("Upgrade 1", upgradeDamage1) {
//            @Override
//            public void onUpgrade() {
//            }
//        });
//        upgrades.add(new TowerUpgrade("Upgrade 2", upgradeDamage2) {
//            @Override
//            public void onUpgrade() {
//            }
//        });
//        upgrades.add(new TowerUpgrade("Single Target Attack", upgradeDamage3) {
//            @Override
//            public void onUpgrade() {
//            }
//        });
//        upgrades.add(new TowerUpgrade("AOE Attack", upgradeDamage3) {});
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.AVENGER_TOWER;
    }

    @Override
    public void whileActive(int ticksElapsed) {
        super.whileActive(ticksElapsed);
        if (ticksElapsed % 5 == 0) {
//            EffectUtils.displayParticle(Particle.CRIMSON_SPORE, centerLocation, 5, .5, .1, .5, 2);
        }
        if (ticksElapsed % 20 == 0) {
            float buffRangeValue = buffRange.getCalculatedValue();
            getTowers(buffRangeValue).forEach(tower -> {
                if (tower instanceof AttackSpeed attackSpeed) {
                    attackSpeed.getAttackSpeeds()
                               .forEach(floatModifiable -> floatModifiable.addMultiplicativeModifierAdd(
                                               getTowerRegistry().name,
                                               buffValue.getCalculatedValue(),
                                               20
                                       )
                               );
                }
            });
        }
    }


    @Override
    public List<TowerUpgrade> getUpgrades() {
        return upgrades;
    }

    @Override
    public List<FloatModifiable> getDamages() {
        return damages;
    }

    @Override
    public List<FloatModifiable> getRanges() {
        return ranges;
    }

    @Override
    public List<FloatModifiable> getAttackSpeeds() {
        return attackSpeeds;
    }

}
