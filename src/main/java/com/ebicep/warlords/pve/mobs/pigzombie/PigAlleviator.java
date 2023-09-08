package com.ebicep.warlords.pve.mobs.pigzombie;

import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

public class PigAlleviator extends AbstractPigZombie implements IntermediateMob {

    public PigAlleviator(Location spawnLocation) {
        super(
                spawnLocation,
                "Pig Alleviator",
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
                400,
                new PigZombieHealing(300, 10)
        );
    }

    public PigAlleviator(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SAMURAI),
                        new ItemStack(Material.DIAMOND_HELMET),
                        new ItemStack(Material.DIAMOND_LEGGINGS),
                        new ItemStack(Material.DIAMOND_BOOTS),
                        new ItemStack(Material.BAKED_POTATO)
                ),
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new PigZombieHealing(300, 10)
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        getWarlordsNPC().getEntity().getWorld().spigot().strikeLightningEffect(getWarlordsNPC().getLocation(), false);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

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
