package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.LinkedCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.defender.InterveneBranch;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class Intervene extends AbstractAbility implements BlueAbilityIcon, Duration {

    public int playersIntervened = 0;
    public int carriersIntervened = 0;

    private int tickDuration = 100;
    private float maxDamagePrevented = 3600;
    private int damageReduction = 50;
    private int radius = 10;
    private int breakRadius = 15;
    private int maxTargets = 1;

    public Intervene() {
        super("Intervene", 14, 20);
    }

    @Override
    public void updateDescription(Player player) {
        if (inPve) {
            description = ComponentBuilder
                    .create("Protect up to 2 target allies, reducing the damage they take by ")
                    .text("100%", NamedTextColor.YELLOW)
                    .text(" and redirecting ")
                    .text(damageReduction + "%", NamedTextColor.YELLOW)
                    .text(" of the damage they would have taken back to you. You can protect the target for a maximum of ")
                    .text(format(maxDamagePrevented), NamedTextColor.RED)
                    .text(" damage. You must remain within ")
                    .text(breakRadius, NamedTextColor.YELLOW)
                    .text(" blocks of each other. For every 100 damage prevented, increase your damage by 1%. Lasts ")
                    .text(format(tickDuration / 20f), NamedTextColor.GOLD)
                    .text(" seconds.\n\nHas an initial cast range of ")
                    .text(radius, NamedTextColor.YELLOW)
                    .text(" blocks.")
                    .build();
        } else {
            description = Component.text("Protect the target ally, reducing the damage they take by ")
                                   .append(Component.text("100%", NamedTextColor.YELLOW))
                                   .append(Component.text(" and redirecting "))
                                   .append(Component.text(damageReduction + "%", NamedTextColor.YELLOW))
                                   .append(Component.text(" of the damage they would have taken back to you. You can protect the target for a maximum of "))
                                   .append(Component.text(format(maxDamagePrevented), NamedTextColor.RED))
                                   .append(Component.text(" damage. You must remain within "))
                                   .append(Component.text(breakRadius, NamedTextColor.YELLOW))
                                   .append(Component.text(" blocks of each other. Lasts "))
                                   .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                                   .append(Component.text(" seconds.\n\nHas an initial cast range of "))
                                   .append(Component.text(radius, NamedTextColor.YELLOW))
                                   .append(Component.text(" blocks."));
        }

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
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        List<InterveneData> venes = new ArrayList<>();

        for (WarlordsEntity veneTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOfExcludingSelf(wp)
                .requireLineOfSightIntervene(wp)
                .lookingAtFirst(wp)
                .limit(maxTargets)
        ) {
            playersIntervened++;
            if (veneTarget.hasFlag()) {
                carriersIntervened++;
            }

            // Green line / Sound
            Utils.playGlobalSound(wp.getLocation(), "warrior.intervene.impact", 1, 1);
            EffectUtils.playParticleLinkAnimation(wp.getLocation(), veneTarget.getLocation(), Particle.VILLAGER_HAPPY);

            // New cooldown, both players have the same instance of intervene.
            InterveneData data = new InterveneData(this, wp, veneTarget, maxDamagePrevented);
            venes.add(data);
            // Removing all other intervenes
            wp.getCooldownManager().removeIf(cd ->
                    cd.getCooldownClass() == Intervene.class &&
                            veneTarget.getCooldownManager().hasCooldown(cd.getCooldownObject()));

            veneTarget.getCooldownManager().removeIf(cd -> {
                if (cd.getCooldownClass() == Intervene.class) {
                    cd.getFrom().sendMessage(WarlordsEntity.RECEIVE_ARROW_RED
                            .append(Component.text(" " + cd.getFrom().getName() + "'s ", NamedTextColor.GRAY))
                            .append(Component.text("Intervene", NamedTextColor.YELLOW))
                            .append(Component.text(" has expired!", NamedTextColor.GRAY))
                    );
                    veneTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED
                            .append(Component.text(" " + cd.getFrom().getName() + "'s ", NamedTextColor.GRAY))
                            .append(Component.text("Intervene", NamedTextColor.YELLOW))
                            .append(Component.text(" has expired!", NamedTextColor.GRAY))
                    );
                    return true;
                } else {
                    return false;
                }
            });

            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" You are now protecting " + veneTarget.getName() + " with your ", NamedTextColor.GRAY))
                    .append(Component.text("Intervene", NamedTextColor.YELLOW))
                    .append(Component.text("!", NamedTextColor.GRAY))
            );
            veneTarget.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" " + wp.getName() + " is shielding you with their ", NamedTextColor.GRAY))
                    .append(Component.text("Intervene", NamedTextColor.YELLOW))
                    .append(Component.text("!", NamedTextColor.GRAY))
            );

            Runnable wpInterference;
            Runnable veneTargetInterference;
            if (pveMasterUpgrade2) {
                wpInterference = wp.addSpeedModifier(wp, "Interference - " + veneTarget.getName(), 25, tickDuration, "VENE"); //TODO test toDisable logic
                veneTargetInterference = veneTarget.addSpeedModifier(wp, "Interference - " + veneTarget.getName(), 25, tickDuration, "VENE");
            } else {
                wpInterference = null;
                veneTargetInterference = null;
            }

            LinkedCooldown<InterveneData> interveneCooldown = new LinkedCooldown<>(
                    name,
                    "VENE",
                    InterveneData.class,
                    data,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                        if (!Objects.equals(cooldownManager.getWarlordsEntity(), wp)) {
                            return;
                        }

                        if (wpInterference != null && veneTargetInterference != null) {
                            wpInterference.run();
                            veneTargetInterference.run();
                        }

                        wp.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED
                                .append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                                .append(Component.text("Intervene", NamedTextColor.YELLOW))
                                .append(Component.text(" has expired!", NamedTextColor.GRAY))
                        );
                        veneTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED
                                .append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                                .append(Component.text("Intervene", NamedTextColor.YELLOW))
                                .append(Component.text(" has expired!", NamedTextColor.GRAY))
                        );
                    },
                    tickDuration,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (wp.isDead() || veneTarget.getLocation().distanceSquared(wp.getLocation()) > breakRadius * breakRadius) {
                            cooldown.setTicksLeft(0);
                            return;
                        }
                        if (ticksElapsed % 20 == 0) {
                            int timeLeft = Math.round(ticksLeft / 20f);
                            veneTarget.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                    .append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                                    .append(Component.text("Intervene", NamedTextColor.YELLOW))
                                    .append(Component.text(" will expire in ", NamedTextColor.GRAY))
                                    .append(Component.text(timeLeft, NamedTextColor.GOLD))
                                    .append(Component.text(" second" + (timeLeft == 1 ? "!" : "s!"), NamedTextColor.GRAY))
                            );
                        }
                    }),
                    veneTarget
            ) {
                @Override
                public void onInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    event.getWarlordsEntity().getCooldownManager().queueUpdatePlayerNames();
                }

                @Override
                public void multiplyKB(Vector currentVector) {
                    if (pveMasterUpgrade2) {
                        currentVector.zero();
                    }
                }

                @Override
                public PlayerNameData addPrefixFromOther() {
                    return new PlayerNameData(
                            Component.text((int) (data.getMaxDamagePrevented() - data.getDamagePrevented()), NamedTextColor.GOLD),
                            we -> we.isTeammate(wp)
                    );
                }

                @Nonnull
                @Override
                public Component getDebugMessage() {
                    return Component.textOfChildren(
                            Component.text(NumberFormat.formatOptionalTenths(data.getDamagePrevented()), NamedTextColor.YELLOW),
                            Component.text("/", NamedTextColor.GRAY),
                            Component.text(NumberFormat.formatOptionalTenths(data.getMaxDamagePrevented()), NamedTextColor.YELLOW),
                            Component.text(" - ", NamedTextColor.GRAY),
                            Component.text(NumberFormat.formatOptionalTenths(damageReduction), NamedTextColor.GREEN)
                    );
                }
            };

            wp.getCooldownManager().addCooldown(interveneCooldown);
            veneTarget.getCooldownManager().addCooldown(interveneCooldown);

            Bukkit.getPluginManager().callEvent(new WarlordsAbilityTargetEvent.WarlordsBlueAbilityTargetEvent(wp, name, veneTarget));

        }

        if (inPve) {
            wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name + " Damage",
                    null,
                    Intervene.class,
                    new Intervene(),
                    wp,
                    CooldownTypes.BUFF,
                    cooldownManager -> {
                    },
                    tickDuration
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return (float) (currentDamageValue * (1 + venes.stream().mapToDouble(InterveneData::getDamagePrevented).sum() / 100 * .01));
                }
            });
        }

        return !venes.isEmpty();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new InterveneBranch(abilityTree, this);
    }

    public float getMaxDamagePrevented() {
        return maxDamagePrevented;
    }

    public void setMaxDamagePrevented(float maxDamagePrevented) {
        this.maxDamagePrevented = maxDamagePrevented;
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

    public int getDamageReduction() {
        return damageReduction;
    }

    public void setDamageReduction(int damageReduction) {
        this.damageReduction = damageReduction;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getMaxTargets() {
        return maxTargets;
    }

    public void setMaxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
    }

    public static class InterveneData {

        private final Intervene intervene;
        private final WarlordsEntity caster;
        private final WarlordsEntity target;
        private final float maxDamagePrevented;
        private float damagePrevented = 0;

        public InterveneData(Intervene intervene, WarlordsEntity caster, WarlordsEntity target, float maxDamagePrevented) {
            this.intervene = intervene;
            this.caster = caster;
            this.target = target;
            this.maxDamagePrevented = maxDamagePrevented;
        }

        public Intervene getIntervene() {
            return intervene;
        }

        public WarlordsEntity getCaster() {
            return caster;
        }

        public WarlordsEntity getTarget() {
            return target;
        }

        public float getMaxDamagePrevented() {
            return maxDamagePrevented;
        }

        public float getDamagePrevented() {
            return damagePrevented;
        }

        public void addDamagePrevented(float amount) {
            this.damagePrevented += amount;
        }

    }

}
