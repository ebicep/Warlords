package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractChain;
import com.ebicep.warlords.abilties.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class SoulfireBeam extends AbstractPiercingProjectile {

    private int speedBuff = 40;
    private int speedTickDuration = 60;

    public SoulfireBeam() {
        super("Soulfire Beam", 376, 508, 10, 10, 20, 175, 30, 30, false);
        this.maxTicks = 0;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Unleash a concentrated beam of demonic power, dealing ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage to all enemies hit. " +
                                       " If the target is affected by the max stacks of Poisonous Hex, remove all stacks, increase the damage dealt of " + name + " by "))
                               .append(Component.text("100%", NamedTextColor.RED))
                               .append(Component.text(". Gain"))
                               .append(Component.text(speedBuff + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" speed for "))
                               .append(Component.text(format(speedTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds.\n\nHas a maximum range of "))
                               .append(Component.text(format(maxDistance), NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {

    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        return 0;
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
        WarlordsEntity wp = projectile.getShooter();

        float minDamage = minDamageHeal;
        float maxDamage = maxDamageHeal;
        int hexStacks = (int) new CooldownFilter<>(hit, RegularCooldown.class)
                .filterCooldownFrom(wp)
                .filterCooldownClass(PoisonousHex.class)
                .stream()
                .count();
        boolean hasAstral = wp.getCooldownManager().hasCooldown(AstralPlague.class);
        if (hexStacks >= 3) {
            if (!hasAstral) {
                hit.getCooldownManager().removeCooldown(PoisonousHex.class, false);
            }
            minDamage *= 2;
            maxDamage *= 2;
        }
        hit.addDamageInstance(wp, name, minDamage, maxDamage, critChance, critMultiplier, hasAstral && hexStacks >= 3);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity shooter, @Nonnull Player player) {
        Location location = Utils.getTargetLocation(player, (int) maxDistance).clone().add(.5, .85, .5).clone();
        AbstractChain.spawnChain(shooter.getLocation(), location, new ItemStack(Material.CRIMSON_FENCE_GATE));
        shooter.addSpeedModifier(shooter, name, speedBuff, 60);
        return super.onActivate(shooter, player);
    }

    @Nullable
    @Override
    protected String getActivationSound() {
        return null;
    }

    @Override
    protected float getSoundVolume() {
        return 0;
    }

    @Override
    protected float getSoundPitch() {
        return 0;
    }
}
