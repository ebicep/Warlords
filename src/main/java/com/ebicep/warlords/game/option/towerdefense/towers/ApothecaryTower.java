package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeInstance;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ApothecaryTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final StrikeAttack strikeAttack = new StrikeAttack();
    private PoisonAttack poisonAttack;

    public ApothecaryTower(Game game, UUID owner, Location location) {
        super(game, owner, location);

        warlordsTower.getAbilities().add(strikeAttack);

        TowerUpgradeInstance.Damage upgradeDamage1 = new TowerUpgradeInstance.Damage(25);
        TowerUpgradeInstance.Damage upgradeDamage2 = new TowerUpgradeInstance.Damage(25);

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
        upgrades.add(new TowerUpgrade("Attacks Heal Allies", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("??????").build();
            }
        }) {
            @Override
            public void onUpgrade() {
                strikeAttack.setPveMasterUpgrade(true);
            }
        });
        upgrades.add(new TowerUpgrade("Poison Attack", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("Adds a new poison attack.").build();
            }
        }) {
            @Override
            protected void onUpgrade() {
                warlordsTower.getAbilities().add(poisonAttack = new PoisonAttack());
            }
        });
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.APOTHECARY_TOWER;
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

    private static class StrikeAttack extends AbstractAbility implements TDAbility, HitBox {

        private final FloatModifiable range = new FloatModifiable(30);
        private final DamageValues damageValues = new DamageValues();

        public StrikeAttack() {
            super("Strike Attack", 100, 100, 3, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                AbstractTower tower = warlordsTower.getTower();
                tower.getEnemyMobs(range, 1).forEach(warlordsNPC -> {
                    warlordsNPC.addInstance(InstanceBuilder
                            .damage()
                            .ability(this)
                            .source(warlordsTower)
                            .value(damageValues.strikeDamage)
                            .flags(InstanceFlags.TD_MAGIC)
                    );
                    PlayerFilter.entitiesAround(warlordsNPC, 3, 3, 3)
                                .isAlive()
                                .excluding(warlordsNPC)
                                .forEach(warlordsEntity -> {
                                    if (warlordsEntity.isEnemy(warlordsTower)) {
                                        warlordsEntity.addInstance(InstanceBuilder
                                                .damage()
                                                .ability(this)
                                                .source(warlordsTower)
                                                .value(damageValues.strikeDamage)
                                                .flags(InstanceFlags.TD_MAGIC)
                                        );
                                    } else if (pveMasterUpgrade) {
                                        warlordsEntity.addInstance(InstanceBuilder
                                                .healing()
                                                .ability(this)
                                                .source(warlordsTower)
                                                .value(healingValues.strikeHeal)
                                                .flags(InstanceFlags.TD_MAGIC)
                                        );
                                    }
                                });
                });
            }
            return true;
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.SetValue strikeDamage = new Value.SetValue(100);
            private final List<Value> values = List.of(strikeDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }

        private final HealingValues healingValues = new HealingValues();

        public static class HealingValues implements Value.ValueHolder {

            private final Value.SetValue strikeHeal = new Value.SetValue(100);
            private final List<Value> values = List.of(strikeHeal);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }

    }

    private static class PoisonAttack extends AbstractAbility implements TDAbility, HitBox {

        private static final int POSION_TICKS = 100;
        private static final int SLOW_TICKS = 40;
        private final FloatModifiable range = new FloatModifiable(30);

        public PoisonAttack() {
            super("Poison Attack", 25, 25, 2, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                AbstractTower tower = warlordsTower.getTower();
                tower.getEnemyMobs(range).forEach(warlordsNPC -> {
                    new CooldownFilter<>(warlordsNPC, RegularCooldown.class)
                            .filterCooldownClass(PoisonAttack.class)
                            .findFirst()
                            .ifPresentOrElse(
                                    regularCooldown -> regularCooldown.setTicksLeft(POSION_TICKS),
                                    () -> warlordsNPC.getCooldownManager().addCooldown(new RegularCooldown<>(
                                            name,
                                            "POISON",
                                            PoisonAttack.class,
                                            null,
                                            wp,
                                            CooldownTypes.DEBUFF,
                                            cooldownManager -> {},
                                            POSION_TICKS,
                                            Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                                if (ticksElapsed % 20 == 0) {
                                                    warlordsNPC.addInstance(InstanceBuilder
                                                            .damage()
                                                            .ability(this)
                                                            .source(warlordsTower)
                                                            .value(damageValues.poisonDamage)
                                                            .flags(InstanceFlags.TD_MAGIC)
                                                    );
                                                }
                                            })
                                    ))
                            );
                    warlordsNPC.addSpeedModifier(warlordsTower, "Apothecary Tower Poison", -10, SLOW_TICKS, "BASE");
                });
            }
            return true;
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

        private final DamageValues damageValues = new DamageValues();

        public static class DamageValues implements Value.ValueHolder {

            private final Value.SetValue poisonDamage = new Value.SetValue(50);
            private final List<Value> values = List.of(poisonDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }

}
