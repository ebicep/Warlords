package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.vindicator.HeartToHeartBranch;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class HeartToHeart extends AbstractAbility implements PurpleAbilityIcon, HitBox, Damages<HeartToHeart.DamageValues>, Heals<HeartToHeart.HealingValues> {

    public int timesUsedWithFlag = 0;
    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private FloatModifiable radius = new FloatModifiable(15);
    private int vindDuration = 6;

    public HeartToHeart() {
        super("Heart to Heart", 12, 20);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Throw a chain towards an ally, grappling the Vindicator towards them. You and the targeted ally gain ")
                               .append(Component.text("VIND", NamedTextColor.GOLD))
                               .append(Component.text(" for "))
                               .append(Component.text(vindDuration, NamedTextColor.GOLD))
                               .append(Component.text(" seconds, granting immunity to de-buffs. You are healed for "))
                               .append(Heals.formatHealing(healingValues.heartToHeartHealing))
                               .append(Component.text(" health after reaching your ally. Has a maximum range of"))
                               .append(Component.text(format(radius.getCalculatedValue()), NamedTextColor.YELLOW))
                               .append(Component.text(" blocks.\n\nHeart to Heart has reduced range when holding a flag.", NamedTextColor.GRAY));

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Times Used With Flag", "" + timesUsedWithFlag));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        float radius = getHitBoxRadius().getCalculatedValue();
        float verticalRadius = getHitBoxRadius().getCalculatedValue();
        if (wp.hasFlag()) {
            radius = 10;
            verticalRadius = 2;
        } else {
            wp.setFlagPickCooldown(2);
        }

        if (wp.isInPve()) {
            for (WarlordsEntity heartTarget : PlayerFilter
                    .entitiesAround(wp, radius, verticalRadius, radius)
                    .requireLineOfSight(wp)
                    .lookingAtFirst(wp)
            ) {
                activateAbility(wp, heartTarget);
                Bukkit.getPluginManager().callEvent(new WarlordsAbilityTargetEvent(wp, name, heartTarget));
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
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HeartToHeartBranch(abilityTree, this);
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    private void activateAbility(WarlordsEntity wp, WarlordsEntity heartTarget) {
        if (wp.hasFlag()) {
            timesUsedWithFlag++;
        }

        Utils.playGlobalSound(wp.getLocation(), "rogue.hearttoheart.activation", 2, 1);
        Utils.playGlobalSound(wp.getLocation(), "rogue.hearttoheart.activation.alt", 2, 1.2f);

        HeartToHeart tempHeartToHeart = new HeartToHeart();
        Vindicate.giveVindicateCooldown(wp, wp, HeartToHeart.class, tempHeartToHeart, vindDuration * 20);
        Vindicate.giveVindicateCooldown(wp, heartTarget, HeartToHeart.class, tempHeartToHeart, vindDuration * 20);

        List<WarlordsEntity> playersHit = new ArrayList<>();
        new GameRunnable(wp.getGame()) {
            final Location playerLoc = wp.getLocation();
            int timer = 0;

            @Override
            public void run() {
                timer++;

                if (timer >= 8 || (heartTarget.isDead() || wp.isDead())) {
                    if (pveMasterUpgrade2) {
                        double distanceTravelled = playerLoc.distance(wp.getLocation());
                        float damageMultiplier = (float) (1 - distanceTravelled * .03);
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Heart in Hearts",
                                "HEART",
                                HeartToHeart.class,
                                new HeartToHeart(),
                                wp,
                                CooldownTypes.BUFF,
                                cooldownManager -> {},
                                6 * 20
                        ) {
                            @Override
                            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return currentDamageValue * damageMultiplier;
                            }
                        });
                    }
                    this.cancel();
                }

                double target = timer / 8D;
                Location targetLoc = heartTarget.getLocation();
                Location newLocation = new Location(
                        playerLoc.getWorld(),
                        MathUtils.lerp(playerLoc.getX(), targetLoc.getX(), target),
                        MathUtils.lerp(playerLoc.getY(), targetLoc.getY(), target),
                        MathUtils.lerp(playerLoc.getZ(), targetLoc.getZ(), target),
                        targetLoc.getYaw(),
                        targetLoc.getPitch()
                );

                EffectUtils.playChainAnimation(wp, heartTarget, new ItemStack(Material.SPRUCE_LEAVES), timer);

                wp.teleportLocationOnly(newLocation);
                wp.setFallDistance(-5);
                newLocation.add(0, 1, 0);
                Matrix4d center = new Matrix4d(newLocation);
                for (float i = 0; i < 6; i++) {
                    double angle = Math.toRadians(i * 90) + timer * 0.6;
                    double width = 1.5D;
                    playerLoc.getWorld().spawnParticle(
                            Particle.SPELL_WITCH,
                            center.translateVector(playerLoc.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width),
                            1,
                            0,
                            0,
                            0,
                            0,
                            null,
                            true
                    );
                }

                if (pveMasterUpgrade) {
                    for (WarlordsNPC we : PlayerFilterGeneric
                            .entitiesAround(wp, 3, 3, 3)
                            .aliveEnemiesOf(wp)
                            .excluding(playersHit)
                            .warlordsNPCs()
                    ) {
                        playersHit.add(we);
                        we.setStunTicks(GameRunnable.SECOND);
                        we.addInstance(InstanceBuilder
                                .damage()
                                .cause("Heart of Hearts")
                                .source(wp)
                                .value(damageValues.heartOfHeartsDamage)
                        );
                    }
                }

                if (timer >= 8) {
                    wp.setVelocity(name, playerLoc.getDirection().multiply(0.4).setY(0.2), true);
                    wp.addInstance(InstanceBuilder
                            .healing()
                            .ability(HeartToHeart.this)
                            .source(wp)
                            .value(healingValues.heartToHeartHealing)
                    );
                }
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValue heartOfHeartsDamage = new Value.RangedValue(1635, 2096);
        private final List<Value> values = List.of(heartOfHeartsDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.SetValue heartToHeartHealing = new Value.SetValue(600);
        private final List<Value> values = List.of(heartToHeartHealing);

        public Value.SetValue getHeartToHeartHealing() {
            return heartToHeartHealing;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}
