package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Optional;

public abstract class AbstractStrikeBase extends AbstractAbility {

    public AbstractStrikeBase(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    protected abstract void onHit(@Nonnull WarlordsEntity wp, @Nonnull Player player, @Nonnull WarlordsEntity nearPlayer);

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        PlayerFilter.entitiesAround(wp, 4.8, 4.8, 4.8)
                .aliveEnemiesOf(wp)
                .closestFirst(wp)
                .requireLineOfSight(wp)
                .lookingAtFirst(wp)
                .first((nearPlayer) -> {
                    if (Utils.isLookingAt(player, nearPlayer.getEntity()) && Utils.hasLineOfSight(player, nearPlayer.getEntity())) {
                        addTimesUsed();
                        AbstractPlayerClass.sendRightClickPacket(player);

                        Optional<HammerOfLight> optionalHammer = new CooldownFilter<>(wp, RegularCooldown.class)
                                .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                                .findAny();

                        if (optionalHammer.isPresent()) {
                            wp.subtractEnergy(energyCost - (optionalHammer.get().isCrownOfLight() ? 10 : 0));
                        } else {
                            wp.subtractEnergy(energyCost);
                        }

                        Location loc = nearPlayer.getLocation();
                        if (this instanceof AvengersStrike || this instanceof CrusadersStrike || this instanceof ProtectorsStrike) {
                            Utils.playGlobalSound(loc, "paladin.paladinstrike.activation", 2, 1);
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
                            Utils.playGlobalSound(loc, "warrior.mortalstrike.impact", 2, 1);
                            randomHitEffect(nearPlayer, 7, 255, 0, 0);
                        } else if (this instanceof JudgementStrike) {
                            Utils.playGlobalSound(loc, "warrior.revenant.orbsoflife", 2, 1.7f);
                            Utils.playGlobalSound(loc, "mage.frostbolt.activation", 2, 2);
                            randomHitEffect(nearPlayer, 7, 255, 255, 255);
                        } else if (this instanceof RighteousStrike) {
                            Utils.playGlobalSound(loc, "rogue.vindicatorstrike.activation", 2, 0.7f);
                            Utils.playGlobalSound(loc, "shaman.earthenspike.impact", 2, 2);
                            randomHitEffect(nearPlayer, 7, 255, 255, 255);
                        } else if (this instanceof ImpalingStrike) {
                            Utils.playGlobalSound(loc, "rogue.apothecarystrike.activation", 2, 0.5f);
                            Utils.playGlobalSound(loc, "mage.fireball.activation", 2, 1.8f);
                            randomHitEffect(nearPlayer, 7, 100, 255, 100);
                        }

                        onHit(wp, player, nearPlayer);
                    }
                });

        return true;
    }

    public void knockbackOnHit(WarlordsEntity giver, WarlordsEntity kbTarget, double velocity, double y) {
        final Location loc = kbTarget.getLocation();
        final Vector v = giver.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-velocity).setY(y);
        kbTarget.setVelocity(v, false);
    }

    public void tripleHit(WarlordsEntity giver, WarlordsEntity initialTarget) {
        for (WarlordsEntity we : PlayerFilter
                .entitiesAround(initialTarget, 4, 4, 4)
                .aliveEnemiesOf(giver)
                .closestFirst(initialTarget)
                .limit(2)
        ) {
            we.addDamageInstance(
                    giver,
                    name,
                    minDamageHeal,
                    maxDamageHeal,
                    critChance,
                    critMultiplier,
                    false
            );
        }
    }

    private void randomHitEffect(WarlordsEntity player, int particleAmount, int red, int green, int blue) {
        for (int i = 0; i < particleAmount; i++) {
            ParticleEffect.REDSTONE.display(
                    new ParticleEffect.OrdinaryColor(red, green, blue),
                    player.getLocation().clone().add((Math.random() * 2) - 1, 1.2 + (Math.random() * 2) - 1, (Math.random() * 2) - 1),
                    500);

        }
    }

    protected Optional<Consecrate> getStandingOnConsecrate(WarlordsEntity owner, WarlordsEntity standing) {
        return new CooldownFilter<>(owner, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(Consecrate.class)
                .filter(consecrate -> consecrate.getLocation().distanceSquared(standing.getLocation()) < consecrate.getRadius() * consecrate.getRadius())
                .max(Comparator.comparingInt(Consecrate::getStrikeDamageBoost));
    }

}
