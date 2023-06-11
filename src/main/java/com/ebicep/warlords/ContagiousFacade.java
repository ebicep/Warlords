package com.ebicep.warlords;

import com.ebicep.warlords.abilties.ArcaneShield;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import com.google.common.util.concurrent.AtomicDouble;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ContagiousFacade extends AbstractAbility implements Duration {

    private int damageAbsorption = 35;
    private int tickDuration = 120;
    private int shieldTickDuration = 120;
    private int vulnerableRange = 6;
    private int damageIncrease = 25;
    private int damageIncreaseDuration = 120;

    public ContagiousFacade() {
        super("Contagious Facade", 0, 0, 30, 40, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Cover yourself in a protective layer that absorbs ")
                               .append(Component.text(damageAbsorption + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" of all incoming damage for "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. "))
                               .append(Component.text(
                                       "\n\nReactivate the ability to grant yourself a shield equal to all the damage you have absorbed during Contagious Facade. Lasts "))
                               .append(Component.text(format(shieldTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Not reactivating the ability will instead make all enemies around you in a " +
                                       vulnerableRange + " block range around you vulnerable to Poisonous Hex. Increasing the damage it deals by "))
                               .append(Component.text(damageIncrease + "%", NamedTextColor.RED))
                               .append(Component.text(" for "))
                               .append(Component.text(format(damageIncreaseDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        AtomicDouble totalAbsorbed = new AtomicDouble(0);
        RegularCooldown<ContagiousFacade> protectiveLayerCooldown = new RegularCooldown<>(
                name,
                "FACADE",
                ContagiousFacade.class,
                new ContagiousFacade(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                            name + " Vulnerability",
                            "VUL",
                            ContagiousFacade.class,
                            new ContagiousFacade(),
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManager2 -> {

                            },
                            damageIncreaseDuration
                    ) {
                        @Override
                        public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            if (event.getWarlordsEntity().getLocation().distanceSquared(wp.getLocation()) < vulnerableRange * vulnerableRange) {
                                return currentDamageValue * (1 + damageIncrease / 100f);
                            }
                            return currentDamageValue;
                        }
                    });
                },
                tickDuration
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float afterValue = currentDamageValue * (100 - damageAbsorption) / 100f;
                totalAbsorbed.addAndGet(currentDamageValue - afterValue);
                return afterValue;
            }
        };
        wp.getCooldownManager().addCooldown(protectiveLayerCooldown);
        addSecondaryAbility(() -> {
                    if (!wp.isAlive()) {
                        return;
                    }
                    wp.getCooldownManager().removeCooldownNoForce(protectiveLayerCooldown);

                    // copied from arcane shield, easiest but not technically an "arcane shield"
                    Utils.playGlobalSound(wp.getLocation(), "mage.arcaneshield.activation", 2, 1);
                    wp.getCooldownManager().addRegularCooldown(
                            name,
                            "ARCA",
                            ArcaneShield.class,
                            new ArcaneShield((int) totalAbsorbed.get()),
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManager -> {
                            },
                            cooldownManager -> {
                                if (new CooldownFilter<>(cooldownManager, RegularCooldown.class).filterCooldownClass(ArcaneShield.class).stream().count() == 1) {
                                    if (wp.getEntity() instanceof Player) {
                                        ((LivingEntity) ((CraftPlayer) wp.getEntity()).getHandle()).setAbsorptionAmount(0);
                                    }
                                }
                            },
                            shieldTickDuration,
                            Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                if (ticksElapsed % 3 == 0) {
                                    Location location = wp.getLocation();
                                    location.add(0, 1.5, 0);
                                    World world = location.getWorld();
                                    world.spawnParticle(Particle.CLOUD, location, 2, 0.15F, 0.3F, 0.15F, 0.01, null, true);
                                    world.spawnParticle(Particle.FIREWORKS_SPARK, location, 1, 0.3F, 0.3F, 0.3F, 0.0001, null, true);
                                    world.spawnParticle(Particle.SPELL_WITCH, location, 1, 0.3F, 0.3F, 0.3F, 0, null, true);
                                }
                            })
                    );
                    if (player != null) {
                        ((LivingEntity) ((CraftPlayer) player).getHandle()).setAbsorptionAmount(20);
                    }
                },
                false,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(protectiveLayerCooldown)
        );
        return true;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }
}
