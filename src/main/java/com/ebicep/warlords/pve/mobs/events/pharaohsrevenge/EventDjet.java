package com.ebicep.warlords.pve.mobs.events.pharaohsrevenge;

import com.ebicep.warlords.abilities.CripplingStrike;
import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class EventDjet extends AbstractZombie implements BossMob {

    private boolean fireFlameBursts = false;

    public EventDjet(Location spawnLocation) {
        super(spawnLocation,
                "Djet",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.ETHEREAL_WITHER_SKULL),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 160, 160),
                        ArmorManager.ArmorSets.GREATER_LEGGINGS.itemRed,
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 255, 160, 160),
                        Weapons.WALKING_STICK.getItem()
                ),
                9000,
                0.32f,
                10,
                930,
                1210
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
//        AbstractAbility redAbility = warlordsNPC.getRedAbility(); TODO
//        redAbility.setMinDamageHeal(1200);
//        redAbility.setMinDamageHeal(1380);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 60 == 0 && aboveHealthThreshold()) {
            //warlordsNPC.getRedAbility().onActivate(warlordsNPC, null); TODO
        }
        if (ticksElapsed % 100 == 0) {
            for (WarlordsPlayer warlordsPlayer : PlayerFilterGeneric
                    .playingGameWarlordsPlayers(warlordsNPC.getGame())
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                SoulShackle.shacklePlayer(warlordsPlayer, warlordsPlayer, 60);
                CripplingStrike.cripple(warlordsNPC, warlordsPlayer, name, 3 * 20);
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (aboveHealthThreshold()) {
            warlordsNPC.setDamageResistance(10);
        } else {
            warlordsNPC.setDamageResistance(30);
        }
    }

    private boolean aboveHealthThreshold() {
        return !(warlordsNPC.getHealth() <= warlordsNPC.getMaxBaseHealth() * .75);
    }
}
