package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.abilties.SoulShackle;
import com.ebicep.warlords.events.player.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.bosses.bossminions.TormentedSoul;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Ghoulcaller extends AbstractZombie implements BossMob {

    private static final HashMap<Integer, Pair<Float, Float>> PLAYER_COUNT_DAMAGE_VALUES = new HashMap<Integer, Pair<Float, Float>>() {{
        put(1, new Pair<>(736f, 778f));
        put(2, new Pair<>(1121f, 1191f));
        put(3, new Pair<>(1502f, 1599f));
        put(4, new Pair<>(1744f, 1859f));
    }};

    public Ghoulcaller(Location spawnLocation) {
        super(spawnLocation,
                "Ghoulcaller",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.DEMON),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
                        new ItemStack(Material.CHAINMAIL_LEGGINGS),
                        new ItemStack(Material.CHAINMAIL_BOOTS),
                        Weapons.DRAKEFANG.getItem()
                ),
                20000,
                0.475f,
                0,
                477,
                616
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        for (WarlordsEntity we : PlayerFilter.playingGame(getWarlordsNPC().getGame())) {
            if (we.getEntity() instanceof Player) {
                PacketUtils.sendTitle(
                        (Player) we.getEntity(),
                        ChatColor.RED + getWarlordsNPC().getName(),
                        ChatColor.GOLD + "Chained Agony?",
                        20, 30, 20
                );
            }
        }
        spawnTormentedSouls(option, 10);
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 20 == 0) {
            getWarlordsNPC().getSecondStats().advanceSecond();
        }
        //every 5 seconds. [this mob’s fury] deals (Damage values depending on total players in game) * 0.95 ^ x damage, where x is the amount of attacks that hit this mob in the past 5s.
        if (ticksElapsed % 100 == 0) {
            List<WarlordsDamageHealingFinalEvent> eventsInLast5Seconds = getWarlordsNPC().getSecondStats().getEventsAsSelfFromLastSecondStream(5)
                    .filter(WarlordsDamageHealingFinalEvent::isDamageInstance)
                    .collect(Collectors.toList());
            int attacksInLast5Seconds = (int) (eventsInLast5Seconds.size() - eventsInLast5Seconds.stream()
                    .filter(event -> event.getAbility().equals("Windfury Weapon"))
                    .count() / 2
            );
            if (attacksInLast5Seconds > 20) {
                attacksInLast5Seconds = 20;
            }
            int playerCount = (int) option.getGame().warlordsPlayers().count();

            float minDamage = (float) (PLAYER_COUNT_DAMAGE_VALUES.getOrDefault(playerCount, PLAYER_COUNT_DAMAGE_VALUES.get(1)).getA() * Math.pow(0.95, attacksInLast5Seconds));
            float maxDamage = (float) (PLAYER_COUNT_DAMAGE_VALUES.getOrDefault(playerCount, PLAYER_COUNT_DAMAGE_VALUES.get(1)).getB() * Math.pow(0.95, attacksInLast5Seconds));
            PlayerFilter.entitiesAround(getWarlordsNPC(), 10, 10, 10)
                    .aliveEnemiesOf(getWarlordsNPC())
                    .forEach(enemyPlayer -> {
                        enemyPlayer.addDamageInstance(
                                getWarlordsNPC(),
                                "Ghoulcaller’s Fury",
                                minDamage,
                                maxDamage,
                                -1,
                                100,
                                false
                        );
                    });

        }
        //Spawn 5 * (The number of players in the game) Tormented Souls every 20 seconds
        if (ticksElapsed % 400 == 0) {
            spawnTormentedSouls(option, (int) (5 * option.getGame().warlordsPlayers().count()));
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        //silence player for 10s per melee
        if (event.getAbility().isEmpty()) {
            SoulShackle.shacklePlayer(attacker, receiver, 200);
        }
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    private void spawnTormentedSouls(WaveDefenseOption option, int amount) {
        for (int i = 0; i < amount; i++) {
            option.spawnNewMob(new TormentedSoul(getWarlordsNPC().getLocation()));
        }
    }
}
