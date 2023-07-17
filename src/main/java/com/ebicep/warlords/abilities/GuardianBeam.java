package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractBeam;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.sentinel.GuardianBeamBranch;
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
import java.util.Collections;
import java.util.List;

public class GuardianBeam extends AbstractBeam implements Duration {

    private float runeTimerIncrease = 1.5f;
    private int shieldPercent = 25;
    private int tickDuration = 120;

    public GuardianBeam() {
        super("Guardian Beam", 329, 445, 10, 10, 20, 175, 30, 30, true);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Unleash a concentrated beam of mystical power, piercing all enemies and allies. Enemies hit take ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage damage and have their cooldowns increased by "))
                               .append(Component.text(format(runeTimerIncrease), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. If an ally has max stacks of Fortifying Hex, remove all stacks and grant them a shield with "))
                               .append(Component.text(shieldPercent + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" of the ally’s maximum health that lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. If Guardian Beam hits a target and you have max stacks of Fortifying Hex, you also receive the shield." +
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
            hit.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
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
                .filterCooldownClass(FortifyingHex.class)
                .stream()
                .count();
        if (selfHexStacks >= 3) {
            if (!hasSanctuary) {
                wp.getCooldownManager().removeCooldown(FortifyingHex.class, false);
            }
            Utils.playGlobalSound(wp.getLocation(), "arcanist.guardianbeam.giveshield", 1, 1.7f);
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
                        if (ticksElapsed % 4 == 0) {
                            Location location = wp.getLocation();
                            location.add(0, 1.5, 0);
                            EffectUtils.displayParticle(Particle.CHERRY_LEAVES, location, 2, 0.15F, 0.3F, 0.15F, 0.01);
                            EffectUtils.displayParticle(Particle.FIREWORKS_SPARK, location, 1, 0.3F, 0.3F, 0.3F, 0.0001);
                            EffectUtils.displayParticle(Particle.CRIMSON_SPORE, location, 1, 0.3F, 0.3F, 0.3F, 0);
                        }
                    })
            ));
        }
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity shooter, @Nonnull Player player) {
        shooter.playSound(shooter.getLocation(), "mage.firebreath.activation", 2, 0.7f);
        return super.onActivate(shooter, player);
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new GuardianBeamBranch(abilityTree, this);
    }

    @Override
    public ItemStack getBeamItem() {
        return new ItemStack(Material.WARPED_SLAB);
    }

    @Nullable
    @Override
    protected String getActivationSound() {
        return "arcanist.guardianbeamalt.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 1;
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

    public float getRuneTimerIncrease() {
        return runeTimerIncrease;
    }

    public void setRuneTimerIncrease(float runeTimerIncrease) {
        this.runeTimerIncrease = runeTimerIncrease;
    }
}
