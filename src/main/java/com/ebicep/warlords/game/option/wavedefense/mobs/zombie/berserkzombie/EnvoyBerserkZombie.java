package com.ebicep.warlords.game.option.wavedefense.mobs.zombie.berserkzombie;

import com.ebicep.warlords.abilties.Berserk;
import com.ebicep.warlords.abilties.BloodLust;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EnvoyBerserkZombie extends AbstractBerserkZombie {

    public EnvoyBerserkZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Envoy Berserker Vanguard",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        ArmorManager.Helmets.LEGENDARY_WARRIOR_HELMET.itemRed,
                        new ItemStack(Material.DIAMOND_CHESTPLATE),
                        new ItemStack(Material.DIAMOND_LEGGINGS),
                        new ItemStack(Material.DIAMOND_BOOTS),
                        new ItemStack(Material.COOKED_FISH, 1, (short) 1)
                ),
                7000,
                0.494f,
                20,
                450,
                600,
                woundingStrikeBerserker -> {
                    woundingStrikeBerserker.setMinDamageHeal(woundingStrikeBerserker.getMinDamageHeal() * 1.5f);
                    woundingStrikeBerserker.setMaxDamageHeal(woundingStrikeBerserker.getMaxDamageHeal() * 1.5f);
                });
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<Berserk>(
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
                        ParticleEffect.VILLAGER_ANGRY.display(
                                0,
                                0,
                                0,
                                0.1f,
                                1,
                                warlordsNPC.getLocation().add(0, 1.75, 0),
                                500
                        );
                    }
                }
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 1.2f;
            }
        });
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<BloodLust>(
                name,
                "LUST",
                BloodLust.class,
                new BloodLust(),
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                }, false,
                (cooldown, ticksElapsed) -> {
                    if (ticksElapsed % 3 == 0) {
                        ParticleEffect.REDSTONE.display(
                                new ParticleEffect.OrdinaryColor(255, 0, 0),
                                warlordsNPC.getLocation().add(
                                        (Math.random() - 0.5) * 1,
                                        1.2,
                                        (Math.random() - 0.5) * 1
                                ),
                                500
                        );
                    }
                }
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                WarlordsEntity attacker = event.getAttacker();
                attacker.addHealingInstance(
                        attacker,
                        name,
                        currentDamageValue * .65f,
                        currentDamageValue * .65f,
                        -1,
                        100,
                        false,
                        false
                );
            }
        });
    }
}
