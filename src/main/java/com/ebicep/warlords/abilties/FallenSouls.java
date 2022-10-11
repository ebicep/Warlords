package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractPiercingProjectileBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

public class FallenSouls extends AbstractPiercingProjectileBase {
    protected int playersHit = 0;
    protected int numberOfDismounts = 0;

    public FallenSouls() {
        super("Fallen Souls", 164f, 212f, 0, 55, 20, 180, 2, 35, false);
        this.maxAngleOfShots = 54;
        this.forwardTeleportAmount = 1.6f;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Summon a wave of fallen souls, dealing\n" +
                "§c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage to all enemies they\n" +
                "§7pass through. Each target hit reduces the\n" +
                "§7cooldown of Spirit Link by §62 §7seconds.";
        description = WordWrap.wrapWithNewline(ChatColor.GRAY +
                        "§7Summon a wave of fallen souls, dealing\n" +
                        "§c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage to all enemies they\n" +
                        "§7pass through. Each target hit reduces the\n" +
                        "§7cooldown of Spirit Link by §62 §7seconds.",
                40
        );
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));
        info.add(new Pair<>("Dismounts", "" + numberOfDismounts));

        return info;
    }

    @Override
    protected String getActivationSound() {
        return "shaman.lightningbolt.impact";
    }

    @Override
    protected float getSoundPitch() {
        return 1.5f;
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(InternalProjectile projectile, Block block) {
        return true;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(InternalProjectile projectile, WarlordsEntity wp) {
        return false;
    }

    @Override
    protected void onNonCancellingHit(InternalProjectile projectile, WarlordsEntity hit, Location impactLocation) {
        WarlordsEntity wp = projectile.getShooter();
        if (!projectile.getHit().contains(hit)) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            playersHit++;
            if (hit.onHorse()) {
                numberOfDismounts++;
            }
            Utils.playGlobalSound(impactLocation, "shaman.lightningbolt.impact", 2, 1);

            hit.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);

            wp.getRedAbility().subtractCooldown(2);
            wp.updateRedItem();

            reduceCooldowns(wp, hit);
        }
    }

    @Override
    protected int onHit(InternalProjectile projectile, WarlordsEntity hit) {
        WarlordsEntity wp = projectile.getShooter();
        Location currentLocation = projectile.getCurrentLocation();

        Utils.playGlobalSound(currentLocation, "shaman.lightningbolt.impact", 2, 1);

        int playersHit = 0;
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(currentLocation, 3, 3, 3)
                .aliveEnemiesOf(wp)
                .excluding(projectile.getHit())
        ) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(enemy));
            playersHit++;
            if (enemy.onHorse()) {
                numberOfDismounts++;
            }
            enemy.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);

            wp.getRedAbility().subtractCooldown(2);
            wp.updateRedItem();

        }

        return playersHit;
    }

    private void reduceCooldowns(WarlordsEntity wp, WarlordsEntity enemy) {
        new CooldownFilter<>(wp, PersistentCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                .filter(soulbinding -> soulbinding.hasBoundPlayerSoul(enemy))
                .forEachOrdered(soulbinding -> {
                    wp.doOnStaticAbility(Soulbinding.class, Soulbinding::addSoulProcs);

                    wp.getRedAbility().subtractCooldown(1.5F);
                    wp.getPurpleAbility().subtractCooldown(1.5F);
                    wp.getBlueAbility().subtractCooldown(1.5F);
                    wp.getOrangeAbility().subtractCooldown(1.5F);

                    wp.updateItems();

                    for (WarlordsEntity teammate : PlayerFilter
                            .entitiesAround(wp.getLocation(), 8, 8, 8)
                            .aliveTeammatesOfExcludingSelf(wp)
                            .closestFirst(wp.getLocation())
                            .limit(2)
                    ) {
                        wp.doOnStaticAbility(Soulbinding.class, Soulbinding::addSoulTeammatesCDReductions);

                        teammate.getRedAbility().subtractCooldown(1);
                        teammate.getPurpleAbility().subtractCooldown(1);
                        teammate.getBlueAbility().subtractCooldown(1);
                        teammate.getOrangeAbility().subtractCooldown(1);

                        teammate.updateItems();
                    }
                });
    }

    @Override
    protected Location getProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
        return new LocationBuilder(startingLocation.clone()).addY(-.5).backward(0f);
    }

    @Override
    protected void onSpawn(InternalProjectile projectile) {
        super.onSpawn(projectile);
        ArmorStand fallenSoul = projectile.getWorld().spawn(projectile.getStartingLocation().clone().add(0, -1.7, 0), ArmorStand.class);
        fallenSoul.setGravity(false);
        fallenSoul.setVisible(false);
        fallenSoul.setMarker(true);
        fallenSoul.setHelmet(new ItemStack(Material.ACACIA_FENCE_GATE));
        fallenSoul.setHeadPose(new EulerAngle(-Math.atan2(
                projectile.getSpeed().getY(),
                Math.sqrt(
                        Math.pow(projectile.getSpeed().getX(), 2) +
                                Math.pow(projectile.getSpeed().getZ(), 2)
                )
        ), 0, 0));
        projectile.addTask(new InternalProjectileTask() {
            @Override
            public void run(InternalProjectile projectile) {
                fallenSoul.teleport(projectile.getCurrentLocation().clone().add(0, -1.7, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                ParticleEffect.SPELL_WITCH.display(0, 0, 0, 0, 1, projectile.getCurrentLocation().clone().add(0, 0, 0), 500);
            }

            @Override
            public void onDestroy(InternalProjectile projectile) {
                fallenSoul.remove();
                ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.7F, 1, projectile.getCurrentLocation(), 500);
            }
        });
    }

    @Override
    protected void playEffect(InternalProjectile projectile) {
        super.playEffect(projectile);
    }

    @Override
    @Deprecated
    protected void playEffect(Location currentLocation, int ticksLived) {
    }

}
