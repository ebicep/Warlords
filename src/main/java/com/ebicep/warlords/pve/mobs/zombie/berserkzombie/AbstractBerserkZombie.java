package com.ebicep.warlords.pve.mobs.zombie.berserkzombie;

import com.ebicep.warlords.abilities.WoundingStrikeBerserker;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BasicMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import org.bukkit.Location;
import org.bukkit.inventory.EntityEquipment;

import java.util.Map;

public abstract class AbstractBerserkZombie extends AbstractZombie implements BasicMob {

    protected final WoundingStrikeBerserker woundingStrike = new WoundingStrikeBerserker();
    private int strikeTickDelay = 0;

    public AbstractBerserkZombie(
            Location spawnLocation,
            String name,
            MobTier mobTier,
            EntityEquipment ee,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation, name, mobTier, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        if (option.getDifficulty() != DifficultyIndex.EASY && option.getGame().onlinePlayersWithoutSpectators().count() == 1) {
            woundingStrike.setHitbox(woundingStrike.getHitbox() - 1);
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (strikeTickDelay > 0) {
            strikeTickDelay--;
            return;
        }
        if (ticksElapsed % 20 == 0) {
            if (woundingStrike.onActivate(warlordsNPC, null)) {
                PacketUtils.playRightClickAnimationForPlayer(entity.getBukkitEntity().getHandle(),
                        warlordsNPC.getGame()
                                   .onlinePlayers()
                                   .map(Map.Entry::getKey)
                                   .toArray(org.bukkit.entity.Player[]::new)
                );
                strikeTickDelay = 100;
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}
