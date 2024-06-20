package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.abilities.*;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.Unsilencable;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventPoseidon extends AbstractMob implements BossMob, God, Unsilencable {

    public EventPoseidon(Location spawnLocation) {
        this(spawnLocation, "Poseidon", 225000, .33f, 25, 725, 846);
    }

    public EventPoseidon(
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
                new EarthenSpike(6, 6) {
                    {
                        this.getDamageValues().getSpikeDamage().min().setBaseValue(600);
                        this.getDamageValues().getSpikeDamage().max().setBaseValue(700);
                    }
                    @Override
                    public boolean onActivate(@Nonnull WarlordsEntity wp) {
                        List<WarlordsEntity> spiked = new ArrayList<>();
                        float rad = 10;
                        for (WarlordsEntity spikeTarget : PlayerFilter
                                .entitiesAround(wp, rad, rad, rad)
                                .aliveEnemiesOf(wp)
                        ) {
                            if (!LocationUtils.hasLineOfSight(wp, spikeTarget)) {
                                continue;
                            }

                            spiked.add(spikeTarget);
                            spikeTarget(wp, spikeTarget);

                            if (spiked.size() >= 3) {
                                addTimesUsed();
                                break;
                            }
                        }
                        if (spiked.size() < 3 && !spiked.isEmpty()) {
                            for (int i = 0; i < 3 - spiked.size(); i++) {
                                spikeTarget(wp, spiked.get(0));
                            }
                        }
                        return !spiked.isEmpty();
                    }

                    @Override
                    protected void onSpikeTarget(WarlordsEntity caster, WarlordsEntity spikeTarget) {
                        super.onSpikeTarget(caster, spikeTarget);
                        Optional<CripplingStrike.CripplingStrikeData> optionalCripplingStrike = new CooldownFilter<>(spikeTarget, RegularCooldown.class)
                                .filterCooldownClassAndMapToObjectsOfClass(CripplingStrike.CripplingStrikeData.class)
                                .findAny();
                        if (optionalCripplingStrike.isPresent()) {
                            CripplingStrike.CripplingStrikeData data = optionalCripplingStrike.get();
                            spikeTarget.getCooldownManager().removeCooldown(CripplingStrike.CripplingStrikeData.class, true);
                            int newCrippleCounter = Math.min(data.consecutiveStrikeCounter() + 1, 2);
                            CripplingStrike.cripple(caster,
                                    spikeTarget,
                                    name,
                                    newCrippleCounter,
                                    2 * 20,
                                    convertToDivisionDecimal(10) - newCrippleCounter * convertToPercent(5)
                            );
                        } else {
                            spikeTarget.sendMessage(Component.text("You are ", NamedTextColor.GRAY)
                                                             .append(Component.text("crippled", NamedTextColor.RED))
                                                             .append(Component.text(".", NamedTextColor.GRAY)));
                            CripplingStrike.cripple(caster, spikeTarget, name, 2 * 20, convertToDivisionDecimal(10));
                        }
                    }
                },
                new Boulder(5, 5) {
                    {
                        this.getDamageValues().getBoulderDamage().min().setBaseValue(551);
                        this.getDamageValues().getBoulderDamage().max().setBaseValue(773);
                    }
                    @Override
                    protected Vector calculateSpeed(WarlordsEntity we) {
                        Location location = we.getLocation();
                        Vector speed = we.getLocation().getDirection().normalize().multiply(.25).setY(.01);
                        if (we instanceof WarlordsNPC npc && npc.getMob() != null) {
                            AbstractMob npcMob = npc.getMob();
                            Entity target = npcMob.getTarget();
                            if (target != null) {
                                double distance = location.distance(target.getLocation());
                                speed.setY(distance * .0025);
                            }
                        }
                        return speed;
                    }
                },
                new GroundSlamBerserker(10, 10) {{
                    this.getDamageValues().getSlamDamage().min().setBaseValue(558);
                    this.getDamageValues().getSlamDamage().max().setBaseValue(616);
                }},
                new LastStand(60f, 60)
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
                        WarlordsEntity dead = event.getWarlordsEntity();
                        if (!(dead instanceof WarlordsNPC npc) || dead == warlordsNPC) {
                            return;
                        }
                        AbstractMob npcMob = npc.getMob();
                        if (npcMob instanceof EventHades) {
                            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 2, .5f);
                            float healing = warlordsNPC.getCurrentHealth() * 0.25f;
                            warlordsNPC.addInstance(InstanceBuilder
                                    .healing()
                                    .cause("Soul")
                                    .source(warlordsNPC)
                                    .value(healing)
                            );
                        } else if (npcMob instanceof EventZeus) {
                            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_PHANTOM_AMBIENT, 2, .5f);
                            warlordsNPC.getAbilitiesMatching(Boulder.class).forEach(boulder -> {
                                boulder.setPveMasterUpgrade(true);
                                Value.RangedValueCritable boulderDamage = boulder.getDamageValues().getBoulderDamage();
                                boulderDamage.min().setBaseValue(720);
                                boulderDamage.max().setBaseValue(860);
                            });
                        }
                    }
                };
            }
        });
    }

    @Override
    public void onFinalAttack(WarlordsDamageHealingFinalEvent event) {
        if (event.isDead()) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 10, .5f);
            warlordsNPC.addSpeedModifier(warlordsNPC, "Purified", 50, 100, "BASE");
        }
    }

    @Override
    public double weaponDropRate() {
        return BossMob.super.weaponDropRate() * 3;
    }

    @Override
    public Component getDescription() {
        return Component.text("God of the Sea", TextColor.color(28,163,236));
    }

    @Override
    public TextColor getColor() {
        return TextColor.color(35,137,218);
    }
}
