package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractBeam;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class RayOfLight extends AbstractBeam {

    private int healingIncrease = 100;

    public RayOfLight() {
        super("Ray of Light", 511, 689, 10, 10, 20, 175, 30, 30, true);
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
        return null;
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
        Matrix4d center = new Matrix4d(currentLocation);
        double angle = Math.toRadians(4 * 90) + 30 * 0.45;
        double width = 0.5D;
        currentLocation.getWorld().spawnParticle(
                Particle.VILLAGER_ANGRY,
                center.translateVector(currentLocation.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width),
                2,
                0,
                0,
                0,
                0,
                null,
                true
        );
    }

    @Override
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        WarlordsEntity wp = projectile.getShooter();
        if (hit.isTeammate(wp) && !projectile.getHit().contains(hit)) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            float minHeal = minDamageHeal;
            float maxHeal = maxDamageHeal;
            int hexStacks = (int) new CooldownFilter<>(hit, RegularCooldown.class)
                    .filterCooldownClass(MercifulHex.class)
                    .stream()
                    .count();
            boolean hasDivineBlessing = wp.getCooldownManager().hasCooldown(DivineBlessing.class);
            if (hexStacks >= 3) {
                if (!hasDivineBlessing) {
                    hit.getCooldownManager().removeCooldown(MercifulHex.class, false);
                }
                minHeal *= 1 + (healingIncrease / 100f);
                maxHeal *= 1 + (healingIncrease / 100f);
            }
            wp.getCooldownManager().removeDebuffCooldowns();
            wp.getSpeed().removeSlownessModifiers();
            hit.addHealingInstance(wp, name, minHeal, maxHeal, critChance, critMultiplier, false, false);
        }
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity shooter, @Nonnull Player player) {
        shooter.addHealingInstance(shooter, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false, false);
        Utils.playGlobalSound(shooter.getLocation(), "arcanist.rayoflightalt.activation", 2, 0.9f);
        return super.onActivate(shooter, player);
    }

    @Override
    public ItemStack getBeamItem() {
        return new ItemStack(Material.CRIMSON_DOOR);
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

}
