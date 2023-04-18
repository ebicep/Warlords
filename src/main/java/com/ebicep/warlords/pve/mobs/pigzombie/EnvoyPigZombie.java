package com.ebicep.warlords.pve.mobs.pigzombie;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
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

public class EnvoyPigZombie extends AbstractPigZombie implements EliteMob {

    public EnvoyPigZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Envoy Alleviator",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SAMURAI),
                        new ItemStack(Material.DIAMOND_HELMET),
                        new ItemStack(Material.DIAMOND_LEGGINGS),
                        new ItemStack(Material.DIAMOND_BOOTS),
                        new ItemStack(Material.BAKED_POTATO)
                ),
                6000,
                0.2f,
                10,
                300,
                400
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
        Utils.playGlobalSound(location, Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, 1, 0.5f);
        Utils.playGlobalSound(location, "paladin.holyradiance.activation", 1, 0.5f);
        WarlordsEntity we = Warlords.getPlayer(getWarlordsNPC().getEntity());
        if (we == null) {
            return;
        }
        EffectUtils.playSphereAnimation(location, 8, Particle.FLAME, 1);
        for (WarlordsEntity ally : PlayerFilter
                .entitiesAround(we, 10, 10, 10)
                .aliveTeammatesOfExcludingSelf(we)
        ) {
            ally.addHealingInstance(
                    we,
                    "Healing",
                    300,
                    300,
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
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ZOMBIFIED_PIGLIN_DEATH, 2, 0.4f);
    }
}
