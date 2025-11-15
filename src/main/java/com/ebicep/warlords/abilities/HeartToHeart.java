package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerHeartToHeartEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.vindicator.HeartToHeartBranch;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class HeartToHeart extends AbstractAbility implements PurpleAbilityIcon, HitBox, Damages<HeartToHeart.DamageValues>, Heals<HeartToHeart.HealingValues>, AbilityStats<HeartToHeart, HeartToHeart.HeartToHeartStats> {

    public static final ItemStack ITEM_STACK = new ItemStack(Material.CRIMSON_ROOTS);
    private final HeartToHeartStats stats = new HeartToHeartStats();
    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private FloatModifiable radius = new FloatModifiable(15);
    private float flagDistance;
    private int vindDuration = 6;
    private boolean targetEnemies = false;

    public HeartToHeart() {
        super(AbstractAbilityBuilder.create("heartToHeart").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.radius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class));
        this.flagDistance = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("flagDistance"), float.class);
        this.vindDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("vindDuration"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        float radius = getHitBoxRadius().getCalculatedValue();
        float verticalRadius = getHitBoxRadius().getCalculatedValue();
        if (wp.hasFlag()) {
            verticalRadius = 2;
        } else {
            wp.setFlagPickCooldown(2);
        }
        if (inPve) {
            if (targetEnemies) {
                for (WarlordsEntity heartTarget : PlayerFilter
                        .entitiesAround(wp, radius, verticalRadius, radius)
                        .aliveEnemiesOf(wp)
                        .requireLineOfSight(wp)
                        .lookingAtFirst(wp)
                ) {
                    activateAbility(wp, heartTarget);
                    heartTarget.addInstance(InstanceBuilder.damage().cause("Heart to Heart").source(wp).value(1800));
                    return true;
                }
            }
            for (WarlordsEntity heartTarget : PlayerFilter
                    .entitiesAround(wp, radius, verticalRadius, radius)
                    .requireLineOfSight(wp)
                    .lookingAtFirst(wp)
            ) {
                activateAbility(wp, heartTarget);
                return true;
            }
        } else if (targetEnemies) {
            for (WarlordsEntity heartTarget : PlayerFilter
                    .entitiesAround(wp, radius, verticalRadius, radius)
                    .excluding(wp)
                    .requireLineOfSight(wp)
                    .lookingAtFirst(wp)
            ) {
                activateAbility(wp, heartTarget);
                return true;
            }
        } else {
            for (WarlordsEntity heartTarget : PlayerFilter
                    .entitiesAround(wp, radius, verticalRadius, radius)
                    .aliveTeammatesOfExcludingSelf(wp)
                    .requireLineOfSight(wp)
                    .lookingAtFirst(wp)
                    .limit(1)
            ) {
                activateAbility(wp, heartTarget);
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Throw a chain towards an ally, grappling yourself towards them. You and the targeted ally gain ")
                                               .text("VIND", NamedTextColor.DARK_GREEN)
                                               .text(" for ")
                                               .durationSeconds(vindDuration)
                                               .text(" and heal ")
                                               .heal(healingValues.heartToHeartHealing)
                                               .text(" health.")
                                               .maxRange(radius)
                                               .emptyLine()
                                               .text("Heart to Heart has reduced vertical range and travel distance when holding a flag.")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HeartToHeartBranch(abilityTree, this);
    }

    private void activateAbility(WarlordsEntity wp, WarlordsEntity heartTarget) {
        float maxDistance = getHitBoxRadius().getCalculatedValue();
        if (wp.hasFlag()) {
            maxDistance = getFlagDistance();
            stats.timesUsedWithFlag++;
        }
        Utils.playGlobalSound(wp.getLocation(), "rogue.hearttoheart.activation", 2, 1);
        Utils.playGlobalSound(wp.getLocation(), "rogue.hearttoheart.activation.alt", 2, 1.2f);
        Vindicate.giveVindicateCooldown(wp, wp, HeartToHeart.class, null, vindDuration * 20);
        if (heartTarget.isTeammateAlive(wp)) {
            Vindicate.giveVindicateCooldown(wp, heartTarget, HeartToHeart.class, null, vindDuration * 20);
        }
        List<WarlordsEntity> playersHit = new ArrayList<>();

        final Location playerLoc = wp.getLocation();
        final Location heartTargetLoc = heartTarget.getLocation();
        double ratio = 1;
        if (playerLoc.distance(heartTargetLoc) > maxDistance) {
            ratio = maxDistance / playerLoc.distance(heartTargetLoc);
        }
        final Location targetLoc = new Location(
                playerLoc.getWorld(),
                MathUtils.lerp(playerLoc.getX(), heartTargetLoc.getX(), ratio),
                MathUtils.lerp(playerLoc.getY(), heartTargetLoc.getY(), ratio),
                MathUtils.lerp(playerLoc.getZ(), heartTargetLoc.getZ(), ratio),
                heartTargetLoc.getYaw(),
                heartTargetLoc.getPitch()
        );

        new GameRunnable(wp.getGame()) {


            int timer = 0;

            @Override
            public void run() {
                timer++;
                if (timer >= 8 || (heartTarget.isDead() || wp.isDead())) {
                    if (pveMasterUpgrade2) {
                        double distanceTravelled = playerLoc.distance(wp.getLocation());
                        float damageMultiplier = (float) (1 - distanceTravelled * .03);
                        wp.getCooldownManager()
                          .addCooldown(new RegularCooldown<>(
                                  "Heart in Hearts",
                                  "HEART",
                                  HeartToHeart.class,
                                  new HeartToHeart(),
                                  wp,
                                  CooldownTypes.BUFF,
                                  cooldownManager -> {
                                  },
                                  6 * 20
                          ).addModifier(Modifier.DAMAGE_AFTER_INTERVENE_SELF, (event, currentDamageValue) -> {
                                      currentDamageValue.addMultiplicativeModifierMult(name, damageMultiplier);
                                  }
                          ));
                    }
                    this.cancel();
                }
                double target = timer / 8D;
                Location newLocation = new Location(playerLoc.getWorld(),
                        MathUtils.lerp(playerLoc.getX(), targetLoc.getX(), target),
                        MathUtils.lerp(playerLoc.getY(), targetLoc.getY(), target),
                        MathUtils.lerp(playerLoc.getZ(), targetLoc.getZ(), target),
                        targetLoc.getYaw(),
                        targetLoc.getPitch()
                );
                EffectUtils.playChainAnimation(wp, heartTarget, ITEM_STACK, timer);
                wp.teleportLocationOnly(newLocation);
                wp.setFallDistance(-5);
                newLocation.add(0, 1, 0);
                Matrix4d center = new Matrix4d(newLocation);
                for (float i = 0; i < 6; i++) {
                    double angle = Math.toRadians(i * 90) + timer * 0.6;
                    double width = 1.5D;
                    EffectUtils.displayParticle(
                            Particle.WITCH,
                            center.translateVector(playerLoc.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width),
                            1,
                            0,
                            0,
                            0,
                            0
                    );
                }
                if (pveMasterUpgrade) {
                    for (WarlordsEntity we : PlayerFilter.entitiesAround(wp, 3, 3, 3).aliveEnemiesOf(wp).excluding(playersHit)) {
                        playersHit.add(we);
                        we.setStunTicks(GameRunnable.SECOND);
                        we.addInstance(InstanceBuilder.damage().cause("Heart of Hearts").source(wp).value(damageValues.heartOfHeartsDamage));
                    }
                }
                if (timer >= 8) {
                    wp.setVelocity(name, playerLoc.getDirection().multiply(0.4).setY(0.2), true);
                    wp.addInstance(InstanceBuilder.healing().ability(HeartToHeart.this).source(wp).value(healingValues.heartToHeartHealing));
                    Bukkit.getPluginManager().callEvent(new WarlordsPlayerHeartToHeartEvent(wp, heartTarget));
                    if (inPve || heartTarget.isTeammate(wp)) {
                        heartTarget.addInstance(InstanceBuilder.create(heartTarget.isTeammate(wp) ? InstanceBuilder.InstanceType.HEALING : InstanceBuilder.InstanceType.DAMAGE)
                                                               .ability(HeartToHeart.this)
                                                               .source(wp)
                                                               .value(healingValues.heartToHeartHealing));
                    }
                }
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    public float getFlagDistance() {
        return flagDistance;
    }

    public void setFlagDistance(float flagDistance) {
        this.flagDistance = flagDistance;
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
    public HeartToHeartStats getAbilityStats() {
        return stats;
    }

    public void setTargetEnemies(boolean targetEnemies) {
        this.targetEnemies = targetEnemies;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValue heartOfHeartsDamage = new Value.RangedValue(1635, 2096);

        private List<Value> values = List.of(heartOfHeartsDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.heartOfHeartsDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameDamage("heartOfHeartsDamage"),
                    Value.RangedValue.class
            );
            this.values = List.of(heartOfHeartsDamage);
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue heartToHeartHealing = new Value.SetValue(600);

        private List<Value> values = List.of(heartToHeartHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.heartToHeartHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("heartToHeartHealing"),
                    Value.SetValue.class
            );
            this.values = List.of(heartToHeartHealing);
        }

        public Value.SetValue getHeartToHeartHealing() {
            return heartToHeartHealing;
        }

    }

    public static class HeartToHeartStats extends AbstractAbilityStats<HeartToHeart, HeartToHeartStats> {

        @Field("times_used_with_flag")
        private int timesUsedWithFlag = 0;

        @Override
        public Class<HeartToHeartStats> getClazz() {
            return HeartToHeartStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public HeartToHeartStats merge(HeartToHeartStats other, int multiplier) {
            HeartToHeartStats stats = super.merge(other, multiplier);
            stats.timesUsedWithFlag = this.timesUsedWithFlag + other.timesUsedWithFlag * multiplier;
            return stats;
        }

        @Override
        public HeartToHeartStats create() {
            return new HeartToHeartStats();
        }

    }

}
