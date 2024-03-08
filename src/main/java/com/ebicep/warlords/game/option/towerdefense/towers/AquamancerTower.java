
package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AquamancerTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final BoltAttack boltAttack = new BoltAttack();

    public AquamancerTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(boltAttack);

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
    }


    @Override
    public List<TowerUpgrade> getUpgrades() {
        return upgrades;
    }

    private static class BoltAttack extends AbstractAbility implements HitBox {

        private final FloatModifiable range = new FloatModifiable(30);

        public BoltAttack() {
            super("Strike Attack", 250, 250, 1);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                AbstractTower tower = warlordsTower.getTower();
                tower.getEnemyMobs(range, 1).forEach(warlordsNPC -> {
                    Entity npcEntity = warlordsNPC.getEntity();
                    Location effectLocation = warlordsNPC.getLocation().add(0, npcEntity.getHeight(), 0);
                    Utils.playGlobalSound(effectLocation, "mage.waterbolt.impact", 2, 1);
                    EffectUtils.playCircularEffectAround(
                            Particle.FALLING_WATER,
                            effectLocation,
                            npcEntity.getBoundingBox().getWidthX(),
                            15
                    );
                    warlordsNPC.addDamageInstance(
                            warlordsTower,
                            "Water",
                            minDamageHeal,
                            maxDamageHeal,
                            critChance,
                            critMultiplier
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
            return true;
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

    }

}
