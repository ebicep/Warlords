package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class RangeOnlySkeleton extends AbstractSkeleton implements EliteMob {

    public RangeOnlySkeleton(Location spawnLocation) {
        super(
                spawnLocation,
                "Celestial Bow Wielder",
                MobTier.ILLUSION,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.BOW_HEAD),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 20, 20, 20),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 20, 20, 20),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 20, 20, 20),
                        Weapons.FROSTBITE.getItem()
                ),
                8000,
                0.25f,
                10,
                600,
                900,
                new Fireball(5.5f)
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
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
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (!Utils.isProjectile(event.getAbility())) {
                    return currentDamageValue * 0.1f;
                }

                return currentDamageValue;
            }
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 160 == 0) {
            warlordsNPC.getSpec().getWeapon().onActivate(warlordsNPC, null);
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}
