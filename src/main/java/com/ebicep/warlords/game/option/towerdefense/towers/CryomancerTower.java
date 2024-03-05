package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.AttackSpeed;
import com.ebicep.warlords.game.option.towerdefense.attributes.Damage;
import com.ebicep.warlords.game.option.towerdefense.attributes.Range;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;

public class CryomancerTower extends AbstractTower implements Damage, Range, AttackSpeed, Upgradeable.Path2 {

    private final List<FloatModifiable> damage = new ArrayList<>();
    private final List<FloatModifiable> range = new ArrayList<>();
    private final List<FloatModifiable> attackSpeed = new ArrayList<>();
    private final List<TowerUpgrade> upgrades = new ArrayList<>();

    private final FloatModifiable slowDamage = new FloatModifiable(500);
    private final FloatModifiable slowRange = new FloatModifiable(30);
    private final FloatModifiable slowAttackSpeed = new FloatModifiable(3 * 20); // 5 seconds

    public CryomancerTower(Game game, Location location) {
        super(game, location);
        range.add(slowRange);
        attackSpeed.add(slowAttackSpeed);
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
        return TowerRegistry.CRYO_TOWER;
    }

    @Override
    public void whileActive(int ticksElapsed) {
        super.whileActive(ticksElapsed);
        if (ticksElapsed % 5 == 0) {
//            EffectUtils.displayParticle(Particle.CRIMSON_SPORE, centerLocation, 5, .5, .1, .5, 2);
        }
        int attackSpeed = (int) slowAttackSpeed.getCalculatedValue();
        if (ticksElapsed % attackSpeed == 0) {
            float rangeValue = slowRange.getCalculatedValue();
            getMob(null, rangeValue, -1).forEach(warlordsNPC -> {
                EffectUtils.displayParticle(Particle.SNOWFLAKE, warlordsNPC.getLocation(), 15, .15, 0, .15, 0);
                warlordsNPC.addSpeedModifier(warlordsTower, "Cryomancer Tower Slow", -20, 20, "BASE");
            });
        }
    }


    @Override
    public List<TowerUpgrade> getUpgrades() {
        return upgrades;
    }

    @Override
    public List<FloatModifiable> getDamages() {
        return damage;
    }

    @Override
    public List<FloatModifiable> getRanges() {
        return range;
    }

    @Override
    public List<FloatModifiable> getAttackSpeeds() {
        return attackSpeed;
    }
}
