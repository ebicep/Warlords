package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsEntity;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class Intervene extends AbstractAbility {
    protected int playersIntervened = 0;
    protected int carriersIntervened = 0;

    private final int duration = 5;
    private float damagePrevented = 0;
    private float maxDamagePrevented = 3600;
    private int radius = 10;
    private int breakRadius = 15;
    private WarlordsEntity caster;
    private WarlordsEntity target;

    public Intervene() {
        super("Intervene", 0, 0, 14.09f, 20, 0, 0);
    }

    public Intervene(float maxDamagePrevented, WarlordsEntity caster, WarlordsEntity target) {
        super("Intervene", 0, 0, 14.09f, 20, 0, 0);
        this.maxDamagePrevented = maxDamagePrevented;
        this.caster = caster;
        this.target = target;
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Protect the target ally, reducing\n" +
                "§7the damage they take by §e100%\n" +
                "§7and redirecting §e50% §7of the damage\n" +
                "§7they would have taken back to you.\n" +
                "§7You can protect the target for a maximum\n" +
                "§7of §c" + format(maxDamagePrevented) + " §7damage. You must remain within\n" +
                "§e" + breakRadius + " §7blocks of each other. Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7Has an initial cast range of §e" + radius + " §7blocks.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Intervened", "" + playersIntervened));
        info.add(new Pair<>("Carriers Intervened", "" + carriersIntervened));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        setDamagePrevented(0);

        for (WarlordsEntity veneTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .requireLineOfSightIntervene(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            playersIntervened++;
            if (veneTarget.hasFlag()) {
                carriersIntervened++;
            }
            wp.subtractEnergy(energyCost);
            // Green line / Sound
            Utils.playGlobalSound(wp.getLocation(), "warrior.intervene.impact", 1, 1);
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), veneTarget.getLocation(), ParticleEffect.VILLAGER_HAPPY);

            // New cooldown, both players have the same instance of intervene.
            Intervene tempIntervene = new Intervene(maxDamagePrevented, wp, veneTarget);

            // Removing all other intervenes
            wp.getCooldownManager().getCooldowns().removeIf(cd ->
                    cd.getCooldownClass() == Intervene.class &&
                            veneTarget.getCooldownManager().hasCooldown(cd.getCooldownObject()));

            veneTarget.getCooldownManager().getCooldowns().removeIf(cd -> {
                if (cd.getCooldownClass() == Intervene.class) {
                    cd.getFrom().sendMessage(WarlordsEntity.RECEIVE_ARROW_RED + " " +
                                    ChatColor.GRAY + cd.getFrom().getName() + "'s " +
                                    ChatColor.YELLOW + "Intervene " +
                                    ChatColor.GRAY + "has expired!"
                    );
                    veneTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED + " " +
                                    ChatColor.GRAY + cd.getFrom().getName() + "'s " +
                                    ChatColor.YELLOW + "Intervene " +
                                    ChatColor.GRAY + "has expired!"
                    );

                    return true;
                } else {
                    return false;
                }
            });

            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN + "§7 You are now protecting " +
                            veneTarget.getName() + " with your §eIntervene!"
            );

            veneTarget.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN + "§7 " +
                            wp.getName() + " is shielding you with their " +
                            ChatColor.YELLOW + "Intervene" +
                            ChatColor.GRAY + "!"
            );

            RegularCooldown<Intervene> interveneCooldownVeneTarget = new RegularCooldown<>(
                    name,
                    "VENE",
                    Intervene.class,
                    tempIntervene,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                        veneTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED + " " +
                                        ChatColor.GRAY + wp.getName() + "'s " +
                                        ChatColor.YELLOW + "Intervene " +
                                        ChatColor.GRAY + "has expired!"
                        );
                    },
                    duration * 20,
                    (cooldown, ticksLeft, counter) -> {
                        if (counter % 20 == 0 && ticksLeft > 0) {
                            int timeLeft = Math.round(ticksLeft / 20f);
                            if (timeLeft == 1) {
                                veneTarget.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN + " " +
                                        ChatColor.GRAY + wp.getName() + "'s §eIntervene §7will expire in §6" +
                                        timeLeft + "§7 second!"
                                );
                            } else {
                                veneTarget.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN + " " +
                                        ChatColor.GRAY + wp.getName() + "'s §eIntervene §7will expire in §6" +
                                        timeLeft + "§7 seconds!"
                                );
                            }
                        }
                    }
            );

            RegularCooldown<Intervene> interveneCooldownWp = new RegularCooldown<>(
                    name,
                    "VENE",
                    Intervene.class,
                    tempIntervene,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                        wp.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED + " " +
                                        ChatColor.GRAY + wp.getName() + "'s " +
                                        ChatColor.YELLOW + "Intervene " +
                                        ChatColor.GRAY + "has expired!"
                        );
                    },
                    duration * 20,
                    (cooldown, ticksLeft, counter) -> {
                        if (wp.isDead() || veneTarget.getLocation().distanceSquared(wp.getLocation()) > breakRadius * breakRadius) {
                            cooldown.setTicksLeft(0);
                            interveneCooldownVeneTarget.setTicksLeft(0);
                        }
                    }
            );

            wp.getCooldownManager().addCooldown(interveneCooldownWp);
            veneTarget.getCooldownManager().addCooldown(interveneCooldownVeneTarget);

            wp.getSpec().getBlue().setCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));
            wp.updateBlueItem();

            return true;
        }

        return false;
    }

    public float getDamagePrevented() {
        return damagePrevented;
    }

    public void setDamagePrevented(float damagePrevented) {
        this.damagePrevented = damagePrevented;
    }

    public void addDamagePrevented(float amount) {
        this.damagePrevented += amount;
    }

    public int getBreakRadius() {
        return breakRadius;
    }

    public void setBreakRadius(int breakRadius) {
        this.breakRadius = breakRadius;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public float getMaxDamagePrevented() {
        return maxDamagePrevented;
    }

    public void setMaxDamagePrevented(float maxDamagePrevented) {
        this.maxDamagePrevented = maxDamagePrevented;
    }

    public WarlordsEntity getCaster() {
        return caster;
    }

    public WarlordsEntity getTarget() {
        return target;
    }
}
