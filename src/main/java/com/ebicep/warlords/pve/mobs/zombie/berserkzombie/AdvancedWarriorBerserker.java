package com.ebicep.warlords.pve.mobs.zombie.berserkzombie;

import com.ebicep.warlords.abilities.Berserk;
import com.ebicep.warlords.abilities.BloodLust;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class AdvancedWarriorBerserker extends AbstractBerserkZombie implements AdvancedMob {

    public AdvancedWarriorBerserker(Location spawnLocation) {
        super(
                spawnLocation,
                "Warrior Berserker",
                new Utils.SimpleEntityEquipment(
                        ArmorManager.Helmets.LEGENDARY_WARRIOR_HELMET.itemRed,
                        new ItemStack(Material.DIAMOND_CHESTPLATE),
                        new ItemStack(Material.DIAMOND_LEGGINGS),
                        new ItemStack(Material.DIAMOND_BOOTS),
                        new ItemStack(Material.COOKED_SALMON)
                ),
                7000,
                0.43f,
                20,
                450,
                600,
                new BerserkerZombieWoundingStrike(497, 632)
        );
        woundingStrike.getMinDamageHeal().addMultiplicativeModifierAdd(name, .5f);
        woundingStrike.getMaxDamageHeal().addMultiplicativeModifierAdd(name, .5f);
    }

    public AdvancedWarriorBerserker(
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
                new Utils.SimpleEntityEquipment(
                        ArmorManager.Helmets.LEGENDARY_WARRIOR_HELMET.itemRed,
                        new ItemStack(Material.DIAMOND_CHESTPLATE),
                        new ItemStack(Material.DIAMOND_LEGGINGS),
                        new ItemStack(Material.DIAMOND_BOOTS),
                        new ItemStack(Material.COOKED_SALMON)
                ),
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new BerserkerZombieWoundingStrike(497, 632)
        );
        woundingStrike.getMinDamageHeal().addMultiplicativeModifierAdd(name, .5f);
        woundingStrike.getMaxDamageHeal().addMultiplicativeModifierAdd(name, .5f);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ADVANCED_WARRIOR_BERSERKER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Berserk",
                "BERS",
                Berserk.class,
                new Berserk(),
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                false,
                (cooldown, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        warlordsNPC.getWorld().spawnParticle(
                                Particle.VILLAGER_ANGRY,
                                warlordsNPC.getLocation().add(0, 1.75, 0),
                                1,
                                0,
                                0,
                                0,
                                0.1,
                                null,
                                true
                        );
                    }
                }
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 1.2f;
            }
        });
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                name,
                "LUST",
                BloodLust.class,
                new BloodLust(),
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                false,
                (cooldown, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        warlordsNPC.getWorld().spawnParticle(
                                Particle.REDSTONE,
                                warlordsNPC.getLocation().add(
                                        (Math.random() - 0.5) * 1,
                                        1.2,
                                        (Math.random() - 0.5) * 1
                                ),
                                1,
                                0,
                                0,
                                0,
                                0,
                                new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1),
                                true
                        );
                    }
                }
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                WarlordsEntity attacker = event.getSource();
                attacker.addInstance(InstanceBuilder
                        .healing()
                        .cause(name)
                        .source(attacker)
                        .value(currentDamageValue * .65f)
                );
            }
        });
    }
}
