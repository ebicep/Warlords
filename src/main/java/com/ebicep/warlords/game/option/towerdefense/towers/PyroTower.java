package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.Damages;
import com.ebicep.warlords.game.option.towerdefense.attributes.Range;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeInstance;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.ArrayList;
import java.util.List;

public class PyroTower extends AbstractTower implements Damages, Range, Upgradeable.Path1 {

    private List<FloatModifiable> damage = new ArrayList<>();
    private List<FloatModifiable> range = new ArrayList<>();
    private List<TowerUpgrade> upgrades = new ArrayList<>();

    public PyroTower(Game game, Location location) {
        super(game, location);
        damage.add(new FloatModifiable(100));
        range.add(new FloatModifiable(10));
        upgrades.add(new TowerUpgrade("Test Upgrade 1", new TowerUpgradeInstance.DamageUpgradeInstance(10)) {
            @Override
            public void onUpgrade() {

            }
        });
        upgrades.add(new TowerUpgrade("Test Upgrade 2", new TowerUpgradeInstance.DamageUpgradeInstance(10)) {
            @Override
            public void onUpgrade() {

            }
        });
        upgrades.add(new TowerUpgrade("Test Upgrade 3", new TowerUpgradeInstance.DamageUpgradeInstance(10)) {
            @Override
            public void onUpgrade() {

            }
        });
        upgrades.add(new TowerUpgrade("Test Upgrade 4", new TowerUpgradeInstance.DamageUpgradeInstance(10)) {
            @Override
            public void onUpgrade() {

            }
        });
//        upgrades.add(new TowerUpgrade("Test Upgrade 5", new TowerUpgradeInstance.DamageUpgradeInstance(10)) {
//            @Override
//            public void upgrade() {
//
//            }
//        });
//        upgrades.add(new TowerUpgrade("Test Upgrade 6", new TowerUpgradeInstance.DamageUpgradeInstance(10)) {
//            @Override
//            public void upgrade() {
//
//            }
//        });
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.PYRO_TOWER;
    }

    @Override
    public void whileActive(int ticksElapsed) {
        super.whileActive(ticksElapsed);
        if (ticksElapsed % 20 == 0) {
            float rangeValue = this.range.get(0).getCalculatedValue();
            float damageValue = this.damage.get(0).getCalculatedValue();
            getNearbyMobs(rangeValue).forEach(warlordsNPC -> {
                warlordsNPC.addDamageInstance(warlordsTower, "Flame", damageValue, damageValue, 0, 100);
                EffectUtils.playParticleLinkAnimation(centerLocation.clone().add(0, -1, 0), warlordsNPC.getLocation(), Particle.FLAME);
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
}
