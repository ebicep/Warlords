package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.PlayerNameInstance;
import com.ebicep.warlords.pve.mobs.player.TestDummy;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.luminary.MercifulHexBranch;
import com.ebicep.warlords.util.bukkit.EntitiesUtils;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MercifulHex extends AbstractPiercingProjectile<MercifulHex, MercifulHex.MercifulHexStats> implements WeaponAbilityIcon, Duration, Damages<MercifulHex.DamageValues> {

    public static final ItemStack ITEM_STACK = new ItemStack(Material.LILY_OF_THE_VALLEY);
    private final MercifulHexStats stats = new MercifulHexStats();
    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private int hexStacksPerHit = 1;
    private int hexStacksPerHitAfter = 1;
    private int maxAlliesHit = 2;
    private int maxEnemiesHit = 1;
    private int ticksBetweenDot = 40;
    private int maxStacks = 3;
    private int tickDuration = 60;

    public MercifulHex() {
        super(AbstractAbilityBuilder.create("mercifulHex").pvp());
        //TODO maybe inflate y separately
        this.hitboxInflation.setBaseValue(hitboxInflation.getBaseValue() + .75f);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public MercifulHexStats getAbilityStats() {
        return stats;
    }

    @Nonnull
    public static MercifulHex getFromHex(WarlordsEntity from) {
        return from.getSpec()
                   .getAbilities()
                   .stream()
                   .filter(MercifulHex.class::isInstance)
                   .map(MercifulHex.class::cast)
                   .findFirst()
                   .orElseGet(() -> {
                       MercifulHex mercifulHex = new MercifulHex();
                       mercifulHex.init(mercifulHex.getBuilder());
                       return mercifulHex;
                   });
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Send a wave of energy forward. The first ")
                .text(maxAlliesHit, NamedTextColor.BLUE)
                .text(" ally hit heals ")
                .heal(healingValues.hexHealing)
                .text(" health and receives ")
                .text(hexStacksPerHit, NamedTextColor.BLUE)
                .text(" stack" + (hexStacksPerHit != 1 ? "s" : "") + " of ")
                .text("MHEX", NamedTextColor.DARK_GREEN)
                .text("; the first ally hit after receives ")
                .text(hexStacksPerHitAfter, NamedTextColor.BLUE)
                .text(" stack" + (hexStacksPerHitAfter != 1 ? "s" : "") + " of ")
                .text("MHEX", NamedTextColor.DARK_GREEN)
                .text(". " + (maxEnemiesHit == Integer.MAX_VALUE ? "Enemies hit take " : "The first enemy hit takes "))
                .damage(damageValues.hexDamage)
                .text(" damage. You also heal for ")
                .heal(healingValues.hexSelfHealing)
                .text(" and receive ")
                .text(hexStacksPerHit, NamedTextColor.BLUE)
                .text(" stack" + (hexStacksPerHit != 1 ? "s" : "") + " of ")
                .text("MHEX", NamedTextColor.DARK_GREEN)
                .text(".")
                .emptyLine()
                .text("Each stack of ")
                .text("MHEX", NamedTextColor.DARK_GREEN)
                .text(" heals ")
                .heal(healingValues.hexDOTHealing)
                .text(" health every ")
                .durationTicks(ticksBetweenDot)
                .text(" for ")
                .durationTicks(tickDuration * 2)
                .text(". Stacks up to")
                .text(maxStacks, NamedTextColor.BLUE)
                .text(" times.")
                .maxRange(maxDistance)
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new MercifulHexBranch(abilityTree, this);
    }

    public int getMaxStacks() {
        return maxStacks;
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.hexStacksPerHit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hexStacksPerHit"), int.class);
        this.hexStacksPerHitAfter = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hexStacksPerHitAfter"), int.class);
        this.maxAlliesHit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxAlliesHit"), int.class);
        this.maxEnemiesHit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxEnemiesHit"), int.class);
        this.ticksBetweenDot = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("ticksBetweenDot"), int.class);
        this.maxStacks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxStacks"), int.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity shooter) {
        boolean activate = super.onActivateInternal(shooter);
        shooter.addInstance(InstanceBuilder.healing().ability(this).source(shooter).value(healingValues.hexSelfHealing));
        for (int i = 0; i < hexStacksPerHit; i++) {
            giveMercifulHex(shooter, shooter);
        }
        return activate;
    }

    public HealingValues getHealValues() {
        return healingValues;
    }

    public int getMaxAlliesHit() {
        return maxAlliesHit;
    }

    public void setMaxAlliesHit(int maxAlliesHit) {
        this.maxAlliesHit = maxAlliesHit;
    }

    public int getMaxEnemiesHit() {
        return maxEnemiesHit;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable hexDamage = new Value.RangedValueCritable(186, 250, 20, 180);

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

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable hexHealing = new Value.RangedValueCritable(229, 313, 20, 180);

        private Value.RangedValueCritable hexSelfHealing = new Value.RangedValueCritable(160, 219, 20, 180);

        private Value.RangedValue hexDOTHealing = new Value.RangedValue(20, 30);

        private List<Value> values = List.of(hexHealing, hexSelfHealing, hexDOTHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.hexHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("hexHealing"), Value.RangedValueCritable.class);
            this.hexSelfHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameHealing("hexSelfHealing"),
                    Value.RangedValueCritable.class
            );
            this.hexDOTHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("hexDOTHealing"), Value.RangedValue.class);
            this.values = List.of(hexHealing, hexSelfHealing, hexDOTHealing);
        }

        public Value.RangedValueCritable getHexHealing() {
            return hexHealing;
        }

        public Value.RangedValueCritable getHexSelfHealing() {
            return hexSelfHealing;
        }

        public Value.RangedValue getHexDOTHealing() {
            return hexDOTHealing;
        }

    }

    public void setMaxEnemiesHit(int maxEnemiesHit) {
        this.maxEnemiesHit = maxEnemiesHit;
    }

    private boolean hitProjectile(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit) {
        if (projectile.getHit().contains(hit)) {
            return false;
        }
        WarlordsEntity wp = projectile.getShooter();
        getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
        List<WarlordsEntity> hits = projectile.getHit();
        boolean isTeammate = hit.isTeammate(wp);
        if (isTeammate) {
            int teammatesHit = (int) hits.stream().filter(we -> we.isTeammate(wp)).count();
            if (teammatesHit > maxAlliesHit) {
                if (teammatesHit == maxAlliesHit + 1) {
                    for (int i = 0; i < hexStacksPerHitAfter; i++) {
                        giveMercifulHex(wp, hit);
                    }
                }
                return false;
            }
            hit.addInstance(InstanceBuilder
                    .healing()
                    .ability(this)
                    .source(wp)
                    .value(healingValues.hexHealing)
            );
            for (int i = 0; i < hexStacksPerHit; i++) {
                giveMercifulHex(wp, hit);
            }
        } else {
            int enemiesHit = (int) hits.stream().filter(we -> we.isEnemy(wp)).count();
            if (enemiesHit > maxEnemiesHit) {
                return false;
            }
            hit.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(wp)
                    .value(damageValues.hexDamage)
            );
            if (hit.onHorse()) {
                stats.addNumberOfDismounts();
            }
        }
        stats.addPlayersHit();
        return true;
    }


    @Override
    protected boolean nonCollisionCheck(
            AbstractPiercingProjectile<MercifulHex, MercifulHexStats>.InternalProjectile projectile,
            Location currentLocation,
            Vector speed,
            WarlordsEntity shooter,
            WarlordsEntity wp
    ) {
        return super.nonCollisionCheck(projectile, currentLocation, speed, shooter, wp) ||
                (wp instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof PlayerMob && !(warlordsNPC.getMob() instanceof TestDummy));
    }

    public int getHexStacksPerHitAfter() {
        return hexStacksPerHitAfter;
    }

    public void setHexStacksPerHitAfter(int hexStacksPerHitAfter) {
        this.hexStacksPerHitAfter = hexStacksPerHitAfter;
    }

    public int getHexStacksPerHit() {
        return hexStacksPerHit;
    }

    public void setHexStacksPerHit(int hexStacksPerHit) {
        this.hexStacksPerHit = hexStacksPerHit;
    }

    public static class MercifulHexStats extends AbstractPiercingProjectileStats<MercifulHex, MercifulHexStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.removeIf(abilityStatDisplay -> abilityStatDisplay.name().equals("Direct Hits"));
            return statsDisplay;
        }

        @Override
        public MercifulHexStats merge(MercifulHexStats other, int multiplier) {
            MercifulHexStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<MercifulHexStats> getClazz() {
            return MercifulHexStats.class;
        }

        @Override
        public MercifulHexStats create() {
            return new MercifulHexStats();
        }

    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        Location startingLocation = projectile.getStartingLocation();
        LocationBuilder location = new LocationBuilder(startingLocation).pitch(0);
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
                LocationBuilder location = new LocationBuilder(currentLocation).pitch(0);
                display.teleport(location);
                if (projectile.getTicksLived() % 3 == 0) {
                    EffectUtils.displayParticle(Particle.EFFECT, new LocationBuilder(projectile.getCurrentLocation()).addY(-.2).left(1f), 1);
                    EffectUtils.displayParticle(Particle.EFFECT, new LocationBuilder(projectile.getCurrentLocation()).addY(-.2).right(1f), 1);
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
        return "arcanist.mercifulhexalt.activation";
    }


    @Override
    protected float getSoundVolume() {
        return 2;
    }


    @Override
    protected float getSoundPitch() {
        return 1.2f;
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
            } else {
                break;
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
        return new LocationBuilder(startingLocation.clone()).addY(-.3).backward(-.5f);
    }


    public static void giveMercifulHex(WarlordsEntity from, WarlordsEntity to) {
        MercifulHex fromHex = getFromHex(from);
        int tickDuration = fromHex.getTickDuration();
        HealingValues values = fromHex.healingValues;
        int ticksBetweenDot = fromHex.getTicksBetweenDot();
        String name = fromHex.getName();
        to.getCooldownManager().limitCooldowns(RegularCooldown.class, MercifulHex.class, 3);
        to.getCooldownManager().addCooldown(new RegularCooldown<>("Merciful Hex", "MHEX", MercifulHex.class, new MercifulHex(), from, CooldownTypes.BUFF, cooldownManager -> {
            to.addInstance(InstanceBuilder.healing().ability(fromHex).source(from).value(values.hexDOTHealing).flags(InstanceFlags.DOT));
        }, // base add 20 to delay damage by a second
                tickDuration * 2, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
            if (ticksElapsed % ticksBetweenDot == 0 && ticksElapsed != 0) {
                to.addInstance(InstanceBuilder.healing().ability(fromHex).source(from).value(values.hexDOTHealing).flags(InstanceFlags.DOT));
            }
        })
        ) {

            @Override
            public PlayerNameData addPrefixFromOther() {
                return PlayerNameData.dynamic(
                        () -> {
                            boolean flag = new CooldownFilter<>(to, RegularCooldown.class).filterCooldownClass(PoisonousHex.class).stream().count() == fromHex.maxStacks;
                            return Component.text("MHEX", NamedTextColor.DARK_GREEN).decoration(TextDecoration.BOLD, flag);
                        },
                        we -> we.isTeammate(from) && we.getSpecClass() == Specializations.LUMINARY
                );
            }

            @Override
            public TextColor customActionBarColor() {
                return NamedTextColor.DARK_GREEN;
            }
        });
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
