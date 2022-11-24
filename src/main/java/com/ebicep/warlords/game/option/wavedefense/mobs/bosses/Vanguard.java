package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.abilties.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.ExiledZombie;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class Vanguard extends AbstractZombie implements BossMob {

    private boolean phaseOneTriggered = false;
    private boolean phaseTwoTriggered = false;
    private boolean phaseThreeTriggered = false;
    private boolean phaseFourTriggered = false;

    private AtomicInteger damageToDeal = new AtomicInteger(0);

    public Vanguard(Location spawnLocation) {
        super(spawnLocation,
                "Illumina",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.DEEP_DARK_WORM),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 200, 200, 200),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 200, 200, 200),
                        Weapons.SILVER_PHANTASM_SWORD_3.getItem()
                ),
                30000,
                0.15f,
                10,
                3200,
                3600
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        ChatColor.LIGHT_PURPLE + "Illumina",
                        ChatColor.DARK_GRAY + "General of the Illusion Legion",
                        20, 30, 20
                );
            }
        }

        for (int i = 0; i < (2 * option.getGame().warlordsPlayers().count()); i++) {
            option.spawnNewMob(new ExiledZombie(spawnLocation));
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        long playerCount = option.getGame().warlordsPlayers().count();

        if (!phaseOneTriggered) {

        }

        if (warlordsNPC.getHealth() < (warlordsNPC.getMaxHealth() * .75f) && !phaseThreeTriggered) {
            phaseThreeTriggered = true;
            damageToDeal.set((int) (5000 * playerCount));

            for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
                if (we.getEntity() instanceof Player) {
                    PacketUtils.sendTitle(
                            (Player) we.getEntity(),
                            "",
                            ChatColor.RED + "Keep attacking Vanguard to stop the draining!",
                            10, 40, 10
                    );
                }
            }

            warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Damage Check",
                    null,
                    DamageCheck.class,
                    DamageCheck.DAMAGE_CHECK,
                    warlordsNPC,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    true
            ) {
                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    if (phaseThreeTriggered) {
                        damageToDeal.set((int) (damageToDeal.get() - currentDamageValue));
                    }

                    return currentDamageValue;
                }
            });

            AtomicInteger countdown = new AtomicInteger(10);
            new GameRunnable(warlordsNPC.getGame()) {
                int counter = 0;
                @Override
                public void run() {
                    if (warlordsNPC.isDead() || damageToDeal.get() <= 0) {
                        FireWorkEffectPlayer.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                                .withColor(Color.WHITE)
                                .with(FireworkEffect.Type.BALL_LARGE)
                                .build());
                        this.cancel();
                        return;
                    }

                    if (counter++ % 20 == 0) {
                        countdown.getAndDecrement();
                        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.NOTE_STICKS, 500, 0.8f);
                        for (WarlordsEntity we : PlayerFilter
                                .entitiesAround(warlordsNPC, 100, 100, 100)
                                .aliveEnemiesOf(warlordsNPC)
                        ) {
                            EffectUtils.playParticleLinkAnimation(we.getLocation(), warlordsNPC.getLocation(), 255, 255, 255, 2);
                            we.addDamageInstance(
                                    warlordsNPC,
                                    "Vampiric Leash",
                                    300,
                                    300,
                                    -1,
                                    100,
                                    true
                            );
                        }
                    }

                    if (countdown.get() <= 0 && damageToDeal.get() > 0) {
                        FireWorkEffectPlayer.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                                .withColor(Color.WHITE)
                                .with(FireworkEffect.Type.BALL_LARGE)
                                .build());
                        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 10);

                        for (WarlordsEntity we : PlayerFilter
                                .entitiesAround(warlordsNPC, 100, 100, 100)
                                .aliveEnemiesOf(warlordsNPC)
                        ) {
                            we.addDamageInstance(
                                    warlordsNPC,
                                    "Vampiric Leash",
                                    300,
                                    300,
                                    -1,
                                    100,
                                    true
                            );
                        }
                        this.cancel();
                    }

                    for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
                        if (we.getEntity() instanceof Player) {
                            PacketUtils.sendTitle(
                                    (Player) we.getEntity(),
                                    ChatColor.YELLOW.toString() + countdown.get(),
                                    ChatColor.RED.toString() + damageToDeal.get(),
                                    0, 4, 0
                            );
                        }
                    }
                }
            }.runTaskTimer(40, 0);
        }

        if (warlordsNPC.getHealth() < (warlordsNPC.getMaxHealth() * .5f) && !phaseFourTriggered) {
            phaseFourTriggered = true;


        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        FireWorkEffectPlayer.playFirework(receiver.getLocation(), FireworkEffect.builder()
                .withColor(Color.BLACK)
                .with(FireworkEffect.Type.BALL)
                .build());
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.BLACK)
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build());
        EffectUtils.strikeLightning(deathLocation, false, 2);
    }
}
