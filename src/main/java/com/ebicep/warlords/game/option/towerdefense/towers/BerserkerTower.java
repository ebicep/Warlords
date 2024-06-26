package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseUtils;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeInstance;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BerserkerTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final StrikeAttack strikeAttack = new StrikeAttack();
    private AOEAttack attack;

    public BerserkerTower(Game game, UUID owner, Location location) {
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
        upgrades.add(new TowerUpgrade("Inflict Bleeding", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("Bleed").build();
            }
        }) {
            @Override
            public void onUpgrade() {
                strikeAttack.setPveMasterUpgrade(true);
            }
        });
        upgrades.add(new TowerUpgrade("AOE Attack", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("AOE Attack").build();
            }
        }) {
            @Override
            protected void onUpgrade() {
                warlordsTower.getAbilities().add(attack = new AOEAttack());
            }
        });
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.BERSERKER_TOWER;
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

    private static class StrikeAttack extends AbstractAbility implements TDAbility, HitBox, Damages<StrikeAttack.DamageValues> {

        private static final ItemStack SWORD_ITEM = new ItemStack(Material.DIAMOND_SWORD);
        private final FloatModifiable range = new FloatModifiable(30);

        public StrikeAttack() {
            super("Strike Attack", 2, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                warlordsTower.getTower().getEnemyMobs(range, 1).forEach(warlordsNPC -> {
                    TowerDefenseUtils.playSwordStrikeAnimation(warlordsTower, warlordsNPC, SWORD_ITEM);
                    warlordsNPC.addInstance(InstanceBuilder
                            .damage()
                            .ability(this)
                            .source(warlordsTower)
                            .value(damageValues.strikeDamage)
                            .flags(InstanceFlags.TD_PHYSICAL)
                    );
                    if (pveMasterUpgrade) {
                        warlordsNPC.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Bleed",
                                "BLEED",
                                StrikeAttack.class,
                                null,
                                wp,
                                CooldownTypes.DEBUFF,
                                cooldownManager -> {
                                },
                                3 * 20, // TODO
                                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                    if (ticksLeft % 20 == 0) {
                                        float healthDamage = warlordsNPC.getMaxHealth() * 0.005f;
                                        healthDamage = DamageCheck.clamp(healthDamage);
                                        warlordsNPC.addInstance(InstanceBuilder
                                                .damage()
                                                .cause("Bleed")
                                                .source(wp)
                                                .value(healthDamage)
                                                .flags(InstanceFlags.DOT)
                                        );
                                    }
                                })
                        ) {
                            @Override
                            public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                                return currentHealValue * .2f;
                            }
                        });
                    }
                });
            }
            return true;
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

        private final DamageValues damageValues = new DamageValues();

        @Override
        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.SetValue strikeDamage = new Value.SetValue(250);
            private final List<Value> values = List.of(strikeDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }

    private static class AOEAttack extends AbstractAbility implements TDAbility, HitBox, Damages<AOEAttack.DamageValues> {

        private final FloatModifiable range = new FloatModifiable(10);

        public AOEAttack() {
            super("AOE Attack", 5, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                warlordsTower.getTower().getEnemyMobs(range).forEach(warlordsNPC -> {
                    warlordsNPC.addInstance(InstanceBuilder
                            .damage()
                            .ability(this)
                            .source(warlordsTower)
                            .value(damageValues.aoeDamage)
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

        @Override
        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.SetValue aoeDamage = new Value.SetValue(50);
            private final List<Value> values = List.of(aoeDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }

}
