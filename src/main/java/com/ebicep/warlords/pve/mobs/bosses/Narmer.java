package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.abilities.AbstractSpawnMobAbility;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.NarmerAcolyte;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.pve.mobs.zombie.BasicZombie;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Narmer extends AbstractZombie implements BossMob {

    private final int executeRadius = 80;
    private int timesMegaEarthQuakeActivated = 0;
    private final List<WarlordsEntity> acolytes = new ArrayList<>();
    private Listener listener;
    private int acolyteDeathTickWindow = 0;

    public Narmer(Location spawnLocation) {
        super(spawnLocation,
                "Narmer",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.BURNING_WITHER_SKELETON),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
                        ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
                        Weapons.WALKING_STICK.getItem()
                ),
                16000,
                0.16f,
                20,
                1600,
                2000,
                new FlameBurst(15),
                new GroundShred()
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        DifficultyIndex difficulty = option.getDifficulty();

        if (difficulty == DifficultyIndex.ENDLESS) {
            for (AbstractAbility ability : warlordsNPC.getAbilities()) {
                if (ability instanceof GroundShred) {
                    ability.setCooldown(ability.getCooldown() / 2);
                }
            }
        }

        SpawnNarmerAcolyteAbility spawnNarmerAcolyteAbility = new SpawnNarmerAcolyteAbility(this);
        this.warlordsNPC.getAbilities().add(spawnNarmerAcolyteAbility);

        float multiplier = difficulty == DifficultyIndex.EXTREME ? 3 : difficulty == DifficultyIndex.HARD ? 2 : 1;

        for (int i = 0; i < (multiplier * option.playerCount()); i++) {
            spawnNarmerAcolyteAbility.spawnMob(warlordsNPC);
        }


        for (int i = 0; i < 8; i++) {
            option.spawnNewMob(new BasicZombie(warlordsNPC.getLocation()));
        }

        listener = new Listener() {

            @EventHandler
            public void onDamageHealEvent(WarlordsDamageHealingEvent event) {
                if (event.getWarlordsEntity().equals(getWarlordsNPC())) {
                    float executeHealth = warlordsNPC.getMaxHealth() * 0.4f;
                    if (warlordsNPC.getHealth() < executeHealth && !acolytes.isEmpty()) {
                        warlordsNPC.setHealth(warlordsNPC.getHealth());
                        Location loc = warlordsNPC.getLocation();
                        warlordsNPC.getGame().forEachOnlineWarlordsEntity(we -> {
                            Utils.playGlobalSound(loc, Sound.ENTITY_BLAZE_HURT, 2, 0.2f);
                            Utils.playGlobalSound(loc, "mage.arcaneshield.activation", 0.4f, 0.5f);
                            we.sendMessage(Component.text("Narmer cannot take more damage while his acolytes are still alive!", NamedTextColor.RED));
                        });
                        event.setCancelled(true);
                    }
                }
            }

            @EventHandler
            private void onAllyDeath(WarlordsDeathEvent event) {
                WarlordsEntity dead = event.getWarlordsEntity();
                Location location = warlordsNPC.getLocation();

                if (dead.isTeammate(warlordsNPC)) {
                    warlordsNPC.setHealth(warlordsNPC.getHealth() * 1.15f);
                }

                if (acolytes.contains(dead)) {
                    acolytes.remove(dead);
                    Utils.playGlobalSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 0.4f);
                    EffectUtils.playHelixAnimation(
                            location.add(0, 0.15, 0),
                            12,
                            Particle.SPELL,
                            3,
                            60
                    );

                    float multiplier = switch (difficulty) {
                        case EASY -> 2;
                        case HARD -> 16;
                        case EXTREME -> 32;
                        default -> 8;
                    };
                    if (acolyteDeathTickWindow > 0) {
                        Utils.playGlobalSound(location, Sound.ENTITY_WITHER_DEATH, 500, 0.2f);
                        Utils.playGlobalSound(location, Sound.ENTITY_WITHER_DEATH, 500, 0.2f);
                        EffectUtils.strikeLightning(location, false, 12);
                        List<WarlordsEntity> warlordsEntities = PlayerFilter
                                .entitiesAround(warlordsNPC, executeRadius, executeRadius, executeRadius)
                                .aliveEnemiesOf(warlordsNPC)
                                .toList();
                        for (WarlordsEntity enemy : warlordsEntities) {
                            enemy.addDamageInstance(
                                    warlordsNPC,
                                    "Death Wish",
                                    965 * multiplier,
                                    1138 * multiplier,
                                    0,
                                    100
                            );
                            enemy.sendMessage(Component.text("HINT: Killing Acolytes too quickly might result in an unfavourable outcome.",
                                    NamedTextColor.RED
                            ));
                        }
                        for (WarlordsEntity warlordsEntity : warlordsEntities) {
                            ChallengeAchievements.checkForAchievement(warlordsEntity, ChallengeAchievements.FISSURED_END);
                            break;
                        }
                        timesMegaEarthQuakeActivated++;
                    } else {
                        for (WarlordsEntity enemy : PlayerFilter
                                .entitiesAround(warlordsNPC, 15, 15, 15)
                                .aliveEnemiesOf(warlordsNPC)
                        ) {
                            Utils.addKnockback(name, warlordsNPC.getLocation(), enemy, -2.5, 0.25);
                            enemy.addDamageInstance(
                                    warlordsNPC,
                                    "Acolyte Revenge",
                                    965,
                                    1138,
                                    0,
                                    100
                            );
                        }
                    }

                    if (acolyteDeathTickWindow <= 0) {
                        acolyteDeathTickWindow = difficulty == DifficultyIndex.EXTREME ? 100 : difficulty == DifficultyIndex.HARD ? 60 : 20;
                    }

                    List<WarlordsEntity> selfAcolytes = spawnNarmerAcolyteAbility.getSelfAcolytes();
                    if (selfAcolytes.contains(dead)) {
                        spawnNarmerAcolyteAbility.setCurrentCooldown(spawnNarmerAcolyteAbility.getCooldown());
                        selfAcolytes.remove(dead);
                    }

                }
            }
        };
        warlordsNPC.getGame().registerEvents(listener);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        Location loc = warlordsNPC.getLocation();

        if (acolyteDeathTickWindow > 0) {
            acolyteDeathTickWindow--;

            ChatUtils.sendTitleToGamePlayers(
                    getWarlordsNPC().getGame(),
                    Component.text("Death Wish", NamedTextColor.RED),
                    Component.text(acolyteDeathTickWindow / 10f, NamedTextColor.YELLOW),
                    0, acolyteDeathTickWindow, 0
            );
        }

        if (ticksElapsed % 15 == 0) {
            for (WarlordsEntity acolyte : acolytes) {
                EffectUtils.playParticleLinkAnimation(loc, acolyte.getLocation(), Particle.DRIP_LAVA);
            }
        }

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        EffectUtils.playRandomHitEffect(self.getLocation(), 255, 255, 255, 7);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, Particle.FIREWORKS_SPARK, 3, 20);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.WHITE)
                                                                       .with(FireworkEffect.Type.STAR)
                                                                       .withTrail()
                                                                       .build());

        if (timesMegaEarthQuakeActivated >= 2) {
            ChallengeAchievements.checkForAchievement(killer, ChallengeAchievements.NEAR_DEATH_EXPERIENCE);
        }

        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }

    @Override
    public NamedTextColor getColor() {
        return NamedTextColor.RED;
    }

    @Override
    public Component getDescription() {
        return Component.text("Unifier of Worlds", NamedTextColor.YELLOW);
    }

    public List<WarlordsEntity> getAcolytes() {
        return acolytes;
    }

    private static class SpawnNarmerAcolyteAbility extends AbstractSpawnMobAbility {

        private final Narmer narmer;
        private final List<WarlordsEntity> selfAcolytes = new ArrayList<>(); // spawned acolytes using this ability

        public SpawnNarmerAcolyteAbility(Narmer narmer) {
            super("Narmer Acolyte", 15);
            this.narmer = narmer;
            this.pveOption = narmer.pveOption;
        }

        @Override
        public AbstractMob<?> createMob(@Nonnull WarlordsEntity wp) {
            return new NarmerAcolyte(wp.getLocation());
        }

        @Override
        public int getSpawnAmount() {
            long playerCount = pveOption.getGame().warlordsPlayers().count();
            DifficultyIndex difficulty = pveOption.getDifficulty();
            float multiplier = difficulty == DifficultyIndex.EXTREME ? 3 : difficulty == DifficultyIndex.HARD ? 2 : 1;
            return narmer.getAcolytes().size() < multiplier * playerCount ? 1 : 0;
        }

        @Override
        public void onMobSpawn(WarlordsNPC warlordsNPC) {
            narmer.getAcolytes().add(warlordsNPC);
            selfAcolytes.add(warlordsNPC);
        }

        public List<WarlordsEntity> getSelfAcolytes() {
            return selfAcolytes;
        }
    }

    private static class GroundShred extends AbstractPveAbility {

        private final int earthQuakeRadius = 12;

        public GroundShred() {
            super(
                    "Ground Shred",
                    750,
                    900,
                    8,
                    100
            );
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            wp.subtractEnergy(energyCost, false);

            Location loc = wp.getLocation();
            Utils.playGlobalSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 0.4f);
            EffectUtils.strikeLightning(loc, false);
            EffectUtils.playSphereAnimation(loc, earthQuakeRadius, Particle.SPELL_WITCH, 2);
            EffectUtils.playHelixAnimation(loc, earthQuakeRadius, Particle.FIREWORKS_SPARK, 2, 40);
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(wp, earthQuakeRadius, earthQuakeRadius, earthQuakeRadius)
                    .aliveEnemiesOf(wp)
            ) {
                Utils.addKnockback(name, loc, enemy, -2.5, 0.25);
                enemy.addDamageInstance(
                        wp,
                        name,
                        minDamageHeal,
                        maxDamageHeal,
                        critChance,
                        critMultiplier
                );
            }
            return true;
        }
    }
}
