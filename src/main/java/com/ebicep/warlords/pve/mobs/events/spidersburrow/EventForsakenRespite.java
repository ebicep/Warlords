package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilties.ImpalingStrike;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class EventForsakenRespite extends AbstractZombie implements BossMob {


    public EventForsakenRespite(Location spawnLocation) {
        super(
                spawnLocation,
                "Forsaken Respite",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SPIDER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 200, 200, 200),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 200, 200, 200),
                        Weapons.SILVER_PHANTASM_SWORD_3.getItem()
                ),
                2200,
                0.45f,
                0,
                300,
                450
        );
    }

    @Override
    public void onSpawn(PveOption option) {

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        // Applies leech to enemies for 3s.
        ImpalingStrike.giveLeechCooldown(
                warlordsNPC,
                attacker,
                3 * 20,
                .25f,
                .15f,
                warlordsDamageHealingFinalEvent -> {

                }
        );
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}
