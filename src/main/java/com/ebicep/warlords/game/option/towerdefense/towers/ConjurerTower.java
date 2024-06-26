package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.SoulfireBeam;
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
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
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

    private static class HexAttack extends AbstractAbility implements TDAbility, HitBox, Damages<HexAttack.DamageValues> {

        private final FloatModifiable range = new FloatModifiable(30);
        private final DamageValues damageValues = new DamageValues();

        @Override
        public DamageValues getDamageValues() {
            return damageValues;
        }

        public HexAttack() {
            super("Hex Attack", 3, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                warlordsTower.getTower().getEnemyMobs(range, 2).forEach(target -> {
                    EffectUtils.playChainAnimation(warlordsTower, target, SoulfireBeam.BEAM_ITEM, 3);
                    target.addInstance(InstanceBuilder
                            .damage()
                            .ability(this)
                            .source(warlordsTower)
                            .value(damageValues.poisonDamage)
                            .flags(InstanceFlags.TD_MAGIC)
                            .customFlag(new CustomInstanceFlags.Valued(
                                    floatModifiable -> floatModifiable.addMultiplicativeModifierMult(name + " Upgrade", .5f, 0),
                                    CustomInstanceFlags.Valued.Flag.TD_MAGIC_RES_REDUCTION
                            ), pveMasterUpgrade)
                    );
                });
            }
            return true;
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.SetValue poisonDamage = new Value.SetValue(300);
            private final List<Value> values = List.of(poisonDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }
}
