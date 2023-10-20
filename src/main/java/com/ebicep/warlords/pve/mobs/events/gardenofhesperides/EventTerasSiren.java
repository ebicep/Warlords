package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.customentities.nms.pve.pathfindergoals.NPCFollowWarlordsEntityGoal;
import com.ebicep.customentities.nms.pve.pathfindergoals.NPCTargetAggroWarlordsEntityGoal;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.Utils;
import net.citizensnpcs.api.ai.GoalController;
import org.bukkit.Location;

public class EventTerasSiren extends AbstractMob implements BossMinionMob, Teras {

    private EventCronus cronus;

    public EventTerasSiren(Location spawnLocation) {
        this(spawnLocation, "Teras Siren", 4100, 0.6f, 0, 250, 350);
    }

    public EventTerasSiren(
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
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_TERAS_SIREN;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
    }

    @Override
    public void giveGoals() {
        super.giveGoals();
        GoalController goalController = npc.getDefaultGoalController();
        goalController.clear();
        goalController.addGoal(new NPCFollowWarlordsEntityGoal(npc, cronus.getWarlordsNPC(), 1, 10), 0);
        goalController.addGoal(new NPCTargetAggroWarlordsEntityGoal(npc, 40), 1);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        //TODO animation
        if (Utils.isProjectile(event.getAbility())) {
            event.setCancelled(true);
        }
    }

    public void setCronus(EventCronus cronus) {
        this.cronus = cronus;
    }
}
