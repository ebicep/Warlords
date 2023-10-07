package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.abilities.FallenSouls;
import com.ebicep.warlords.abilities.ImpalingStrike;
import com.ebicep.warlords.abilities.IncendiaryCurse;
import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

public class EventHades extends AbstractZombie implements BossMinionMob {

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
                new FallenSouls(464, 512, 3),
                new IncendiaryCurse(524, 607, 10) {
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
                new UndyingArmy(60)
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
            AbstractMob<?> resurrected = (ThreadLocalRandom.current().nextBoolean() ? Mob.EVENT_ZEUS : Mob.EVENT_POSEIDON).createMob(warlordsNPC.getLocation());
            pveOption.spawnNewMob(resurrected);
            resurrected.getWarlordsNPC().setHealth(resurrected.getWarlordsNPC().getMaxHealth() / 2f);
            resurrectionTicksLeft = 2 * 60 * 20; // 2 minutes
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        ImpalingStrike.giveLeechCooldown(warlordsNPC, receiver, 5, 15, 25, finalEvent -> {});
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
