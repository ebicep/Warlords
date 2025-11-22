package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.conjurer.SoulfireBeamBranch;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoulfireBeam extends AbstractBeam<SoulfireBeam, SoulfireBeam.SoulfireBeamStats> implements Damages<SoulfireBeam.DamageValues> {

    public static final ItemStack BEAM_ITEM = new ItemStack(Material.CRIMSON_FENCE_GATE);
    private final SoulfireBeamStats stats = new SoulfireBeamStats();
    private final Map<InternalProjectile, Integer> maxStacksHit = new HashMap<>();
    private final DamageValues damageValues = new DamageValues();

    public SoulfireBeam() {
        super(AbstractAbilityBuilder.create("soulfireBeam").pvp());
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity shooter) {
        shooter.playSound(shooter.getLocation(), "mage.firebreath.activation", 2, 0.6f);
        return super.onActivateInternal(shooter);
    }

    @Override
    public Pair<Float, Float> getChainAnimationData(int distance) {
        float increment = distance / 3f;
        return new Pair<>(increment * .8f, increment);
    }

    @Override
    public ItemStack getBeamItem() {
        return BEAM_ITEM;
    }

    @Override
    public void updateDescription(Player player) {
        AbilityDescriptionBuilder builder = AbilityDescriptionBuilder
                .create("Unleash a concentrated beam of demonic power, dealing ")
                .damage(damageValues.beamDamage)
                .text(" damage to all enemies hit. If the target is affected by ")
                .text("PHEX", NamedTextColor.DARK_RED)
                .text(" the damage dealt is increased by ");
        for (int i = 1; i < damageValues.damageMultipliers.size(); i++) {
            builder.percent((damageValues.damageMultipliers.get(i) - 1) * 100f, NamedTextColor.RED);
            if (i < damageValues.damageMultipliers.size() - 1) {
                builder.text("/");
            }
        }
        description = builder
                .text(" relative to the number of stacks and all stacks are removed.")
                .maxRange(maxDistance)
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SoulfireBeamBranch(abilityTree, this);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

    @Override
    protected void onSpawn(@Nonnull AbstractPiercingProjectile<SoulfireBeam, SoulfireBeamStats>.InternalProjectile projectile) {
        super.onSpawn(projectile);
        projectile.addTask(new InternalProjectileTask() {

            @Override
            public void run(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
            }

            @Override
            public void onDestroy(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                maxStacksHit.remove(projectile);
            }
        });
    }

    @Nullable
    @Override
    protected String getActivationSound() {
        return "arcanist.soulfirebeam.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 0.5f;
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
    }

    @Override
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        WarlordsEntity wp = projectile.getShooter();
        if (projectile.getHit().contains(hit)) {
            return;
        }
        if (pveMasterUpgrade2) {
            PoisonousHex.givePoisonousHex(wp, hit);
        }
        getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
        int hexStacks = (int) new CooldownFilter<>(hit, RegularCooldown.class)
                .filterCooldownClass(PoisonousHex.class)
                .stream()
                .count();
        boolean hasAstral = wp.getCooldownManager().hasCooldown(AstralPlague.class);
        if (!hasAstral) {
            hit.getCooldownManager().removeCooldown(PoisonousHex.class, false);
        } else {
            wp.doOnStaticAbility(AstralPlague.class,
                    astralPlague -> astralPlague.getAbilityStats().setHexesNotConsumed(astralPlague.getAbilityStats().getHexesNotConsumed() + hexStacks)
            );
        }
        float multiplier = damageValues.damageMultipliers.get(MathUtils.clamp(hexStacks, 0, PoisonousHex.getFromHex(wp).getMaxStacks()));
        getAbilityStats().getStacksRemoved().merge(hexStacks, 1, Integer::sum);
        InstanceBuilder instanceBuilder = InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .min(damageValues.beamDamage.getMinValue() * multiplier)
                .max(damageValues.beamDamage.getMaxValue() * multiplier)
                .crit(damageValues.beamDamage);
        if (maxStacksHit.getOrDefault(projectile, 0) == 0 && hexStacks >= PoisonousHex.getFromHex(wp).getMaxStacks()) {
            instanceBuilder.flags(InstanceFlags.FIRST_HIT);
            maxStacksHit.put(projectile, 1);
        }
        hit.addInstance(instanceBuilder);
    }

    @Override
    public SoulfireBeamStats getAbilityStats() {
        return stats;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private List<Float> damageMultipliers = new ArrayList<>(List.of(1.0f, 1.25f, 1.5f, 2.0f));

        private Value.RangedValueCritable beamDamage = new Value.RangedValueCritable(376, 508, 20, 175);

        private List<Value> values = List.of(beamDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.damageMultipliers = ConfigManager.getAbilityConfigListValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("damageMultipliers"), float.class);
            this.beamDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("beamDamage"), Value.RangedValueCritable.class);
            this.values = List.of(beamDamage);
        }

        public List<Float> getDamageMultipliers() {
            return damageMultipliers;
        }

        public void setDamageMultipliers(List<Float> damageMultipliers) {
            this.damageMultipliers = damageMultipliers;
        }

        public Value.RangedValueCritable getBeamDamage() {
            return beamDamage;
        }

    }

    public static class SoulfireBeamStats extends AbstractBeamStats<SoulfireBeam, SoulfireBeamStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public SoulfireBeamStats merge(SoulfireBeamStats other, int multiplier) {
            SoulfireBeamStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<SoulfireBeamStats> getClazz() {
            return SoulfireBeamStats.class;
        }

        @Override
        public SoulfireBeamStats create() {
            return new SoulfireBeamStats();
        }

    }

}
