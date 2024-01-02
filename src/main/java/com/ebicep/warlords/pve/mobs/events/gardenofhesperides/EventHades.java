package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.abilities.FallenSouls;
import com.ebicep.warlords.abilities.ImpalingStrike;
import com.ebicep.warlords.abilities.IncendiaryCurse;
import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.DynamicFlags;
import com.ebicep.warlords.pve.mobs.flags.ForceGivesEventPoints;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class EventHades extends AbstractMob implements BossMob, God, ForceGivesEventPoints {

    private int resurrectionCooldown = 0;

    public EventHades(Location spawnLocation) {
        this(spawnLocation, "Hades", 185000, .33f, 25, 524, 607);
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
                            AbstractMob npcMob = npc.getMob();
                            Entity target = npcMob.getTarget();
                            if (target != null) {
                                double distance = location.distance(target.getLocation());
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
    public Component getDescription() {
        return Component.text("God of the Underworld", NamedTextColor.GRAY);
    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.DARK_GRAY;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Defeated Check",
                null,
                EventHades.class,
                null,
                warlordsNPC,
                CooldownTypes.INTERNAL,
                cooldownManager -> {
                },
                false
        ) {
            @Override
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler(priority = EventPriority.HIGHEST)
                    public void onDeath(WarlordsDeathEvent event) {
                        WarlordsEntity dead = event.getWarlordsEntity();
                        if (!(dead instanceof WarlordsNPC npc) || dead == warlordsNPC) {
                            return;
                        }
                        AbstractMob npcMob = npc.getMob();
                        if (npcMob instanceof EventZeus || npcMob instanceof EventPoseidon) {
                            if (resurrectionCooldown > 0) {
                                return;
                            }
                            resurrect(npcMob.getMobRegistry());
                        }
                    }
                };
            }
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (resurrectionCooldown > 0) {
            resurrectionCooldown--;
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

    private void resurrect(Mob resurrectedMob) {
        resurrectionCooldown = 2 * 60 * 20; // 2 minutes
        Location spawnLocation = LocationUtils.getGroundLocation(warlordsNPC.getLocation());
        Utils.playGlobalSound(spawnLocation, Sound.ENTITY_WARDEN_DIG, 10, .75f);
        AbstractMob resurrected = resurrectedMob.createMob(spawnLocation.clone().add(0, -2.99, 0));
        resurrected.getDynamicFlags().add(DynamicFlags.UNSWAPPABLE);
        pveOption.spawnNewMob(resurrected);
        resurrected.getWarlordsNPC().setCurrentHealth(resurrected.getWarlordsNPC().getMaxHealth() / 2f);
        resurrected.getWarlordsNPC().setStunTicks(62);
        new GameRunnable(warlordsNPC.getGame()) {
            int ticksElapsed = 0;

            @Override
            public void run() {
                if (resurrected.getWarlordsNPC().isDead() || resurrected.getNpc().getStoredLocation() == null) {
                    this.cancel();
                    return;
                }
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
                resurrected.getNpc().teleport(resurrected.getNpc().getStoredLocation().add(0, .05, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
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
    }

    @Override
    public double weaponDropRate() {
        return BossMob.super.weaponDropRate() * 3;
    }
}
