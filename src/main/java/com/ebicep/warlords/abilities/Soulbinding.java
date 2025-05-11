package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.spiritguard.SoulbindingWeaponBranch;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Soulbinding extends AbstractAbility implements PurpleAbilityIcon, Duration, CanReduceCooldowns, Heals<Soulbinding.HealingValues>, AbilityStats<Soulbinding, Soulbinding.SoulbindingStats> {

    private final HealingValues healingValues = new HealingValues();
    private final SoulbindingStats stats = new SoulbindingStats();
    private int tickDuration = 240;
    private float selfCooldownReduction = 1.5f;
    private float allyCooldownReduction = .75f;
    private int bindDuration = 60;
    private int radius = 8;
    private int maxAlliesHit = 2;

    public Soulbinding() {
        super(AbstractAbilityBuilder.create("soulbindingWeapon").pvp());
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Your melee attacks give enemies ")
                .text("BOUND", NamedTextColor.LIGHT_PURPLE)
                .text(" for ")
                .durationTicks(bindDuration)
                .text(". Against ")
                .text("BOUND", NamedTextColor.LIGHT_PURPLE)
                .text(" targets, your next Spirit Link will heal you for ")
                .heal(healingValues.selfHealing)
                .text(" health and ")
                .text(maxAlliesHit, NamedTextColor.BLUE)
                .text(" nearby allies for ")
                .heal(healingValues.allyHealing)
                .text(". Your next Fallen Souls will reduce the cooldown of all abilities by ")
                .durationSeconds(selfCooldownReduction)
                .text(". (")
                .durationSeconds(allyCooldownReduction)
                .text(" for ")
                .text(maxAlliesHit, NamedTextColor.BLUE)
                .text(" nearby allies). Both buffs may be activated for every melee hit. Lasts ")
                .durationTicks(tickDuration)
                .text(".")
                .emptyLine()
                .text("Successful soulbind procs will grant you ")
                .percent(25, AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" knockback resistance for ")
                .durationSeconds(1.2f)
                .text(" (Max ")
                .durationSeconds(3.6f)
                .text(").")
                .build();
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        activeSoulbinding(wp);

        return true;
    }

    public SoulbindingData activeSoulbinding(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "paladin.consecrate.activation", 2, 2);

        if (wp.isInPve()) {
            wp.getCooldownManager().limitCooldowns(PersistentCooldown.class, Soulbinding.SoulbindingData.class, 2);
        }
        SoulbindingData data = new SoulbindingData(this);
        wp.getCooldownManager().addCooldown(new PersistentCooldown<>(
                name,
                "SOUL",
                SoulbindingData.class,
                data,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    if (new CooldownFilter<>(cooldownManager, PersistentCooldown.class).filterCooldownClass(Soulbinding.SoulbindingData.class).stream().count() == 1) {
                        if (wp.getEntity() instanceof Player) {
                            ItemStack item = ((Player) wp.getEntity()).getInventory().getItem(0);
                            if (item != null) {
                                item.removeEnchantment(Enchantment.RESPIRATION);
                            }
                        }
                    }
                },
                tickDuration,
                soulbinding -> soulbinding.getSoulBindedPlayers().isEmpty(),
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.2, 0);
                        location.getWorld().spawnParticle(
                                Particle.WITCH,
                                location,
                                2,
                                0.2,
                                0,
                                0.2,
                                0.1,
                                null,
                                true
                        );
                    }
                    data.getSoulBindedPlayers().forEach(SoulBoundPlayer::decrementTimeLeft);
                    data.getSoulBindedPlayers().removeIf(soulBoundPlayer ->
                            soulBoundPlayer.getTimeLeft() == 0 || (soulBoundPlayer.isHitWithSoul() && soulBoundPlayer.isHitWithLink())
                    );
                })
        ) {
            @Override
            public void damageDoBeforeVariableSetFromAttacker(WarlordsDamageHealingEvent event) {
                WarlordsEntity wpAttacker = event.getSource();
                WarlordsEntity wpVictim = event.getWarlordsEntity();
                if (!event.getCause().isEmpty() || wpAttacker == wpVictim) {
                    return;
                }
                data.bindPlayer(wpAttacker, wpVictim);
            }

            @Override
            public PlayerNameData addSuffixFromSelf() {
                return new PlayerNameData(
                        Component.text("BOUND", NamedTextColor.LIGHT_PURPLE),
                        we -> data.getSoulBindedPlayers().stream().anyMatch(soulBoundPlayer -> soulBoundPlayer.getBoundPlayer() == we)
                );
            }
        });

        if (wp.getEntity() instanceof Player player) {
            ItemStack item = player.getInventory().getItem(0);
            if (item != null) {
                ItemMeta newItemMeta = item.getItemMeta();
                newItemMeta.addEnchant(Enchantment.RESPIRATION, 1, true);
                item.setItemMeta(newItemMeta);
            }
        }

        return data;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SoulbindingWeaponBranch(abilityTree, this);
    }

    public void addPlayersBinded() {
        stats.playersBinded++;
    }

    public void addSoulProcs() {
        stats.soulProcs++;
    }

    public void addLinkProcs() {
        stats.linkProcs++;
    }

    public void addSoulTeammatesCDReductions() {
        stats.soulTeammatesCDReductions++;
    }

    public void addLinkTeammatesHealed() {
        stats.linkTeammatesHealed++;
    }

    public int getBindDuration() {
        return bindDuration;
    }

    public void setBindDuration(int bindDuration) {
        this.bindDuration = bindDuration;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public float getSelfCooldownReduction() {
        return selfCooldownReduction;
    }

    public void setSelfCooldownReduction(float selfCooldownReduction) {
        this.selfCooldownReduction = selfCooldownReduction;
    }

    public float getAllyCooldownReduction() {
        return allyCooldownReduction;
    }

    public void setAllyCooldownReduction(float allyCooldownReduction) {
        this.allyCooldownReduction = allyCooldownReduction;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getMaxAlliesHit() {
        return maxAlliesHit;
    }

    public void setMaxAlliesHit(int maxAlliesHit) {
        this.maxAlliesHit = maxAlliesHit;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public SoulbindingStats getAbilityStats() {
        return stats;
    }

    public static class SoulbindingData {

        private final Soulbinding soulbinding;
        private final List<SoulBoundPlayer> soulBindedPlayers = new ArrayList<>();
        private final List<WarlordsEntity> playersProcedBySouls = new ArrayList<>();
        private final List<WarlordsEntity> playersProcedByLink = new ArrayList<>();

        public SoulbindingData(Soulbinding soulbinding) {
            this.soulbinding = soulbinding;
        }

        public void bindPlayer(WarlordsEntity wpAttacker, WarlordsEntity wpVictim) {
            soulbinding.addPlayersBinded();
            if (hasBoundPlayer(wpVictim)) {
                getSoulBindedPlayers()
                        .stream()
                        .filter(p -> p.getBoundPlayer() == wpVictim)
                        .forEach(boundPlayer -> {
                            boundPlayer.setHitWithSoul(false);
                            boundPlayer.setHitWithLink(false);
                            boundPlayer.setTicksLeft(soulbinding.bindDuration);
                        });
            } else {
                wpVictim.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED
                        .append(Component.text(" You have been bound by " + wpAttacker.getName() + "'s ", NamedTextColor.GRAY))
                        .append(Component.text("Soulbinding Weapon", NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text("!", NamedTextColor.GRAY))
                );
                wpAttacker.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                        .append(Component.text(" Your ", NamedTextColor.GRAY))
                        .append(Component.text("Soulbinding Weapon", NamedTextColor.LIGHT_PURPLE))
                        .append(Component.text(" has bound " + wpVictim.getName() + "!", NamedTextColor.GRAY))
                );
                getSoulBindedPlayers().add(new SoulBoundPlayer(wpVictim, soulbinding.bindDuration));
                Utils.playGlobalSound(wpVictim.getLocation(), "shaman.earthlivingweapon.activation", 2, 1);
            }
        }

        public boolean hasBoundPlayer(WarlordsEntity warlordsPlayer) {
            for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
                if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                    return true;
                }
            }
            return false;
        }

        public List<SoulBoundPlayer> getSoulBindedPlayers() {
            return soulBindedPlayers;
        }

        public boolean hasBoundPlayerSoul(WarlordsEntity warlordsPlayer) {
            for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
                if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                    if (!soulBindedPlayer.isHitWithSoul()) {
                        soulBindedPlayer.setHitWithSoul(true);
                        playersProcedBySouls.add(warlordsPlayer);
                        soulbinding.addSoulProcs();
                        return true;
                    }
                    break;
                }
            }
            return false;
        }

        public boolean hasBoundPlayerLink(WarlordsEntity warlordsPlayer) {
            for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
                if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                    if (!soulBindedPlayer.isHitWithLink()) {
                        soulBindedPlayer.setHitWithLink(true);
                        playersProcedByLink.add(warlordsPlayer);
                        soulbinding.addLinkProcs();
                        return true;
                    }
                    break;
                }
            }
            return false;
        }

        public List<WarlordsEntity> getAllProcedPlayers() {
            List<WarlordsEntity> procedPlayers = new ArrayList<>();
            procedPlayers.addAll(playersProcedBySouls);
            procedPlayers.addAll(playersProcedByLink);
            return procedPlayers;
        }

        public Soulbinding getSoulbinding() {
            return soulbinding;
        }

        public List<WarlordsEntity> getPlayersProcedBySouls() {
            return playersProcedBySouls;
        }

        public List<WarlordsEntity> getPlayersProcedByLink() {
            return playersProcedByLink;
        }
    }

    public static class SoulBoundPlayer {
        private WarlordsEntity boundPlayer;
        private int ticksLeft;
        private boolean hitWithLink;
        private boolean hitWithSoul;

        public SoulBoundPlayer(WarlordsEntity boundPlayer, int timeLeft) {
            this.boundPlayer = boundPlayer;
            this.ticksLeft = timeLeft;
        }

        public WarlordsEntity getBoundPlayer() {
            return boundPlayer;
        }

        public void setBoundPlayer(WarlordsEntity boundPlayer) {
            this.boundPlayer = boundPlayer;
        }

        public float getTimeLeft() {
            return ticksLeft;
        }

        public void setTicksLeft(int timeLeft) {
            this.ticksLeft = timeLeft;
        }

        public void decrementTimeLeft() {
            this.ticksLeft--;
        }

        public boolean isHitWithLink() {
            return hitWithLink;
        }

        public void setHitWithLink(boolean hitWithLink) {
            this.hitWithLink = hitWithLink;
        }

        public boolean isHitWithSoul() {
            return hitWithSoul;
        }

        public void setHitWithSoul(boolean hitWithSoul) {
            this.hitWithSoul = hitWithSoul;
        }
    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.SetValue allyHealing = new Value.SetValue(200);
        private final Value.SetValue selfHealing = new Value.SetValue(300);
        private final List<Value> values = List.of(allyHealing, selfHealing);

        public Value.SetValue getAllyHealing() {
            return allyHealing;
        }

        public Value.SetValue getSelfHealing() {
            return selfHealing;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class SoulbindingStats extends AbstractAbilityStats<Soulbinding, SoulbindingStats> {

        @Field("targets_binded")
        private int playersBinded = 0;

        @Field("soul_procs")
        private int soulProcs = 0;

        @Field("link_procs")
        private int linkProcs = 0;

        @Field("soul_teammates_cd_reductions")
        private int soulTeammatesCDReductions = 0;

        @Field("link_teammates_healed")
        private int linkTeammatesHealed = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Binded", playersBinded));
            statsDisplay.add(new AbilityStatDisplay("Soul Procs", soulProcs));
            statsDisplay.add(new AbilityStatDisplay("Soul Teammates CD Reductions", soulTeammatesCDReductions));
            statsDisplay.add(new AbilityStatDisplay("Link Procs", linkProcs));
            statsDisplay.add(new AbilityStatDisplay("Link Teammates Healed", linkTeammatesHealed));
            return statsDisplay;
        }

        @Override
        public SoulbindingStats merge(SoulbindingStats other, int multiplier) {
            SoulbindingStats stats = super.merge(other, multiplier);
            stats.playersBinded = this.playersBinded + other.playersBinded * multiplier;
            stats.soulProcs = this.soulProcs + other.soulProcs * multiplier;
            stats.linkProcs = this.linkProcs + other.linkProcs * multiplier;
            stats.soulTeammatesCDReductions = this.soulTeammatesCDReductions + other.soulTeammatesCDReductions * multiplier;
            stats.linkTeammatesHealed = this.linkTeammatesHealed + other.linkTeammatesHealed * multiplier;
            return stats;
        }

        @Override
        public Class<SoulbindingStats> getClazz() {
            return SoulbindingStats.class;
        }

        @Override
        public SoulbindingStats create() {
            return new SoulbindingStats();
        }
    }
}
