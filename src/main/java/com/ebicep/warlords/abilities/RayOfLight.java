package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.luminary.RayOfLightBranch;
import com.ebicep.warlords.util.bukkit.EntitiesUtils;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RayOfLight extends AbstractBeam<RayOfLight, RayOfLight.RayOfLightStats> implements Heals<RayOfLight.HealingValues> {

    public static final ItemStack BEAM_ITEM = new ItemStack(Material.WITHER_ROSE);
    private static final ItemStack ORB_ITEM = new ItemStack(Material.GLOWSTONE);
    private static final int ORB_TICK_DURATION = 240;
    private static final int ORB_BEAM_INTERVAL_TICKS = 60;
    private static final int ORB_BEAM_COUNT = 2;
    private static final float ORB_HEIGHT = 2.5f;
    private final RayOfLightStats stats = new RayOfLightStats();
    private final HealingValues healingValues = new HealingValues();
    private boolean removeDebuffs = true;

    public RayOfLight() {
        super(AbstractAbilityBuilder.create("rayOfLight").pvp());
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity shooter) {
        beamPlayer(shooter, shooter);
        Utils.playGlobalSound(shooter.getLocation(), "arcanist.rayoflightalt.activation", 2, 0.9f);
        boolean activated = super.onActivateInternal(shooter);
        if (pveMasterUpgrade2) {
            spawnRadiantOrb(shooter);
        }
        return activated;
    }

    @Override
    public Pair<Float, Float> getChainAnimationData(int distance) {
        float increment = distance;
        return new Pair<>(increment * .55f, increment);
    }

    @Override
    public ItemStack getBeamItem() {
        return BEAM_ITEM;
    }

    private void spawnRadiantOrb(@Nonnull WarlordsEntity shooter) {
        shooter.getCooldownManager().limitCooldowns(RegularCooldown.class, RadiantOrbData.class, 1);
        Location spawnLocation = getOrbLocation(shooter, 0);
        World world = spawnLocation.getWorld();
        if (world == null) {
            return;
        }
        ItemDisplay orb = world.spawn(spawnLocation, ItemDisplay.class, display -> {
            display.setItemStack(ORB_ITEM);
            display.setBillboard(Display.Billboard.FIXED);
            display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
            display.setBrightness(EntitiesUtils.MAX_BRIGHTNESS);
            display.setPersistent(false);
            display.setInvulnerable(true);
            display.setGravity(false);
            display.setTeleportDuration(2);
            display.setTransformation(new Transformation(
                    new Vector3f(),
                    new Quaternionf(),
                    new Vector3f(0.7f, 0.7f, 0.7f),
                    new Quaternionf()
            ));
        });
        RadiantOrbData data = new RadiantOrbData(orb);
        shooter.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Radiant Orb",
                "ORB",
                RadiantOrbData.class,
                data,
                shooter,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    ItemDisplay display = data.getOrb();
                    if (!display.isDead()) {
                        display.remove();
                    }
                },
                ORB_TICK_DURATION,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    ItemDisplay display = data.getOrb();
                    if (display.isDead()) {
                        return;
                    }
                    Location orbLocation = getOrbLocation(shooter, ticksElapsed);
                    display.teleport(orbLocation);
                    if (ticksElapsed % 5 == 0) {
                        EffectUtils.displayParticle(Particle.END_ROD, orbLocation, 1, 0.15, 0.15, 0.15, 0.01);
                    }
                    if (ticksElapsed % ORB_BEAM_INTERVAL_TICKS == 0) {
                        fireOrbBeams(shooter, orbLocation);
                    }
                })
        ));
    }

    private void fireOrbBeams(@Nonnull WarlordsEntity shooter, @Nonnull Location orbLocation) {
        float range = maxDistance.getCalculatedValue();
        List<WarlordsEntity> targets = PlayerFilter
                .entitiesAround(orbLocation, range, range, range)
                .aliveTeammatesOf(shooter)
                .filter(WarlordsPlayer.class::isInstance)
                .closestFirst(orbLocation)
                .limit(ORB_BEAM_COUNT)
                .toList();
        int distance = (int) range;
        for (WarlordsEntity target : targets) {
            Location start = new LocationBuilder(orbLocation).faceTowards(target.getEyeLocation());
            Location end = Utils.getTargetLocation(start, distance).clone().add(.5, .5, .5);
            Pair<Float, Float> animationData = getChainAnimationData((int) Math.ceil(start.distance(end)));
            EffectUtils.playChainAnimation(shooter.getGame(), start, end, getBeamItem(), animationData.getA(), animationData.getB(), 10);
            fire(shooter, start);
        }
    }

    private Location getOrbLocation(@Nonnull WarlordsEntity shooter, int ticksElapsed) {
        Location location = shooter.getLocation().add(0, ORB_HEIGHT + Math.sin(ticksElapsed / 8d) * 0.15, 0);
        location.setYaw(ticksElapsed * 4f);
        return location;
    }

    private void beamPlayer(@Nonnull WarlordsEntity hit, WarlordsEntity wp) {
        int hexStacks = (int) new CooldownFilter<>(hit, RegularCooldown.class).filterCooldownClass(MercifulHex.class).stream().count();
        boolean hasDivineBlessing = wp.getCooldownManager().hasCooldown(DivineBlessing.DivineBlessingData.class);
        if (!hasDivineBlessing) {
            hit.getCooldownManager().removeCooldown(MercifulHex.class, false);
        } else {
            wp.doOnStaticAbility(DivineBlessing.class,
                    divineBlessing -> divineBlessing.getAbilityStats().setHexesNotConsumed(divineBlessing.getAbilityStats().getHexesNotConsumed() + hexStacks)
            );
        }
        boolean maxStacks = hexStacks >= 3;
        if (maxStacks && removeDebuffs) {
            hit.getCooldownManager().removeDebuffCooldowns();
        }
        float multiplier = switch (hexStacks) {
            case 0 -> 1f;
            case 1 -> 1.25f;
            case 2 -> 1.5f;
            default -> 2f;
        };
        getAbilityStats().getStacksRemoved().merge(hexStacks, 1, Integer::sum);
        if (pveMasterUpgrade) {
            hit.getCooldownManager().addCooldown(new RegularCooldown<>(name, "RAY", RayOfLight.class, new RayOfLight(), wp, CooldownTypes.ABILITY, cooldownManager -> {
            }, cooldownManager -> {
            }, 100
            ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, maxStacks ? 1.2f : 1.05f);
                    }
            ));
        }
        hit.addInstance(InstanceBuilder.healing()
                                       .ability(this)
                                       .source(wp)
                                       .min(healingValues.rayHealing.getMinValue() * multiplier)
                                       .max(healingValues.rayHealing.getMaxValue() * multiplier)
                                       .crit(healingValues.rayHealing));
    }

    @Override
    public RayOfLightStats getAbilityStats() {
        return stats;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Unleash a concentrated beam of holy light, healing ")
                                               .heal(healingValues.rayHealing)
                                               .text(" health to all allies hit and cleansing all ")
                                               .text("de-buffs", NamedTextColor.DARK_RED)
                                               .text(" from allies with max stacks of ")
                                               .text("MHEX", NamedTextColor.DARK_GREEN)
                                               .text(". If the target is affected by ")
                                               .text("MHEX", NamedTextColor.DARK_GREEN)
                                               .text(" the healing given is increased by ")
                                               .percent(25, NamedTextColor.GREEN)
                                               .text("/")
                                               .percent(50, NamedTextColor.GREEN)
                                               .text("/")
                                               .percent(100, NamedTextColor.GREEN)
                                               .text(" relative to the number of stacks and all stacks are removed.")
                                               .maxRange(maxDistance)
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new RayOfLightBranch(abilityTree, this);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

    @Nullable
    @Override
    protected String getActivationSound() {
        return "arcanist.energyseer.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 1.1f;
    }

    @Override
    protected void playEffect(@Nonnull InternalProjectile projectile) {
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
    }

    @Override
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        WarlordsEntity wp = projectile.getShooter();
        if (hit.isTeammate(wp) && !projectile.getHit().contains(hit)) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            beamPlayer(hit, wp);
        }
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public void setRemoveDebuffs(boolean removeDebuffs) {
        this.removeDebuffs = removeDebuffs;
    }

    public static class RadiantOrbData {

        private final ItemDisplay orb;

        public RadiantOrbData(ItemDisplay orb) {
            this.orb = orb;
        }

        public ItemDisplay getOrb() {
            return orb;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable rayHealing = new Value.RangedValueCritable(389, 523, 20, 150);

        private List<Value> values = List.of(rayHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.rayHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("rayHealing"), Value.RangedValueCritable.class);
            this.values = List.of(rayHealing);
        }

        public Value.RangedValueCritable getRayHealing() {
            return rayHealing;
        }

    }

    public static class RayOfLightStats extends AbstractBeamStats<RayOfLight, RayOfLightStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public RayOfLightStats merge(RayOfLightStats other, int multiplier) {
            RayOfLightStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<RayOfLightStats> getClazz() {
            return RayOfLightStats.class;
        }

        @Override
        public RayOfLightStats create() {
            return new RayOfLightStats();
        }

    }

}
