package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeInstance;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConjurerTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final HexAttack hexAttack = new HexAttack();

    public ConjurerTower(Game game, UUID owner, Location location) {
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
        upgrades.add(new TowerUpgrade("More Damage", upgradeDamage3) {
            @Override
            public void onUpgrade() {
            }
        });
        upgrades.add(new TowerUpgrade("Partially Ignores Magic Resistance", upgradeDamage3) {
            @Override
            protected void onUpgrade() {
                hexAttack.setPveMasterUpgrade2(true);
            }
        });
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.CONJURER_TOWER;
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

    private static class HexAttack extends AbstractAbility implements HitBox {

        private final FloatModifiable range = new FloatModifiable(30);

        public HexAttack() {
            super("Hex Attack", 300, 300, 3, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                warlordsTower.getTower().getEnemyMobs(range, 2).forEach(target -> {
                    target.addDamageInstance(
                            warlordsTower,
                            name,
                            minDamageHeal,
                            maxDamageHeal,
                            critChance,
                            critMultiplier,
                            InstanceFlags.TD_PHYSICAL
                    );
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
