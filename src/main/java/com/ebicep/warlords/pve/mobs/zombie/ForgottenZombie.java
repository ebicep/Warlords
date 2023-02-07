package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.abilties.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.util.Vector;

public class ForgottenZombie extends AbstractZombie implements EliteMob {

    public ForgottenZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Forgotten Nightmare",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SHADOW_DEMON),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 70, 50, 20),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 70, 50, 20),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 70, 50, 20),
                        Weapons.FABLED_HEROICS_SWORD.getItem()
                ),
                2000,
                0.6f,
                0,
                1200,
                1800
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 2);

        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Damage Check",
                null,
                DamageCheck.class,
                DamageCheck.DAMAGE_CHECK,
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                true
        ) {
            @Override
            public void multiplyKB(Vector currentVector) {
                // immune to KB
                currentVector.multiply(0.05);
            }
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        receiver.getCooldownManager().subtractTicksOnRegularCooldowns(CooldownTypes.BUFF, 60);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.SKELETON_DEATH, 2, 0.4f);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (Utils.isProjectile(event.getAbility())) {
            attacker.addDamageInstance(self, "Projectile Thorns", 300, 300, -1, 100, false);
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.ZOMBIE_DEATH, 2, 0.4f);
    }
}
