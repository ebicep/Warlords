package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.CustomInstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.player.ingame.instances.type.PlayerNameInstance;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.sentinel.FortifyingHexBranch;
import com.ebicep.warlords.util.bukkit.EntitiesUtils;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FortifyingHex extends AbstractPiercingProjectile<FortifyingHex, FortifyingHex.FortifyingHexStats> implements WeaponAbilityIcon, Duration, Damages<FortifyingHex.DamageValues> {

    private final FortifyingHexStats stats = new FortifyingHexStats();
    private final DamageValues damageValues = new DamageValues();
    private FloatModifiable damageReduction = new FloatModifiable(4);
    private float damageReductionFlagMultiplier;
    private int maxEnemiesHit = 1;
    private int maxAlliesHit = 2;
    private int maxFullDistance = 40;
    private int tickDuration = 120;
    private int hexStacksPerHit = 1;
    private int maxStacks = 3;

    public FortifyingHex() {
        super(AbstractAbilityBuilder.create("fortifyingHex").pvp());
        this.hitboxInflation.setBaseValue(hitboxInflation.getBaseValue() + .4f);
    }

    public static void giveFortifyingHex(WarlordsEntity from, WarlordsEntity to) {
        FortifyingHex fromHex = getFromHex(from);
        String hexName = fromHex.getName();
        int maxStacks = fromHex.getMaxStacks();
        int duration = fromHex.getTickDuration();
        to.getCooldownManager().limitCooldowns(RegularCooldown.class, FortifyingHexData.class, maxStacks);
        FortifyingHexData data = new FortifyingHexData(fromHex.getDamageReduction().getCalculatedValue(), fromHex.getDamageReductionFlagMultiplier());
        RegularCooldown<FortifyingHexData> cd = new RegularCooldown<>(
                hexName,
                "FHEX",
                FortifyingHexData.class,
                data,
                from,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                duration
        ) {
            @Override
            public PlayerNameData addPrefixFromOther() {
                return PlayerNameData.dynamic(
                        () -> {
                            boolean flag = new CooldownFilter<>(to, RegularCooldown.class).filterCooldownClass(PoisonousHex.class).stream().count() == fromHex.maxStacks;
                            return Component.text("FHEX", NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, flag);
                        },
                        we -> we.isTeammate(from) && we.getSpecClass() == Specializations.SENTINEL
                );
            }

            @Override
            public TextColor customActionBarColor() {
                return NamedTextColor.YELLOW;
            }
        };
        cd.addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
            currentDamageValue.addModifier(
                    FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, hexName + " " + Integer.toHexString(cd.hashCode()),
                    -data.damageReduction * (event.getWarlordsEntity().hasFlag() ? data.damageReductionFlagMultiplier : 1) / 100f,
                    contribution -> fromHex.getAbilityStats().damageReduced += Math.abs(contribution)
                    );
                }
        );
        to.getCooldownManager().addCooldown(cd);
        from.playSound(from.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        if (from != to) {
            from.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" Your ", NamedTextColor.GRAY))
                    .append(Component.text("Fortifying Hex", NamedTextColor.YELLOW))
                    .append(Component.text(" is now protecting " + to.getName() + "!", NamedTextColor.GRAY)));
            to.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" " + from.getName() + " is now protecting you with their ", NamedTextColor.GRAY))
                    .append(Component.text("Fortifying Hex", NamedTextColor.YELLOW))
                    .append(Component.text("!", NamedTextColor.GRAY)));
        }
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Fling a wave of protective energy forward, hitting ")
                .text(maxEnemiesHit, NamedTextColor.BLUE)
                .text((maxEnemiesHit == 1 ? " enemy" : " enemies") + " and ")
                .text(maxAlliesHit, NamedTextColor.BLUE)
                .text((maxAlliesHit == 1 ? " ally" : " allies") + ". Enemies take ")
                .damage(damageValues.hexDamage)
                .text(" damage. You and allies receive ")
                .text(hexStacksPerHit, NamedTextColor.BLUE)
                .text(" stack" + (hexStacksPerHit != 1 ? "s" : "") + " of ")
                .text("FHEX", NamedTextColor.YELLOW)
                .text(".")
                .emptyLine()
                .text("Each stack of ")
                .text("FHEX", NamedTextColor.YELLOW)
                .text(" lasts ")
                .durationTicks(tickDuration)
                .text(" and grants")
                .percent(damageReduction, AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" damage reduction, increased by ")
                .text(format(damageReductionFlagMultiplier) + "x", AbilityDescriptionBuilder.COLOR_BROWN)
                .text("on flag carriers. Stacks up to")
                .text(maxStacks, NamedTextColor.BLUE)
                .text(" times.")
                .maxRange(maxFullDistance)
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new FortifyingHexBranch(abilityTree, this);
    }

    @Override
    public FortifyingHexStats getAbilityStats() {
        return stats;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
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

        private Value.RangedValueCritable hexDamage = new Value.RangedValueCritable(271, 365, 20, 175);

        private List<Value> values = List.of(hexDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.hexDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("hexDamage"), Value.RangedValueCritable.class);
            this.values = List.of(hexDamage);
        }

        public Value.RangedValueCritable getHexDamage() {
            return hexDamage;
        }

    }

     static class WeakeningHex {

    }

    public static class FortifyingHexData {

        private final float damageReductionFlagMultiplier;
        private float damageReduction;

        public FortifyingHexData(float damageReduction, float damageReductionFlagMultiplier) {
            this.damageReduction = damageReduction;
            this.damageReductionFlagMultiplier = damageReductionFlagMultiplier;
        }

        public float getDamageReduction() {
            return damageReduction;
        }

        public void setDamageReduction(float damageReduction) {
            this.damageReduction = damageReduction;
        }

    }

    private void hitEnemy(@Nonnull WarlordsEntity hit, WarlordsEntity wp, float toReduceBy, InternalProjectile projectile) {
        hit.addInstance(InstanceBuilder.damage()
                                       .ability(this)
                                       .source(wp)
                                       .min(damageValues.hexDamage.getMinValue() * toReduceBy)
                                       .max(damageValues.hexDamage.getMaxValue() * toReduceBy)
                                       .crit(damageValues.hexDamage)
                                       .customFlags(new CustomInstanceFlags.ProjectileHitInstanceFlag(projectile)));
        if (pveMasterUpgrade2) {
            hit.getCooldownManager().limitCooldowns(RegularCooldown.class, WeakeningHex.class, 4);
            WeakeningHex data = new WeakeningHex();
            hit.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Weakening Hex",
                    "WHEX",
                    WeakeningHex.class,
                    data,
                    wp,
                    CooldownTypes.LOW_LEVEL_DEBUFF,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                    },
                    6 * 20
            ).addModifier(Modifier.INCOMING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                int stacks = (int) new CooldownFilter<>(hit, RegularCooldown.class)
                        .filterCooldownClass(WeakeningHex.class)
                        .stream()
                        .count();
                currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,"Weakening Hex", (1 + 0.05f * stacks));
            }));
        }
        stats.addPlayersHit();
    }

    public static class FortifyingHexStats extends AbstractPiercingProjectileStats<FortifyingHex, FortifyingHexStats> {

        @Field("damage_reduced")
        private float damageReduced = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.removeIf(abilityStatDisplay -> abilityStatDisplay.name().equals("Direct Hits"));
            statsDisplay.add(new AbilityStatDisplay("Damage Reduced", damageReduced));
            return statsDisplay;
        }

        @Override
        public FortifyingHexStats merge(FortifyingHexStats other, int multiplier) {
            FortifyingHexStats stats = super.merge(other, multiplier);
            stats.damageReduced = this.damageReduced + other.damageReduced * multiplier;
            return stats;
        }

        @Override
        public Class<FortifyingHexStats> getClazz() {
            return FortifyingHexStats.class;
        }

        @Override
        public FortifyingHexStats create() {
            return new FortifyingHexStats();
        }

    }


    @Nonnull
    public static FortifyingHex getFromHex(WarlordsEntity from) {
        return from.getSpec()
                   .getAbilities()
                   .stream()
                   .filter(FortifyingHex.class::isInstance)
                   .map(FortifyingHex.class::cast)
                   .findFirst()
                   .orElseGet(() -> {
                       FortifyingHex fortifyingHex = new FortifyingHex();
                       fortifyingHex.init(fortifyingHex.getBuilder());
                       return fortifyingHex;
                   });
    }


    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.damageReduction = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReduction"), float.class));
        this.damageReductionFlagMultiplier = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                builder.getAppendedFieldName("damageReductionFlagMultiplier"),
                float.class
        );
        this.maxEnemiesHit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxEnemiesHit"), int.class);
        this.maxAlliesHit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxAlliesHit"), int.class);
        this.maxFullDistance = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxFullDistance"), int.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.hexStacksPerHit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hexStacksPerHit"), int.class);
        this.maxStacks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxStacks"), int.class);
    }


    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        Location startingLocation = projectile.getStartingLocation();
        LocationBuilder location = new LocationBuilder(startingLocation).pitch(0);
        ItemDisplay display = startingLocation.getWorld().spawn(location, ItemDisplay.class, itemDisplay -> {
                    itemDisplay.setItemStack(new ItemStack(Material.WARPED_DOOR));
                    itemDisplay.setTeleportDuration(1);
                    itemDisplay.setBrightness(EntitiesUtils.MAX_BRIGHTNESS);
                    itemDisplay.setTransformation(new Transformation(new Vector3f(),
                            new AxisAngle4f((float) Math.toRadians(startingLocation.getPitch()), 1, 0, 0),
                            new Vector3f(1f),
                            new AxisAngle4f()
                    ));
                }
        );
        projectile.addTask(new InternalProjectileTask() {

            @Override
            public void run(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                Location currentLocation = projectile.getCurrentLocation();
                LocationBuilder location = new LocationBuilder(currentLocation).pitch(0);
                display.teleport(location);
                if (projectile.getTicksLived() % 3 == 0) {
                    EffectUtils.displayParticle(Particle.END_ROD, new LocationBuilder(projectile.getCurrentLocation()).addY(.875).left(.8f), 1);
                    EffectUtils.displayParticle(Particle.END_ROD, new LocationBuilder(projectile.getCurrentLocation()).addY(.875).right(.8f), 1);
                }
            }

            @Override
            public void onDestroy(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                display.remove();
                Utils.playGlobalSound(projectile.getCurrentLocation(), "shaman.chainheal.activation", 2, 2);
                EffectUtils.displayParticle(Particle.EXPLOSION, projectile.getCurrentLocation(), 1, 0, 0, 0, 0.7);
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


    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
    }


    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        if (hit != null) {
            return 0;
        }
        int playersHit = 0;
        for (WarlordsEntity enemy : PlayerFilter.entitiesAround(projectile.getCurrentLocation(), 2, 2, 2).excluding(projectile.getHit())) {
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

    private boolean hitProjectile(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit) {
        if (projectile.getHit().contains(hit) || projectile.getShooter().equals(hit)) {
            return false;
        }
        WarlordsEntity wp = projectile.getShooter();
        Location currentLocation = projectile.getCurrentLocation();
        Location startingLocation = projectile.getStartingLocation();
        getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
        List<WarlordsEntity> hits = projectile.getHit();
        if (hit.isTeammateAlive(wp)) {
            int teammatesHit = (int) hits.stream().filter(we -> we.isTeammate(wp)).count();
            if (teammatesHit > maxAlliesHit) {
                return false;
            }
            giveFortifyingHex(wp, hit);
            stats.addPlayersHit();
        } else {
            int enemiesHit = (int) hits.stream().filter(we -> !we.isTeammate(wp)).count();
            if (enemiesHit > maxEnemiesHit) {
                return false;
            }
            double distanceSquared = startingLocation.distanceSquared(currentLocation);
            float toReduceBy = maxFullDistance * maxFullDistance > distanceSquared ? 1 : (float) (1 - (Math.sqrt(distanceSquared) - maxFullDistance) / 75);
            if (toReduceBy < .2) {
                toReduceBy = .2f;
            }
            hitEnemy(hit, wp, toReduceBy, projectile);
            if (pveMasterUpgrade2) {
                for (WarlordsEntity warlordsEntity : PlayerFilter.entitiesAround(hit, 3, 3, 3).aliveTeammatesOfExcludingSelf(hit).toList()) {
                    hitEnemy(warlordsEntity, wp, toReduceBy, projectile);
                    stats.addPlayersHit();
                }
                EffectUtils.displayParticle(Particle.EXPLOSION, hit.getLocation().add(0, 1, 0), 1, .1, .1, .1, 0);
            }
        }
        return true;
    }


    public float getDamageReductionFlagMultiplier() {
        return damageReductionFlagMultiplier;
    }

    public void setDamageReductionFlagMultiplier(float damageReductionFlagMultiplier) {
        this.damageReductionFlagMultiplier = damageReductionFlagMultiplier;
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity shooter) {
        giveFortifyingHex(shooter, shooter);
        return super.onActivateInternal(shooter);
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

}
