package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.sentinel.FortifyingHexBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
import java.util.Optional;

public class FortifyingHex extends AbstractPiercingProjectile implements WeaponAbilityIcon, Duration, Damages<FortifyingHex.DamageValues> {

    protected FloatModifiable damageReduction = new FloatModifiable(8);

    private final DamageValues damageValues = new DamageValues();
    private int maxEnemiesHit = 1;
    private int maxAlliesHit = 1;
    private int maxFullDistance = 40;
    private int tickDuration = 120;
    private int hexStacksPerHit = 1;
    private int maxStacks = 3;

    public FortifyingHex(float damageReduction) {
        this();
        this.damageReduction = new FloatModifiable(damageReduction);
    }

    public FortifyingHex() {
        super("Fortifying Hex", 287, 387, 0, 70, 20, 175, 2.5, 40, true);
        this.hitboxInflation.setBaseValue(hitboxInflation.getBaseValue() + .4f);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Fling a wave of protective energy forward, hitting ")
                               .append(Component.text(maxEnemiesHit, NamedTextColor.RED))
                               .append(Component.text((maxEnemiesHit == 1 ? " enemy" : " enemies") + " and "))
                               .append(Component.text(maxAlliesHit, NamedTextColor.YELLOW))
                               .append(Component.text((maxAlliesHit == 1 ? " ally" : " allies") + ". The enemy takes "))
                               .append(Damages.formatDamage(damageValues.hexDamage))
                               .append(Component.text(" damage. The ally receives "))
                               .append(Component.text(hexStacksPerHit, NamedTextColor.BLUE))
                               .append(Component.text(" stack" + (hexStacksPerHit != 1 ? "s" : "") + " of Fortifying Hex. If Fortifying Hex hits a target, you receive "))
                               .append(Component.text(hexStacksPerHit, NamedTextColor.BLUE))
                               .append(Component.text(" stack" + (hexStacksPerHit != 1 ? "s" : "") + " of Fortifying Hex.\n\nEach stack of Fortifying Hex lasts  "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds and grants"))
                               .append(Component.text(format(damageReduction.getCalculatedValue()) + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" damage reduction. Stacks up to"))
                               .append(Component.text(maxStacks, NamedTextColor.BLUE))
                               .append(Component.text(" times.\n\nHas a maximum range of "))
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
        return new FortifyingHexBranch(abilityTree, this);
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
        return new LocationBuilder(startingLocation.clone()).addY(-.63).backward(0f);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);

        Location startingLocation = projectile.getStartingLocation();
        LocationBuilder location = new LocationBuilder(startingLocation)
                .pitch(0);
        ItemDisplay display = startingLocation.getWorld().spawn(location, ItemDisplay.class, itemDisplay -> {
            itemDisplay.setItemStack(new ItemStack(Material.WARPED_DOOR));
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
                        .pitch(0);
                display.teleport(location);
                if (projectile.getTicksLived() % 3 == 0) {
                    EffectUtils.displayParticle(
                            Particle.END_ROD,
                            new LocationBuilder(projectile.getCurrentLocation()).addY(.875).left(.8f),
                            1
                    );
                    EffectUtils.displayParticle(
                            Particle.END_ROD,
                            new LocationBuilder(projectile.getCurrentLocation()).addY(.875).right(.8f),
                            1
                    );
                }
            }

            @Override
            public void onDestroy(InternalProjectile projectile) {
                display.remove();
                Utils.playGlobalSound(projectile.getCurrentLocation(), "shaman.chainheal.activation", 2, 2);
                EffectUtils.displayParticle(
                        Particle.EXPLOSION_LARGE,
                        projectile.getCurrentLocation(),
                        1,
                        0,
                        0,
                        0,
                        0.7
                );
            }
        });
    }

    @Nullable
    @Override
    protected String getActivationSound() {
        return "arcanist.fortifyinghex.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 1.4f;
    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        super.runEveryTick(warlordsEntity);
        damageReduction.tick();
    }

