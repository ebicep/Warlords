package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseUtils;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgrade;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.TowerUpgradeInstance;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VindicatorTower extends AbstractTower implements Upgradeable.Path2 {

    private final List<TowerUpgrade> upgrades = new ArrayList<>();
    private final StrikeAttack strikeAttack = new StrikeAttack();

    public VindicatorTower(Game game, UUID owner, Location location) {
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
        upgrades.add(new TowerUpgrade("Silence", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("Can silence enemies").build();
            }
        }) {
            @Override
            public void onUpgrade() {
                strikeAttack.setPveMasterUpgrade(true);
            }
        });
        upgrades.add(new TowerUpgrade("Two Targets at Reduced Damage", new TowerUpgradeInstance() {
            @Override
            public Component getDescription() {
                return ComponentBuilder.create("Hit two targets at once for reduced damage").build();
            }
        }) {
            @Override
            protected void onUpgrade() {
                strikeAttack.setMobsHit(2);
                strikeAttack.getMinDamageHeal().addMultiplicativeModifierAdd(name, -.3f);
                strikeAttack.getMaxDamageHeal().addMultiplicativeModifierAdd(name, -.3f);
            }
        });
    }

    @Override
    public TowerRegistry getTowerRegistry() {
        return TowerRegistry.VINDICATOR_TOWER;
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

        private static final ItemStack SWORD_ITEM = new ItemStack(Material.STONE_SWORD);
        private final FloatModifiable range = new FloatModifiable(30);
        private int mobsHit = 1;

        public StrikeAttack() {
            super("Strike Attack", 150, 150, 4, 0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            if (wp instanceof WarlordsTower warlordsTower) {
                warlordsTower.getTower().getEnemyMobs(range, mobsHit).forEach(warlordsNPC -> {
                    TowerDefenseUtils.playSwordStrikeAnimation(warlordsTower, warlordsNPC, SWORD_ITEM);
                    warlordsNPC.addDamageInstance(
                            warlordsTower,
                            name,
                            minDamageHeal,
                            maxDamageHeal,
                            critChance,
                            critMultiplier,
                            InstanceFlags.TD_PHYSICAL
                    );
                    if (warlordsNPC.getMob() instanceof TowerDefenseMob towerDefenseMob) {
                        towerDefenseMob.getMagicResistance().addAdditiveModifier(name, -10);
                    }
                    if (pveMasterUpgrade) {
                        SoulShackle.shacklePlayer(wp, warlordsNPC, 10);
                    }
                });
            }
            return true;
        }

        @Override
        public FloatModifiable getHitBoxRadius() {
            return range;
        }

        public void setMobsHit(int mobsHit) {
            this.mobsHit = mobsHit;
        }

    }

}
