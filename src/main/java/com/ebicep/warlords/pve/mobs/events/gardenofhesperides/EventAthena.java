package com.ebicep.warlords.pve.mobs.events.gardenofhesperides;

import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.abilities.AbstractSpawnMobAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventAthena extends AbstractZombie implements BossMinionMob {

    private static final List<Mob> INITIAL_SPAWN = Arrays.asList(Mob.ZOMBIE_VANGUARD, Mob.ZOMBIE_LANCER, Mob.OVERGROWN_ZOMBIE, Mob.SCRUPULOUS_ZOMBIE);
    private boolean healthCheck = false;

    public EventAthena(Location spawnLocation) {
        this(spawnLocation, "Athena", 35000, .33f, 20, 820, 930);
    }

    public EventAthena(
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
                new AbstractSpawnMobAbility("Athena Mobs", 10, 100, 2) {
                    private int spawnCounter = 0;
                    private List<Location> spawnLocations = new ArrayList<>();

                    @Override
                    public int getSpawnAmount() {
                        return (int) (pveOption.getGame().warlordsPlayers().count() * INITIAL_SPAWN.size());
                    }

                    @Override
                    public AbstractMob<?> createMob(@Nonnull WarlordsEntity wp) {
                        if (spawnCounter % pveOption.getGame().warlordsPlayers().count() == 0 || spawnLocations.isEmpty()) {
                            Location randomSpawnLocation = pveOption.getRandomSpawnLocation(null);
                            if (randomSpawnLocation == null) {
                                return null;
                            }
                            spawnLocations = LocationUtils.getCircle(randomSpawnLocation, 3, pveOption.playerCount());
                        }
                        Mob mobToSpawn = INITIAL_SPAWN.get(spawnCounter);
                        spawnCounter++;
                        return mobToSpawn.createMob(spawnLocations.remove(0));
                    }
                }
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_ATHENA;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (pveOption.mobCount() > 1) {
            //TODO animation
            event.setCancelled(true);
            return;
        }
        if (!healthCheck && self.getHealth() / self.getMaxHealth() < 0.25) {
            healthCheck = true;
            warlordsNPC.getAbilitiesMatching(Shockwave.class).forEach(ability -> ability.getCooldown().setCurrentValue(5));
        }
    }

    private static class Shockwave extends AbstractPveAbility {

        private float radius = 10;

        public Shockwave() {
            super("Shockwave", 650, 800, 8, 100);
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            wp.subtractEnergy(name, energyCost, false);
            //TODO spear in ground animation
            new FallingBlockWaveEffect(wp.getLocation().add(0, 1, 0), radius, 1, Material.COARSE_DIRT).play();
            PlayerFilter.entitiesAround(wp, radius, radius, radius)
                        .aliveEnemiesOf(wp)
                        .forEach(warlordsEntity -> {
                            warlordsEntity.addSpeedModifier(wp, name, -15, 40);
                            warlordsEntity.addDamageInstance(
                                    wp,
                                    name,
                                    minDamageHeal,
                                    maxDamageHeal,
                                    critChance,
                                    critMultiplier
                            );
                        });
            return true;
        }
    }

    @Override
    public Component getDescription() {
        return Component.text("Dude", NamedTextColor.LIGHT_PURPLE);
    }

    @Override
    public NamedTextColor getColor() {
        return NamedTextColor.DARK_PURPLE;
    }
}
