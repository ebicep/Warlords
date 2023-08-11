package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.BoltaroExiled;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.BoltaroShadow;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
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

public class Boltaro extends AbstractZombie implements BossMob {

    private boolean split = false;
    private int mobsKilledBeforeSplit = 0;
    private Listener listener;

    public Boltaro(Location spawnLocation) {
        super(spawnLocation,
                "Boltaro",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.DEMON),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
                        new ItemStack(Material.CHAINMAIL_LEGGINGS),
                        new ItemStack(Material.CHAINMAIL_BOOTS),
                        Weapons.DRAKEFANG.getItem()
                ),
                12500,
                0.475f,
                20,
                350,
                500
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        option.getGame().registerEvents(listener = new Listener() {
            @EventHandler
            public void onMobDeath(WarlordsDeathEvent event) {
                if (!split && event.getWarlordsEntity() instanceof WarlordsNPC) {
                    mobsKilledBeforeSplit++;
                }
            }
        });

        for (int i = 0; i < option.getGame().warlordsPlayers().count(); i++) {
            option.spawnNewMob(new BoltaroExiled(spawnLocation));
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 100 == 0) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 1.5f);
        }

        if (warlordsNPC.getHealth() < 6000) {
            split = true;
            split(option);
            warlordsNPC.die(warlordsNPC);
        }
    }

    private void split(PveOption option) {
        if (mobsKilledBeforeSplit == 0) {
            option.getGame()
                  .warlordsPlayers()
                  .forEach(warlordsPlayer -> ChallengeAchievements.checkForAchievement(warlordsPlayer, ChallengeAchievements.SIRE));
        }
        HandlerList.unregisterAll(listener);

        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, Particle.SMOKE_NORMAL, 3, 20);
        for (int i = 0; i < option.getGame().warlordsPlayers().count(); i++) {
            option.spawnNewMob(new BoltaroShadow(warlordsNPC.getLocation()));
        }

        for (int i = 0; i < 6; i++) {
            option.spawnNewMob(new BoltaroExiled(warlordsNPC.getLocation()));
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        if (!(event.getAbility().equals("Multi Hit") || event.getAbility().equals("Intervene"))) {
            new GameRunnable(attacker.getGame()) {
                int counter = 0;

                @Override
                public void run() {
                    counter++;
                    Utils.playGlobalSound(receiver.getLocation(), "warrior.mortalstrike.impact", 2, 1.5f);
                    Utils.addKnockback(name, attacker.getLocation(), receiver, -0.7, 0.2);
                    receiver.addDamageInstance(attacker, "Multi Hit", 120, 180, 0, 100);

                    if (counter == 3 || receiver.isDead()) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(10, 3);
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(self.getLocation(), "warrior.intervene.block", 2, 0.3f);
        EffectUtils.playRandomHitEffect(self.getLocation(), 255, 0, 0, 4);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), 6, Particle.SMOKE_NORMAL, 3, 20);
        EffectUtils.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.WHITE)
                                                                       .with(FireworkEffect.Type.STAR)
                                                                       .withTrail()
                                                                       .build());
        if (!split) {
            split(option);
        }
    }

    @Override
    public NamedTextColor getColor() {
        return NamedTextColor.RED;
    }

    @Override
    public Component getDescription() {
        return Component.text("Right Hand of the Illusion Vanguard", NamedTextColor.GOLD);
    }
}
