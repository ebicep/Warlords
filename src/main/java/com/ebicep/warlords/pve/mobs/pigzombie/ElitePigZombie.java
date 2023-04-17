package com.ebicep.warlords.pve.mobs.pigzombie;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

public class ElitePigZombie extends AbstractPigZombie implements EliteMob {

    public ElitePigZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Shaman",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SAMURAI),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        new ItemStack(Material.DIAMOND_LEGGINGS),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                        new ItemStack(Material.COOKIE)
                ),
                4000,
                0.25f,
                10,
                200,
                300
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        getWarlordsNPC().getEntity().getWorld().spigot().strikeLightningEffect(getWarlordsNPC().getLocation(), false);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 60 != 0) {
            return;
        }
        Location location = getWarlordsNPC().getLocation();
        Utils.playGlobalSound(location, Sound.ZOMBIE_PIG_ANGRY, 1, 0.5f);
        Utils.playGlobalSound(location, "paladin.holyradiance.activation", 0.8f, 0.6f);
        WarlordsEntity we = Warlords.getPlayer(getWarlordsNPC().getEntity());
        if (we == null) return;
        EffectUtils.playCylinderAnimation(location, 6, ParticleEffect.FIREWORKS_SPARK, 1);
        for (WarlordsEntity ally : PlayerFilter
                .entitiesAround(we, 6, 6, 6)
                .aliveTeammatesOfExcludingSelf(we)
        ) {
            ally.addHealingInstance(
                    we,
                    "Healing",
                    150,
                    150,
                    0,
                    100,
                    false,
                    false
            );
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.PURPLE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.ZOMBIE_PIG_DEATH, 2, 0.4f);
    }
}
