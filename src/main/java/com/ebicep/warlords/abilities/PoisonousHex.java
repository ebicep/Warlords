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
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.PlayerNameInstance;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.conjurer.PoisonousHexBranch;
import com.ebicep.warlords.util.bukkit.EntitiesUtils;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
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

public class PoisonousHex extends AbstractPiercingProjectile<PoisonousHex, PoisonousHex.PoisonousHexStats> implements WeaponAbilityIcon, Duration, Damages<PoisonousHex.DamageValues> {

    public static final ItemStack ITEM_STACK = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
    private final PoisonousHexStats stats = new PoisonousHexStats();
    private final DamageValues damageValues = new DamageValues();
    private int hexStacksPerHit = 1;
    private int maxStacks = 3;
    private int tickDuration = 40;
    private int ticksBetweenDot = 40;
    private int maxEnemiesHit = 2;
    private int tickDurationDot;

    public PoisonousHex() {
        super(AbstractAbilityBuilder.create("poisonousHex").pvp());
        this.shotsFiredAtATime = 2;
        this.setMaxAngleOfShots(26);
        this.forwardTeleportAmount = 1.6f;
        this.hitboxInflation.setBaseValue(hitboxInflation.getBaseValue() + .4f);
    }

    public PoisonousHex(AbstractAbilityBuilder builder) {
        super(builder);
        this.shotsFiredAtATime = 2;
        this.setMaxAngleOfShots(26);
        this.forwardTeleportAmount = 1.6f;
        this.hitboxInflation.setBaseValue(hitboxInflation.getBaseValue() + .4f);
    }

    @Override
    public void updateDescription(Player player) {
        boolean infiniteHit = maxEnemiesHit >= 200;
        description = AbilityDescriptionBuilder
                .create("Throw Hex Fangs in front of you, dealing ")
                .damage(damageValues.hexDamage)
                .text(" damage " + (infiniteHit ? "" : "to up to "))
                .text((infiniteHit ? "infinite" : "" + maxEnemiesHit), NamedTextColor.RED)
                .text(" enemies. Additionally, hit targets receive ")
                .text(hexStacksPerHit, NamedTextColor.BLUE)
                .text(" stack" + (hexStacksPerHit != 1 ? "s" : "") + " of ")
                .text("PHEX", NamedTextColor.DARK_RED)
                .text(".")
                .emptyLine()
                .text("Each stack of ")
                .text("PHEX", NamedTextColor.DARK_RED)
                .text(" deals ")
                .damage(damageValues.hexDOTDamage)
                .text(" damage every ")
                .durationTicks(ticksBetweenDot)
                .text(" for ")
                .durationTicks(tickDuration)
                .text(". Stacks up to ")
                .text(maxStacks, NamedTextColor.BLUE)
                .text(" times.")
                .maxRange(maxDistance)
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new PoisonousHexBranch(abilityTree, this);
    }

    public static void givePoisonousHex(WarlordsEntity from, WarlordsEntity to) {
        if (to.isDead()) {
            return;
        }
        PoisonousHex fromHex = getFromHex(from);
        String hexName = fromHex.getName();
        int tickDuration = fromHex.getTickDuration();
        int tickDurationDot = fromHex.getTickDurationDot();
        int dotTickFrequency = fromHex.getTicksBetweenDot();
        DamageValues values = fromHex.damageValues;
        to.getCooldownManager().limitCooldowns(RegularCooldown.class, PoisonousHex.class, fromHex.getMaxStacks());
        to.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Poisonous Hex",
                "PHEX",
                PoisonousHex.class,
                null,
                from,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (tickDurationDot >= tickDuration) {
                        to.addInstance(InstanceBuilder
                                .damage()
                                .ability(fromHex)
                                .source(from)
                                .value(values.hexDOTDamage)
                                .flags(InstanceFlags.NO_DISMOUNT, InstanceFlags.DOT)
                        );
                    }
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % dotTickFrequency == 0 && ticksElapsed != 0 && tickDurationDot >= ticksElapsed) {
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
                return PlayerNameData.dynamic(
                        () -> {
                            boolean flag = new CooldownFilter<>(to, RegularCooldown.class).filterCooldownClass(PoisonousHex.class).stream().count() == fromHex.maxStacks;
                            return Component.text("PHEX", CooldownTypes.HIGH_LEVEL_DEBUFF_COLOR).decoration(TextDecoration.BOLD, flag);
                        },
                        we -> we.isTeammate(from) && we.getSpecClass() == Specializations.CONJURER
                );
            }

