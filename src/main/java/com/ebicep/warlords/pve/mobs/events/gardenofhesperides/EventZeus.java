package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.abilities.ChainLightning;
import com.ebicep.warlords.abilities.HealingRain;
import com.ebicep.warlords.abilities.LightningBolt;
import com.ebicep.warlords.abilities.LightningRod;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EventZeus extends AbstractZombie implements BossMinionMob, God {

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
                new ZeusLightningRod(),
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
                        } else if (npcMob instanceof EventPoseidon) {
                            warlordsNPC.getAbilitiesMatching(ZeusLightningRod.class).forEach(lightningRod -> {
                                lightningRod.setHealthRestore(10);
                                lightningRod.setDamageBuff(lightningRod.getDamageBuff() + .1f);
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

    private static class ZeusLightningRod extends LightningRod {

        private float damageBuff = 1.15f;

        public ZeusLightningRod() {
            super(15);
            this.setHealthRestore(0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull @Nullable Player player) {
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
                    if (event.getAbility().isEmpty()) {
                        return currentDamageValue;
                    }
                    return currentDamageValue * damageBuff;
                }
            });
            return super.onActivate(wp, player);
        }

        public float getDamageBuff() {
            return damageBuff;
        }

        public void setDamageBuff(float damageBuff) {
            this.damageBuff = damageBuff;
        }
    }
}
