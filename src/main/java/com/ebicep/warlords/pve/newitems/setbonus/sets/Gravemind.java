package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Gravemind extends BaseSet {

    private int summonChance;
    private float duration;

    public static final ItemStack CHESTPLATE = Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 200, 0);
    public static final ItemStack LEGGINGS = Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 200, 0);
    public static final ItemStack BOOTS = Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 200, 0);

    @Override
    public void init() {
        super.init();
        this.summonChance = getValue("summonChance", int.class);
        this.duration = getValue("duration", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "gravemind";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(summonChance, duration);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            Listener listener = new Listener() {

                @EventHandler
                private void onEnemyDeath(WarlordsDeathEvent event) {
                    if (event.getWarlordsEntity().equals(warlordsPlayer)) {
                        return;
                    }
                    if (event.getWarlordsEntity().getTeam().equals(warlordsPlayer.getTeam())) {
                        return;
                    }
                    if (ThreadLocalRandom.current().nextDouble() > summonChance / 100.0) {
                        return;
                    }
                    spawnMob(warlordsPlayer, event.getWarlordsEntity().getLocation());
                }
            };
            warlordsPlayer.getGame().registerEvents(listener);
        }

    }

    private void spawnMob(WarlordsPlayer warlordsPlayer, Location summonLocation) {
        Optional<PveOption> pveOption = warlordsPlayer.getGame().getOption(PveOption.class)
                .stream()
                .findFirst();
        warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.5f, 0.5f);
        warlordsPlayer.sendMessage(Component.text("Your gravemind has summoned a mob for 30 seconds!", NamedTextColor.GREEN));

        HashSet<AbstractMob> spawnedMobs = new HashSet<>();
        AbstractMob mob = Mob.ZOMBIE_SWORDSMAN.createMob(summonLocation);
        updateMobEquipment(mob, warlordsPlayer);
        spawnedMobs.add(mob);
        if (warlordsPlayer.getTeam() == null) {
            return;
        }
        pveOption.get().spawnNewMob(mob, warlordsPlayer.getTeam());

        new GameRunnable(warlordsPlayer.getGame()) {
            @Override
            public void run() {
                spawnedMobs.forEach(mob -> {
                    if (pveOption.get().getMobs().contains(mob)) {
                        mob.getWarlordsNPC().die(mob.getWarlordsNPC(), WarlordsDeathEvent.DeathInfoBuilder.create().setForced(true));
                        pveOption.get().despawnMob(mob);
                    }
                });
                spawnedMobs.clear();
            }
        }.runTaskLater((long) (20 * duration));
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
}