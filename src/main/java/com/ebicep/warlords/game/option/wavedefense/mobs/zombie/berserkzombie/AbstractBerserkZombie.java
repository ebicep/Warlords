package com.ebicep.warlords.game.option.wavedefense.mobs.zombie.berserkzombie;

import com.ebicep.warlords.abilties.WoundingStrikeBerserker;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BasicMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.DifficultyIndex;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.inventory.EntityEquipment;

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
    public void onSpawn(WaveDefenseOption option) {
        if (option.getDifficulty() != DifficultyIndex.EASY && option.getGame().onlinePlayersWithoutSpectators().count() == 1) {
            woundingStrike.setHitbox(woundingStrike.getHitbox() - 1);
        }
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (strikeTickDelay > 0) {
            strikeTickDelay--;
            return;
        }
        if (ticksElapsed % 20 == 0) {
            if (woundingStrike.onActivate(warlordsNPC, null)) {
                //right click animation
                PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(entity.getBukkitEntity().getHandle(), 0);
                warlordsNPC.getGame().forEachOnlinePlayer((player, team) -> {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);
                });
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
