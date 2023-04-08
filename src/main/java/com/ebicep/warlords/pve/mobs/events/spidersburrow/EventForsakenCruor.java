package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilties.WoundingStrikeBerserker;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.Spider;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

public class EventForsakenCruor extends AbstractZombie implements BossMob, Spider {


    public EventForsakenCruor(Location spawnLocation) {
        super(
                spawnLocation,
                "Forsaken Respite",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.BLOOD_SPIDER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 20, 20),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 255, 20, 20),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 20, 20),
                        Weapons.ARMBLADE.getItem()
                ),
                2700,
                0.45f,
                0,
                300,
                450
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        int currentWave = option.getWaveCounter();
        if (currentWave % 5 == 0 && currentWave > 5) {
            float additionalHealthMultiplier = 1 + .15f * (currentWave / 5f - 1);
            warlordsNPC.setMaxBaseHealth(warlordsNPC.getMaxBaseHealth() * additionalHealthMultiplier);
            warlordsNPC.heal();
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 140 == 0) {
            PlayerFilterGeneric.playingGameWarlordsPlayers(option.getGame())
                               .enemiesOf(warlordsNPC)
                               .forEach(receiver -> {
                                   receiver.getCooldownManager().removePreviousWounding();
                                   receiver.getCooldownManager().addCooldown(new RegularCooldown<WoundingStrikeBerserker>(
                                           name,
                                           "WND",
                                           WoundingStrikeBerserker.class,
                                           new WoundingStrikeBerserker(),
                                           warlordsNPC,
                                           CooldownTypes.DEBUFF,
                                           cooldownManager -> {
                                           },
                                           cooldownManager -> {
                                               if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterNameActionBar("WND")
                                                                                                               .stream()
                                                                                                               .count() == 1) {
                                                   receiver.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.RED + "wounded" + ChatColor.GRAY + ".");
                                               }
                                           },
                                           3 * 20
                                   ) {
                                       @Override
                                       public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                                           return currentHealValue * .5f;
                                       }
                                   });
                               });
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        // Applies wounding to enemies for 3s.

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

}
