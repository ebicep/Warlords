package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.abilities.*;
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
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class EventPoseidon extends AbstractZombie implements BossMinionMob {

    public EventPoseidon(Location spawnLocation) {
        this(spawnLocation, "Poseidon", 75000, .33f, 15, 725, 846);
    }

    public EventPoseidon(
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
                new EarthenSpike(600, 700, 6) {
                    @Override
                    protected void onSpikeTarget(WarlordsEntity caster, WarlordsEntity spikeTarget) {
                        super.onSpikeTarget(caster, spikeTarget);
                        CripplingStrike.cripple(caster, spikeTarget, name, 2 * 20);
                    }
                },
                new Boulder(551, 773, 5) {
                    @Override
                    protected Vector calculateSpeed(WarlordsEntity we) {
                        Location location = we.getLocation();
                        Vector speed = we.getLocation().getDirection().normalize().multiply(.25).setY(.01);
                        if (we instanceof WarlordsNPC npc && npc.getMob() != null) {
                            AbstractMob<?> npcMob = npc.getMob();
                            LivingEntity target = npcMob.getTarget();
                            if (target != null) {
                                double distance = location.distance(target.getBukkitLivingEntity().getLocation());
                                speed.setY(distance * .0025);
                            }
                        }
                        return speed;
                    }
                },
                new GroundSlamBerserker(558, 616, 10),
                new LastStand(60)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_POSEIDON;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Defeated Check",
                null,
                EventZeus.class,
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
                        if (pveOption.getMobs().size() != 1) {
                            return;
                        }
                        WarlordsEntity dead = event.getWarlordsEntity();
                        if (!(dead instanceof WarlordsNPC npc) || dead == warlordsNPC) {
                            return;
                        }
                        AbstractMob<?> npcMob = npc.getMob();
                        if (npcMob instanceof EventHades) {
                            float healing = warlordsNPC.getHealth() * 0.25f;
                            warlordsNPC.addHealingInstance(
                                    npc,
                                    "Soul",
                                    healing,
                                    healing,
                                    0,
                                    100
                            );
                        } else if (npcMob instanceof EventZeus) {
                            warlordsNPC.getAbilitiesMatching(Boulder.class).forEach(boulder -> {
                                boulder.setPveMasterUpgrade(true);
                                boulder.setMinDamageHeal(720);
                                boulder.setMaxDamageHeal(860);
                            });
                        }
                    }
                };
            }
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onFinalAttack(WarlordsDamageHealingFinalEvent event) {
        if (event.isDead()) {
            warlordsNPC.addSpeedModifier(warlordsNPC, "Purified", 50, 100, "BASE");
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}
