package com.ebicep.warlords.classes.internal;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class AbstractStrikeBase extends AbstractAbility {

    public AbstractStrikeBase(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    protected abstract void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer);

    @Override
    public void onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        PlayerFilter.entitiesAround(wp, 5, 5, 5)
                .aliveEnemiesOf(wp)
                .closestFirst(wp)
                .requireLineOfSight(wp)
                .first((nearPlayer) -> {
                    if (Utils.isLookingAt(player, nearPlayer.getEntity()) && Utils.hasLineOfSight(player, nearPlayer.getEntity())) {
                        PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);
                        wp.subtractEnergy(energyCost);

                        if (this instanceof AvengersStrike || this instanceof CrusadersStrike || this instanceof ProtectorsStrike) {
                            for (Player player1 : player.getWorld().getPlayers()) {
                                player1.playSound(player.getLocation(), "paladin.paladinstrike.activation", 2, 1);
                            }
                        } else if (this instanceof WoundingStrikeBerserker || this instanceof WoundingStrikeDefender || this instanceof CripplingStrike) {
                            for (Player player1 : Bukkit.getOnlinePlayers()) {
                                player1.playSound(player.getLocation(), "warrior.mortalstrike.impact", 2, 1);
                            }
                        }

                        onHit(wp, player, nearPlayer);

                    }
                });
    }

    protected boolean standingOnConsecrate(Player owner, WarlordsPlayer standing) {
        return standingOnConsecrate(owner, standing.getEntity());
    }

    protected boolean standingOnConsecrate(Player owner, LivingEntity standing) {
        for (Entity entity : owner.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Consecrate - " + owner.getName())) {
                if (entity.getLocation().clone().add(0, 2, 0).distanceSquared(standing.getLocation()) < 5 * 5.25) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
}
