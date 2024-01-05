package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.abilities.AbstractSpawnMobAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EventAthena extends AbstractMob implements BossMob, LesserGod {

    private static final List<Mob> INITIAL_SPAWN = Arrays.asList(Mob.ZOMBIE_VANGUARD, Mob.ZOMBIE_LANCER, Mob.OVERGROWN_ZOMBIE, Mob.SCRUPULOUS_ZOMBIE);
    private boolean healthCheck = false;

    public EventAthena(Location spawnLocation) {
        this(spawnLocation, "Athena", 35000, .33f, 20, 820, 930);
    }

    public EventAthena(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new Shockwave(),
                new AbstractSpawnMobAbility("Athena Mobs", 45, 100, 2) {
                    private int spawnCounter = 0;
                    private List<Location> spawnLocations = new ArrayList<>();

                    @Override
                    public int getSpawnAmount() {
                        return (int) (pveOption.getGame().warlordsPlayers().count());
                    }

                    @Override
                    public AbstractMob createMob(@Nonnull WarlordsEntity wp) {
                        if (spawnCounter % pveOption.getGame().warlordsPlayers().count() == 0 || spawnLocations.isEmpty()) {
                            Location randomSpawnLocation = pveOption.getRandomSpawnLocation(null);
                            if (randomSpawnLocation == null) {
                                return null;
                            }
                            spawnLocations = LocationUtils.getCircle(randomSpawnLocation, 3, pveOption.playerCount());
                        }
                        Mob mobToSpawn = INITIAL_SPAWN.get(ThreadLocalRandom.current().nextInt(INITIAL_SPAWN.size()));
                        spawnCounter++;
                        return mobToSpawn.createMob(spawnLocations.remove(0));
                    }
                }
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_ATHENA;
    }

    @Override
    public Component getDescription() {
        return Component.text("God of Wisdom", NamedTextColor.AQUA);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.DARK_AQUA;
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (pveOption.mobCount() > 1) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ITEM_SHIELD_BLOCK, 10, 1.7f);
            pveOption.getMobs().forEach(mob -> {
                if (mob == this) {
                    return;
                }
                EffectUtils.playParticleLinkAnimation(
                        mob.getWarlordsNPC().getLocation(),
                        warlordsNPC.getLocation(),
                        Particle.ENCHANTMENT_TABLE
                );
            });
            event.setCancelled(true);
            return;
        }
        if (!healthCheck && self.getCurrentHealth() / self.getMaxHealth() < 0.25) {
            healthCheck = true;
            warlordsNPC.getAbilitiesMatching(Shockwave.class).forEach(ability -> ability.getCooldown().setBaseValue(5));
        }
    }

    private static class Shockwave extends AbstractPveAbility {

        private float radius = 10;

        public Shockwave() {
            super("Shockwave", 650, 800, 8, 100, false);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {

            ItemStack item;
            Equipment equipment;
            if (wp instanceof WarlordsNPC warlordsNPC) {
                equipment = warlordsNPC.getNpc().getTraitNullable(Equipment.class);
            } else {
                equipment = null;
            }
            if (equipment != null) {
                item = equipment.get(Equipment.EquipmentSlot.HAND);
            } else {
                item = Weapons.NEW_LEAF_AXE.getItem();
            }
            double yOffset = 5;
            int animationTicks = 10;
            ArmorStand stand = Utils.spawnArmorStand(wp.getLocation().add(0, yOffset, 0), armorStand -> {
                armorStand.getEquipment().setHelmet(item);
                armorStand.setHeadPose(new EulerAngle(Math.toRadians(180), 0, 0));
            });
            Utils.playGlobalSound(wp.getLocation(), "rogue.healingremedy.impact", 500, 1.2f);
            if (wp instanceof WarlordsNPC warlordsNPC) {
                warlordsNPC.setStunTicks(animationTicks);
            }
            new GameRunnable(wp.getGame()) {
                int ticksElapsed = 0;

                @Override
                public void run() {
                    stand.teleport(stand.getLocation().add(0, -yOffset / (animationTicks * .9), 0));
                    if (ticksElapsed++ == animationTicks) {
                        stand.remove();
                        EffectUtils.strikeLightning(wp.getLocation(), false, 2);
                        Utils.playGlobalSound(wp.getLocation(), "warrior.groundslam.activation", 2, 1);
                        new FallingBlockWaveEffect(wp.getLocation().add(0, 1.1, 0), radius, 1, Material.COARSE_DIRT).play();
                        PlayerFilter.entitiesAround(wp, radius, radius, radius)
                                    .aliveEnemiesOf(wp)
                                    .forEach(warlordsEntity -> {
                                        warlordsEntity.addSpeedModifier(wp, name, -15, 40);
                                        warlordsEntity.addDamageInstance(
                                                wp,
                                                name,
                                                minDamageHeal,
                                                maxDamageHeal,
                                                critChance,
                                                critMultiplier
                                        );
                                    });
                        if (equipment != null) {
                            equipment.set(Equipment.EquipmentSlot.HAND, item);
                        }
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);
            return true;
        }
    }
}
