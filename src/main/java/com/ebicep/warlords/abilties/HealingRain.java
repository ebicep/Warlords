package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Overheal;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.AreaEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class HealingRain extends AbstractAbility {
    private boolean pveUpgrade = false;
    protected int playersHealed = 0;

    private int duration = 12;
    private int radius = 8;

    public HealingRain() {
        super("Healing Rain", 100, 125, 52.85f, 50, 25, 200);
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHealed));

        return info;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Conjure rain at targeted\n" +
                "location that will restore §a" + format(minDamageHeal) + "\n" +
                "§7- §a" + format(maxDamageHeal) + " §7health every 0.5 seconds\n" +
                "to allies. Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "You may move Healing Rain to your location\n" +
                "using your SNEAK key." +
                "\n\n" +
                "§7Healing Rain can overheal allies for up to\n" +
                "§a10% §7of their max health as bonus health\n" +
                "§7for §6" + Overheal.OVERHEAL_DURATION + " §7seconds.";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        if (player.getTargetBlock((Set<Material>) null, 25).getType() == Material.AIR) return false;
        wp.subtractEnergy(energyCost);
        wp.getSpec().getOrange().setCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));

        Location location = player.getTargetBlock((Set<Material>) null, 25).getLocation().clone();
        Utils.playGlobalSound(location, "mage.healingrain.impact", 2, 1);

        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location,
                radius,
                new CircumferenceEffect(ParticleEffect.VILLAGER_HAPPY, ParticleEffect.REDSTONE),
                new AreaEffect(5, ParticleEffect.CLOUD).particlesPerSurface(0.025),
                new AreaEffect(5, ParticleEffect.DRIP_WATER).particlesPerSurface(0.025)
        );

        BukkitTask particleTask = wp.getGame().registerGameTask(circleEffect::playEffects, 0, 1);

        RegularCooldown<HealingRain> healingRainCooldown = new RegularCooldown<>(
                name,
                "RAIN",
                HealingRain.class,
                new HealingRain(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    particleTask.cancel();
                },
                duration * 20,
                (cooldown, ticksLeft, counter) -> {
                    if (counter % 10 == 0) {
                        for (WarlordsEntity teammateInRain : PlayerFilter
                                .entitiesAround(location, radius, radius, radius)
                                .aliveTeammatesOf(wp)
                        ) {
                            playersHealed++;
                            teammateInRain.addHealingInstance(
                                    wp,
                                    name,
                                    minDamageHeal,
                                    maxDamageHeal,
                                    critChance,
                                    critMultiplier,
                                    false,
                                    false);

                            if (teammateInRain != wp) {
                                teammateInRain.getCooldownManager().removeCooldownByObject(Overheal.OVERHEAL_MARKER);
                                teammateInRain.getCooldownManager().addRegularCooldown("Overheal",
                                        "OVERHEAL", Overheal.class, Overheal.OVERHEAL_MARKER, wp, CooldownTypes.BUFF, cooldownManager -> {
                                        }, Overheal.OVERHEAL_DURATION * 20);
                            }
                        }
                    }

                    if (counter % 40 == 0) {
                        if (pveUpgrade) {
                            for (WarlordsEntity enemyInRain : PlayerFilter
                                    .entitiesAround(location, radius, radius, radius)
                                    .aliveEnemiesOf(wp)
                                    .limit(5)
                            ) {
                                Utils.playGlobalSound(enemyInRain.getLocation(), Sound.AMBIENCE_THUNDER, 2, 1.8f);
                                FireWorkEffectPlayer.playFirework(enemyInRain.getLocation(), FireworkEffect.builder()
                                        .withColor(Color.AQUA)
                                        .with(FireworkEffect.Type.BURST)
                                        .build());
                                strikeInRain(wp, enemyInRain);
                            }
                        }
                    }
                }
        );
        wp.getCooldownManager().addCooldown(healingRainCooldown);

        addSecondaryAbility(() -> {
                    if (wp.isAlive()) {
                        wp.playSound(wp.getLocation(), "mage.timewarp.teleport", 2, 1.35f);
                        wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN + " §7You moved your §aHealing Rain §7to your current location.");
                        location.setX(wp.getLocation().getX());
                        location.setY(wp.getLocation().getY());
                        location.setZ(wp.getLocation().getZ());
                    }
                },
                true,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(healingRainCooldown)
        );

        return true;
    }

    private void strikeInRain(WarlordsEntity giver, WarlordsEntity hit) {
        for (WarlordsEntity strikeTarget : PlayerFilter
                .entitiesAround(hit, 2, 3, 2)
                .aliveEnemiesOf(giver)
        ) {
            strikeTarget.getWorld().spigot().strikeLightningEffect(strikeTarget.getLocation(), true);
            float healthDamage = strikeTarget.getMaxHealth() * 0.01f;
            strikeTarget.addDamageInstance(giver, name, 124 + healthDamage, 277 + healthDamage, critChance, critMultiplier, false);
        }

    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isPveUpgrade() {
        return pveUpgrade;
    }

    public void setPveUpgrade(boolean pveUpgrade) {
        this.pveUpgrade = pveUpgrade;
    }
}
