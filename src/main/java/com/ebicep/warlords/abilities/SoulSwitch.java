package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.flags.DynamicFlags;
import com.ebicep.warlords.pve.mobs.flags.Unswappable;
import com.ebicep.warlords.pve.mobs.player.Animus;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.SoulSwitchBranch;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.Pair;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoulSwitch extends AbstractAbility implements BlueAbilityIcon, HitBox, Heals<SoulSwitch.HealingValues> {

    private final HealingValues healingValues = new HealingValues();
    private FloatModifiable radius = new FloatModifiable(13);
    private int blindnessTicks = 30;
    private int decoyMaxTicksLived = 60;
    // pve
    private int invisTicks = 30;

    public SoulSwitch() {
        super("Soul Switch", 30, 40);
    }

    @Override
    public void updateDescription(Player player) {
        if (inPve) {
            description = ComponentBuilder.create("Switch locations with an enemy, stunning them for ")
                                          .text(format(blindnessTicks / 20f), NamedTextColor.GOLD)
                                          .text(" seconds. Upon swapping, self heal for ")
                                          .append(Heals.formatHealing(healingValues.switchHealing))
                                          .text(" health, go invisible for ")
                                          .text(format(invisTicks / 20f), NamedTextColor.GOLD)
                                          .text(" seconds, and transform the swapped enemy into your own Animus. " +
                                                  "The Animus will inherit the max HP of the mob swapped and your current movement speed when swapped, no longer has its original stats/abilities, and will use Judgment Strike every 2 seconds based on the current your own Judgment Strike. " +
                                                  "Enemies cannot target the Animus, and only 1 Animus can exist at a time. " +
                                                  "For every enemy the Animus defeats, reduce the cooldown of Soul Switch by 1 second. Has a range of ")
                                          .text(format(radius.getCalculatedValue()), NamedTextColor.YELLOW)
                                          .text("blocks. Soul Switch has low vertical range.")
                                          .build();
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
                AbstractMob mob = warlordsNPC.getMob();
                if (mob instanceof Unswappable || mob.getDynamicFlags().contains(DynamicFlags.UNSWAPPABLE) || mob instanceof BossMob || mob instanceof BossMinionMob) {
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

            if (swapTarget instanceof WarlordsNPC npc) {
                PveOption pveOption = wp.getGame()
                                        .getOptions()
                                        .stream()
                                        .filter(PveOption.class::isInstance)
                                        .map(PveOption.class::cast)
                                        .findFirst()
                                        .orElse(null);
                if (pveOption != null) {
                    wp.addInstance(InstanceBuilder
                            .healing()
                            .ability(this)
                            .source(wp)
                            .value(healingValues.switchHealing)
                    );
                    wp.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30, 0, true, false));
                    pveOption.despawnMob(npc.getMob());
                    Animus animus = new Animus(ownLocation, wp, swapTarget);
                    pveOption.spawnNewMob(animus, wp.getTeam());
                    if (pveMasterUpgrade2) {
                        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Tricky Switch",
                                null,
                                SoulSwitch.class,
                                null,
                                wp,
                                CooldownTypes.ABILITY,
                                cooldownManager -> {},
                                10 * 60 * 20,
                                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                    if (animus.getWarlordsNPC().isDead()) {
                                        cooldown.setTicksLeft(0);
                                    }
                                })
                        ) {
                            @Override
                            public float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
                                return currentCritChance + 15;
                            }
                        });
                        animus.getWarlordsNPC().getCooldownManager().addCooldown(new PermanentCooldown<>(
                                "Tricky Switch",
                                null,
                                SoulSwitch.class,
                                null,
                                wp,
                                CooldownTypes.ABILITY,
                                cooldownManager -> {},
                                false
                        ) {
                            @Override
                            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                                if (event.getCause().equals("Judgement Strike")) {
                                    wp.addEnergy(wp, "Tricky Switch", 10);
                                    float heal = currentDamageValue * .1f;
                                    wp.addInstance(InstanceBuilder
                                            .healing()
                                            .cause("Tricky Switch")
                                            .source(wp)
                                            .value(heal)
                                    );
                                }
                            }
                        });
                    }
                }
            }

            if (pveMasterUpgrade) {
                wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                        "Soul Burst",
                        "SOUL",
                        SoulSwitch.class,
                        null,
                        wp,
                        CooldownTypes.BUFF,
                        cooldownManager -> {},
                        cooldownManager -> {
                            wp.removePotionEffect(PotionEffectType.INVISIBILITY);
                        },
                        5 * 20,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 3 == 0) {
                                wp.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, ticksLeft, 0, true, false));
                            }
                        })
                ) {
                    @Override
                    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * 0.5f;
                    }
                });
                PlayerFilter.entitiesAround(swapLocation, 3, 3, 3)
                            .aliveTeammatesOf(wp)
                            .forEach(warlordsEntity -> warlordsEntity.addSpeedModifier(wp, "Shadow Burst", 25, 3 * 20, "BASE"));
                PlayerFilter.entitiesAround(ownLocation, 3, 3, 3)
                            .aliveTeammatesOf(wp)
                            .forEach(warlordsEntity -> warlordsEntity.addSpeedModifier(wp, "Shadow Burst", 25, 3 * 20, "BASE"));
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
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    public int getInvisTicks() {
        return invisTicks;
    }

    public void setInvisTicks(int invisTicks) {
        this.invisTicks = invisTicks;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.RangedValueCritable switchHealing = new Value.RangedValueCritable(300, 500, 15, 175);
        private final List<Value> values = List.of(switchHealing);

        public Value.RangedValueCritable getSwitchHealing() {
            return switchHealing;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
