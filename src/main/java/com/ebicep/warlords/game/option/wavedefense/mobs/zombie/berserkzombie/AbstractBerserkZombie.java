package com.ebicep.warlords.game.option.wavedefense.mobs.zombie.berserkzombie;

import com.ebicep.warlords.abilties.WoundingStrikeBerserker;
import com.ebicep.warlords.events.player.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BasicMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.inventory.EntityEquipment;

import java.util.function.Consumer;

public abstract class AbstractBerserkZombie extends AbstractZombie implements BasicMob {

    private int strikeTickDelay = 0;
    private final WoundingStrikeBerserker woundingStrike = new WoundingStrikeBerserker();
    private final Consumer<WoundingStrikeBerserker> woundingStrikeConsumer;

    public AbstractBerserkZombie(Location spawnLocation, String name, MobTier mobTier, EntityEquipment ee, int maxHealth, float walkSpeed, int damageResistance, float minMeleeDamage, float maxMeleeDamage, Consumer<WoundingStrikeBerserker> woundingStrikeConsumer) {
        super(spawnLocation, name, mobTier, ee, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
        this.woundingStrikeConsumer = woundingStrikeConsumer;
        woundingStrikeConsumer.accept(woundingStrike);
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
