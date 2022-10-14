package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecklessCharge extends AbstractAbility implements Listener {

    private static final List<UUID> STUNNED_PLAYERS = new ArrayList<>();

    public int playersCharged = 0;

    private int stunTimeInTicks = 10;

    public RecklessCharge() {
        super("Reckless Charge", 457, 601, 9.32f, 60, 20, 200);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Charge forward, dealing" + formatRangeDamage(minDamageHeal, maxDamageHeal) +
                "damage to all enemies you pass through. Enemies hit are §5IMMOBILIZED§7, preventing movement for §6" + (stunTimeInTicks / 20f) +
                " §7seconds.";
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
                        wp.setVelocity(location.getDirection().multiply(2).setY(.2), true);
                    } else {
                        wp.setVelocity(location.getDirection().multiply(1.5).setY(.2), true);
                    }
                }
                //cancel charge if hit a block, making the player stand still
                if (wp.getLocation().distanceSquared(chargeLocation) > finalChargeDistance * finalChargeDistance ||
                        (wp.getEntity().getVelocity().getX() == 0 && wp.getEntity().getVelocity().getZ() == 0) ||
                        maxChargeDuration <= 0
                ) {
                    wp.setVelocity(new Vector(0, 0, 0), true);
                    this.cancel();
                }
                for (int i = 0; i < 4; i++) {
                    ParticleEffect.REDSTONE.display(
                            new ParticleEffect.OrdinaryColor(255, 0, 0),
                            wp.getLocation().clone().add((Math.random() * 1.5) - .75, .5 + (Math.random() * 2) - 1, (Math.random() * 1.5) - .75),
                            500
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
                                    AbstractMob<?> mob = ((WarlordsNPC) otherPlayer).getMob();
                                    EntityInsentient entityInsentient = mob.getEntityInsentient();
                                    PathfinderGoalSelector oldGoalSelector = entityInsentient.goalSelector;
                                    mob.getEntity().resetGoalAI(((CraftWorld) wp.getWorld()).getHandle());
                                    new GameRunnable(wp.getGame()) {
                                        @Override
                                        public void run() {
                                            entityInsentient.goalSelector = oldGoalSelector;
                                        }
                                    }.runTaskLater(getStunTimeInTicks());
//                                new GameRunnable(wp.getGame()) {
//                                    Location location = enemy.getLocation();
//                                    int counter = 0;
//                                    @Override
//                                    public void run() {
//                                        enemy.teleport(location);
//                                        if(counter++ == 60) {
//                                            this.cancel();
//                                        }
//                                    }
//                                }.runTaskTimer(0, 0);
                                } else {
                                    STUNNED_PLAYERS.add(otherPlayer.getUuid());
                                    new GameRunnable(wp.getGame()) {
                                        @Override
                                        public void run() {
                                            STUNNED_PLAYERS.remove(otherPlayer.getUuid());
                                        }
                                    }.runTaskLater(getStunTimeInTicks());
                                    if (otherPlayer.getEntity() instanceof Player) {
                                        PacketUtils.sendTitle((Player) otherPlayer.getEntity(), "", "§dIMMOBILIZED", 0, stunTimeInTicks, 0);
                                    }
                                }
                            } else if (otherPlayer.isTeammateAlive(wp) && pveUpgrade) {
                                otherPlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                                        "Probiotic",
                                        "PROBIO",
                                        RecklessCharge.class,
                                        null,
                                        wp,
                                        CooldownTypes.ABILITY,
                                        cooldownManager -> {
                                        },
                                        5 * 20
                                ) {
                                    @Override
                                    public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                                        return currentHealValue * 1.3f;
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (STUNNED_PLAYERS.contains(e.getPlayer().getUniqueId())) {
            if (
                    (e.getFrom().getX() != e.getTo().getX() ||
                            e.getFrom().getZ() != e.getTo().getZ()) &&
                            !(e instanceof PlayerTeleportEvent)
            ) {
                e.getPlayer().teleport(e.getFrom());
            }
        }
    }
}
