package com.ebicep.warlords.pve.mobs.events.boltarobonanza;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.skeleton.AbstractSkeleton;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

import java.util.concurrent.ThreadLocalRandom;

public class EventBoltaroShadow extends AbstractSkeleton implements BossMob {

    private boolean forceSplit = false;
    private int split;

    public EventBoltaroShadow(Location spawnLocation, int split) {
        super(spawnLocation,
                "Shadow Boltaro",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.END_MONSTER),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 0, 0),
                        Weapons.DEMONBLADE.getItem()
                ),
                (int) (6000 * (1 + split * .025)),
                0.42f,
                10,
                200 * (1 + split * .025f),
                400 * (1 + split * .025f)
        );
        this.split = split;
    }

    @Override
    public void onSpawn(PveOption option) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.addKnockback(name, attacker.getLocation(), receiver, -1.1, 0.3);
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                                                                       .withColor(Color.ORANGE)
                                                                       .with(FireworkEffect.Type.BALL)
                                                                       .withTrail()
                                                                       .build());
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ENDERMAN_DEATH, 2, 0.5f);

        int nextSplit = split + 1;
        option.spawnNewMob(new EventBoltaroShadow(warlordsNPC.getLocation(), nextSplit));
        if (forceSplit || ThreadLocalRandom.current().nextDouble(0, 1) < (1.0 / nextSplit)) {
            option.spawnNewMob(new EventBoltaroShadow(warlordsNPC.getLocation(), nextSplit));
        }
    }

    public int getSplit() {
        return split;
    }

    @Override
    public double weaponDropRate() {
        return 2;
    }
}
