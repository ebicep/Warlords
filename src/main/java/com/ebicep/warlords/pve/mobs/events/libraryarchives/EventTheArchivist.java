package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.Unsilencable;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EventTheArchivist extends AbstractMob implements BossMob, Unsilencable {

    private int grimoireDeathCounter = 0;

    public EventTheArchivist(Location spawnLocation) {
        this(
                spawnLocation,
                "The Archivist",
                125000,
                0.21f,
                15,
                0,
                0
        );
    }

    public EventTheArchivist(
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
                new CripplingStrike() {{
                    this.pveMasterUpgrade = true;
                }},
                new ChainLightning(7, 7) {{
                    this.pveMasterUpgrade2 = true;
                }},
                new GroundSlamBerserker(10, 10),
                new PrismGuard(20),
                new LastStand(50, 50)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_THE_ARCHIVIST;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        option.getGame().registerEvents(new Listener() {
            @EventHandler
            public void onAbilityUse(WarlordsAbilityActivateEvent.Post event) {
                if (event.getWarlordsEntity().equals(warlordsNPC)) {
                    warlordsNPC.setMaxBaseHealth(warlordsNPC.getMaxBaseHealth() - 500);
                }
            }

            @EventHandler
            public void onMobDeath(WarlordsDeathEvent event) {
                if (event.getWarlordsEntity() instanceof WarlordsNPC wNPC && wNPC.getMob() instanceof EventGrimoire) {
                    grimoireDeathCounter++;
                    if (grimoireDeathCounter % 4 == 0) {
                        warlordsNPC.getSpec().setDamageResistance(warlordsNPC.getSpec().getDamageResistance() + 5);
                    }
                }
            }
        });
    }

    @Override
    public double weaponDropRate() {
        return BossMob.super.weaponDropRate() * 1.5;
    }

}
