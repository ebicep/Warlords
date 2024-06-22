package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractBeam;
import com.ebicep.warlords.abilities.internal.Heals;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RayOfLight extends AbstractBeam implements Heals<RayOfLight.HealingValues> {

    public static final ItemStack BEAM_ITEM = new ItemStack(Material.MANGROVE_FENCE);

    public Map<Integer, Integer> stacksRemoved = new HashMap<>();

    private final HealingValues healingValues = new HealingValues();

    public RayOfLight() {
        super("Ray of Light", 10, 10, 30, 30, true);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Unleash a concentrated beam of holy light, healing ")
                               .append(Heals.formatHealing(healingValues.rayHealing))
                               .append(Component.text(" health to all allies hit and cleansing all "))
                               .append(Component.text("de-buffs", NamedTextColor.YELLOW))
                               .append(Component.text(". If the target is affected by Merciful Hex the healing given is increased by "))
                               .append(Component.text("25%", NamedTextColor.GREEN))
                               .append(Component.text("/"))
                               .append(Component.text("50%", NamedTextColor.GREEN))
                               .append(Component.text("/"))
                               .append(Component.text("100%", NamedTextColor.GREEN))
                               .append(Component.text(" relative to the number of stacks and all stacks are removed.\n\nHas a maximum range of "))
                               .append(Component.text(format(maxDistance), NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        stacksRemoved.entrySet()
                     .stream()
                     .forEach(integerIntegerEntry -> {
                         info.add(new Pair<>("Stacks Removed (" + integerIntegerEntry.getKey() + ")", "" + integerIntegerEntry.getValue()));
                     });
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
            hit.getCooldownManager().removeDebuffCooldowns();
            hit.getSpeed().removeSlownessModifiers();
            int hexStacks = (int) new CooldownFilter<>(hit, RegularCooldown.class)
                    .filterCooldownClass(MercifulHex.class)
                    .stream()
                    .count();
            if (hexStacks <= 0) {
                return;
            }
            boolean hasDivineBlessing = wp.getCooldownManager().hasCooldown(DivineBlessing.class);
            if (!hasDivineBlessing) {
                hit.getCooldownManager().removeCooldown(MercifulHex.class, false);
            } else {
                wp.doOnStaticAbility(DivineBlessing.class, divineBlessing -> divineBlessing.hexesNotConsumed += hexStacks);
            }
            boolean maxStacks = hexStacks >= 3;
            float multiplier = switch (hexStacks) {
                case 1 -> 1.25f;
                case 2 -> 1.5f;
                default -> 2f;
            };
            stacksRemoved.merge(hexStacks, 1, Integer::sum);
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
                    .min(healingValues.rayHealing.getMinValue() * multiplier)
                    .max(healingValues.rayHealing.getMaxValue() * multiplier)
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
        shooter.getCooldownManager().removeDebuffCooldowns();
        shooter.getSpeed().removeSlownessModifiers();
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

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.RangedValueCritable rayHealing = new Value.RangedValueCritable(389, 523, 20, 150);
        private final List<Value> values = List.of(rayHealing);

        public Value.RangedValueCritable getRayHealing() {
            return rayHealing;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
