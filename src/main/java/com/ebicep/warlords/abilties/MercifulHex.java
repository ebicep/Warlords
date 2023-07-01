package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.abilties.internal.Duration;
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
import java.util.Collections;
import java.util.List;

public class MercifulHex extends AbstractPiercingProjectile implements Duration {

    @Nonnull
    public static MercifulHex getFromHex(WarlordsEntity from) {
        return Arrays.stream(from.getSpec().getAbilities()).filter(MercifulHex.class::isInstance)
                     .map(MercifulHex.class::cast)
                     .findFirst()
                     .orElse(new MercifulHex());
    }

    private int hexStacksPerHit = 1;
    private int minDamage = 217;
    private int maxDamage = 292;
    private int subsequentReduction = 40;
    private int minSelfHeal = 230;
    private int maxSelfHeal = 310;
    private float dotMinHeal = 30;
    private float dotMaxHeal = 40;
    private int maxStacks = 3;
    private int tickDuration = 40;

    public MercifulHex() {
        super("Merciful Hex", 307, 415, 0, 70, 20, 180, 2.5, 20, true);
        this.playerHitbox += .5; //TODO maybe inflate y separately
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Send a wave of piercing magical wind forward. The first ally hit by the magical wind heals ")
                               .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" health (subsequent hit allies are healed for 30%) and receives "))
                               .append(Component.text(hexStacksPerHit, NamedTextColor.BLUE))
                               .append(Component.text(" stack" + (hexStacksPerHit != 1 ? "s" : "") + " of Merciful Hex. The first enemy hit by the wind takes "))
                               .append(formatRangeDamage(minDamage, maxDamage))
                               .append(Component.text(" damage. Also heal yourself for by "))
                               .append(formatRangeHealing(minSelfHeal, maxSelfHeal))
                               .append(Component.text(" If Merciful Hex hits a target, you receive "))
                               .append(Component.text(hexStacksPerHit, NamedTextColor.BLUE))
                               .append(Component.text(" stack of Merciful Hex. Each stack of Merciful Hex heals "))
                               .append(formatRangeHealing(dotMinHeal, dotMaxHeal))
                               .append(Component.text(" health every "))
                               .append(Component.text("2", NamedTextColor.GOLD))
                               .append(Component.text("seconds for "))
                               .append(Component.text(format(tickDuration / 10f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Stacks up to"))
                               .append(Component.text(maxStacks, NamedTextColor.BLUE))
                               .append(Component.text(" times.\n\nHas a maximum range of "))
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
        Location currentLocation = projectile.getCurrentLocation();

        Utils.playGlobalSound(projectile.getCurrentLocation(), "shaman.chainheal.activation", 2, 2);

        getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
        if (hit.onHorse()) {
            numberOfDismounts++;
        }
        List<WarlordsEntity> hits = projectile.getHit();
        if (hits.size() == 1) {
            giveMercifulHex(wp, wp);
        }
        boolean isTeammate = hit.isTeammate(wp);
        if (isTeammate) {
            int teammatesHit = (int) hits.stream().filter(we -> we.isTeammate(wp)).count();
            float reduction = teammatesHit == 1 ? 1 : subsequentReduction / 100f;
            hit.addHealingInstance(
                    wp,
                    name,
                    minDamageHeal * reduction,
                    maxDamageHeal * reduction,
                    critChance,
                    critMultiplier,
                    false,
                    false
            );
            giveMercifulHex(wp, hit);
        } else {
            int enemiesHit = (int) hits.stream().filter(we -> we.isEnemy(wp)).count();
            float reduction = enemiesHit == 1 ? 1 : subsequentReduction / 100f;
            hit.addDamageInstance(
                    wp,
                    name,
                    minDamage * reduction,
                    maxDamage * reduction,
                    critChance,
                    critMultiplier,
                    false
            );
        }

    }

    private void giveMercifulHex(WarlordsEntity from, WarlordsEntity to) {
        to.getCooldownManager().limitCooldowns(RegularCooldown.class, MercifulHex.class, 3);
        to.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Merciful Hex",
                "MHEX",
                MercifulHex.class,
                new MercifulHex(),
                from,
                CooldownTypes.BUFF,
                cooldownManager -> {
                    to.addHealingInstance(
                            from,
                            name,
                            dotMinHeal,
                            dotMaxHeal,
                            0,
                            100,
                            false,
                            false
                    );
                },
                tickDuration * 2, // base add 20 to delay damage by a second
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 40 == 0 && ticksElapsed != 0) {
                        to.addHealingInstance(
                                from,
                                name,
                                dotMinHeal,
                                dotMaxHeal,
                                0,
                                100,
                                false,
                                false
                        );
                    }
                })
        ) {
            @Override
            public PlayerNameData addSuffixFromOther() {
                return new PlayerNameData(Component.text("MHEX", NamedTextColor.GREEN), we -> we.isTeammate(from) && we.getSpecClass() == Specializations.CLERIC);
            }
        });
    }


    @Override
    protected Location getProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
        return new LocationBuilder(startingLocation.clone()).addY(-.5).backward(0f);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity shooter, @Nonnull Player player) {
        boolean activate = super.onActivate(shooter, player);
        shooter.addHealingInstance(
                shooter,
                name,
                minSelfHeal,
                maxSelfHeal,
                critChance,
                critMultiplier,
                false,
                false
        );
        return activate;
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        ArmorStand fallenSoul = Utils.spawnArmorStand(projectile.getStartingLocation().clone().add(0, -1.7, 0), armorStand -> {
            armorStand.setMarker(true);
            armorStand.getEquipment().setHelmet(new ItemStack(Material.WARPED_FENCE));
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
                        Particle.SPELL,
                        projectile.getCurrentLocation().clone().add(0, 0, 0),
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
        return "arcanist.mercifulhexalt.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 1.3f;
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

    public float getDotMinHeal() {
        return dotMinHeal;
    }

    public void setDotMinHeal(float dotMinHeal) {
        this.dotMinHeal = dotMinHeal;
    }

    public float getDotMaxHeal() {
        return dotMaxHeal;
    }

    public void setDotMaxHeal(float dotMaxHeal) {
        this.dotMaxHeal = dotMaxHeal;
    }
}