            @Override
            public TextColor customActionBarColor() {
                return CooldownTypes.HIGH_LEVEL_DEBUFF_COLOR;
            }
        });
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public PoisonousHexStats getAbilityStats() {
        return stats;
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.hexStacksPerHit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hexStacksPerHit"), int.class);
        this.maxStacks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxStacks"), int.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.tickDurationDot = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDurationDot"), int.class);
        this.ticksBetweenDot = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("ticksBetweenDot"), int.class);
        this.maxEnemiesHit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxEnemiesHit"), int.class);
    }

    public int getMaxStacks() {
        return maxStacks;
    }

    public int setMaxStacks(int maxStacks) {
        return this.maxStacks = maxStacks;
    }

    public int getMaxEnemiesHit() {
        return maxEnemiesHit;
    }

    public void setMaxEnemiesHit(int maxEnemiesHit) {
        this.maxEnemiesHit = maxEnemiesHit;
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

        getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
        if (hit.onHorse()) {
            stats.addNumberOfDismounts();
        }
        hit.addInstance(InstanceBuilder.damage()
                                       .ability(this)
                                       .source(wp)
                                       .value(damageValues.hexDamage)
                                       .crit(damageValues.hexDamage));
        givePoisonousHex(wp, hit);
        if (projectile.getHit().size() >= maxEnemiesHit) {
            getProjectiles(projectile).forEach(InternalProjectile::cancel);
        }
        stats.addPlayersHit();
        return true;
    }



    public int getTickDurationDot() {
        return tickDurationDot;
    }


    public void setTickDurationDot(int tickDurationDot) {
        this.tickDurationDot = tickDurationDot;
    }


    @Nonnull
    public static PoisonousHex getFromHex(WarlordsEntity from) {
        return from.getSpec()
                   .getAbilities()
                   .stream()
                   .filter(PoisonousHex.class::isInstance)
                   .map(PoisonousHex.class::cast)
                   .findFirst()
                   .orElseGet(() -> {
                       PoisonousHex poisonousHex = new PoisonousHex();
                       poisonousHex.init(poisonousHex.getBuilder());
                       return poisonousHex;
                   });
    }


    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        Location startingLocation = projectile.getStartingLocation();
        LocationBuilder location = new LocationBuilder(startingLocation).pitch(0).yaw(startingLocation.getYaw() - 180);
        ItemDisplay display = startingLocation.getWorld().spawn(location, ItemDisplay.class, itemDisplay -> {
            itemDisplay.setItemStack(ITEM_STACK);
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
                LocationBuilder location = new LocationBuilder(currentLocation).pitch(0).yaw(currentLocation.getYaw() - 180);
                display.teleport(location);
                if (projectile.getTicksLived() % 3 == 0) {
                    EffectUtils.displayParticle(Particle.DUST, projectile.getCurrentLocation(), 1, new Particle.DustOptions(Color.fromRGB(90, 90, 190), 1));
                }
            }

            @Override
            public void onDestroy(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                display.remove();
                Utils.playGlobalSound(projectile.getCurrentLocation(), Sound.ENTITY_EVOKER_FANGS_ATTACK, 0.2f, 2);
                EffectUtils.displayParticle(Particle.EXPLOSION, projectile.getCurrentLocation(), 1, 0, 0, 0, 0.7f);
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


    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
    }


    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        if (hit != null) {
            return 0;
        }
        int playersHit = 0;
        for (WarlordsEntity enemy : PlayerFilter.entitiesAround(projectile.getCurrentLocation(), 2, 2, 2).aliveEnemiesOf(projectile.getShooter()).excluding(projectile.getHit())) {
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

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable hexDamage = new Value.RangedValueCritable(263, 356, 20, 175);

        private Value.RangedValue hexDOTDamage = new Value.RangedValue(25, 35);

        private List<Value> values = List.of(hexDamage, hexDOTDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.hexDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("hexDamage"), Value.RangedValueCritable.class);
            this.hexDOTDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("hexDOTDamage"), Value.RangedValue.class);
            this.values = List.of(hexDamage, hexDOTDamage);
        }

        public Value.RangedValueCritable getHexDamage() {
            return hexDamage;
        }

        public Value.RangedValue getHexDOTDamage() {
            return hexDOTDamage;
        }

    }

    public static class PoisonousHexStats extends AbstractPiercingProjectileStats<PoisonousHex, PoisonousHexStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.removeIf(abilityStatDisplay -> abilityStatDisplay.name().equals("Direct Hits"));
            return statsDisplay;
        }

        @Override
        public PoisonousHexStats merge(PoisonousHexStats other, int multiplier) {
            PoisonousHexStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<PoisonousHexStats> getClazz() {
            return PoisonousHexStats.class;
        }

        @Override
        public PoisonousHexStats create() {
            return new PoisonousHexStats();
        }

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


}
