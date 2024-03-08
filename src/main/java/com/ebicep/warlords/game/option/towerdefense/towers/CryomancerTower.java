package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CryomancerTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final SlowAttack slowAttack = new SlowAttack();

    public CryomancerTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(slowAttack);

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
    }

    @Override
    public List<TowerUpgrade> getUpgrades() {
        return upgrades;
    }

    private static class SlowAttack extends AbstractAbility implements HitBox {

        private final FloatModifiable range = new FloatModifiable(30);

        public SlowAttack() {
            super("Slow", 0, 0, 5, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                warlordsTower.getTower().getEnemyMobs(range).forEach(warlordsNPC -> {
                    EffectUtils.displayParticle(Particle.SNOWFLAKE, warlordsNPC.getLocation(), 15, .15, 0, .15, 0);
                    warlordsNPC.addSpeedModifier(warlordsTower, "Cryomancer Tower Slow", -20, 20, "BASE");
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
