package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.conjurer.PoisonousHexBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PoisonousHex extends AbstractPiercingProjectile implements WeaponAbilityIcon, Duration, Damages<PoisonousHex.DamageValues> {

    private final DamageValues damageValues = new DamageValues();
    private int maxFullDistance = 40;
    private int hexStacksPerHit = 1;
    private int maxStacks = 3;
    private int tickDuration = 40;
    private int ticksBetweenDot = 40;
    private int maxEnemiesHit = 2;

    public PoisonousHex() {
        super("Poisonous Hex", 0, 60, 2.5, 40, false);
        this.shotsFiredAtATime = 2;
        this.maxAngleOfShots = 26;
        this.forwardTeleportAmount = 1.6f;
        this.hitboxInflation.setBaseValue(hitboxInflation.getBaseValue() + .4f);
    }

    @Override
    public void updateDescription(Player player) {
        boolean infiniteHit = maxEnemiesHit >= 200;
        description = Component.text("Throw Hex Fangs in front of you, dealing ")
                               .append(Damages.formatDamage(damageValues.hexDamage))
                               .append(Component.text(" damage " + (infiniteHit ? "" : "to up to "))
                                                .append(Component.text((infiniteHit ? "infinite" : "" + maxEnemiesHit), NamedTextColor.RED))
                                                .append(Component.text(" enemies. Additionally, hit targets receive ")))
                               .append(Component.text(hexStacksPerHit, NamedTextColor.BLUE))
                               .append(Component.text(" stack" + (hexStacksPerHit != 1 ? "s" : "") + " of Poisonous Hex.\n\nEach stack of Poisonous Hex deals "))
                               .append(Damages.formatDamage(damageValues.hexDOTDamage))
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
        return new LocationBuilder(startingLocation.clone()).addY(-.3).backward(0f);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);

        Location startingLocation = projectile.getStartingLocation();
        LocationBuilder location = new LocationBuilder(startingLocation)
                .pitch(0)
                .yaw(startingLocation.getYaw() - 180);
        ItemDisplay display = startingLocation.getWorld().spawn(location, ItemDisplay.class, itemDisplay -> {
            itemDisplay.setItemStack(new ItemStack(Material.GREEN_STAINED_GLASS));
            itemDisplay.setTeleportDuration(1);
            itemDisplay.setBrightness(new Display.Brightness(15, 15));
            itemDisplay.setTransformation(new Transformation(
                    new Vector3f(),
                    new AxisAngle4f((float) Math.toRadians(startingLocation.getPitch()), 1, 0, 0),
                    new Vector3f(1f),
                    new AxisAngle4f()
            ));
        });

        projectile.addTask(new InternalProjectileTask() {
            @Override
            public void run(InternalProjectile projectile) {
                Location currentLocation = projectile.getCurrentLocation();
                LocationBuilder location = new LocationBuilder(currentLocation)
                        .pitch(0)
                        .yaw(currentLocation.getYaw() - 180);
                display.teleport(location);
                if (projectile.getTicksLived() % 3 == 0) {
                    EffectUtils.displayParticle(
                            Particle.REDSTONE,
                            projectile.getCurrentLocation(),
                            1,
                            new Particle.DustOptions(Color.fromRGB(90, 90, 190), 1)
                    );
                }
            }

            @Override
            public void onDestroy(InternalProjectile projectile) {
                display.remove();
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
        float toReduceBy = maxFullDistance * maxFullDistance > distanceSquared ? 1 :
                           (float) (1 - (Math.sqrt(distanceSquared) - maxFullDistance) / 75);
        if (toReduceBy < .2) {
            toReduceBy = .2f;
        }
        getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
        if (hit.onHorse()) {
            numberOfDismounts++;
        }
        hit.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .min(damageValues.hexDamage.getMinValue() * toReduceBy)
                .max(damageValues.hexDamage.getMaxValue() * toReduceBy)
        );
        givePoisonousHex(wp, hit);
        if (projectile.getHit().size() >= maxEnemiesHit) {
            getProjectiles(projectile).forEach(InternalProjectile::cancel);
        }
        return true;
    }

    public static void givePoisonousHex(WarlordsEntity from, WarlordsEntity to) {
        if (to.isDead()) {
            return;
        }
        PoisonousHex fromHex = getFromHex(from);
        String hexName = fromHex.getName();
        int tickDuration = fromHex.getTickDuration();
        int dotTickFrequency = fromHex.getTicksBetweenDot();
        DamageValues values = fromHex.damageValues;
        to.getCooldownManager().limitCooldowns(RegularCooldown.class, PoisonousHex.class, 3);
        to.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Poisonous Hex",
                "PHEX",
                PoisonousHex.class,
                new PoisonousHex(),
                from,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    to.addInstance(InstanceBuilder
                            .damage()
                            .ability(fromHex)
                            .source(from)
                            .value(values.hexDOTDamage)
                            .flags(InstanceFlags.NO_DISMOUNT, InstanceFlags.DOT)
                    );
                },
                tickDuration * 2,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % dotTickFrequency == 0 && ticksElapsed != 0) {
                        to.addInstance(InstanceBuilder
                                .damage()
                                .ability(fromHex)
                                .source(from)
                                .value(values.hexDOTDamage)
                                .flags(InstanceFlags.NO_DISMOUNT, InstanceFlags.DOT)
                        );
                    }
                })
        ) {
            @Override
            public PlayerNameData addSuffixFromOther() {
                boolean flag = new CooldownFilter<>(to, RegularCooldown.class).filterCooldownClass(PoisonousHex.class).stream().count() == fromHex.maxStacks;
                return new PlayerNameData(
                        Component.text("PHEX", AbstractCooldown.PSEUDO_DEBUFF_COLOR).decoration(TextDecoration.BOLD, flag),
                        we -> we.isTeammate(from) && we.getSpecClass() == Specializations.CONJURER
                );
            }

            @Override
            public TextColor customActionBarColor() {
                return AbstractCooldown.PSEUDO_DEBUFF_COLOR;
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

    public void setTicksBetweenDot(int ticksBetweenDot) {
        this.ticksBetweenDot = ticksBetweenDot;
    }

    public int getMaxStacks() {
        return maxStacks;
    }

    public void setMaxEnemiesHit(int maxEnemiesHit) {
        this.maxEnemiesHit = maxEnemiesHit;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable hexDamage = new Value.RangedValueCritable(263, 356, 20, 175);
        private final Value.RangedValue hexDOTDamage = new Value.RangedValue(25, 35);
        private final List<Value> values = List.of(hexDamage, hexDOTDamage);

        public Value.RangedValueCritable getHexDamage() {
            return hexDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}
