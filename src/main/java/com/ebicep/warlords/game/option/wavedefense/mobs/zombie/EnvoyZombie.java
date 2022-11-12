package com.ebicep.warlords.game.option.wavedefense.mobs.zombie;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

public class EnvoyZombie extends AbstractZombie implements EliteMob {

    public EnvoyZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Envoy Vanguard",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        new ItemStack(Material.WOOD_STEP, 1, (short) 4),
                        new ItemStack(Material.DIAMOND_CHESTPLATE),
                        new ItemStack(Material.DIAMOND_LEGGINGS),
                        new ItemStack(Material.DIAMOND_BOOTS),
                        new ItemStack(Material.COOKED_FISH, 1, (short) 1)
                ),
                7000,
                0.4f,
                20,
                450,
                600
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 2);
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(receiver.getLocation(), Sound.AMBIENCE_THUNDER, 2, 0.7f);
        receiver.addSpeedModifier(attacker, "Envoy Slowness", -20, 2 * 20);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.ZOMBIE_DEATH, 2, 0.4f);
    }
}
