package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.mobs.flags.DynamicFlags;
import com.ebicep.warlords.pve.mobs.flags.Unswappable;
import com.ebicep.warlords.pve.mobs.player.Decoy;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.SoulSwitchBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SoulSwitch extends AbstractAbility implements BlueAbilityIcon, HitBox {

    private FloatModifiable radius = new FloatModifiable(13);
    private int blindnessTicks = 30;
    private int decoyMaxTicksLived = 60;

    public SoulSwitch() {
        super("Soul Switch", 0, 0, 30, 40, -1, 50);
    }

    @Override
    public void updateDescription(Player player) {
        if (inPve) {
            description = Component.text("Switch locations with an enemy, blinding them for ")
                                   .append(Component.text("1.5 ", NamedTextColor.GOLD))
                                   .append(Component.text("seconds. If a mob is swapped, create a decoy at your original position with "))
                                   .append(Component.text("5000 ", NamedTextColor.RED))
                                   .append(Component.text("health that explodes after "))
                                   .append(Component.text("3 ", NamedTextColor.GOLD))
                                   .append(Component.text("seconds or if killed, damaging nearby enemies. Has an optimal range of "))
                                   .append(Component.text(format(radius.getCalculatedValue()), NamedTextColor.YELLOW))
                                   .append(Component.text("blocks. Soul Switch has low vertical range."));
        } else {
            description = Component.text("Switch locations with an enemy, blinding them for ")
                                   .append(Component.text("1.5 ", NamedTextColor.GOLD))
                                   .append(Component.text("seconds. Has a range of "))
                                   .append(Component.text(format(radius.getCalculatedValue()), NamedTextColor.YELLOW))
                                   .append(Component.text("blocks. Soul Switch has low vertical range."));
        }

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        if (wp.getCarriedFlag() != null) {
            wp.sendMessage(Component.text(" You cannot Soul Switch while holding the flag!", NamedTextColor.RED));
            return false;
        }
        float rad = radius.getCalculatedValue();
        for (WarlordsEntity swapTarget : PlayerFilter
                .entitiesAround(wp.getLocation(), rad, rad / 2f, rad)
                .aliveEnemiesOf(wp)
                .requireLineOfSight(wp)
                .lookingAtFirst(wp)
        ) {
            if (swapTarget.getCarriedFlag() != null) {
                wp.sendMessage(Component.text(" You cannot Soul Switch with a player holding the flag!", NamedTextColor.RED));
                continue;
            }
            if (swapTarget instanceof WarlordsNPC warlordsNPC) {
                if (warlordsNPC.getMob() instanceof Unswappable || warlordsNPC.getMob().getDynamicFlags().contains(DynamicFlags.UNSWAPPABLE)) {
                    wp.sendMessage(Component.text(" You cannot Soul Switch with that mob!", NamedTextColor.RED));
                    continue;
                }
            }

            Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 1.5f);

            Location swapLocation = swapTarget.getLocation();
            Location ownLocation = wp.getLocation();

            EffectUtils.playCylinderAnimation(swapLocation, 1.05, Particle.CLOUD, 1);
            EffectUtils.playCylinderAnimation(ownLocation, 1.05, Particle.CLOUD, 1);

            swapTarget.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindnessTicks, 0, true, false));
            swapTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED
                    .append(Component.text(" You've been Soul Swapped by ", NamedTextColor.GRAY))
                    .append(Component.text(wp.getName(), NamedTextColor.YELLOW))
                    .append(Component.text("!", NamedTextColor.GRAY))
            );
            swapTarget.teleport(new Location(
                    wp.getWorld(),
                    ownLocation.getX(),
                    ownLocation.getY(),
                    ownLocation.getZ(),
                    swapLocation.getYaw(),
                    swapLocation.getPitch()
            ));

            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" You swapped with ", NamedTextColor.GRAY))
                    .append(Component.text(swapTarget.getName(), NamedTextColor.YELLOW))
                    .append(Component.text("!", NamedTextColor.GRAY))
            );
            wp.teleport(new Location(
                    swapLocation.getWorld(),
                    swapLocation.getX(),
                    swapLocation.getY(),
                    swapLocation.getZ(),
                    ownLocation.getYaw(),
                    ownLocation.getPitch()
            ));

            if (swapTarget instanceof WarlordsNPC) {
                PveOption pveOption = wp.getGame()
                                        .getOptions()
                                        .stream()
                                        .filter(PveOption.class::isInstance)
                                        .map(PveOption.class::cast)
                                        .findFirst()
                                        .orElse(null);
                Decoy decoy;
                if (pveOption != null) {
                    wp.addSpeedModifier(wp, "Tricky Switch", 30, decoyMaxTicksLived);
                    decoy = new Decoy(ownLocation,
                            wp.getName(),
                            wp.getHead(),
                            wp.getChestplate(),
                            wp.getLeggings(),
                            wp.getBoots(),
                            wp.getMainHand()
                    ) {

                        @Override
                        public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
                            wp.getSpeed().removeModifier("Tricky Switch");
                            PlayerFilter.entitiesAround(ownLocation, 5, 5, 5)
                                        .aliveEnemiesOf(wp)
                                        .forEach(hit -> {
                                            hit.addDamageInstance(
                                                    wp,
                                                    "Decoy",
                                                    782 * (pveMasterUpgrade ? 2 : 1),
                                                    1034 * (pveMasterUpgrade ? 2 : 1),
                                                    0,
                                                    100
                                            );
                                            if (pveMasterUpgrade) {
                                                hit.getCooldownManager().addCooldown(new RegularCooldown<>(
                                                        "Switch Crippling",
                                                        "CRIP",
                                                        SoulSwitch.class,
                                                        new SoulSwitch(),
                                                        wp,
                                                        CooldownTypes.DEBUFF,
                                                        cooldownManager -> {
                                                        },
                                                        20 * 5
                                                ) {
                                                    @Override
                                                    public float modifyDamageBeforeInterveneFromAttacker(
                                                            WarlordsDamageHealingEvent event,
                                                            float currentDamageValue
                                                    ) {
                                                        return currentDamageValue * .5f;
                                                    }
                                                });
                                            }
                                        });

                            ownLocation.getWorld().spawnParticle(
                                    Particle.EXPLOSION_LARGE,
                                    ownLocation.add(0, 1, 0),
                                    5,
                                    0,
                                    0,
                                    0,
                                    0.5,
                                    null,
                                    true
                            );
                        }
                    };
                    pveOption.spawnNewMob(decoy, Team.BLUE);
                } else {
                    decoy = null;
                }
                if (pveMasterUpgrade) {
                    float healing = (wp.getMaxHealth() - wp.getCurrentHealth()) * 0.1f;
                    wp.addHealingInstance(
                            wp,
                            name,
                            healing,
                            healing,
                            -1,
                            100
                    );
                }
                new GameRunnable(wp.getGame()) {
                    @Override
                    public void run() {
                        if (pveOption != null && pveOption.getMobs().contains(decoy)) {
                            decoy.getWarlordsNPC().die(decoy.getWarlordsNPC());
                        }
                    }
                }.runTaskLater(decoyMaxTicksLived);
            }

            return true;
        }
        return false;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SoulSwitchBranch(abilityTree, this);
    }

    public int getBlindnessTicks() {
        return blindnessTicks;
    }

    public void setBlindnessTicks(int blindnessTicks) {
        this.blindnessTicks = blindnessTicks;
    }

    public int getDecoyMaxTicksLived() {
        return decoyMaxTicksLived;
    }

    public void setDecoyMaxTicksLived(int decoyMaxTicksLived) {
        this.decoyMaxTicksLived = decoyMaxTicksLived;
    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        radius.tick();
        super.runEveryTick(warlordsEntity);
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }
}
