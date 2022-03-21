package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class AbstractStrikeBase extends AbstractAbility {

    public AbstractStrikeBase(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    protected abstract void onHit(@Nonnull WarlordsPlayer wp, @Nonnull Player player, @Nonnull WarlordsPlayer nearPlayer);

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        PlayerFilter.entitiesAround(wp, 4.8, 4.8, 4.8)
                .aliveEnemiesOf(wp)
                .closestFirst(wp)
                .requireLineOfSight(wp)
                .lookingAtFirst(wp)
                .first((nearPlayer) -> {
                    if (Utils.isLookingAt(player, nearPlayer.getEntity()) && Utils.hasLineOfSight(player, nearPlayer.getEntity())) {
                        PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);

                        Optional<HammerOfLight> optionalHammer = new CooldownFilter<>(wp, RegularCooldown.class).filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class).findAny();
                        if (optionalHammer.isPresent()) {
                            wp.subtractEnergy(energyCost - (optionalHammer.get().isCrownOfLight() ? 10 : 0));
                        } else {
                            wp.subtractEnergy(energyCost);
                        }

                        if (this instanceof AvengersStrike || this instanceof CrusadersStrike || this instanceof ProtectorsStrike) {
                            Utils.playGlobalSound(nearPlayer.getLocation(), "paladin.paladinstrike.activation", 2, 1);
                            randomHitEffect(nearPlayer, 5, 255, 0, 0);
                            ParticleEffect.SPELL.display(
                                    (float) ((Math.random() * 2) - 1),
                                    (float) ((Math.random() * 2) - 1),
                                    (float) ((Math.random() * 2) - 1),
                                    1,
                                    4,
                                    nearPlayer.getLocation().clone().add(0, 1, 0),
                                    500);
                        } else if (this instanceof WoundingStrikeBerserker || this instanceof WoundingStrikeDefender || this instanceof CripplingStrike) {
                            Utils.playGlobalSound(nearPlayer.getLocation(), "warrior.mortalstrike.impact", 2, 1);
                            randomHitEffect(nearPlayer, 7, 255, 0, 0);
                        } else if (this instanceof JudgementStrike) {
                            Utils.playGlobalSound(nearPlayer.getLocation(), "warrior.revenant.orbsoflife", 2, 1.7f);
                            Utils.playGlobalSound(nearPlayer.getLocation(), "mage.frostbolt.activation", 2, 2);
                            randomHitEffect(nearPlayer, 7, 255, 255, 255);
                        } else if (this instanceof RighteousStrike) {
                            Utils.playGlobalSound(nearPlayer.getLocation(), "rogue.vindicatorstrike.activation", 2, 0.7f);
                            Utils.playGlobalSound(nearPlayer.getLocation(), "shaman.earthenspike.impact", 2, 2);
                            randomHitEffect(nearPlayer, 7, 255, 255, 255);
                        } else if (this instanceof ImpalingStrike) {
                            Utils.playGlobalSound(nearPlayer.getLocation(), "rogue.apothecarystrike.activation", 2, 0.5f);
                            Utils.playGlobalSound(nearPlayer.getLocation(), "mage.fireball.activation", 2, 1.8f);
                            randomHitEffect(nearPlayer, 7, 100, 255, 100);
                        }

                        onHit(wp, player, nearPlayer);
                    }
                });

        return true;
    }

    private void randomHitEffect(WarlordsPlayer player, int particleAmount, int red, int green, int blue) {
        for (int i = 0; i < particleAmount; i++) {
            ParticleEffect.REDSTONE.display(
                    new ParticleEffect.OrdinaryColor(red, green, blue),
                    player.getLocation().clone().add((Math.random() * 2) - 1, 1.2 + (Math.random() * 2) - 1, (Math.random() * 2) - 1),
                    500);

        }
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
