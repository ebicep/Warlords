package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractBeam;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.conjurer.SoulfireBeamBranch;
import com.ebicep.warlords.util.java.Pair;
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

public class SoulfireBeam extends AbstractBeam implements Damages<SoulfireBeam.DamageValues> {

    public static final ItemStack BEAM_ITEM = new ItemStack(Material.CRIMSON_FENCE_GATE);
    private final DamageValues damageValues = new DamageValues();

    public Map<Integer, Integer> stacksRemoved = new HashMap<>();

    public SoulfireBeam() {
        super("Soulfire Beam", 10, 10, 30, 30, false);
        this.maxTicks = 0;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Unleash a concentrated beam of demonic power, dealing ")
                               .append(Damages.formatDamage(damageValues.beamDamage))
                               .append(Component.text(" damage to all enemies hit. If the target is affected by Poisonous Hex the damage dealt is increased by "))
                               .append(Component.text("25%", NamedTextColor.RED))
                               .append(Component.text("/"))
                               .append(Component.text("50%", NamedTextColor.RED))
                               .append(Component.text("/"))
                               .append(Component.text("100%", NamedTextColor.RED))
                               .append(Component.text(" relative to the number of stacks and all stacks are removed.\n\nHas a maximum range of"))
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
        return new SoulfireBeamBranch(abilityTree, this);
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {

    }

    @Override
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        WarlordsEntity wp = projectile.getShooter();
        if (!projectile.getHit().contains(hit)) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            int hexStacks = (int) new CooldownFilter<>(hit, RegularCooldown.class)
                    .filterCooldownClass(PoisonousHex.class)
                    .stream()
                    .count();
            if (hexStacks <= 0) {
                return;
            }
            boolean hasAstral = wp.getCooldownManager().hasCooldown(AstralPlague.class);
            if (!hasAstral) {
                hit.getCooldownManager().removeCooldown(PoisonousHex.class, false);
            } else {
                wp.doOnStaticAbility(AstralPlague.class, astralPlague -> astralPlague.hexesNotConsumed += hexStacks);
            }
            float multiplier = switch (hexStacks) {
                case 1 -> 1.25f;
                case 2 -> 1.5f;
                default -> 2f;
            };
            stacksRemoved.merge(hexStacks, 1, Integer::sum);
            if (hexStacks >= PoisonousHex.getFromHex(wp).getMaxStacks() && projectile.getHit().size() <= 4 && pveMasterUpgrade) {
                multiplier += 5;
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

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable beamDamage = new Value.RangedValueCritable(376, 508, 20, 175);
        private final List<Value> values = List.of(beamDamage);

        public Value.RangedValueCritable getBeamDamage() {
            return beamDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}
