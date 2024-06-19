package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractBeam;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.luminary.RayOfLightBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RayOfLight extends AbstractBeam {

    public static final ItemStack BEAM_ITEM = new ItemStack(Material.MANGROVE_FENCE);

    private int healingIncrease = 100;

    public RayOfLight() {
        super("Ray of Light", 409, 551, 10, 10, 20, 150, 30, 30, true);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Unleash a concentrated beam of holy light, healing ")
                               .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                               .append(Component.text(
                                       " health to all allies hit. If the target is affected by the max stacks of Merciful Hex, remove all stacks and increase the healing of Ray of Light by "))
                               .append(Component.text(healingIncrease + "%", NamedTextColor.GREEN))
                               .append(Component.text(" and removes their debuffs.\n\nHas a maximum range of "))
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
        return new RayOfLightBranch(abilityTree, this);
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
            float minHeal = minDamageHeal.getCalculatedValue();
            float maxHeal = maxDamageHeal.getCalculatedValue();
            int hexStacks = (int) new CooldownFilter<>(hit, RegularCooldown.class)
                    .filterCooldownClass(MercifulHex.class)
                    .stream()
                    .count();
            boolean hasDivineBlessing = wp.getCooldownManager().hasCooldown(DivineBlessing.class);
            boolean maxStacks = hexStacks >= 3;
            if (maxStacks) {
                if (!hasDivineBlessing) {
                    hit.getCooldownManager().removeCooldown(MercifulHex.class, false);
                }
                minHeal *= convertToMultiplicationDecimal(healingIncrease);
                maxHeal *= convertToMultiplicationDecimal(healingIncrease);
                hit.getCooldownManager().removeDebuffCooldowns();
                hit.getSpeed().removeSlownessModifiers();
            }
            if (pveMasterUpgrade) {
                hit.getCooldownManager().addCooldown(new RegularCooldown<>(
                        name,
                        "RAY",
                        RayOfLight.class,
                        new RayOfLight(),
                        wp,
                        CooldownTypes.ABILITY,
                        cooldownManager -> {
                        },
                        cooldownManager -> {
                        },
                        100
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * (maxStacks ? 1.35f : 1.15f);
                    }
                });
            }
            hit.addInstance(InstanceBuilder
                    .healing()
                    .ability(this)
                    .source(wp)
                    .min(minHeal)
                    .max(maxHeal)
                    .crit(healingValues.rayHealing)
            );
        }
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
    public boolean onActivate(@Nonnull WarlordsEntity shooter) {
        int hexStacks = (int) new CooldownFilter<>(shooter, RegularCooldown.class)
                .filterCooldownClass(MercifulHex.class)
                .stream()
                .count();
        if (hexStacks >= 3) {
            shooter.getCooldownManager().removeDebuffCooldowns();
            shooter.getSpeed().removeSlownessModifiers();
        }
        shooter.addInstance(InstanceBuilder
                .healing()
                .ability(this)
                .source(shooter)
                .value(healingValues.rayHealing)
        );
        Utils.playGlobalSound(shooter.getLocation(), "arcanist.rayoflightalt.activation", 2, 0.9f);
        return super.onActivate(shooter);
    }

    @Override
    public ItemStack getBeamItem() {
        return BEAM_ITEM;
    }

    private final HealingValues healingValues = new HealingValues();

    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.RangedValueCritable rayHealing = new Value.RangedValueCritable(409, 551, 20, 150);
        private final List<Value> values = List.of(rayHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
