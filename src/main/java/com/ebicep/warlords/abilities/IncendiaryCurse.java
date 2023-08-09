package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.IncendiaryCurseBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class IncendiaryCurse extends AbstractAbility implements RedAbilityIcon {

    private static final double SPEED = 0.250;
    private static final double GRAVITY = -0.008;

    public int playersHit = 0;

    private float hitbox = 5;
    private int blindDurationInTicks = 30;

    public IncendiaryCurse() {
        super("Incendiary Curse", 408, 552, 8, 60, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Ignite the targeted area with a cross flame, dealing")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text("damage. Enemies hit are blinded for "))
                               .append(Component.text(format(blindDurationInTicks / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. " + (inPve ? "Mobs that are blinded become stunned and lose agro on their current target." : "")));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "mage.frostbolt.activation", 2, 0.7f);

        Location location = player.getLocation();
        Vector speed = player.getLocation().getDirection().multiply(SPEED);
        ArmorStand stand = Utils.spawnArmorStand(location, armorStand -> {
            armorStand.getEquipment().setHelmet(new ItemStack(Material.FIRE_CHARGE));
        });

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                quarterStep(false);
                quarterStep(false);
                quarterStep(false);
                quarterStep(false);
                quarterStep(false);
                quarterStep(false);
                quarterStep(true);
            }

            private void quarterStep(boolean last) {

                if (!stand.isValid()) {
                    this.cancel();
                    return;
                }

                speed.add(new Vector(0, GRAVITY * SPEED, 0));
                Location newLoc = stand.getLocation();
                newLoc.add(speed);
                stand.teleport(newLoc);
                newLoc.add(0, 1.75, 0);

                stand.setHeadPose(new EulerAngle(-speed.getY() * 3, 0, 0));

                boolean shouldExplode;

                if (last) {
                    EffectUtils.displayParticle(
                            Particle.FIREWORKS_SPARK,
                            newLoc.clone().add(0, -1, 0),
                            1,
                            0.1,
                            0.1,
                            0.1,
                            0.1
                    );

                }

                WarlordsEntity directHit;
                if (
                        !newLoc.getBlock().isEmpty()
                                && newLoc.getBlock().getType() != Material.GRASS
                                && newLoc.getBlock().getType() != Material.BARRIER
                                && newLoc.getBlock().getType() != Material.VINE
                ) {
                    // Explode based on collision
                    shouldExplode = true;
                } else {
                    directHit = PlayerFilter
                            .entitiesAroundRectangle(newLoc, 1, 2, 1)
                            .aliveEnemiesOf(wp)
                            .findFirstOrNull();
                    shouldExplode = directHit != null;
                }

                if (shouldExplode) {
                    stand.remove();

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

                    for (WarlordsEntity nearEntity : PlayerFilter
                            .entitiesAround(newLoc, hitbox, hitbox, hitbox)
                            .aliveEnemiesOf(wp)
                    ) {
                        playersHit++;

                        nearEntity.addDamageInstance(
                                wp,
                                name,
                                minDamageHeal,
                                maxDamageHeal,
                                critChance,
                                critMultiplier
                        );
                        nearEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindDurationInTicks, 0, true, false));
                        nearEntity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, blindDurationInTicks, 0, true, false));

                        if (pveMasterUpgrade && nearEntity instanceof WarlordsNPC) {
                            EffectUtils.playFirework(
                                    newLoc,
                                    FireworkEffect.builder()
                                        .withColor(Color.RED)
                                        .withColor(Color.BLACK)
                                        .with(FireworkEffect.Type.BALL_LARGE)
                                        .build(),
                                    1);

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
                        }
                    }

                    this.cancel();
                }
            }

        }.runTaskTimer(0, 1);

        return true;
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

    public float getHitbox() {
        return hitbox;
    }

    public void setHitbox(float hitbox) {
        this.hitbox = hitbox;
    }


}
