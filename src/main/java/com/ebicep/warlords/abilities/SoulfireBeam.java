package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.conjurer.SoulfireBeamBranch;
import com.ebicep.warlords.util.java.MathUtils;
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

    private final DamageValues damageValues = new DamageValues();
    private final SoulfireBeamStats stats = new SoulfireBeamStats();
    private final Map<InternalProjectile, Integer> maxStacksHit = new HashMap<>();

    public SoulfireBeam() {
        super("Soulfire Beam", 10, 10, 30, 30, false);
        this.maxTicks = 0;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Unleash a concentrated beam of demonic power, dealing ")
                .damage(damageValues.beamDamage)
                .text(" damage to all enemies hit. If the target is affected by Poisonous Hex the damage dealt is increased by ")
                .percent((damageValues.damageMultipliers.get(1) - 1) * 100, NamedTextColor.RED)
                .text("/")
                .percent((damageValues.damageMultipliers.get(2) - 1) * 100, NamedTextColor.RED)
                .text("/")
                .percent((damageValues.damageMultipliers.get(3) - 1) * 100, NamedTextColor.RED)
                .text(" relative to the number of stacks and all stacks are removed.")
                .maxRange(maxDistance)
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SoulfireBeamBranch(abilityTree, this);
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {

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

    @Override
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        WarlordsEntity wp = projectile.getShooter();
        if (projectile.getHit().contains(hit)) {
            return;
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
        float multiplier = damageValues.damageMultipliers.get(MathUtils.clamp(hexStacks, 0, 3));
        getAbilityStats().getStacksRemoved().merge(hexStacks, 1, Integer::sum);
        if (pveMasterUpgrade && maxStacksHit.getOrDefault(projectile, 0) <= 8 && hexStacks >= PoisonousHex.getFromHex(wp).getMaxStacks()) {
            multiplier += 5;
            maxStacksHit.put(projectile, maxStacksHit.getOrDefault(projectile, 0) + 1);
        }
        hit.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(wp)
                .min(damageValues.beamDamage.getMinValue() * multiplier)
                .max(damageValues.beamDamage.getMaxValue() * multiplier)
                .crit(damageValues.beamDamage)
        );
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
    public boolean onActivate(@Nonnull WarlordsEntity shooter) {
        shooter.playSound(shooter.getLocation(), "mage.firebreath.activation", 2, 0.6f);
        return super.onActivate(shooter);
    }

    @Override
    public ItemStack getBeamItem() {
        return BEAM_ITEM;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public SoulfireBeamStats getAbilityStats() {
        return stats;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final List<Float> damageMultipliers = new ArrayList<>() {{
            add(1.0f);
            add(1.25f);
            add(1.5f);
            add(2.0f);
        }};
        private final Value.RangedValueCritable beamDamage = new Value.RangedValueCritable(376, 508, 20, 175);
        private final List<Value> values = List.of(beamDamage);

        public List<Float> getDamageMultipliers() {
            return damageMultipliers;
        }

        public Value.RangedValueCritable getBeamDamage() {
            return beamDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
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