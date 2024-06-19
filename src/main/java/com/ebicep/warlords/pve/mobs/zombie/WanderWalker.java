package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class WanderWalker extends AbstractMob implements AdvancedMob {

    private boolean recovered = false;
    private float healthRecover = .3f;
    private int speedIncrease = 5;
    private int knockbackResistance = 40;


    public WanderWalker(Location spawnLocation) {
        super(
                spawnLocation,
                "Zombie Raider",
                8500,
                0.31f,
                20,
                600,
                750
        );
    }

    public WanderWalker(
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
        return Mob.WANDER_WALKER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (!recovered && self.getCurrentHealth() <= self.getMaxHealth() * .1f) {
            recovered = true;
            float healAmount = self.getMaxHealth() * healthRecover;
            self.addInstance(InstanceBuilder
                    .healing()
                    .cause("Void Recovery")
                    .source(self)
                    .value(healAmount)
            );
            removeTarget();
            self.getSpeed().addBaseModifier(speedIncrease);
            warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Void Recovery",
                    null,
                    null,
                    null,
                    warlordsNPC,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                    }
            ) {
                final float calculatedKBRes = 1 - knockbackResistance / 100f;

                @Override
                public void multiplyKB(Vector currentVector) {
                    currentVector.multiply(calculatedKBRes);
                }
            });
        }
    }

}
