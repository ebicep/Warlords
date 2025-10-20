package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsMobConvertEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Aspect;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.annotation.Transient;

import java.util.*;

public class LegendaryRequiem extends AbstractLegendaryWeapon implements PassiveCounter {

    public static final RandomCollection<Integer> SPAWN_AMOUNT = new RandomCollection<Integer>()
            .add(2, 4)
            .add(2, 5)
            .add(1, 6)
            .add(1, 7);
    public static final HashMap<DifficultyIndex, Mob> DIFFICULTY_SPAWNS = new HashMap<>() {{
        put(DifficultyIndex.EASY, Mob.ZOMBIE_LAMENT);
        put(DifficultyIndex.NORMAL, Mob.ZOMBIE_LAMENT);
        put(DifficultyIndex.HARD, Mob.REQIUEM_GUARDIAN);
        put(DifficultyIndex.EXTREME, Mob.REQIUEM_GUARDIAN);
        put(DifficultyIndex.ENDLESS, Mob.REQIUEM_GUARDIAN);
    }};
    public static final ItemStack CHESTPLATE = com.ebicep.warlords.util.warlords.Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 200, 0);
    public static final ItemStack LEGGINGS = com.ebicep.warlords.util.warlords.Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 200, 0);
    public static final ItemStack BOOTS = com.ebicep.warlords.util.warlords.Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 200, 0);
    public static final int SPAWN_LIMIT = 12;
    public static final int COOLDOWN = 60;
    public static final int COOLDOWN_INCREASE_PER_UPGRADE = -5;

    @Transient
    private int counter = 0;

    public LegendaryRequiem() {
    }

    public LegendaryRequiem(UUID uuid) {
        super(uuid);
    }

    public LegendaryRequiem(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        this.counter = 0;

        DifficultyIndex difficulty = pveOption.getDifficulty();

        Game game = player.getGame();
//        game.registerEvents(new Listener() {
//
//            @EventHandler
//            public void onAddCooldown(WarlordsAddCooldownEvent event) {
//                AbstractCooldown<?> cooldown = event.getAbstractCooldown();
//                if (!(cooldown.getCooldownObject() instanceof UndyingArmy.UndyingArmyData)) {
//                    return;
//                }
//                if (!Objects.equals(event.getWarlordsEntity(), player)) {
//                    return;
//                }
//                int alliedNPCs = (int) game.warlordsNPCs()
//                                           .filter(warlordsNPC -> warlordsNPC.isTeammate(player))
//                                           .count();
//                int spawnAmount = MathUtils.generateRandomValueBetweenInclusive(2, 4);
//                if (alliedNPCs + spawnAmount > SPAWN_LIMIT) {
//                    spawnAmount = SPAWN_LIMIT - alliedNPCs;
//                }
//                if (spawnAmount <= 0) {
//                    return;
//                }
//                List<WarlordsNPC> toConvert = PlayerFilterGeneric.playingGameWarlordsNPCs(game)
//                                                                 .aliveEnemiesOf(player)
//                                                                 .filter(warlordsNPC -> !(warlordsNPC.getMob() instanceof BossMob) && !(warlordsNPC.getMob() instanceof BossMinionMob))
//                                                                 .filter(warlordsNPC -> warlordsNPC.getMob().getEquipment() != null)
//                                                                 .limit(spawnAmount)
//                                                                 .toList();
//                toConvert.forEach(convertedEnemy -> {
//                    EffectUtils.playCylinderAnimation(convertedEnemy.getLocation(), 1.05, Particle.HAPPY_VILLAGER, 1);
//                    convertedEnemy.setTeam(warlordsPlayer.getTeam());
//                    AbstractMob mob = DIFFICULTY_SPAWNS.getOrDefault(difficulty, Mob.ZOMBIE_LANCER).createMob(player.getLocation());
//                    mob.setAspect(Aspect.getRandomAspect(List.of(Aspect.INFERNAL)));
//                    updateMobEquipment(mob, player);
//                    //removing teammate mobs that are aggroed on converted target
//                    PlayerFilterGeneric.playingGameWarlordsNPCs(game)
//                                       .aliveTeammatesOf(player)
//                                       .filter(teammate -> {
//                                           Entity target = teammate.getMob().getTarget();
//                                           return target != null && Objects.equals(target, convertedEnemy.getEntity());
//                                       })
//                                       .forEach(teammate -> teammate.getMob().removeTarget());
//                    mob.removeTarget();
//                });
//                Bukkit.getPluginManager().callEvent(new WarlordsMobConvertEvent(player, toConvert));
//                new GameRunnable(game) {
//
//                    @Override
//                    public void run() {
//                        toConvert.forEach(convertedEnemy -> {
//                            AbstractMob mob = convertedEnemy.getMob();
//                            if (pveOption.getMobs().contains(mob)) {
//                                mob.getWarlordsNPC().die(mob.getWarlordsNPC(), WarlordsDeathEvent.DeathInfoBuilder.create().setForced(true));
//                            }
//                        });
//                        toConvert.clear();
//                    }
//                }.runTaskLater(20 * 180);
//            }
//
//        });
        int cooldown = (COOLDOWN + COOLDOWN_INCREASE_PER_UPGRADE * getTitleLevel()) * 20;

        new GameRunnable(game) {

            final HashSet<AbstractMob> allSpawnedMobs = new HashSet<>();
            int ticksElapsed = -1;
            int shiftTickTime = 0;

            @Override
            public void run() {
                ticksElapsed++;
                counter = (ticksElapsed % cooldown) / 20;
                if (player.isDead()) {
                    return;
                }
                if (player.isSneaking()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, .5f + .05f * shiftTickTime);
                    shiftTickTime++;
                    if (shiftTickTime == 20) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        allSpawnedMobs.forEach(mob -> {
                            if (pveOption.getMobs().contains(mob)) {
                                mob.getWarlordsNPC().die(mob.getWarlordsNPC(), WarlordsDeathEvent.DeathInfoBuilder.create().setForced(true));
                                pveOption.despawnMob(mob);
                            }
                        });
                        allSpawnedMobs.clear();
                        shiftTickTime = -20;
                    }
                } else {
                    shiftTickTime = 0;
                }
                if (ticksElapsed % cooldown != 0) {
                    return;
                }
                int spawnAmount = SPAWN_AMOUNT.next();
                int alliedNPCs = (int) game.warlordsNPCs()
                                           .filter(warlordsNPC -> warlordsNPC.isTeammate(player))
                                           .count();
                if (alliedNPCs + spawnAmount > SPAWN_LIMIT) {
                    spawnAmount = SPAWN_LIMIT - alliedNPCs;
                }
                if (spawnAmount <= 0) {
                    return;
                }
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                HashSet<AbstractMob> spawnedMobs = new HashSet<>();
                for (int i = 0; i < spawnAmount; i++) {
                    AbstractMob mob = DIFFICULTY_SPAWNS.getOrDefault(difficulty, Mob.ZOMBIE_LANCER).createMob(player.getLocation());
                    updateMobEquipment(mob, player);
                    allSpawnedMobs.add(mob);
                    spawnedMobs.add(mob);
                    if (warlordsPlayer.getTeam() == null) {
                        return;
                    }
                    pveOption.spawnNewMob(mob, warlordsPlayer.getTeam());
                }
                new GameRunnable(game) {
                    @Override
                    public void run() {
                        spawnedMobs.forEach(mob -> {
                            if (pveOption.getMobs().contains(mob)) {
                                mob.getWarlordsNPC().die(mob.getWarlordsNPC(), WarlordsDeathEvent.DeathInfoBuilder.create().setForced(true));
                                pveOption.despawnMob(mob);
                            }
                        });
                        spawnedMobs.clear();
                    }
                }.runTaskLater(20 * 90);
            }
        }.runTaskTimer(100, 0);

    }

    private static void updateMobEquipment(AbstractMob mob, WarlordsPlayer player) {
        mob.setEquipment(new Utils.SimpleEntityEquipment(
                HeadUtils.getHead(player.getUuid()),
                CHESTPLATE,
                LEGGINGS,
                BOOTS,
                mob.getEquipment().getItemInMainHand(),
                mob.getEquipment().getItemInOffHand()
        ));
        mob.updateEquipment();
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Every ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(COOLDOWN + COOLDOWN_INCREASE_PER_UPGRADE * getTitleLevel(), "s"))
                        .append(Component.text(" summon a random assortment of mobs to fight for you for 90 seconds. Shift for 1 second to remove all summoned mobs."));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.REQUIEM;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 160;
    }

    @Override
    protected float getHealthBonusValue() {
        return 800;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 8;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 180;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 175;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Collections.singletonList(new Pair<>(
                formatTitleUpgrade(COOLDOWN + COOLDOWN_INCREASE_PER_UPGRADE * getTitleLevel(), "s"),
                formatTitleUpgrade(COOLDOWN + COOLDOWN_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "s")
        ));
    }

    @Override
    public int getCounter() {
        return (COOLDOWN + COOLDOWN_INCREASE_PER_UPGRADE * getTitleLevel()) - counter;
    }
}
