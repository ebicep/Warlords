package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

public class NineCrystal extends AbstractMob implements BossMinionMob {

    private SpecType spec;
    private TextDisplay holo;

    public NineCrystal(Location spawnLocation, SpecType spec) {
        super(
                spawnLocation,
                LegacyComponentSerializer.legacySection().serialize(Component.text(spec.name(), spec.getTextColor(), TextDecoration.BOLD)),
                2500,
                0,
                0,
                0,
                0
        );
        this.spec = spec;
    }

    public NineCrystal(Location spawnLocation) {
        super(
                spawnLocation,
                "Pylon",
                2500,
                0,
                0,
                0,
                0
        );
    }

    public NineCrystal(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.NINE_CRYSTAL;
    }

    @Override
    public void onSpawn(PveOption option) {
        float newHealth;
        switch (spec) {
            case DAMAGE -> newHealth = 5000;
            case HEALER -> newHealth = 1250;
            case TANK -> newHealth = 2500;
            default -> newHealth = 2000;
        }

        warlordsNPC.setMaxHealthAndHeal(newHealth);
        warlordsNPC.addKnockbackModifier(warlordsNPC, "KB RES", -100, 100000);
        warlordsNPC.getCooldownManager().removeCooldown(DamageCheck.class, false);
        warlordsNPC.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Damage Check",
                null,
                DamageCheck.class,
                DamageCheck.DAMAGE_CHECK,
                warlordsNPC,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                true
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                    if (spec == event.getSource().getSpecClass().specType) {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, name, 3);
                    } else {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, name, 0.1f);
                    }
                }
        ));

        holo = warlordsNPC.getWorld().spawn(warlordsNPC.getLocation().clone().add(0, 3, 0), TextDisplay.class, td -> {
                    td.setBillboard(Display.Billboard.CENTER);
                    td.setSeeThrough(true);
                    td.setBackgroundColor(Color.GRAY);
                    td.setText(LegacyComponentSerializer.legacySection().serialize(Component.text(spec.name(), spec.getTextColor(), TextDecoration.BOLD)));
                    td.setLineWidth(80);
                    td.setTransformation(new Transformation(
                            new Vector3f(),
                            new Quaternionf(),
                            new Vector3f(2f, 2f, 2f),
                            new Quaternionf()
                    ));
                }
        );
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 20 == 0) {
            EffectUtils.playParticleLinkAnimation(warlordsNPC.getLocation(), new Location(warlordsNPC.getWorld(), 112.5, 13, 62.5), Particle.CHERRY_LEAVES);
        }

        if (!holo.isDead() && holo.getWorld() == warlordsNPC.getWorld()) {
            holo.teleport(warlordsNPC.getLocation().clone().add(0, 3, 0));
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        holo.remove();
    }

}
