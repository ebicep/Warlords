package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Optional;


public class Intervene extends AbstractAbility {

    private float damagePrevented = 0;

    private final int duration = 5;
    private float maxDamagePrevented = 3600;
    private int radius = 10;
    private int breakRadius = 15;

    public Intervene() {
        super("Intervene", 0, 0, 14.09f, 20, 0, 0);
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
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        setDamagePrevented(0);

        for (WarlordsPlayer vt : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .requireLineOfSightIntervene(wp)
                .lookingAtFirst(wp)
                .limit(1)
        ) {
            // Green line / Sound
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), vt.getLocation(), ParticleEffect.VILLAGER_HAPPY);
            Utils.playGlobalSound(wp.getLocation(), "warrior.intervene.impact", 1, 1);

            // New cooldown, both players have the same instance of intervene.
            Intervene tempIntervene = new Intervene();

            // Removing all other intervenes
            wp.getCooldownManager().getCooldowns().removeIf(cd ->
                    cd.getCooldownClass() == Intervene.class &&
                    vt.getCooldownManager().hasCooldown(cd.getCooldownObject()));

            vt.getCooldownManager().getCooldowns().removeIf(cd -> {
                if (cd.getCooldownClass() == Intervene.class) {
                    cd.getFrom().sendMessage(
                        WarlordsPlayer.RECEIVE_ARROW_RED + " " +
                                cd.getFrom().getName() + "'s " +
                                ChatColor.YELLOW + "Intervene " +
                                ChatColor.GRAY + "has expired!"
                    );
                    vt.sendMessage(
                            WarlordsPlayer.RECEIVE_ARROW_RED + " " +
                                    cd.getFrom().getName() + "'s " +
                                    ChatColor.YELLOW + "Intervene " +
                                    ChatColor.GRAY + "has expired!"
                    );

                    return true;
                } else {
                    return false;
                }
            });

            wp.sendMessage(
                    WarlordsPlayer.GIVE_ARROW_GREEN + "§7 You are now protecting " +
                            vt.getName() + " with your §eIntervene!"
            );
            wp.getCooldownManager().addRegularCooldown(
                    name,
                    "VENE",
                    Intervene.class,
                    tempIntervene,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {},
                    duration * 20
            );

            vt.sendMessage(
                    WarlordsPlayer.GIVE_ARROW_GREEN + "§7 " +
                            wp.getName() + " is shielding you with their " +
                            ChatColor.YELLOW + "Intervene" +
                            ChatColor.GRAY + "!"
            );
            vt.getCooldownManager().addRegularCooldown(
                    name,
                    "VENE",
                    Intervene.class,
                    tempIntervene,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {},
                    duration * 20
            );

            wp.getSpec().getBlue().setCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));
            wp.updateBlueItem();
            wp.subtractEnergy(energyCost);

            new GameRunnable(wp.getGame()) {
                @Override
                public void run() {
                    Optional<RegularCooldown> optionalRegularCooldown = new CooldownFilter<>(vt, RegularCooldown.class).filterCooldownObject(tempIntervene).findFirst();
                    if (optionalRegularCooldown.isPresent()) {
                        RegularCooldown interveneRegularCooldown = optionalRegularCooldown.get();
                        if (interveneRegularCooldown.getTicksLeft() <= 20)
                            vt.sendMessage(
                                    WarlordsPlayer.GIVE_ARROW_GREEN + " " +
                                            ChatColor.GRAY + wp.getName() + "'s §eIntervene §7will expire in §6" +
                                            (int) (interveneRegularCooldown.getTicksLeft() / 20 + .5) + "§7 second!"
                            );
                        else
                            vt.sendMessage(
                                    WarlordsPlayer.GIVE_ARROW_GREEN + " " +
                                            ChatColor.GRAY + wp.getName() + "'s §eIntervene §7will expire in §6" +
                                            (int) (interveneRegularCooldown.getTicksLeft() / 20 + .5) + "§7 seconds!"
                            );
                    } else {
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 20);

            new GameRunnable(wp.getGame()) {
                @Override
                public void run() {
                    Optional<RegularCooldown> optionalRegularCooldown = new CooldownFilter<>(vt, RegularCooldown.class).filterCooldownObject(tempIntervene).findFirst();
                    if (wp.isDead() ||
                            tempIntervene.damagePrevented >= (maxDamagePrevented / 2) ||
                            !optionalRegularCooldown.isPresent() ||
                            vt.getLocation().distanceSquared(optionalRegularCooldown.get().getFrom().getEntity().getLocation()) > breakRadius * breakRadius
                    ) {
                        wp.sendMessage(
                                WarlordsPlayer.RECEIVE_ARROW_RED + " " +
                                        ChatColor.GRAY + wp.getName() + "'s " +
                                        ChatColor.YELLOW + "Intervene " +
                                        ChatColor.GRAY + "has expired!"
                        );
                        wp.getCooldownManager().removeCooldown(tempIntervene);

                        vt.sendMessage(
                                WarlordsPlayer.RECEIVE_ARROW_RED + " " +
                                        ChatColor.GRAY + wp.getName() + "'s " +
                                        ChatColor.YELLOW + "Intervene " +
                                        ChatColor.GRAY + "has expired!"
                        );
                        vt.getCooldownManager().removeCooldown(tempIntervene);

                        this.cancel();
                    }
                }

            }.runTaskTimer(0, 0);

            return true;
        }

        return false;
    }

    public void setDamagePrevented(float damagePrevented) {
        this.damagePrevented = damagePrevented;
    }

    public float getDamagePrevented() {
        return damagePrevented;
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
}
