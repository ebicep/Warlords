package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.luminary.MercifulHexBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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

public class MercifulHex extends AbstractPiercingProjectile implements WeaponAbilityIcon, Duration, Damages<MercifulHex.DamageValues> {

    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private int hexStacksPerHit = 1;
    private int maxAlliesHit = 2;
    private int subsequentReduction = 40;
    private int ticksBetweenDot = 40;
    private int maxStacks = 3;
    private int tickDuration = 60;

    public MercifulHex() {
        super("Merciful Hex", 0, 60, 2.5, 40, true);
        //TODO maybe inflate y separately
        this.hitboxInflation.setBaseValue(hitboxInflation.getBaseValue() + .75f);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Send a wave of energy forward. The first two allies hit heal ")
                               .append(Heals.formatHealing(healingValues.hexHealing))
                               .append(Component.text(" health (subsequent hit allies are healed for 40%) and receives "))
                               .append(Component.text(hexStacksPerHit, NamedTextColor.BLUE))
                               .append(Component.text(" stack" + (hexStacksPerHit != 1 ? "s" : "") + " of Merciful Hex. The first enemy hit takes "))
                               .append(Damages.formatDamage(damageValues.hexDamage))
                               .append(Component.text(" damage. Also heal yourself for "))
                               .append(Heals.formatHealing(healingValues.hexSelfHealing))
                               .append(Component.text(". If Merciful Hex hits a target, you receive "))
                               .append(Component.text(hexStacksPerHit, NamedTextColor.BLUE))
                               .append(Component.text(" stack of Merciful Hex.\n\nEach stack of Merciful Hex heals "))
                               .append(Heals.formatHealing(healingValues.hexDOTHealing))
                               .append(Component.text(" health every "))
                               .append(Component.text(format(ticksBetweenDot / 20f), NamedTextColor.GOLD))
                               .append(Component.text("seconds for "))
                               .append(Component.text(format(tickDuration / 10f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Stacks up to"))
                               .append(Component.text(maxStacks, NamedTextColor.BLUE))
                               .append(Component.text(" times.\n\nHas a maximum range of "))
                               .append(Component.text(format(maxDistance), NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new MercifulHexBranch(abilityTree, this);
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

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity shooter) {
        boolean activate = super.onActivate(shooter);
        shooter.addInstance(InstanceBuilder
                .healing()
                .ability(this)
                .source(shooter)
                .value(healingValues.hexSelfHealing)
        );
        return activate;
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);

        Location startingLocation = projectile.getStartingLocation();
        LocationBuilder location = new LocationBuilder(startingLocation)
                .pitch(0);
        ItemDisplay display = startingLocation.getWorld().spawn(location, ItemDisplay.class, itemDisplay -> {
            itemDisplay.setItemStack(new ItemStack(Material.WARPED_FENCE));
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
                            Particle.SPELL,
                            new LocationBuilder(projectile.getCurrentLocation()).addY(-.2).left(1f),
                            1
                    );
                    EffectUtils.displayParticle(
                            Particle.SPELL,
                            new LocationBuilder(projectile.getCurrentLocation()).addY(-.2).right(1f),
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

    private boolean hitProjectile(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit) {
        if (projectile.getHit().contains(hit)) {
            return false;
        }

        WarlordsEntity wp = projectile.getShooter();
        getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
        if (hit.onHorse()) {
            numberOfDismounts++;
        }

        List<WarlordsEntity> hits = projectile.getHit();
        if (hits.size() == 1) {
            giveMercifulHex(wp, wp);
        }

        boolean isTeammate = hit.isTeammate(wp);
        if (isTeammate) {
            int teammatesHit = (int) hits.stream().filter(we -> we.isTeammate(wp)).count();
            float reduction = teammatesHit <= maxAlliesHit ? 1 : convertToPercent(subsequentReduction);
            hit.addInstance(InstanceBuilder
                    .healing()
                    .ability(this)
                    .source(wp)
                    .min(healingValues.hexHealing.getMinValue() * reduction)
                    .max(healingValues.hexHealing.getMaxValue() * reduction)
                    .crit(healingValues.hexHealing)
            );
            if (teammatesHit > maxAlliesHit) {
                return false;
            }
            giveMercifulHex(wp, hit);
            if (pveMasterUpgrade) {
                giveMercifulHex(wp, hit);
            }
        } else {
            int enemiesHit = (int) hits.stream().filter(we -> we.isEnemy(wp)).count();
            float reduction = enemiesHit == 1 ? 1 : convertToPercent(subsequentReduction);
            hit.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(wp)
                    .min(damageValues.hexDamage.getMinValue() * reduction)
                    .max(damageValues.hexDamage.getMaxValue() * reduction)
                    .crit(damageValues.hexDamage)
            );
        }
        return true;
    }

    public static void giveMercifulHex(WarlordsEntity from, WarlordsEntity to) {
        MercifulHex fromHex = getFromHex(from);
        int tickDuration = fromHex.getTickDuration();
        HealingValues values = fromHex.healingValues;
        int ticksBetweenDot = fromHex.getTicksBetweenDot();
        String name = fromHex.getName();
        to.getCooldownManager().limitCooldowns(RegularCooldown.class, MercifulHex.class, 3);
        to.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Merciful Hex",
                "MHEX",
                MercifulHex.class,
                new MercifulHex(),
                from,
                CooldownTypes.BUFF,
                cooldownManager -> {
                    to.addInstance(InstanceBuilder
                            .healing()
                            .ability(fromHex)
                            .source(from)
                            .value(values.hexDOTHealing)
                            .flags(InstanceFlags.DOT)
                    );
                },
                tickDuration * 2, // base add 20 to delay damage by a second
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % ticksBetweenDot == 0 && ticksElapsed != 0) {
                        to.addInstance(InstanceBuilder
                                .healing()
                                .ability(fromHex)
                                .source(from)
                                .value(values.hexDOTHealing)
                                .flags(InstanceFlags.DOT)
                        );
                    }
                })
        ) {
            @Override
            public PlayerNameData addPrefixFromOther() {
                boolean flag = new CooldownFilter<>(to, RegularCooldown.class).filterCooldownClass(PoisonousHex.class).stream().count() == fromHex.maxStacks;
                return new PlayerNameData(
                        Component.text("MHEX", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, flag),
                        we -> we.isTeammate(from) && we.getSpecClass() == Specializations.LUMINARY
                );
            }
        });
    }

    @Nonnull
    public static MercifulHex getFromHex(WarlordsEntity from) {
        return from.getSpec().getAbilities().stream()
                   .filter(MercifulHex.class::isInstance)
                   .map(MercifulHex.class::cast)
                   .findFirst()
                   .orElse(new MercifulHex());
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

    public int getSubsequentReduction() {
        return subsequentReduction;
    }

    public void setSubsequentReduction(int subsequentReduction) {
        this.subsequentReduction = subsequentReduction;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable hexDamage = new Value.RangedValueCritable(186, 250, 20, 180);
        private final List<Value> values = List.of(hexDamage);

        public Value.RangedValueCritable getHexDamage() {
            return hexDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.RangedValueCritable hexHealing = new Value.RangedValueCritable(229, 313, 20, 180);
        private final Value.RangedValueCritable hexSelfHealing = new Value.RangedValueCritable(197, 266, 20, 180);
        private final Value.RangedValue hexDOTHealing = new Value.RangedValue(20, 30);
        private final List<Value> values = List.of(hexHealing, hexSelfHealing, hexDOTHealing);

        public Value.RangedValueCritable getHexHealing() {
            return hexHealing;
        }

        public Value.RangedValueCritable getHexSelfHealing() {
            return hexSelfHealing;
        }

        public Value.RangedValue getHexDOTHealing() {
            return hexDOTHealing;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}