    private boolean hitProjectile(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit) {
        if (projectile.getHit().contains(hit) || projectile.getShooter().equals(hit)) {
            return false;
        }
        WarlordsEntity wp = projectile.getShooter();
        Location currentLocation = projectile.getCurrentLocation();
        Location startingLocation = projectile.getStartingLocation();

        getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
        List<WarlordsEntity> hits = projectile.getHit();
        if (hit.isTeammate(wp)) {
            int teammatesHit = (int) hits.stream().filter(we -> we.isTeammate(wp)).count();
            if (teammatesHit > maxAlliesHit) {
                return false;
            }
            giveFortifyingHex(wp, hit);
        } else {
            int enemiesHit = (int) hits.stream().filter(we -> !we.isTeammate(wp)).count();
            if (enemiesHit > maxEnemiesHit) {
                return false;
            }
            double distanceSquared = startingLocation.distanceSquared(currentLocation);
            float toReduceBy = maxFullDistance * maxFullDistance > distanceSquared ? 1 :
                               (float) (1 - (Math.sqrt(distanceSquared) - maxFullDistance) / 75);
            if (toReduceBy < .2) {
                toReduceBy = .2f;
            }
            hitEnemy(hit, wp, toReduceBy);
            if (pveMasterUpgrade2) {
                for (WarlordsEntity warlordsEntity : PlayerFilter
                        .entitiesAround(hit, 3, 3, 3)
                        .aliveTeammatesOfExcludingSelf(hit)
                        .toList()
                ) {
                    hitEnemy(warlordsEntity, wp, toReduceBy);
                }
                EffectUtils.displayParticle(
                        Particle.EXPLOSION_LARGE,
                        hit.getLocation().add(0, 1, 0),
                        1,
                        .1,
                        .1,
                        .1,
                        0
                );
            }
        }
        if (hits.size() == 1) {
            giveFortifyingHex(wp, wp);
        }
        return true;
    }

    public static void giveFortifyingHex(WarlordsEntity from, WarlordsEntity to) {
        FortifyingHex fromHex = getFromHex(from);
        String hexName = fromHex.getName();
        int maxStacks = fromHex.getMaxStacks();
        int duration = fromHex.getTickDuration();
        to.getCooldownManager().limitCooldowns(RegularCooldown.class, FortifyingHex.class, maxStacks);
        FortifyingHex tempFortifyingHex = new FortifyingHex(fromHex.getDamageReduction().getCalculatedValue());
        to.getCooldownManager().addCooldown(new RegularCooldown<>(
                hexName,
                "FHEX",
                FortifyingHex.class,
                tempFortifyingHex,
                from,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                duration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    tempFortifyingHex.getDamageReduction().tick();
                })
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 - tempFortifyingHex.getDamageReduction().getCalculatedValue() / 100f);
            }

            @Override
            public PlayerNameData addPrefixFromOther() {
                return new PlayerNameData(Component.text("FHEX", NamedTextColor.YELLOW), we -> we.isTeammate(from) && we.getSpecClass() == Specializations.SENTINEL);
            }
        });
        from.playSound(from.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }

    private void hitEnemy(@Nonnull WarlordsEntity hit, WarlordsEntity wp, float toReduceBy) {
        hit.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .min(damageValues.hexDamage.getMinValue() * toReduceBy)
                .max(damageValues.hexDamage.getMaxValue() * toReduceBy)
                .crit(damageValues.hexDamage)
        );
        if (pveMasterUpgrade2) {
            Optional<RegularCooldown> weakeningHexCooldown = new CooldownFilter<>(hit, RegularCooldown.class)
                    .filterCooldownClass(WeakeningHex.class)
                    .findFirst();
            if (weakeningHexCooldown.isPresent()) {
                RegularCooldown regularCooldown = weakeningHexCooldown.get();
                WeakeningHex weakeningHex = (WeakeningHex) regularCooldown.getCooldownObject();
                weakeningHex.setStacks(weakeningHex.getStacks() + 1);
                regularCooldown.setTicksLeft(tickDuration);
            } else {
                hit.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Weakening Hex",
                        "WHEX",
                        WeakeningHex.class,
                        new WeakeningHex(),
                        wp,
                        CooldownTypes.DEBUFF,
                        cooldownManager -> {
                        },
                        6 * 20
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * (1 + 0.05f * cooldownObject.getStacks());
                    }
                });
            }
        }
    }

    @Nonnull
    public static FortifyingHex getFromHex(WarlordsEntity from) {
        return from.getSpec().getAbilities().stream()
                   .filter(FortifyingHex.class::isInstance)
                   .map(FortifyingHex.class::cast)
                   .findFirst()
                   .orElse(new FortifyingHex());
    }

    public int getMaxStacks() {
        return maxStacks;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    public FloatModifiable getDamageReduction() {
        return damageReduction;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getMaxEnemiesHit() {
        return maxEnemiesHit;
    }

    public void setMaxEnemiesHit(int maxEnemiesHit) {
        this.maxEnemiesHit = maxEnemiesHit;
    }

    public int getMaxAlliesHit() {
        return maxAlliesHit;
    }

    public void setMaxAlliesHit(int maxAlliesHit) {
        this.maxAlliesHit = maxAlliesHit;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable hexDamage = new Value.RangedValueCritable(287, 387, 20, 175);
        private final List<Value> values = List.of(hexDamage);

        public Value.RangedValueCritable getHexDamage() {
            return hexDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    static class WeakeningHex {
        private int stacks = 1;

        public int getStacks() {
            return stacks;
        }

        public void setStacks(int stacks) {
            this.stacks = stacks;
        }
    }
}
