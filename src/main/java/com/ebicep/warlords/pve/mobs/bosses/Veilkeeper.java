package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossabilities.*;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.List;

public class Veilkeeper extends AbstractMob implements BossMob {

    private Location mapCenter;
    private OrbitingItemManager oribitingItemManager;
    private OrbitingItemManager oribitingItemManagerUp;
    private OrbitingItemManager oribitingItemManagerFloating;
    private PairedSequenceAbility pairedSequenceAbility;

    public Veilkeeper(Location spawnLocation) {
        super(spawnLocation,
                "Veilkeeper",
                320000,
                0,
                30,
                1200,
                2000
        );
    }

    public Veilkeeper(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
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
                maxMeleeDamage
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

        warlordsNPC.setStunTicks(99999);
        mapCenter = new Location(warlordsNPC.getWorld(), 112.5, 11, 62.5);

        oribitingItemManager = new OrbitingItemManager(() -> warlordsNPC.getLocation(), 4, 1, 6, 2, warlordsNPC, Material.CRIMSON_ROOTS);
        oribitingItemManagerUp = new OrbitingItemManager(() -> warlordsNPC.getLocation(), 4, 3, 6, 2, warlordsNPC, Material.CRIMSON_ROOTS);

        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                ChatUtils.sendTitleToGamePlayers(
                        warlordsNPC.getGame(),
                        Component.empty(),
                        Component.text("You have a lot of nerve coming here... Come back when you are worthy!", NamedTextColor.RED),
                        20, 40, 20
                );
                Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 100, 0.5f);
            }
        }.runTaskLater(100);

        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                ChatUtils.sendTitleToGamePlayers(
                        warlordsNPC.getGame(),
                        Component.empty(),
                        Component.text("NOW, GET OUT OF MY SIGHT!", NamedTextColor.RED),
                        20, 40, 20
                );
                Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 100, 0.5f);
            }
        }.runTaskLater(180);

        new GameRunnable(warlordsNPC.getGame()) {
            @Override
            public void run() {
                PlayerFilter.playingGame(warlordsNPC.getGame())
                        .aliveEnemiesOf(warlordsNPC).forEach(enemy -> {
                            EffectUtils.strikeLightning(enemy.getLocation(), false);
                            enemy.addInstance(InstanceBuilder.damage()
                                    .cause("Unknown")
                                    .source(warlordsNPC)
                                    .value(500)
                                    .critChance(100)
                                    .critMultiplier(200)
                                    .flags(InstanceFlags.TRUE_DAMAGE)
                            );
                        });
            }
        }.runTaskTimer(210, 0);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        float health = warlordsNPC.getCurrentHealth();
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        oribitingItemManager.stop();
        oribitingItemManagerUp.stop();
    }

    @Override
    public Component getDescription() {
        return Component.text("Commandment of the Nine", TextColor.color(80, 80, 80));
    }

    @Override
    public TextColor getColor() {
        return TextColor.color(90, 0, 0);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.VEILKEEPER;
    }
}
