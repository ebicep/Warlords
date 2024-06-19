package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.customentities.nms.pve.pathfindergoals.NPCGuardWarlordsEntityGoal;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.Utils;
import net.citizensnpcs.api.ai.GoalController;
import org.bukkit.Location;
import org.bukkit.Sound;

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
            float damageResistance,
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
    public void giveGoals() {
        super.giveGoals();
        GoalController goalController = npc.getDefaultGoalController();
        goalController.clear();
        goalController.addBehavior(new NPCGuardWarlordsEntityGoal(npc, cronus.getWarlordsNPC(), 10), 2);
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ITEM_SHIELD_BLOCK, 10, 2f);
        if (Utils.isProjectile(event.getCause())) {
            event.setCancelled(true);
        }
    }

    public void setCronus(EventCronus cronus) {
        this.cronus = cronus;
    }
}
