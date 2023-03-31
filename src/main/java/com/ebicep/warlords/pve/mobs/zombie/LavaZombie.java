package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

public class LavaZombie extends AbstractZombie implements EliteMob {

    public LavaZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Exiled Fire Splitter",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.LAVA_MONSTER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 252, 170, 53),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 252, 170, 53),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 252, 170, 53),
                        Weapons.SILVER_PHANTASM_SWORD_2.getItem()
                ),
                8000,
                0.38f,
                10,
                600,
                900
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), true);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 40 == 0) {
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.FIRE, 2, 0.5f);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.addKnockback(name, attacker.getLocation(), receiver, 1, 0.15);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.ORANGE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.ZOMBIE_DEATH, 2, 0.4f);
    }
}
