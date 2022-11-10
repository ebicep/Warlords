package com.ebicep.warlords.game.option.wavedefense.mobs.bosses.bossminions;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

public class TormentedSoul extends AbstractZombie implements BossMob {

    private float reduceCooldown = 0.2f;

    public TormentedSoul(Location spawnLocation) {
        super(spawnLocation,
                "Tormented Soul",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.FACELESS_MAGE),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 140, 140, 140),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 140, 140, 15),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 140, 140, 140),
                        Weapons.CLAWS.getItem()
                ),
                2000,
                0.38f,
                0,
                214,
                338
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        reduceCooldown = option.getDifficulty() == DifficultyIndex.HARD ? 0.4f : 0.2f;
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        EffectUtils.playParticleLinkAnimation(self.getLocation(), attacker.getLocation(), 200, 200, 200, 1);
        Utils.playGlobalSound(self.getLocation(), Sound.AMBIENCE_CAVE, 2, 2);
        if (!event.getAbility().isEmpty()) {
            attacker.getSpec().increaseAllCooldownTimersBy(reduceCooldown);
        }
    }
}
