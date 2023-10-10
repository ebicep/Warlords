package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.abilities.FallenSouls;
import com.ebicep.warlords.abilities.ImpalingStrike;
import com.ebicep.warlords.abilities.IncendiaryCurse;
import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.DynamicFlags;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class EventHades extends AbstractZombie implements BossMob, God {

    private int resurrectionTicksLeft = 2 * 60 * 20;

    public EventHades(Location spawnLocation) {
        this(spawnLocation, "Hades", 50000, .33f, 20, 524, 607);
    }

    public EventHades(
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
                new FallenSouls(464, 512, 3, 3),
                new IncendiaryCurse(524, 607, 8, 8) {
                    @Override
                    protected Vector calculateSpeed(WarlordsEntity we) {
                        Location location = we.getLocation();
                        Vector speed = we.getLocation().getDirection().normalize().multiply(.3).setY(.01);
                        if (we instanceof WarlordsNPC npc && npc.getMob() != null) {
                            AbstractMob<?> npcMob = npc.getMob();
                            LivingEntity target = npcMob.getTarget();
                            if (target != null) {
                                double distance = location.distance(target.getBukkitLivingEntity().getLocation());
                                speed.setY(distance * .003);
                            }
                        }
                        return speed;
                    }
                },
                new UndyingArmy(60f, 60)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_HADES;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (option.getMobs().size() != 1 || !option.getMobs().contains(this)) {
            return;
        }
        resurrectionTicksLeft--;
        if (resurrectionTicksLeft == 0) {
            Mob resurrectedMob = ThreadLocalRandom.current().nextBoolean() ? Mob.EVENT_ZEUS : Mob.EVENT_POSEIDON;
            Location spawnLocation = LocationUtils.getGroundLocation(warlordsNPC.getLocation());
            Utils.playGlobalSound(spawnLocation, Sound.ENTITY_WARDEN_DIG, 10, .75f);
            AbstractMob<?> resurrected = resurrectedMob.createMob(spawnLocation.clone().add(0, -2.99, 0));
            resurrected.getDynamicFlags().add(DynamicFlags.UNSWAPPABLE);
            pveOption.spawnNewMob(resurrected);
            resurrected.getWarlordsNPC().setHealth(resurrected.getWarlordsNPC().getMaxHealth() / 2f);
            resurrected.getWarlordsNPC().setStunTicks(62);
            new GameRunnable(warlordsNPC.getGame()) {
                int ticksElapsed = 0;

                @Override
                public void run() {
                    EffectUtils.displayParticle(
                            Particle.BLOCK_CRACK,
                            spawnLocation,
                            6,
                            .25,
                            0,
                            .25,
                            0,
                            Material.DIRT.createBlockData()
                    );
                    resurrected.getLivingEntity().teleport(resurrected.getLivingEntity().getLocation().add(0, .05, 0));
                    if (ticksElapsed++ == 60) {
                        if (resurrectedMob == Mob.EVENT_ZEUS) {
                            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_PHANTOM_AMBIENT, 10, .5f);
                        } else {
                            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 10, .5f);
                        }
                        resurrected.getDynamicFlags().remove(DynamicFlags.UNSWAPPABLE);
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);
            resurrectionTicksLeft = 2 * 60 * 20; // 2 minutes
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        ImpalingStrike.giveLeechCooldown(warlordsNPC, receiver, 3, .20f, .35f, finalEvent -> {});
    }

    @Override
    public void onFinalAttack(WarlordsDamageHealingFinalEvent event) {
        if (event.isDead()) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 10, .5f);
            warlordsNPC.addSpeedModifier(warlordsNPC, "Purified", 50, 100, "BASE");
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public double weaponDropRate() {
        return BossMob.super.weaponDropRate() * 1.5;
    }
}
