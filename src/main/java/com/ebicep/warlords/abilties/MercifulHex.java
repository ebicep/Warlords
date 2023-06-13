package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class MercifulHex extends AbstractPiercingProjectile {

    private int minDamage = 310;
    private int maxDamage = 418;
    private int subsequentReduction = 30;
    private int minSelfHeal = 329;
    private int maxSelfHeal = 443;

    public MercifulHex() {
        super("Merciful Hex", 438, 591, 0, 100, 20, 180, 2.5, 20, true);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Send a merciful gust of wind forward, passing through all allies and enemies. The first ally and enemy to receive the wind will heal for ")
                               .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" health and take "))
                               .append(formatRangeDamage(minDamage, maxDamage))
                               .append(Component.text(" damage, respectively. All other allies and enemies the wind passes through will only receive "))
                               .append(Component.text(subsequentReduction, NamedTextColor.YELLOW))
                               .append(Component.text(" of the effect. Also heal yourself by "))
                               .append(formatRangeHealing(minSelfHeal, maxSelfHeal))
                               .append(Component.text(".\n\nHas a maximum range of "))
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
        return false;
    }

    @Override
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {

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
