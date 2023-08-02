package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.abilities.RemoveTarget;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
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
                338,
                new RemoveTarget(20)
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        DifficultyIndex difficulty = option.getDifficulty();
        reduceCooldown = difficulty == DifficultyIndex.EXTREME ? 0.5f : difficulty == DifficultyIndex.HARD ? 0.4f : 0.2f;
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        EffectUtils.playParticleLinkAnimation(self.getLocation(), attacker.getLocation(), 200, 200, 200, 1);
        Utils.playGlobalSound(self.getLocation(), Sound.AMBIENT_CAVE, 2, 2);
        if (!event.getAbility().isEmpty()) {
            attacker.getSpec().increaseAllCooldownTimersBy(reduceCooldown);
        }
    }
}
