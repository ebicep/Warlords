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
import com.ebicep.warlords.pve.mobs.flags.Unsilencable;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class EventZeus extends AbstractMob implements BossMob, God, Unsilencable {

    public EventZeus(Location spawnLocation) {
        this(spawnLocation, "Zeus", 250000, .33f, 30, 825, 946);
    }

    public EventZeus(
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
                maxMeleeDamage,
                new LightningBolt(258, 415, 3, 3),
                new ChainLightning(7, 7) {{
                    this.setTickDuration(40);
                }},
                new ZeusLightningRod(),
                new HealingRain(60, 60) {{
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
                        WarlordsEntity dead = event.getWarlordsEntity();
                        if (!(dead instanceof WarlordsNPC npc) || dead == warlordsNPC) {
                            return;
                        }
                        AbstractMob npcMob = npc.getMob();
                        if (npcMob instanceof EventHades) {
                            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 2, .5f);
                            float healing = warlordsNPC.getCurrentHealth() * 0.25f;
                            warlordsNPC.addHealingInstance(
                                    npc,
                                    "Soul",
                                    healing,
                                    healing,
                                    0,
                                    100
                            );
                        } else if (npcMob instanceof EventPoseidon) {
                            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_DROWNED_AMBIENT, 2, .5f);
                            warlordsNPC.getAbilitiesMatching(ZeusLightningRod.class).forEach(lightningRod -> {
                                lightningRod.setHealthRestore(2);
                                lightningRod.setDamageBuff(lightningRod.getDamageBuff() + .1f);
                            });
                        }
                    }
                };
            }
        });
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
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 10, .5f);
            warlordsNPC.addSpeedModifier(warlordsNPC, "Purified", 50, 100, "BASE");
        }
    }

    @Override
    public Component getDescription() {
        return Component.text("God of the Sky", NamedTextColor.WHITE);
    }

    @Override
    public TextColor getColor() {
        return TextColor.color(242, 242, 242);
    }

    @Override
    public double weaponDropRate() {
        return BossMob.super.weaponDropRate() * 3;
    }

    private static class ZeusLightningRod extends LightningRod {

        private float damageBuff = 1.15f;

        public ZeusLightningRod() {
            super(15, 15);
            this.setHealthRestore(0);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
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
            return super.onActivate(wp);
        }

        public float getDamageBuff() {
            return damageBuff;
        }

        public void setDamageBuff(float damageBuff) {
            this.damageBuff = damageBuff;
        }
    }
}
