
package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.AttackSpeed;
import com.ebicep.warlords.game.option.towerdefense.attributes.Damage;
import com.ebicep.warlords.game.option.towerdefense.attributes.Heal;
import com.ebicep.warlords.game.option.towerdefense.attributes.Range;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AquamancerTower extends AbstractTower implements Damage, Heal, Range, AttackSpeed, Upgradeable.Path2 {

    private final List<FloatModifiable> damages = new ArrayList<>();
    private final List<FloatModifiable> heals = new ArrayList<>();
    private final List<FloatModifiable> ranges = new ArrayList<>();
    private final List<FloatModifiable> attackSpeeds = new ArrayList<>();
    private final List<TowerUpgrade> upgrades = new ArrayList<>();

    private final FloatModifiable boltDamage = new FloatModifiable(100);
    private final FloatModifiable boltHealing = new FloatModifiable(100);
    private final FloatModifiable boltRange = new FloatModifiable(30);
    private final FloatModifiable boltAttackSpeed = new FloatModifiable(1 * 20); // 5 seconds

    public AquamancerTower(Game game, UUID owner, Location location) {
        super(game, owner, location);
        damages.add(boltDamage);
        heals.add(boltHealing);
        ranges.add(boltRange);
        attackSpeeds.add(boltAttackSpeed);
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
        return TowerRegistry.AQUA_TOWER;
    }

    @Override
    public void whileActive(int ticksElapsed) {
        super.whileActive(ticksElapsed);
        if (ticksElapsed % 5 == 0) {
//            EffectUtils.displayParticle(Particle.CRIMSON_SPORE, centerLocation, 5, .5, .1, .5, 2);
        }
        int attackSpeed = (int) boltAttackSpeed.getCalculatedValue();
        if (ticksElapsed % attackSpeed == 0) {
            float rangeValue = boltRange.getCalculatedValue();
            float damageValue = this.boltDamage.getCalculatedValue();
            float healValue = this.boltHealing.getCalculatedValue();
            getEnemyMobs(rangeValue, 1).forEach(warlordsNPC -> {
                Entity npcEntity = warlordsNPC.getEntity();
                Location effectLocation = warlordsNPC.getLocation().add(0, npcEntity.getHeight(), 0);
                Utils.playGlobalSound(effectLocation, "mage.waterbolt.impact", 2, 1);
                EffectUtils.playCircularEffectAround(
                        Particle.DRIP_WATER,
                        effectLocation,
                        npcEntity.getBoundingBox().getWidthX()
                );
                warlordsNPC.addDamageInstance(
                        warlordsTower,
                        "Water",
                        damageValue,
                        damageValue,
                        0,
                        100
                );
            });
//            getAllyMob(rangeValue, 1).forEach(warlordsNPC -> {
//                Entity npcEntity = warlordsNPC.getEntity();
//                Location effectLocation = warlordsNPC.getLocation().add(0, npcEntity.getHeight(), 0);
//                Utils.playGlobalSound(effectLocation, "mage.waterbolt.impact", 2, 1);
//                EffectUtils.playCircularEffectAround(
//                        Particle.DRIP_WATER,
//                        effectLocation,
//                        npcEntity.getBoundingBox().getWidthX()
//                );
//                warlordsNPC.addHealingInstance(
//                        warlordsTower,
//                        "Water",
//                        healValue,
//                        healValue,
//                        0,
//                        100
//                );
//            });
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
    public List<FloatModifiable> getHeals() {
        return heals;
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
