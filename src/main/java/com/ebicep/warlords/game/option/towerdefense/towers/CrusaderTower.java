package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseUtils;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeCategory;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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

        upgrades.add(TowerUpgrade.TowerUpgradeBuilder
                .create(1, 850)
                .addUpgradeCategory(TowerUpgradeCategory.TowerUpgradeCategoryBuilder
                        .create(strikeAttack)
                        .damage(5)
                )
                .build()
        );
        upgrades.add(TowerUpgrade.TowerUpgradeBuilder
                .create(2, 1200)
                .addUpgradeCategory(TowerUpgradeCategory.TowerUpgradeCategoryBuilder
                        .create(strikeAttack)
                        .damage(10)
                )
                .build()
        );
        upgrades.add(TowerUpgrade.TowerUpgradeBuilder
                .create(3, 1650)
                .addUpgradeCategory(TowerUpgradeCategory.TowerUpgradeCategoryBuilder
                        .create(strikeAttack)
                        .damage(15)
                )
                .build()
        );
        upgrades.add(TowerUpgrade.TowerUpgradeBuilder
                .create(4, 1500)
                .addUpgradeCategory(TowerUpgradeCategory.TowerUpgradeCategoryBuilder
                        .create(strikeAttack)
                        .range(10)
                )
                .addUpgradeCategory(TowerUpgradeCategory.TowerUpgradeCategoryBuilder
                        .create(buffTowers)
                        .range(10)
                )
                .build()
        );
        upgrades.add(TowerUpgrade.TowerUpgradeBuilder
                .create(4, 1500)
                .addUpgradeCategory(TowerUpgradeCategory.TowerUpgradeCategoryBuilder
                        .create(buffTowers)
                        .value("Buff", 10, BuffTowers::getBuffValue)
                )
                .build()
        );
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

    private static class BuffTowers extends AbstractAbility implements TDAbility, HitBox {

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

    private static class StrikeAttack extends AbstractAbility implements TDAbility, HitBox, Damages<StrikeAttack.DamageValues> {

        private static final ItemStack SWORD_ITEM = new ItemStack(Material.IRON_SWORD);
        private final FloatModifiable range = new FloatModifiable(30);

        public StrikeAttack() {
            super("Strike Attack", 5, 5, .9f, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                AbstractTower tower = warlordsTower.getTower();
                tower.getEnemyMobs(range).forEach(warlordsNPC -> {
                    TowerDefenseUtils.playSwordStrikeAnimation(warlordsTower, warlordsNPC, SWORD_ITEM);
                    warlordsNPC.addInstance(InstanceBuilder
                            .damage()
                            .ability(this)
                            .source(warlordsTower)
                            .value(damageValues.strikeDamage)
                            .flags(InstanceFlags.TD_PHYSICAL)
                    );
                });
            }
            return true;
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

        private final DamageValues damageValues = new DamageValues();

        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.SetValue strikeDamage = new Value.SetValue(5);
            private final List<Value> values = List.of(strikeDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }


}
