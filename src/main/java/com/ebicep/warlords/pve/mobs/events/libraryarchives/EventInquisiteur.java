package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.irongolem.GolemApprentice;
import com.ebicep.warlords.pve.mobs.pigzombie.PigShaman;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.ZombieSwordsman;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class EventInquisiteur extends AbstractMob implements BossMob {

    public EventInquisiteur(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        List<AbstractAbility> abilities = warlordsNPC.getAbilities();
        for (int i = 0; i < abilities.size(); i++) {
            AbstractAbility ability = abilities.get(i);
            FloatModifiable abilityCooldown = ability.getCooldown();
            switch (i) {
                case 0 -> abilityCooldown.setCurrentValue(5);
                case 1 -> abilityCooldown.setCurrentValue(8);
                case 2 -> abilityCooldown.setCurrentValue(13);
                case 3 -> abilityCooldown.setCurrentValue(18);
                case 4 -> abilityCooldown.setCurrentValue(30);
            }
            ability.setCurrentCooldown(abilityCooldown.getCurrentValue());
        }

        for (int i = 0; i < 3; i++) {
            option.spawnNewMob(new GolemApprentice(warlordsNPC.getLocation()));
            option.spawnNewMob(new PigShaman(warlordsNPC.getLocation()));
            option.spawnNewMob(new ZombieSwordsman(warlordsNPC.getLocation()));
        }

        new GameRunnable(option.getGame()) {

            final List<Location> necronomiconSpawnLocations = List.of(
                    new Location(warlordsNPC.getWorld(), -1.5, 34, 26.5),
                    new Location(warlordsNPC.getWorld(), -23.5, 34, 10.5),
                    new Location(warlordsNPC.getWorld(), -7.5, 34, -11.5),
                    new Location(warlordsNPC.getWorld(), 14.5, 34, 4.5)
            );

            @Override
            public void run() {
                if (warlordsNPC.isDead()) {
                    this.cancel();
                    return;
                }
                // spawn 1 Necronomicon, 1 random Boss Minion Grimoire, and 1 Scripted Grimoire
                option.spawnNewMob(new EventNecronomiconGrimoire(necronomiconSpawnLocations.get(ThreadLocalRandom.current().nextInt(necronomiconSpawnLocations.size()))));
                Mob minionGrimoire = switch (ThreadLocalRandom.current().nextInt(4)) {
                    case 0 -> Mob.EVENT_ROUGE_GRIMOIRE;
                    case 1 -> Mob.EVENT_VIOLETTE_GRIMOIRE;
                    case 2 -> Mob.EVENT_BLEUE_GRIMOIRE;
                    default -> Mob.EVENT_ORANGE_GRIMOIRE;
                };
                option.spawnNewMob(minionGrimoire.createMob(warlordsNPC.getLocation()));
                option.spawnNewMob(new EventScriptedGrimoire(warlordsNPC.getLocation()));
            }

        }.runTaskTimer(10 * 20, 10 * 20);

        AtomicInteger damageResistance = new AtomicInteger(0);
        option.getGame().registerEvents(new Listener() {
            @EventHandler
            public void onMobSpawn(WarlordsMobSpawnEvent event) {
                int mobCount = option.mobCount();
                // 10 other mobs excluding inquisiteur
                if (mobCount < 11) {
                    return;
                }
                // kill other mobs
                for (AbstractMob mob : option.getMobs()) {
                    if (mob == EventInquisiteur.this) {
                        continue;
                    }
                    // effects
                    EffectUtils.playParticleLinkAnimation(
                            warlordsNPC.getLocation(),
                            mob.getWarlordsNPC().getLocation(),
                            255, 0, 0,
                            1,
                            2
                    );
                    EffectUtils.strikeLightning(mob.getWarlordsNPC().getLocation(), false);
                    mob.getWarlordsNPC().die(warlordsNPC);
                }
                warlordsNPC.addHealingInstance(warlordsNPC, "Killing Blow", 10000, 10000, 0, 0);
                if (damageResistance.get() >= 50) {
                    return;
                }
                damageResistance.addAndGet(25);
            }
        });
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Killing Blow",
                "",
                EventInquisiteur.class,
                null,
                warlordsNPC,
                CooldownTypes.INTERNAL,
                cooldownManager -> {
                },
                false
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 - damageResistance.get() / 100f);
            }
        });

    }

    @Override
    public double weaponDropRate() {
        return BossMob.super.weaponDropRate() * 1.5;
    }

}
