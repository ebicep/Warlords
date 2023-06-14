package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractProjectile;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.abilties.internal.Shield;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class FortifyingHex extends AbstractProjectile implements Duration {

    private float runeTickIncrease = 0.5f;
    private int tickDuration = 60;
    private int hexStacksPerHit = 1;
    private int hexShieldAmount = 320;
    private int hexMaxStacks = 3;

    public FortifyingHex() {
        super("Fortifying Hex", 293, 395, 0, 80, 20, 175, 2.5, 20, false);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Fling a hexed tendril forward, dealing ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage to a single target, increasing their rune timers by "))
                               .append(Component.text(format(runeTickIncrease), NamedTextColor.GOLD))
                               .append(Component.text(", and granting you "))
                               .append(Component.text(hexStacksPerHit, NamedTextColor.BLUE))
                               .append(Component.text(" stack" + (hexStacksPerHit != 1 ? "s" : "") + " of Fortifying Hex.  Fortifying Hex lasts  "))
                               .append(Component.text(" and absorbs"))
                               .append(Component.text("1", NamedTextColor.YELLOW))
                               .append(Component.text(" instance of incoming damage, up to "))
                               .append(Component.text(hexShieldAmount, NamedTextColor.YELLOW))
                               .append(Component.text(", and stacks up to "))
                               .append(Component.text(hexMaxStacks, NamedTextColor.BLUE))
                               .append(Component.text(" times.\n\nHas an optimal range of "))
                               .append(Component.text(maxDistance, NamedTextColor.YELLOW))
                               .append(Component.text("blocks."));
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
        if (hit != null) {
            WarlordsEntity wp = projectile.getShooter();
            hit.addDamageInstance(
                    wp,
                    name,
                    minDamageHeal,
                    maxDamageHeal,
                    critChance,
                    critMultiplier,
                    false
            );
            hit.getSpec().increaseAllCooldownTimersBy(runeTickIncrease);
            wp.getCooldownManager().limitCooldowns(RegularCooldown.class, FortifyingHex.class, hexMaxStacks);
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "ASTRAL",
                    Shield.class,
                    new Shield(name, hexShieldAmount) {
                        @Override
                        public void addShieldHealth(float damage) {
                            if (damage < getShieldHealth()) {
                                setShieldHealth(0);
                            } else {
                                setShieldHealth(getShieldHealth() - damage);
                            }
                        }
                    },
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    tickDuration
            ));
        }
        return hit == null ? 0 : 1;
    }

    @Override
    protected Location getProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
        return new LocationBuilder(startingLocation.clone()).addY(-.5).backward(0f);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        ArmorStand fallenSoul = Utils.spawnArmorStand(projectile.getStartingLocation().clone().add(0, -1.7, 0), armorStand -> {
            armorStand.setMarker(true);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.ACACIA_FENCE_GATE));
            armorStand.setHeadPose(new EulerAngle(-Math.atan2(
                    projectile.getSpeed().getY(),
                    Math.sqrt(
                            Math.pow(projectile.getSpeed().getX(), 2) +
                                    Math.pow(projectile.getSpeed().getZ(), 2)
                    )
            ), 0, 0));
        });

        projectile.addTask(new InternalProjectileTask() {
            @Override
            public void run(InternalProjectile projectile) {
                fallenSoul.teleport(projectile.getCurrentLocation().clone().add(0, -1.7, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                projectile.getCurrentLocation().getWorld().spawnParticle(
                        Particle.SPELL_WITCH,
                        projectile.getCurrentLocation().clone().add(0, 0, 0),
                        1,
                        0,
                        0,
                        0,
                        0,
                        null,
                        true
                );
            }

            @Override
            public void onDestroy(InternalProjectile projectile) {
                fallenSoul.remove();
                projectile.getCurrentLocation().getWorld().spawnParticle(
                        Particle.SPELL_WITCH,
                        projectile.getCurrentLocation(),
                        1,
                        0,
                        0,
                        0,
                        0.7f,
                        null,
                        true
                );
            }
        });
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
}
