package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.revenant.OrbsOfLifeBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class OrbsOfLife extends AbstractAbility implements BlueAbilityIcon, Duration, Heals<OrbsOfLife.HealingValues> {

    public static final double SPAWN_RADIUS = 1.15;
    public static final double ORB_HITBOX = 1.35;
    public static final double ORB_HITBOX_SQUARED = ORB_HITBOX * ORB_HITBOX;
    public static final int MAX_ALLIES = 2;

    public int orbsProduced = 0;

    private final int floatingOrbRadius = 20;
    private final HealingValues healingValues = new HealingValues();
    private int tickDuration = 280;
    private int orbTickMultiplier = 1;

    public OrbsOfLife() {
        super("Orbs of Life", 19.57f, 20);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Spawn ")
                               .append(Component.text("2 ", NamedTextColor.YELLOW))
                               .append(Component.text("initial orbs on cast."))
                               .append(Component.text("\n\nStriking and hitting enemies with abilities causes them to drop an orb of life that lasts "))
                               .append(Component.text("8", NamedTextColor.GOLD))
                               .append(Component.text(" seconds, restoring "))
                               .append(Heals.formatHealing(healingValues.orbHealing))
                               .append(Component.text(" health to the ally that picks it up. Other nearby allies recover "))
                               .append(Heals.formatHealing(healingValues.orbHealing))
                               .append(Component.text(" health. After 1.5 seconds the healing will increase by "))
                               .append(Component.text("40%", NamedTextColor.GREEN))
                               .append(Component.text(" over 6.5 seconds. Lasts "))
                               .append(Component.text(format(tickDuration / 20f) + " ", NamedTextColor.GOLD))
                               .append(Component.text("seconds."))
                               .append(Component.text("\n\nRecast to make the orbs levitate towards you or the nearest ally in a "))
                               .append(Component.text(floatingOrbRadius + " ", NamedTextColor.YELLOW))
                               .append(Component.text("block radius."));

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Orbs Produced", "" + orbsProduced));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "warrior.revenant.orbsoflife", 2, 1);

        OrbsOfLifeData data = new OrbsOfLifeData(this);
        PersistentCooldown<OrbsOfLifeData> orbsOfLifeCooldown = new PersistentCooldown<>(
                name,
                "ORBS",
                OrbsOfLifeData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    List<OrbPassenger> orbs = new ArrayList<>(data.getSpawnedOrbs());
                    orbs.forEach(OrbPassenger::remove);
                },
                false,
                tickDuration,
                orbsOfLife -> orbsOfLife.getSpawnedOrbs().isEmpty(),
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 != 0) {
                        return;
                    }
                    OrbsOfLifeData orbsOfLife = cooldown.getCooldownObject();
                    Iterator<OrbOfLife> itr = new ArrayList<>(orbsOfLife.getSpawnedOrbs()).iterator();
                    while (itr.hasNext()) {
                        OrbOfLife orb = itr.next();
                        Location orbPosition = orb.getArmorStand().getLocation();
                        WarlordsEntity teammateToHeal =
                                orb.getPlayerToMoveTowards() != null && orbPosition.distanceSquared(orb.getPlayerToMoveTowards().getLocation()) < ORB_HITBOX_SQUARED ?
                                orb.getPlayerToMoveTowards() :
                                PlayerFilter.entitiesAround(orbPosition, ORB_HITBOX, ORB_HITBOX, ORB_HITBOX)
                                            .aliveTeammatesOf(wp)
                                            .closestFirst(orbPosition)
                                            .findFirst()
                                            .orElse(null);
                        if (teammateToHeal != null) {
                            orb.remove();
                            itr.remove();

                            float orbHeal = healingValues.orbHealing.getValue();
                            // Increasing heal for low long orb lived for (up to +25%)
                            // 6.5 seconds = 130 ticks
                            // 6.5 seconds = 1 + (130/325) = 1.4
                            // 225 *= 1.4 = 315
                            if (orb.getPlayerToMoveTowards() == null) {
                                orbHeal *= 1 + orb.getTicksLived() / 325f;
                            }

                            healPlayer(teammateToHeal, wp, orbHeal);
                            Utils.playGlobalSound(teammateToHeal.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.2f, 1);

                            for (WarlordsEntity nearPlayer : PlayerFilter
                                    .entitiesAround(teammateToHeal, 6, 6, 6)
                                    .aliveTeammatesOfExcludingSelf(teammateToHeal)
                                    .leastAliveFirst()
                                    .limit(MAX_ALLIES)
                            ) {
                                healPlayer(nearPlayer, wp, orbHeal);
                                Utils.playGlobalSound(teammateToHeal.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.2f, 1);
                            }
                        } else {
                            // Checks whether the Orb of Life has lived for 8 seconds.
                            if (orb.getTicksLived() > orb.getTicksToLive() || (orb.getPlayerToMoveTowards() != null && orb.getPlayerToMoveTowards().isDead())) {
                                orb.remove();
                                itr.remove();
                            }
                        }
                    }
                })
        ) {

            @Override
            public void doBeforeReductionFromAttacker(WarlordsDamageHealingEvent event) {
                String ability = event.getCause();
                if (ability.equals("Vengeful Army") || event.getFlags().contains(InstanceFlags.RECURSIVE)) {
                    return;
                }
                spawnOrbs(wp, event.getWarlordsEntity(), ability, this);
                if (ability.equals("Crippling Strike")) {
                    spawnOrbs(wp, event.getWarlordsEntity(), ability, this);
                }
            }

        };
        wp.getCooldownManager().addCooldown(orbsOfLifeCooldown);

        spawnOrbs(wp, wp, "Orbs Of Life", orbsOfLifeCooldown);
        spawnOrbs(wp, wp, "Orbs Of Life", orbsOfLifeCooldown);
        if (pveMasterUpgrade) {
            spawnOrbs(wp, wp, "Orbs Of Life", orbsOfLifeCooldown);
        }

        addSecondaryAbility(
                1,
                () -> {
                    if (!wp.isAlive()) {
                        return;
                    }
                    Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.08f, 0.7f);
                    wp.getWorld().spawnParticle(
                            Particle.VILLAGER_HAPPY,
                            wp.getLocation().add(0, 1.5, 0),
                            10,
                            0.8,
                            0,
                            0.8,
                            0.2,
                            null,
                            true
                    );

                    //setting target player to move towards (includes self)
                    if (wp.isInPve()) {
                        data.getSpawnedOrbs()
                            .forEach(orb -> {
                                orb.getArmorStand().setGravity(false);
                                orb.setPlayerToMoveTowards(PlayerFilter
                                        .entitiesAround(orb.getArmorStand().getLocation(), floatingOrbRadius, floatingOrbRadius, floatingOrbRadius)
                                        .aliveTeammatesOf(wp)
                                        .leastAliveFirst()
                                        .findFirstOrNull()
                                );
                            });
                    } else {
                        data.getSpawnedOrbs()
                            .forEach(orb -> {
                                orb.getArmorStand().setGravity(false);
                                orb.setPlayerToMoveTowards(PlayerFilter
                                        .entitiesAround(orb.getArmorStand().getLocation(), floatingOrbRadius, floatingOrbRadius, floatingOrbRadius)
                                        .aliveTeammatesOf(wp)
                                        .closestFirst(orb.getArmorStand().getLocation())
                                        .findFirstOrNull()
                                );
                            });
                    }
                    //moving orb
                    new GameRunnable(wp.getGame()) {
                        @Override
                        public void run() {
                            data.getSpawnedOrbs().stream().filter(orb -> orb.getPlayerToMoveTowards() != null).forEach(targetOrb -> {
                                WarlordsEntity target = targetOrb.getPlayerToMoveTowards();
                                ArmorStand orbArmorStand = targetOrb.getArmorStand();
                                Location orbLocation = orbArmorStand.getLocation();
                                @NotNull List<Entity> orb = orbArmorStand.getPassengers();
                                //must eject passenger then reassign it before teleporting bc ???
                                orbArmorStand.eject();
                                orbArmorStand.teleport(
                                        new LocationBuilder(orbLocation.clone())
                                                .add(target.getLocation().toVector().subtract(orbLocation.toVector()).normalize().multiply(1))
                                );
                                orb.forEach(orbArmorStand::addPassenger);
                                orbArmorStand.getWorld().spawnParticle(
                                        Particle.VILLAGER_HAPPY,
                                        orbArmorStand.getLocation().add(0, 1.65, 0),
                                        1,
                                        0,
                                        0,
                                        0,
                                        0,
                                        null,
                                        true
                                );
                            });

                            if (data.getSpawnedOrbs().stream().noneMatch(orb -> orb.getPlayerToMoveTowards() != null)) {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(0, 1);

                    wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                            .append(Component.text(" Your current ", NamedTextColor.GRAY))
                            .append(Component.text(name, NamedTextColor.GREEN))
                            .append(Component.text(" will now levitate towards you or a teammate!", NamedTextColor.GRAY))
                    );
                },
                true,
                secondaryAbility -> wp.isDead() || !wp.getCooldownManager().hasCooldown(orbsOfLifeCooldown) || orbsOfLifeCooldown.isHidden()
        );

        return true;
    }

    private void healPlayer(WarlordsEntity teammateToHeal, @Nonnull WarlordsEntity wp, float orbHeal) {
        teammateToHeal.addInstance(InstanceBuilder
                .healing()
                .ability(this)
                .source(wp)
                .value(orbHeal)
                .flag(InstanceFlags.CAN_OVERHEAL_OTHERS, pveMasterUpgrade2)
        );
        if (pveMasterUpgrade2) {
            Overheal.giveOverHeal(wp, teammateToHeal);
        }
    }

    public static void spawnOrbs(WarlordsEntity owner, WarlordsEntity victim, String ability, PersistentCooldown<OrbsOfLifeData> cooldown) {
        if (ability.isEmpty() || ability.equals("Intervene")) {
            return;
        }
        if (cooldown.isHidden()) {
            return;
        }

        OrbsOfLifeData data = cooldown.getCooldownObject();
        Location location = victim.getLocation();
        Location spawnLocation = generateSpawnLocation(location, data.getSpawnedOrbs().stream().map(orb -> orb.getArmorStand().getLocation()).toList());

        OrbOfLife orb = new OrbOfLife(spawnLocation, cooldown.getFrom(), data.getOrbTickMultiplier(), data);
        data.getSpawnedOrbs().add(orb);
        data.getOrbsOfLife().orbsProduced++;
        if (++data.orbsProduced >= 50) {
            ChallengeAchievements.checkForAchievement(owner, ChallengeAchievements.ORBIFICATION);
        }
    }

    public static Location generateSpawnLocation(Location location, List<Location> previousLocations) {
        Location spawnLocation;
        int counter = 0;
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        do {
            counter++;
            //generate random  position in circle
            double angle = rand.nextDouble() * 360;
            double x = SPAWN_RADIUS * Math.cos(angle) + (rand.nextDouble() - .5);
            double z = SPAWN_RADIUS * Math.sin(angle) + (rand.nextDouble() - .5);
            spawnLocation = location.clone().add(x, 0, z);
        } while (counter < 50 && (orbsInsideBlock(spawnLocation) || nearLocation(spawnLocation, previousLocations)));
        return spawnLocation;
    }

    public static boolean orbsInsideBlock(Location location) {
        return location.getBlock().getType() != Material.AIR;
    }

    public static boolean nearLocation(Location location, List<Location> previousLocations) {
        for (Location previousLocation : previousLocations) {
            double distance = previousLocation.distanceSquared(location);
            if (distance < 1) {
                return true;
            }
        }
        return false;
    }

    public void setOrbTickMultiplier(int orbTickMultiplier) {
        this.orbTickMultiplier = orbTickMultiplier;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new OrbsOfLifeBranch(abilityTree, this);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class OrbOfLife extends OrbPassenger {

        private final OrbsOfLifeData data;
        private WarlordsEntity playerToMoveTowards = null;

        public OrbOfLife(Location location, WarlordsEntity owner, int tickMultiplier, OrbsOfLifeData data) {
            super(location, owner, tickMultiplier);
            this.data = data;
        }

        @Override
        public void remove() {
            super.remove();
            data.getSpawnedOrbs().remove(this);
        }

        public WarlordsEntity getPlayerToMoveTowards() {
            return playerToMoveTowards;
        }

        public void setPlayerToMoveTowards(WarlordsEntity playerToMoveTowards) {
            this.playerToMoveTowards = playerToMoveTowards;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.SetValue orbHealing = new Value.SetValue(210);
        private final List<Value> values = List.of(orbHealing);

        public Value.SetValue getOrbHealing() {
            return orbHealing;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class OrbsOfLifeData {

        private final OrbsOfLife orbsOfLife;
        private final List<OrbOfLife> spawnedOrbs = new ArrayList<>();
        private final int orbTickMultiplier;
        private int orbsProduced;

        public OrbsOfLifeData(OrbsOfLife orbsOfLife) {
            this.orbsOfLife = orbsOfLife;
            this.orbTickMultiplier = orbsOfLife.orbTickMultiplier;
        }

        public OrbsOfLife getOrbsOfLife() {
            return orbsOfLife;
        }

        public List<OrbOfLife> getSpawnedOrbs() {
            return spawnedOrbs;
        }

        public int getOrbTickMultiplier() {
            return orbTickMultiplier;
        }
    }

}
