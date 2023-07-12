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
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;

import javax.annotation.Nonnull;
import java.util.Map;

public abstract class AbstractBerserkZombie extends AbstractZombie implements BasicMob {

    protected final BerserkerZombieWoundingStrike woundingStrike;

    public AbstractBerserkZombie(
            Location spawnLocation,
            String name,
            MobTier mobTier,
            EntityEquipment ee,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            BerserkerZombieWoundingStrike woundingStrike
    ) {
        super(
                spawnLocation,
                name,
                mobTier,
                ee,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                woundingStrike
        );
        this.woundingStrike = woundingStrike;
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

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    static class BerserkerZombieWoundingStrike extends WoundingStrikeBerserker {

        public BerserkerZombieWoundingStrike(float minDamageHeal, float maxDamageHeal) {
            super("Wounding Strike", minDamageHeal, maxDamageHeal, 5, 100, 20, 175);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
            boolean onActivate = super.onActivate(wp, player);
            if (onActivate) {
                PacketUtils.playRightClickAnimationForPlayer(((CraftEntity) wp.getEntity()).getHandle(),
                        wp.getGame()
                          .onlinePlayers()
                          .map(Map.Entry::getKey)
                          .toArray(org.bukkit.entity.Player[]::new)
                );
            }
            return onActivate;
        }
    }
}
