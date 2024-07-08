package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.IncendiaryCurseBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class IncendiaryCurse extends AbstractAbility implements RedAbilityIcon, HitBox, Damages<IncendiaryCurse.DamageValues> {

    private static final double SPEED = 0.250;
    private static final double GRAVITY = -0.008;

    public int playersHit = 0;
    private final DamageValues damageValues = new DamageValues();
    private FloatModifiable hitbox = new FloatModifiable(5);
    private int blindDurationInTicks = 30;

    public IncendiaryCurse() {
        this(8, 0);
    }

    public IncendiaryCurse(float cooldown, float startCooldown) {
        super("Incendiary Curse", cooldown, 60, startCooldown);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Ignite the targeted area with a cross flame, dealing")
                               .append(Damages.formatDamage(damageValues.curseDamage))
                               .append(Component.text("damage. Enemies hit are " + (inPve ? "stunned" : "blinded") + " for "))
                               .append(Component.text(format(blindDurationInTicks / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "mage.frostbolt.activation", 2, 0.7f);

        Utils.spawnThrowableProjectile(
                wp.getGame(),
                Utils.spawnArmorStand(wp.getLocation(), armorStand -> {
                    armorStand.getEquipment().setHelmet(new ItemStack(Material.FIRE_CHARGE));
                }),
                calculateSpeed(wp),
                GRAVITY,
                SPEED,
                (newLoc, integer) -> {},
                newLoc -> PlayerFilter
                        .entitiesAroundRectangle(newLoc, 1, 2, 1)
                        .aliveEnemiesOf(wp)
                        .findFirstOrNull(),
                (newLoc, directHit) -> {
                    onImpact(wp, newLoc);
                }
        );

        return true;
    }

    protected Vector calculateSpeed(WarlordsEntity we) {
        return we.getLocation().getDirection().multiply(SPEED);
    }

    public void onImpact(@Nonnull WarlordsEntity wp, Location newLoc) {
        Utils.playGlobalSound(newLoc, Sound.ITEM_FLINTANDSTEEL_USE, 2, 0.1f);

        EffectUtils.playFirework(
                newLoc,
                FireworkEffect.builder()
                              .withColor(Color.ORANGE)
                              .withColor(Color.RED)
                              .with(FireworkEffect.Type.BURST)
                              .build(),
                1
        );

        EffectUtils.displayParticle(Particle.SMOKE_NORMAL, newLoc, 100, 0.4, 0.05, 0.4, 0.2);

        float hitboxValue = hitbox.getCalculatedValue();
        List<WarlordsEntity> enemies = PlayerFilter
                .entitiesAround(newLoc, hitboxValue, hitboxValue, hitboxValue)
                .aliveEnemiesOf(wp)
                .toList();
        for (WarlordsEntity nearEntity : enemies) {
            playersHit++;

            nearEntity.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(wp)
                    .value(damageValues.curseDamage)
            );
            if (inPve && nearEntity instanceof WarlordsNPC warlordsNPC) {
                warlordsNPC.setStunTicks(blindDurationInTicks);
            } else {
                nearEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindDurationInTicks, 0, true, false));
            }
            nearEntity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, blindDurationInTicks, 0, true, false));

            if (pveMasterUpgrade) {
                EffectUtils.playFirework(
                        newLoc,
                        FireworkEffect.builder()
                                      .withColor(Color.RED)
                                      .withColor(Color.BLACK)
                                      .with(FireworkEffect.Type.BALL_LARGE)
                                      .build(),
                        1
                );

                nearEntity.getCooldownManager().removeCooldown(IncendiaryCurse.class, false);
                nearEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                        name,
                        "INCEN",
                        IncendiaryCurse.class,
                        new IncendiaryCurse(),
                        wp,
                        CooldownTypes.DEBUFF,
                        cooldownManager -> {
                        },
                        5 * 20
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * 1.5f;
                    }
                });
            } else if (pveMasterUpgrade2) {
                EffectUtils.displayParticle(
                        Particle.REDSTONE,
                        nearEntity.getLocation().add(0, 1.2, 0),
                        3,
                        0.3,
                        0.2,
                        0.3,
                        0,
                        new Particle.DustOptions(Color.fromRGB(255, 255, 0), 2)
                );
            }
        }
        if (pveMasterUpgrade2) {
            wp.addEnergy(wp, "Unforseen Curse", Math.min(50, enemies.size() * 5));
        }
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new IncendiaryCurseBranch(abilityTree, this);
    }

    public int getBlindDurationInTicks() {
        return blindDurationInTicks;
    }

    public void setBlindDurationInTicks(int blindDurationInTicks) {
        this.blindDurationInTicks = blindDurationInTicks;
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return hitbox;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable curseDamage = new Value.RangedValueCritable(408, 552, 20, 175);
        private final List<Value> values = List.of(curseDamage);

        public Value.RangedValueCritable getCurseDamage() {
            return curseDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }
}
