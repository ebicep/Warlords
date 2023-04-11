package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilties.UndyingArmy;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.BasicZombie;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.RandomCollection;
import com.ebicep.warlords.util.java.Utils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.annotation.Transient;

import java.util.*;

public class LegendaryRequiem extends AbstractLegendaryWeapon implements PassiveCounter {

    public static final RandomCollection<Integer> SPAWN_AMOUNT = new RandomCollection<Integer>()
            .add(2, 2)
            .add(2, 3)
            .add(1, 4)
            .add(1, 5);
    public static final ItemStack CHESTPLATE = com.ebicep.warlords.util.warlords.Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 200, 0);
    public static final ItemStack LEGGINGS = com.ebicep.warlords.util.warlords.Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 200, 0);
    public static final ItemStack BOOTS = com.ebicep.warlords.util.warlords.Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 200, 0);
    public static final int SPAWN_LIMIT = 20;
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

        Game game = player.getGame();
        game.registerEvents(new Listener() {

            @EventHandler
            public void onAddCooldown(WarlordsAddCooldownEvent event) {
                AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                if (!(cooldown.getCooldownObject() instanceof UndyingArmy)) {
                    return;
                }
                if (!Objects.equals(event.getWarlordsEntity(), player)) {
                    return;
                }
                PlayerFilterGeneric.playingGameWarlordsNPCs(game)
                                   .aliveEnemiesOf(player)
                                   .filter(warlordsNPC -> !(warlordsNPC.getMob() instanceof BossMob))
                                   .filter(warlordsNPC -> warlordsNPC.getMob().getEe() != null)
                                   .limit(Utils.generateRandomValueBetweenInclusive(1, 3))
                                   .forEach(convertedEnemy -> {
                                       EffectUtils.playCylinderAnimation(convertedEnemy.getLocation(), 1.05, ParticleEffect.VILLAGER_HAPPY, 1);
                                       convertedEnemy.setTeam(Team.BLUE);
                                       AbstractMob<?> mob = convertedEnemy.getMob();
                                       updateMobEquipment(mob, player);
                                       //removing teammate mobs that are agroed on converted target
                                       PlayerFilterGeneric.playingGameWarlordsNPCs(game)
                                                          .aliveTeammatesOf(player)
                                                          .filter(teammate -> {
                                                              EntityLiving target = teammate.getMob().getTarget();
                                                              return target != null && Objects.equals(target.getBukkitEntity(), convertedEnemy.getEntity());
                                                          })
                                                          .forEach(teammate -> teammate.getMob().removeTarget());
                                       mob.removeTarget();
                                   });
            }

        });
        int cooldown = (COOLDOWN + COOLDOWN_INCREASE_PER_UPGRADE * getTitleLevel());

        new GameRunnable(game) {

            int ticksElapsed = -1;

            @Override
            public void run() {
                ticksElapsed++;
                counter = ticksElapsed % cooldown;
                if (player.isDead()) {
                    return;
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
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 2);
                HashSet<AbstractMob<?>> spawnedMobs = new HashSet<>();
                for (int i = 0; i < spawnAmount; i++) {
                    BasicZombie mob = new BasicZombie(player.getLocation());
                    updateMobEquipment(mob, player);
                    spawnedMobs.add(mob);
                    pveOption.spawnNewMob(mob, Team.BLUE);
                }
                new GameRunnable(game) {

                    @Override
                    public void run() {
                        spawnedMobs.forEach(mob -> {
                            if (pveOption.getMobs().contains(mob)) {
                                mob.getWarlordsNPC().die(mob.getWarlordsNPC());
                            }
                        });
                        spawnedMobs.clear();
                    }
                }.runTaskLater(20 * 60);
            }
        }.runTaskTimer(100, 20);

    }

    private static void updateMobEquipment(AbstractMob<?> mob, WarlordsPlayer player) {
        EntityEquipment equipment = mob.getEe();
        equipment.setHelmet(HeadUtils.getHead(player.getUuid()));
        equipment.setChestplate(CHESTPLATE);
        equipment.setLeggings(LEGGINGS);
        equipment.setBoots(BOOTS);
        mob.updateEquipment();
    }

    @Override
    public String getPassiveEffect() {
        return "Every " + formatTitleUpgrade(COOLDOWN + COOLDOWN_INCREASE_PER_UPGRADE * getTitleLevel(), "s") +
                " summon a random assortment of mobs to fight for you. Using Undying Army has additional effect of converting enemy mobs to allies.";
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
    public List<Pair<String, String>> getPassiveEffectUpgrade() {
        return Collections.singletonList(new Pair<>(
                formatTitleUpgrade(COOLDOWN + COOLDOWN_INCREASE_PER_UPGRADE * getTitleLevel(), "s"),
                formatTitleUpgrade(COOLDOWN + COOLDOWN_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "s")
        ));
    }

    @Override
    public int getCounter() {
        return 60 - counter;
    }
}
