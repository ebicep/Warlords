package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class VoidRaider extends AbstractZombie implements EliteMob {

    private int knockbackResistance = 20;

    public VoidRaider(Location spawnLocation) {
        super(
                spawnLocation,
                "Void Raider",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.FANCY_CUBE_2),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 56, 71, 74),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 56, 71, 74),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 56, 71, 74),
                        Weapons.NETHERSTEEL_KATANA.getItem()
                ),
                9500,
                0.42f,
                0,
                650,
                850
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Debuff Immunity",
                null,
                null,
                null,
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                false,
                (cooldown, ticksElapsed) -> {
                }
        ) {
            final float calculatedKBRes = 1 - knockbackResistance / 100f;

            @Override
            public void multiplyKB(Vector currentVector) {
                currentVector.multiply(calculatedKBRes);
            }
        });
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

}
