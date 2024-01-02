package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.conjurer.PoisonousHexBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class PoisonousHex extends AbstractPiercingProjectile implements WeaponAbilityIcon, Duration {

    private int maxFullDistance = 40;
    private int hexStacksPerHit = 1;
    private float dotMinDamage = 30;
    private float dotMaxDamage = 40;
    private int maxStacks = 3;
    private int tickDuration = 40;
    private int ticksBetweenDot = 40;
    private int maxEnemiesHit = 2;

    public PoisonousHex() {
        super("Poisonous Hex", 307, 415, 0, 70, 20, 175, 2.5, 40, false);
        this.shotsFiredAtATime = 2;
        this.maxAngleOfShots = 26;
        this.forwardTeleportAmount = 1.6f;
        this.hitboxInflation.setBaseValue(hitboxInflation.getBaseValue() + .4f);
    }

    @Override
    public void updateDescription(Player player) {
        boolean infiniteHit = maxEnemiesHit >= 200;
        description = Component.text("Throw Hex Fangs in front of you, dealing ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage " + (infiniteHit ? "" : "to up to "))
                                                .append(Component.text((infiniteHit ? "infinite" : "" + maxEnemiesHit), NamedTextColor.RED))
                                                .append(Component.text(" enemies. Additionally, hit targets receive ")))
                               .append(Component.text(hexStacksPerHit, NamedTextColor.BLUE))
                               .append(Component.text(" stack" + (hexStacksPerHit != 1 ? "s" : "") + " of Poisonous Hex.\n\nEach stack of Poisonous Hex deals "))
                               .append(formatRangeDamage(dotMinDamage, dotMaxDamage))
                               .append(Component.text(" damage every "))
                               .append(Component.text(format(ticksBetweenDot / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds for "))
                               .append(Component.text(format(tickDuration / 10f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Stacks up to "))
                               .append(Component.text(maxStacks, NamedTextColor.BLUE))
                               .append(Component.text(" times."))
                               .append(Component.text("\n\nHas a maximum range of "))
                               .append(Component.text(maxFullDistance, NamedTextColor.YELLOW))
                               .append(Component.text("blocks."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new PoisonousHexBranch(abilityTree, this);
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {

    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        if (hit != null) {
            return 0;
        }

        int playersHit = 0;
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(projectile.getCurrentLocation(), 2, 2, 2)
                .aliveEnemiesOf(projectile.getShooter())
                .excluding(projectile.getHit())
        ) {
            if (hitProjectile(projectile, enemy)) {
                playersHit++;
            }
        }
        return playersHit;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, WarlordsEntity wp) {
        return false;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, Block block) {
        return true;
    }

    @Override
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        hitProjectile(projectile, hit);
    }

    @Override
    protected Location modifyProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
        return new LocationBuilder(startingLocation.clone()).addY(-.5).backward(0f);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        ArmorStand fallenSoul = Utils.spawnArmorStand(projectile.getStartingLocation().clone().add(0, -1.7, 0), armorStand -> {
            armorStand.setMarker(true);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.GREEN_STAINED_GLASS));
            armorStand.setHeadPose(new EulerAngle(-Math.atan2(
                    projectile.getSpeed().getY(),
                    Math.sqrt(
                            Math.pow(projectile.getSpeed().getX(), 2) +
                                    Math.pow(projectile.getSpeed().getZ(), 2)
                    )
            ), 0, 0));
        });

        projectile.addTask(new InternalProjectileTask() {
            @Override
            public void run(InternalProjectile projectile) {
                fallenSoul.teleport(projectile.getCurrentLocation().clone().add(0, -1.7, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                Matrix4d center = new Matrix4d(projectile.getCurrentLocation());

                for (float i = 0; i < 2; i++) {
                    double angle = Math.toRadians(i * 180) + projectile.getTicksLived() * 0.45;
                    double width = 0.2D;
                    EffectUtils.displayParticle(
                            Particle.REDSTONE,
                            center.translateVector(projectile.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width),
                            2,
                            0,
                            0,
                            0,
                            0,
                            new Particle.DustOptions(Color.fromRGB(90, 90, 190), 1)
                    );
                }
            }

            @Override
            public void onDestroy(InternalProjectile projectile) {
                fallenSoul.remove();
                Utils.playGlobalSound(projectile.getCurrentLocation(), Sound.ENTITY_EVOKER_FANGS_ATTACK, 0.2f, 2);
                EffectUtils.displayParticle(
                        Particle.EXPLOSION_LARGE,
                        projectile.getCurrentLocation(),
                        1,
                        0,
                        0,
                        0,
                        0.7f
                );
            }
        });
    }

    @Nullable
    @Override
    protected String getActivationSound() {
        return "arcanist.poisonoushex.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 0.7f;
    }

    private boolean hitProjectile(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit) {
        if (projectile.getHit().contains(hit)) {
            return false;
        }
        if (projectile.getHit().size() >= maxEnemiesHit) {
            return false;
        }
        WarlordsEntity wp = projectile.getShooter();
        Location currentLocation = projectile.getCurrentLocation();
        Location startingLocation = projectile.getStartingLocation();

        Utils.playGlobalSound(currentLocation, Sound.ENTITY_EVOKER_FANGS_ATTACK, 1, 0.9f);

        double distanceSquared = startingLocation.distanceSquared(currentLocation);
        double toReduceBy = maxFullDistance * maxFullDistance > distanceSquared ? 1 :
                            1 - (Math.sqrt(distanceSquared) - maxFullDistance) / 75;
        if (toReduceBy < .2) {
            toReduceBy = .2;
        }
        getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
        if (hit.onHorse()) {
            numberOfDismounts++;
        }
        hit.addDamageInstance(
                wp,
                name,
                (float) (minDamageHeal * toReduceBy),
                (float) (maxDamageHeal * toReduceBy),
                critChance,
                critMultiplier
        );
        givePoisonousHex(wp, hit);
        if (projectile.getHit().size() >= maxEnemiesHit) {
            getProjectiles(projectile).forEach(InternalProjectile::cancel);
        }
        return true;
    }

    public static void givePoisonousHex(WarlordsEntity from, WarlordsEntity to) {
        PoisonousHex fromHex = getFromHex(from);
        String hexName = fromHex.getName();
        int tickDuration = fromHex.getTickDuration();
        int dotTickFrequency = fromHex.getTicksBetweenDot();
        float dotMinDamage = fromHex.getDotMinDamage();
        float dotMaxDamage = fromHex.getDotMaxDamage();
        to.getCooldownManager().limitCooldowns(RegularCooldown.class, PoisonousHex.class, 3);
        to.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Poisonous Hex",
                "PHEX",
                PoisonousHex.class,
                new PoisonousHex(),
                from,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                    to.addDamageInstance(
                            from,
                            hexName,
                            dotMinDamage,
                            dotMaxDamage,
                            0,
                            100,
                            EnumSet.of(InstanceFlags.NO_DISMOUNT)
                    );
                },
                tickDuration * 2,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % dotTickFrequency == 0 && ticksElapsed != 0) {
                        to.addDamageInstance(
                                from,
                                hexName,
                                dotMinDamage,
                                dotMaxDamage,
                                0,
                                100,
                                EnumSet.of(InstanceFlags.NO_DISMOUNT)
                        );
                    }
                })
        ) {
            @Override
            public PlayerNameData addSuffixFromOther() {
                return new PlayerNameData(Component.text("PHEX", NamedTextColor.RED), we -> we.isTeammate(from) && we.getSpecClass() == Specializations.CONJURER);
            }
        });
    }

    @Nonnull
    public static PoisonousHex getFromHex(WarlordsEntity from) {
        return from.getSpec().getAbilities().stream()
                   .filter(PoisonousHex.class::isInstance)
                   .map(PoisonousHex.class::cast)
                   .findFirst()
                   .orElse(new PoisonousHex());
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getTicksBetweenDot() {
        return ticksBetweenDot;
    }

    public float getDotMinDamage() {
        return dotMinDamage;
    }

    public void setDotMinDamage(float dotMinDamage) {
        this.dotMinDamage = dotMinDamage;
    }

    public float getDotMaxDamage() {
        return dotMaxDamage;
    }

    public void setDotMaxDamage(float dotMaxDamage) {
        this.dotMaxDamage = dotMaxDamage;
    }

    public void setTicksBetweenDot(int ticksBetweenDot) {
        this.ticksBetweenDot = ticksBetweenDot;
    }

    public int getMaxStacks() {
        return maxStacks;
    }

    public int getMaxEnemiesHit() {
        return maxEnemiesHit;
    }

    public void setMaxEnemiesHit(int maxEnemiesHit) {
        this.maxEnemiesHit = maxEnemiesHit;
    }
}
