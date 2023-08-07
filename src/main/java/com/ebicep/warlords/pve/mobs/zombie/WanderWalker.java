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

public class WanderWalker extends AbstractZombie implements EliteMob {

    private boolean recovered = false;
    private float healthRecover = .3f;
    private int speedIncrease = 5;
    private int knockbackResistance = 40;


    public WanderWalker(Location spawnLocation) {
        super(
                spawnLocation,
                "Void Raider",
                MobTier.ILLUSION,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SCULK_MONSTER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 204, 204),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 204, 204),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 204, 204),
                        Weapons.LUNAR_JUSTICE.getItem()
                ),
                8500,
                0.31f,
                20,
                600,
                750
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (!recovered && self.getHealth() <= self.getMaxHealth() * .1f) {
            recovered = true;
            float healAmount = self.getMaxHealth() * healthRecover;
            self.addHealingInstance(self, "Void Recovery", healAmount, healAmount, 0, 100);
            removeTarget();
            self.getSpeed().addBaseModifier(speedIncrease);
            warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Void Recovery",
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
    }

}
