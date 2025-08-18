package com.ebicep.warlords.abilities;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.AreaEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownUtils;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.apothecary.SoothingElixirBranch;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SoothingElixir extends AbstractAbility implements RedAbilityIcon, Duration, HitBox, Damages<SoothingElixir.DamageValues>, Heals<SoothingElixir.HealingValues>, AbilityStats<SoothingElixir, SoothingElixir.SoothingElixirStats> {

    public static final ItemStack ITEM_STACK = new ItemStack(Material.CORNFLOWER);
    private final SoothingElixirStats stats = new SoothingElixirStats();
    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private double speed = 0.220;
    private double gravity = -0.008;
    private FloatModifiable puddleRadius = new FloatModifiable(5);
    private int puddleTickDuration = 80;
    private int leechStacksApplied;

    public SoothingElixir() {
        super(AbstractAbilityBuilder.create("soothingElixir").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.puddleRadius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("puddleRadius"), float.class));
        this.puddleTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("puddleTickDuration"), int.class);
        this.leechStacksApplied = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("leechStacksApplied"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Location location = wp.getLocation();
        Vector speed = wp.getLocation().getDirection().multiply(this.speed);
        Utils.spawnThrowableProjectile(
                wp.getGame(),
                Utils.spawnArmorStand(
                        location,
                        armorStand -> {
                            armorStand.getEquipment().setHelmet(ITEM_STACK);
                        }
                ),
                speed,
                gravity,
                this.speed,
                (newLoc, integer) -> {
                    Matrix4d center = new Matrix4d(newLoc);
                    for (float i = 0; i < 6; i++) {
                        double angle = Math.toRadians(i * 90) + integer * 0.3;
                        double width = 0.3D;
                        EffectUtils.displayParticle(
                                Particle.HAPPY_VILLAGER,
                                center.translateVector(newLoc.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width),
                                2,
                                0,
                                0,
                                0,
                                0
                        );
                    }
                },
                newLoc -> PlayerFilter.entitiesAroundRectangle(newLoc, .5, .5, .5).isAlive().excluding(wp).findFirstOrNull(),
                (newLoc, directHit) -> {
                    Utils.playGlobalSound(newLoc, "rogue.healingremedy.impact", 1.5f, 0.1f);
                    Utils.playGlobalSound(newLoc, Sound.BLOCK_GLASS_BREAK, 1.5f, 0.7f);
                    Utils.playGlobalSound(newLoc, "mage.waterbolt.impact", 1.5f, 0.3f);
                    float radius = puddleRadius.getCalculatedValue();
                    CircleEffect circleEffect = new CircleEffect(wp.getGame(),
                            wp.getTeam(),
                            newLoc,
                            radius,
                            new CircumferenceEffect(Particle.HAPPY_VILLAGER, Particle.DUST),
                            new AreaEffect(1, Particle.DRIPPING_WATER).particlesPerSurface(0.025)
                    );
                    BukkitTask particleTask = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), circleEffect::playEffects, 0, 2);
                    wp.getGame().registerGameTask(particleTask);
                    EffectUtils.playFirework(newLoc, FireworkEffect.builder().withColor(Color.WHITE).with(FireworkEffect.Type.BURST).build());
                    List<WarlordsEntity> teammatesHit = PlayerFilter.entitiesAround(newLoc, radius, radius, radius).aliveTeammatesOf(wp).toList();
                    for (WarlordsEntity nearEntity : teammatesHit) {
                        stats.targetsHealed++;
                        nearEntity.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.elixirHealing));
                        if (pveMasterUpgrade2) {
                            nearEntity.getCooldownManager().removeDebuffCooldowns();
                            nearEntity.getCooldownManager()
                                      .addCooldown(new RegularCooldown<>("Healing Elixir",
                                              "ELIXIR",
                                              SoothingElixir.class,
                                              new SoothingElixir(),
                                              wp,
                                              CooldownTypes.BUFF,
                                              cooldownManager -> {
                                              },
                                              4 * 20
                                      ) {

                                          @Override
                                          protected Listener getListener() {
                                              return CooldownUtils.getFullDebuffImmunityListener(nearEntity);
                                          }
                                      });
                        }
                    }
                    new GameRunnable(wp.getGame()) {

                        int timeLeft = puddleTickDuration / 20;

                        @Override
                        public void run() {
                            PlayerFilter.entitiesAround(newLoc, radius, radius, radius)
                                        .aliveTeammatesOf(wp)
                                        .forEach(ally -> ally.addInstance(InstanceBuilder
                                                .healing()
                                                .ability(SoothingElixir.this)
                                                .source(wp)
                                                .value(healingValues.elixirDOTHealing)
                                                .flags(InstanceFlags.DOT)
                                        ));
                            timeLeft--;
                            if (timeLeft <= 0) {
                                this.cancel();
                                particleTask.cancel();
                            }
                        }
                    }.runTaskTimer(20, pveMasterUpgrade ? 5 : 20);
                    List<WarlordsEntity> enemiesHit = PlayerFilter.entitiesAround(newLoc, radius, radius, radius).aliveEnemiesOf(wp).toList();
                    for (WarlordsEntity nearEntity : enemiesHit) {
                        Utils.playGlobalSound(nearEntity.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 0.5f);
                        Leech.giveLeechCooldown(Leech.LeechInstance
                                .create(wp, nearEntity)
                                .withImpalingStrike()
                                .withInitialStacks(pveMasterUpgrade ? 3 : leechStacksApplied)
                        );
                        nearEntity.addInstance(InstanceBuilder.damage().ability(this).source(wp).value(damageValues.elixirDamage));
                    }
                    if (pveMasterUpgrade2) {
                        float healthBoost = (float) (wp.getMaxHealth() * Math.max(.25, (teammatesHit.size() + enemiesHit.size()) * .015f));
                        wp.getHealth().addAdditiveModifier("Soothing Elixir", healthBoost, 4 * 20);
                        wp.setCurrentHealth(wp.getCurrentHealth() + healthBoost);
                    }
                }
        );
        Utils.playGlobalSound(wp.getLocation(), "mage.frostbolt.activation", 2, 0.7f);
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Throw a short range elixir bottle. The bottle will shatter upon impact, healing nearby allies for ")
                                               .heal(healingValues.elixirHealing)
                                               .text(" health and damaging nearby enemies for ")
                                               .damage(damageValues.elixirDamage)
                                               .text(" damage and inflicting them with ")
                                               .text("LEECH", NamedTextColor.DARK_GREEN)
                                               .text(". The projectile will form a small puddle that heals allies for ")
                                               .heal(healingValues.elixirDOTHealing)
                                               .text(" health every ")
                                               .durationSeconds(1)
                                               .text(". Lasts ")
                                               .durationTicks(puddleTickDuration)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SoothingElixirBranch(abilityTree, this);
    }

    @Override
    public int getTickDuration() {
        return puddleTickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.puddleTickDuration = tickDuration;
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return puddleRadius;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public SoothingElixirStats getAbilityStats() {
        return stats;
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getPuddleTickDuration() {
        return puddleTickDuration;
    }

    public int getLeechStacksApplied() {
        return leechStacksApplied;
    }

    public void setLeechStacksApplied(int leechStacksApplied) {
        this.leechStacksApplied = leechStacksApplied;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable elixirDamage = new Value.RangedValueCritable(235, 342, 25, 175);

        private List<Value> values = List.of(elixirDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.elixirDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("elixirDamage"), Value.RangedValueCritable.class);
            this.values = List.of(elixirDamage);
        }

        public Value.RangedValueCritable getElixirDamage() {
            return elixirDamage;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable elixirHealing = new Value.RangedValueCritable(551, 648, 25, 175);
        private List<Value> values = List.of(elixirHealing, elixirHealing);
        private Value.RangedValueCritable elixirDOTHealing = new Value.RangedValueCritable(158, 204, 25, 175);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.elixirHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("elixirHealing"),
                    Value.RangedValueCritable.class
            );
            this.elixirDOTHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("elixirDOTHealing"),
                    Value.RangedValueCritable.class
            );
            this.values = List.of(elixirHealing, elixirDOTHealing);
        }

        public Value.RangedValueCritable getElixirHealing() {
            return elixirHealing;
        }

        public Value.RangedValueCritable getElixirDOTHealing() {
            return elixirDOTHealing;
        }

    }

    public static class SoothingElixirStats extends AbstractAbilityStats<SoothingElixir, SoothingElixirStats> {

        @Field("targets_healed")
        private int targetsHealed = 0;

        @Override
        public Class<SoothingElixirStats> getClazz() {
            return SoothingElixirStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Healed", targetsHealed));
            return statsDisplay;
        }

        @Override
        public SoothingElixirStats merge(SoothingElixirStats other, int multiplier) {
            SoothingElixirStats stats = super.merge(other, multiplier);
            stats.targetsHealed = this.targetsHealed + other.targetsHealed * multiplier;
            return stats;
        }

        @Override
        public SoothingElixirStats create() {
            return new SoothingElixirStats();
        }

    }

}
