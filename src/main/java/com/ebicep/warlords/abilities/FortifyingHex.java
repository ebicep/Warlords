package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.Specializations;
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
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class FortifyingHex extends AbstractPiercingProjectile implements Duration {

    private int maxEnemiesHit = 1;
    private int maxAlliesHit = 1;
    private int maxFullDistance = 20;
    private int tickDuration = 80;
    private int damageReduction = 5;
    private int hexStacksPerHit = 1;
    private int maxStacks = 3;

    public FortifyingHex() {
        super("Fortifying Hex", 256, 350, 0, 70, 20, 175, 2.5, 300, true);
        this.playerHitbox += .25;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Fling a wave of protective energy forward, hitting ")
                               .append(Component.text(maxEnemiesHit, NamedTextColor.RED))
                               .append(Component.text((maxEnemiesHit == 1 ? " enemy" : " enemies") + " and "))
                               .append(Component.text(maxAlliesHit, NamedTextColor.RED))
                               .append(Component.text((maxAlliesHit == 1 ? " ally" : " allies") + ". The enemy takes "))
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage. The ally receives "))
                               .append(Component.text(hexStacksPerHit, NamedTextColor.BLUE))
                               .append(Component.text(" stack" + (hexStacksPerHit != 1 ? "s" : "") + " of Fortifying Hex. If Fortifying Hex hits a target, you receive "))
                               .append(Component.text(hexStacksPerHit, NamedTextColor.BLUE))
                               .append(Component.text(" stack" + (hexStacksPerHit != 1 ? "s" : "") + " of Fortifying Hex. Each stack of Fortifying Hex lasts  "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds and grants"))
                               .append(Component.text(damageReduction + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" damage reduction. Stacks up to"))
                               .append(Component.text(maxStacks, NamedTextColor.BLUE))
                               .append(Component.text(" times.\n\nHas an optimal range of "))
                               .append(Component.text(maxFullDistance, NamedTextColor.YELLOW))
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
        Location currentLocation = projectile.getCurrentLocation();
        Location startingLocation = projectile.getStartingLocation();

        getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
        List<WarlordsEntity> hits = projectile.getHit();
        if (hit.isTeammate(wp)) {
            int teammatesHit = (int) hits.stream().filter(we -> we.isTeammate(wp)).count();
            if (teammatesHit > maxAlliesHit) {
                return;
            }
            giveFortifyingHex(wp, hit);
        } else {
            int enemiesHit = (int) hits.stream().filter(we -> !we.isTeammate(wp)).count();
            if (enemiesHit > maxEnemiesHit) {
                return;
            }
            double distanceSquared = startingLocation.distanceSquared(currentLocation);
            double toReduceBy = maxFullDistance * maxFullDistance > distanceSquared ? 1 :
                                1 - (Math.sqrt(distanceSquared) - maxFullDistance) / 75;
            if (toReduceBy < .2) {
                toReduceBy = .2;
            }
            hit.addDamageInstance(
                    wp,
                    name,
                    (float) (minDamageHeal * toReduceBy),
                    (float) (maxDamageHeal * toReduceBy),
                    critChance,
                    critMultiplier
            );
        }
        if (hits.size() == 1) {
            giveFortifyingHex(wp, wp);
        }
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
            armorStand.getEquipment().setHelmet(new ItemStack(Material.WARPED_DOOR));
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

    public static void giveFortifyingHex(WarlordsEntity from, WarlordsEntity to) {
        FortifyingHex fromHex = getFromHex(from);
        String hexName = fromHex.getName();
        int damageReduction = fromHex.getDamageReduction();
        int maxStacks = fromHex.getMaxStacks();
        int duration = fromHex.getTickDuration();
        to.getCooldownManager().limitCooldowns(RegularCooldown.class, FortifyingHex.class, maxStacks);
        to.getCooldownManager().addCooldown(new RegularCooldown<>(
                hexName,
                "FHEX",
                FortifyingHex.class,
                new FortifyingHex(),
                from,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                duration
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 - damageReduction / 100f);
            }

            @Override
            public PlayerNameData addSuffixFromOther() {
                return new PlayerNameData(Component.text("FHEX", NamedTextColor.YELLOW), we -> we.isTeammate(from) && we.getSpecClass() == Specializations.SENTINEL);
            }
        });
    }

    @Nonnull
    public static FortifyingHex getFromHex(WarlordsEntity from) {
        return Arrays.stream(from.getSpec().getAbilities()).filter(FortifyingHex.class::isInstance)
                     .map(FortifyingHex.class::cast)
                     .findFirst()
                     .orElse(new FortifyingHex());
    }

    public int getDamageReduction() {
        return damageReduction;
    }

    public int getMaxStacks() {
        return maxStacks;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    static class FortifyingHexShield extends Shield {

        private int maxStacks;

        public FortifyingHexShield(String name, float hexShieldAmount, int maxStacks) {
            super(name, hexShieldAmount);
            this.maxStacks = maxStacks;
        }

        @Override
        public void addShieldHealth(float damage) {
            if (-damage < getShieldHealth()) {
                setShieldHealth(0);
            } else {
                setShieldHealth(getShieldHealth() + damage);
            }
        }

        public int getMaxStacks() {
            return maxStacks;
        }
    }
}
