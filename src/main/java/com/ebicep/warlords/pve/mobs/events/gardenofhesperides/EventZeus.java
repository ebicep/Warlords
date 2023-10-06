package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.abilities.ChainLightning;
import com.ebicep.warlords.abilities.HealingRain;
import com.ebicep.warlords.abilities.LightningBolt;
import com.ebicep.warlords.abilities.LightningRod;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class EventZeus extends AbstractZombie implements BossMinionMob {

    public EventZeus(Location spawnLocation) {
        this(spawnLocation, "Zeus", 100000, .33f, 20, 825, 946);
    }

    public EventZeus(
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
                new LightningBolt(258, 415, 3),
                new ChainLightning(7) {{
                    this.setTickDuration(40);
                }},
                new LightningRod() {
                    {
                        this.setHealthRestore(0);
                    }

                    @Override
                    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                name,
                                "ROD DMG",
                                LightningRod.class,
                                new LightningRod(),
                                wp,
                                CooldownTypes.BUFF,
                                cooldownManager -> {
                                },
                                5 * 20
                        ) {
                            @Override
                            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {

                                return currentDamageValue * 1.15f;
                            }
                        });
                        return super.onActivate(wp, player);
                    }
                },
                new HealingRain(60) {{
                    this.setPveMasterUpgrade(true);
                    this.setTickDuration(320);
                }}
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_ZEUS;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        if (event.getAbility().equals("Lightning Bolt")) {
            event.getFlags().add(InstanceFlags.PIERCE);
        }
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
