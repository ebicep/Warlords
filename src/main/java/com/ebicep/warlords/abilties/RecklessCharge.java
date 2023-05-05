package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RecklessCharge extends AbstractAbility implements Listener {

    public int playersCharged = 0;

    private int stunTimeInTicks = 10;

    public RecklessCharge() {
        super("Reckless Charge", 457, 601, 9.32f, 60, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Charge forward, dealing ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage to all enemies you pass through. Enemies hit are "))
                               .append(Component.text("IMMOBILIZED", NamedTextColor.DARK_PURPLE))
                               .append(Component.text(", preventing movement for "))
                               .append(Component.text((stunTimeInTicks / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Charged", "" + playersCharged));

        return info;
    }


    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "warrior.seismicwave.activation", 2, 1);

        Location location = player.getLocation();
        location.setPitch(0);
        Location chargeLocation = location.clone();
        double chargeDistance;
        List<WarlordsEntity> playersHit = new ArrayList<>();
        playersHit.add(wp);
        boolean inAir = false;

        if (location.getWorld().getBlockAt(location.clone().add(0, -1, 0)).getType() != Material.AIR) {
            inAir = true;
            //travels 5 blocks
            chargeDistance = 5;
        } else {
            //travels 7 at peak jump
            chargeDistance = Math.max(Math.min(Utils.getDistance(player, .1) * 5, 6.9), 6);
        }

        boolean finalInAir = inAir;
        double finalChargeDistance = chargeDistance;

        new GameRunnable(wp.getGame()) {
            //safety precaution
            int maxChargeDuration = 5;

            @Override
            public void run() {
                if (maxChargeDuration == 5) {
                    if (finalInAir) {
                        wp.setVelocity(name, location.getDirection().multiply(2).setY(.2), true);
                    } else {
                        wp.setVelocity(name, location.getDirection().multiply(1.5).setY(.2), true);
                    }
                }
                //cancel charge if hit a block, making the player stand still
                if (wp.getLocation().distanceSquared(chargeLocation) > finalChargeDistance * finalChargeDistance ||
                        (wp.getEntity().getVelocity().getX() == 0 && wp.getEntity().getVelocity().getZ() == 0) ||
                        maxChargeDuration <= 0
                ) {
                    wp.setVelocity(name, new Vector(0, 0, 0), true);
                    this.cancel();
                }
                for (int i = 0; i < 4; i++) {
                    wp.getLocation().getWorld().spawnParticle(
                            Particle.REDSTONE,
                            wp.getLocation().clone().add((Math.random() * 1.5) - .75, .5 + (Math.random() * 2) - 1, (Math.random() * 1.5) - .75),
                            1,
                            0,
                            0,
                            0,
                            0,
                            new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1),
                            true
                    );
                }
                PlayerFilter.entitiesAround(wp, 2.5, 5, 2.5)
                            .excluding(playersHit)
                            .forEach(otherPlayer -> {
                                playersHit.add(otherPlayer);

                                if (otherPlayer.isEnemyAlive(wp)) {
                                    playersCharged++;
                                    otherPlayer.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier, false);

                                    if (otherPlayer instanceof WarlordsNPC) {
                                        ((WarlordsNPC) otherPlayer).setStunTicks(getStunTimeInTicks());
                                        //otherPlayer.addSpeedModifier(wp, "Charge Stun", -99, getStunTimeInTicks(), "BASE");
                                    } else if (otherPlayer instanceof WarlordsPlayer) {
                                        ((WarlordsPlayer) otherPlayer).stun();
                                        new GameRunnable(wp.getGame()) {
                                            @Override
                                            public void run() {
                                                ((WarlordsPlayer) otherPlayer).unstun();
                                            }
                                        }.runTaskLater(getStunTimeInTicks());
                                        otherPlayer.getEntity().showTitle(Title.title(
                                                Component.empty(),
                                                Component.text("IMMOBILIZED", NamedTextColor.LIGHT_PURPLE),
                                                Title.Times.times(Ticks.duration(0), Ticks.duration(stunTimeInTicks), Ticks.duration(0))
                                        ));

                                    }
                                } else if (pveUpgrade && otherPlayer.isTeammateAlive(wp)) {
                                    otherPlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                                            "Probiotic",
                                            "PROBIO",
                                            RecklessCharge.class,
                                            null,
                                            wp,
                                            CooldownTypes.ABILITY,
                                            cooldownManager -> {
                                            },
                                            8 * 20
                                    ) {
                                        @Override
                                        public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                                            return currentHealValue * 2;
                                        }
                                    });
                                }
                            });

                maxChargeDuration--;
            }

        }.runTaskTimer(1, 0);

        return true;
    }

    public int getStunTimeInTicks() {
        return stunTimeInTicks;
    }

    public void setStunTimeInTicks(int stunTimeInTicks) {
        this.stunTimeInTicks = stunTimeInTicks;
    }

}
