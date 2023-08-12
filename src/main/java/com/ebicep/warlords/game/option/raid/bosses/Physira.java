package com.ebicep.warlords.game.option.raid.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.raid.BossAbilityPhase;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.witherskeleton.AbstractWitherSkeleton;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.cos;

public class Physira extends AbstractWitherSkeleton implements BossMob {

    private Listener listener;
    List<WarlordsNPC> pylons = new ArrayList<>();

    private BossAbilityPhase phaseOne;

    public Physira(Location spawnLocation) {
        super(spawnLocation,
                "Physira",
                MobTier.RAID_BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.GRADIENT_SOUL),
                        new ItemStack(Material.NETHERITE_CHESTPLATE),
                        new ItemStack(Material.NETHERITE_LEGGINGS),
                        new ItemStack(Material.NETHERITE_BOOTS),
                        Weapons.VIRIDIAN_BLADE.getItem()
                ),
                10000,
                0f,
                0,
                3000,
                4000
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        phaseOne = new BossAbilityPhase(warlordsNPC, 95, () -> {
            ChatUtils.sendTitleToGamePlayers(
                    warlordsNPC.getGame(),
                    Component.empty(),
                    Component.text("Destroy Physira's pylons before the time runs out!", NamedTextColor.GRAY),
                    20,
                    60,
                    20
            );

            Location loc = warlordsNPC.getLocation();
            Location crystalLoc = loc.clone();
            for (int j = 0; j < 6; j++) {
                double angle = j / 6D * Math.PI * 2;
                crystalLoc.setX(loc.getX() + Math.sin(angle) * 20);
                crystalLoc.setZ(loc.getZ() + cos(angle) * 20);
                PhysiraCrystal crystal = new PhysiraCrystal(crystalLoc, warlordsNPC, SpecType.VALUES[j % 3]);
                pylons.add(crystal.getWarlordsNPC());
                Bukkit.broadcast(Component.text("pylons: " + pylons.get(j)));
                pveOption.spawnNewMob(crystal, Team.RED);
            }

            AtomicInteger countdown = new AtomicInteger(30);
            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;
                @Override
                public void run() {
                    if (counter % 20 == 0) {
                        countdown.getAndDecrement();
                        Utils.playGlobalSound(warlordsNPC.getLocation(), "warrior.laststand.activation", 500, 0.6f);
                    }

                    if (pylons.isEmpty() && countdown.get() > 0) {
                        EffectUtils.playFirework(
                                warlordsNPC.getLocation(),
                                FireworkEffect.builder()
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .withColor(Color.WHITE)
                                        .withTrail()
                                        .build()
                        );
                        warlordsNPC.getGame().registerEvents(listener);
                        this.cancel();
                    }

                    if (countdown.get() <= 0) {
                        EffectUtils.strikeLightningTicks(warlordsNPC.getLocation(), true, 60);
                        EffectUtils.playFirework(
                                warlordsNPC.getLocation(),
                                FireworkEffect.builder()
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .withColor(Color.RED)
                                        .withTrail()
                                        .build()
                        );
                        warlordsNPC.getGame().registerEvents(listener);
                        this.cancel();
                    }

                    ChatUtils.sendTitleToGamePlayers(
                            warlordsNPC.getGame(),
                            Component.text(countdown.get(), NamedTextColor.GOLD),
                            Component.empty(),
                            0, 4, 0
                    );

                    counter++;
                }
            }.runTaskTimer(60, 0);

            listener = new Listener() {
                @EventHandler
                private void onAllyDeath(WarlordsDeathEvent event) {
                    WarlordsEntity we = event.getWarlordsEntity();
                    pylons.remove(we);
                    Bukkit.broadcast(Component.text("pylon removed"));
                }
            };
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        phaseOne.initialize(warlordsNPC.getHealth());
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }
}