package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractBeam;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.abilties.internal.Shield;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class GuardianBeam extends AbstractBeam implements Duration {

    private float runeTimerIncrease = 1.5f;
    private int shieldPercent = 20;
    private int tickDuration = 120;

    public GuardianBeam() {
        super("Guardian Beam", 329, 445, 10, 10, 20, 175, 30, 30, true);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Unleash a concentrated beam of mystical power, piercing all enemies and allies. Enemies hit take ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage damage and have their rune timers increased by "))
                               .append(Component.text(format(runeTimerIncrease), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. If an ally has max stacks of Fortifying Hex, remove all stacks and grant them a shield with "))
                               .append(Component.text(shieldPercent + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" of the ally’s maximum health and lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. If Guardian Beam hits a target and you have max stacks of Fortifying Hex, also receive the shield." +
                                       "\n\nHas a maximum range of "))
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
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        WarlordsEntity wp = projectile.getShooter();

        boolean hasSanctuary = wp.getCooldownManager().hasCooldown(Sanctuary.class);
        if (hit.isEnemy(wp)) {
            hit.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);
        } else {
            giveShield(hit, hasSanctuary);
        }
        if (projectile.getHit().isEmpty()) {
            giveShield(wp, hasSanctuary);
        }
        projectile.getHit().add(hit);
    }

    private void giveShield(WarlordsEntity wp, boolean hasSanctuary) {
        int selfHexStacks = (int) new CooldownFilter<>(wp, RegularCooldown.class)
                .filterCooldownFrom(wp)
                .filterCooldownClass(FortifyingHex.class)
                .stream()
                .count();
        if (selfHexStacks >= 3) {
            if (!hasSanctuary) {
                wp.getCooldownManager().removeCooldown(FortifyingHex.class, false);
            }
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "SHIELD",
                    Shield.class,
                    new Shield(name, wp.getMaxHealth() * (shieldPercent / 100f)),
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                    },
                    tickDuration,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    })
            ));
        }
    }

    @Override
    public ItemStack getBeamItem() {
        return new ItemStack(Material.CRIMSON_FENCE_GATE);
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

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getShieldPercent() {
        return shieldPercent;
    }

    public void setShieldPercent(int shieldPercent) {
        this.shieldPercent = shieldPercent;
    }
}
