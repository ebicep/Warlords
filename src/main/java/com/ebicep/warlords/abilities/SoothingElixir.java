package com.ebicep.warlords.abilities;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.AreaEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.apothecary.SoothingElixirBranch;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SoothingElixir extends AbstractAbility implements RedAbilityIcon, Duration, HitBox {

    private static final double SPEED = 0.220;
    private static final double GRAVITY = -0.008;

    public int playersHealed = 0;

    private final int puddleMinDamage = 235;
    private final int puddleMaxDamage = 342;
    private FloatModifiable puddleRadius = new FloatModifiable(5);
    private int puddleTickDuration = 80;
    private int puddleMinHealing = 158;
    private int puddleMaxHealing = 204;

    public SoothingElixir() {
        super("Soothing Elixir", 551, 648, 7, 60, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Throw a short range elixir bottle. The bottle will shatter upon impact, healing nearby allies for ")
                               .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" health and damaging nearby enemies for "))
                               .append(formatRangeDamage(puddleMinDamage, puddleMaxDamage))
                               .append(Component.text(" damage. The projectile will form a small puddle that heals allies for "))
                               .append(formatRangeHealing(puddleMinHealing, puddleMaxHealing))
                               .append(Component.text(" health per second. Lasts "))
                               .append(Component.text(format(puddleTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHealed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {


        Location location = wp.getLocation();
        Vector speed = wp.getLocation().getDirection().multiply(SPEED);

        Utils.spawnThrowableProjectile(
                wp.getGame(),
                Utils.spawnArmorStand(location, armorStand -> {
                    armorStand.getEquipment().setHelmet(new ItemStack(Material.PINK_STAINED_GLASS));
                }),
                speed,
                GRAVITY,
                SPEED,
                (newLoc, integer) -> {
                    Matrix4d center = new Matrix4d(newLoc);
                    for (float i = 0; i < 6; i++) {
                        double angle = Math.toRadians(i * 90) + integer * 0.3;
                        double width = 0.3D;
                        newLoc.getWorld().spawnParticle(
                                Particle.VILLAGER_HAPPY,
                                center.translateVector(newLoc.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width),
                                2,
                                0,
                                0,
                                0,
                                0,
                                null,
                                true
                        );

                    }
                },
                newLoc -> PlayerFilter
                        .entitiesAroundRectangle(newLoc, 1, 2, 1)
                        .aliveTeammatesOfExcludingSelf(wp)
                        .findFirstOrNull(),
                (newLoc, directHit) -> {
                    Utils.playGlobalSound(newLoc, "rogue.healingremedy.impact", 1.5f, 0.1f);
                    Utils.playGlobalSound(newLoc, Sound.BLOCK_GLASS_BREAK, 1.5f, 0.7f);
                    Utils.playGlobalSound(newLoc, "mage.waterbolt.impact", 1.5f, 0.3f);

                    float radius = puddleRadius.getCalculatedValue();
                    CircleEffect circleEffect = new CircleEffect(
                            wp.getGame(),
                            wp.getTeam(),
                            newLoc,
                            radius,
                            new CircumferenceEffect(Particle.VILLAGER_HAPPY, Particle.REDSTONE),
                            new AreaEffect(1, Particle.DRIP_WATER).particlesPerSurface(0.025)
                    );
                    BukkitTask particleTask = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), circleEffect::playEffects, 0, 2);
                    wp.getGame().registerGameTask(particleTask);

                    EffectUtils.playFirework(newLoc, FireworkEffect.builder()
                                                                   .withColor(Color.WHITE)
                                                                   .with(FireworkEffect.Type.BURST)
                                                                   .build());

                    List<WarlordsEntity> teammatesHit = PlayerFilter
                            .entitiesAround(newLoc, radius, radius, radius)
                            .aliveTeammatesOf(wp)
                            .toList();
                    for (WarlordsEntity nearEntity : teammatesHit) {
                        playersHealed++;
                        nearEntity.addInstance(InstanceBuilder
                                .healing()
                                .ability(this)
                                .source(wp)
                                .value(healingValues.elixirHealing)
                        );

                        if (pveMasterUpgrade2) {
                            nearEntity.getCooldownManager().removeDebuffCooldowns();
                            nearEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                                    "Debuff Immunity",
                                    "ELIXIR",
                                    SoothingElixir.class,
                                    new SoothingElixir(),
                                    wp,
                                    CooldownTypes.BUFF,
                                    cooldownManager -> {
                                    },
                                    4 * 20
                            ));
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
                                        ));

                            timeLeft--;

                            if (timeLeft <= 0) {
                                this.cancel();
                                particleTask.cancel();
                            }
                        }

                    }.runTaskTimer(20, pveMasterUpgrade ? 10 : 20);

                    List<WarlordsEntity> enemiesHit = PlayerFilter
                            .entitiesAround(newLoc, radius, radius, radius)
                            .aliveEnemiesOf(wp)
                            .toList();
                    for (WarlordsEntity nearEntity : enemiesHit) {
                        Utils.playGlobalSound(nearEntity.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 0.5f);
                        nearEntity.addInstance(InstanceBuilder
                                .damage()
                                .ability(this)
                                .source(wp)
                                .value(damageValues.elixirDamage)
                        );

                        if (pveMasterUpgrade) {
                            ImpalingStrike.giveLeechCooldown(
                                    wp,
                                    nearEntity,
                                    5,
                                    0.25f,
                                    0.15f,
                                    warlordsDamageHealingFinalEvent -> {
                                    }
                            );
                        }
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
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SoothingElixirBranch(abilityTree, this);
    }

    public int getPuddleMinHealing() {
        return puddleMinHealing;
    }

    public void setPuddleMinHealing(int puddleMinHealing) {
        this.puddleMinHealing = puddleMinHealing;
    }

    public int getPuddleMaxHealing() {
        return puddleMaxHealing;
    }

    public void setPuddleMaxHealing(int puddleMaxHealing) {
        this.puddleMaxHealing = puddleMaxHealing;
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

    private final DamageValues damageValues = new DamageValues();

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable elixirDamage = new Value.RangedValueCritable(235, 342, 25, 175);
        private final List<Value> values = List.of(elixirDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    private final HealingValues healingValues = new HealingValues();

    public static class HealingValues implements Value.ValueHolder {

        private final Value.RangedValueCritable elixirHealing = new Value.RangedValueCritable(551, 648, 25, 175);
        private final Value.RangedValueCritable elixirDOTHealing = new Value.RangedValueCritable(158, 204, 25, 175);
        private final List<Value> values = List.of(elixirHealing, elixirHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}