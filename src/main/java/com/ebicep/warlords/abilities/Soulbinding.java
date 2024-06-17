package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.CanReduceCooldowns;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.spiritguard.SoulbindingWeaponBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Soulbinding extends AbstractAbility implements PurpleAbilityIcon, Duration, CanReduceCooldowns {

    public int playersBinded = 0;
    public int soulProcs = 0;
    public int linkProcs = 0;
    public int soulTeammatesCDReductions = 0;
    public int linkTeammatesHealed = 0;
    private final List<SoulBoundPlayer> soulBindedPlayers = new ArrayList<>();
    private final List<WarlordsEntity> playersProcedBySouls = new ArrayList<>();
    private final List<WarlordsEntity> playersProcedByLink = new ArrayList<>();
    private int tickDuration = 240;
    private float selfCooldownReduction = 1.5f;
    private int bindDuration = 40;
    private int radius = 8;
    private int maxAlliesHit = 2;
    private int selfHealing = 400;
    private int allyHealing = 300;

    public Soulbinding() {
        super("Soulbinding Weapon", 0, 0, 21.92f, 30, 0, 100);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Your melee attacks ")
                               .append(Component.text("BIND", NamedTextColor.LIGHT_PURPLE))
                               .append(Component.text(" enemies for "))
                               .append(Component.text(format(bindDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Against "))
                               .append(Component.text("BOUND", NamedTextColor.LIGHT_PURPLE))
                               .append(Component.text(" targets, your next Spirit Link will heal you for "))
                               .append(Component.text(selfHealing, NamedTextColor.GREEN))
                               .append(Component.text(" health and "))
                               .append(Component.text(maxAlliesHit, NamedTextColor.YELLOW))
                               .append(Component.text(" nearby allies for "))
                               .append(Component.text(allyHealing, NamedTextColor.GREEN))
                               .append(Component.text(". Your next Fallen Souls will reduce the cooldown of all abilities by "))
                               .append(Component.text(format(selfCooldownReduction), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. ("))
                               .append(Component.text("1", NamedTextColor.GOLD))
                               .append(Component.text(" second for "))
                               .append(Component.text(maxAlliesHit, NamedTextColor.YELLOW))
                               .append(Component.text(" nearby allies). Both buffs may be activated for every melee hit. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."))
                               .append(Component.newline())
                               .append(Component.text("Successful soulbind procs will grant you "))
                               .append(Component.text("25%", NamedTextColor.GOLD))
                               .append(Component.text(" knockback resistance for "))
                               .append(Component.text("1.2", NamedTextColor.GOLD))
                               .append(Component.text(" seconds (Max "))
                               .append(Component.text("3.6 ", NamedTextColor.GOLD))
                               .append(Component.text("seconds)."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Binded", "" + playersBinded));
        info.add(new Pair<>("Soul Procs", "" + soulProcs));
        info.add(new Pair<>("Soul Teammates CD Reductions", "" + soulTeammatesCDReductions));
        info.add(new Pair<>("Link Procs", "" + linkProcs));
        info.add(new Pair<>("Link Teammates Healed", "" + linkTeammatesHealed));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        activeSoulbinding(wp);

        return true;
    }

    public Soulbinding activeSoulbinding(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "paladin.consecrate.activation", 2, 2);

        Soulbinding tempSoulBinding = new Soulbinding();
        tempSoulBinding.setMaxAlliesHit(maxAlliesHit);
        tempSoulBinding.setRadius(radius);
        tempSoulBinding.setInPve(inPve);
        tempSoulBinding.setPveMasterUpgrade(pveMasterUpgrade);
        tempSoulBinding.setPveMasterUpgrade2(pveMasterUpgrade2);
        if (wp.isInPve()) {
            wp.getCooldownManager().limitCooldowns(PersistentCooldown.class, Soulbinding.class, 2);
        }
        wp.getCooldownManager().addCooldown(new PersistentCooldown<>(
                name,
                "SOUL",
                Soulbinding.class,
                tempSoulBinding,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    if (new CooldownFilter<>(cooldownManager, PersistentCooldown.class).filterCooldownClass(Soulbinding.class).stream().count() == 1) {
                        if (wp.getEntity() instanceof Player) {
                            ItemStack item = ((Player) wp.getEntity()).getInventory().getItem(0);
                            if (item != null) {
                                item.removeEnchantment(Enchantment.OXYGEN);
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
                                Particle.SPELL_WITCH,
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
                    tempSoulBinding.getSoulBindedPlayers().forEach(SoulBoundPlayer::decrementTimeLeft);
                    tempSoulBinding.getSoulBindedPlayers().removeIf(soulBoundPlayer ->
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
                tempSoulBinding.bindPlayer(wpAttacker, wpVictim);
            }

            @Override
            public PlayerNameData addSuffixFromSelf() {
                return new PlayerNameData(
                        Component.text("BOUND", NamedTextColor.LIGHT_PURPLE),
                        we -> tempSoulBinding.getSoulBindedPlayers().stream().anyMatch(soulBoundPlayer -> soulBoundPlayer.getBoundPlayer() == we)
                );
            }
        });

        if (wp.getEntity() instanceof Player player) {
            ItemStack item = player.getInventory().getItem(0);
            if (item != null) {
                ItemMeta newItemMeta = item.getItemMeta();
                newItemMeta.addEnchant(Enchantment.OXYGEN, 1, true);
                item.setItemMeta(newItemMeta);
            }
        }

        return tempSoulBinding;
    }

    public List<SoulBoundPlayer> getSoulBindedPlayers() {
        return soulBindedPlayers;
    }

    public void bindPlayer(WarlordsEntity wpAttacker, WarlordsEntity wpVictim) {
        addPlayersBinded();
        if (hasBoundPlayer(wpVictim)) {
            getSoulBindedPlayers()
                    .stream()
                    .filter(p -> p.getBoundPlayer() == wpVictim)
                    .forEach(boundPlayer -> {
                        boundPlayer.setHitWithSoul(false);
                        boundPlayer.setHitWithLink(false);
                        boundPlayer.setTicksLeft(bindDuration);
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
            getSoulBindedPlayers().add(new SoulBoundPlayer(wpVictim, bindDuration));
            Utils.playGlobalSound(wpVictim.getLocation(), "shaman.earthlivingweapon.activation", 2, 1);
        }
    }

    public void addPlayersBinded() {
        playersBinded++;
    }

    public boolean hasBoundPlayer(WarlordsEntity warlordsPlayer) {
        for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
            if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SoulbindingWeaponBranch(abilityTree, this);
    }

    public void addSoulProcs() {
        soulProcs++;
    }

    public void addLinkProcs() {
        linkProcs++;
    }

    public void addSoulTeammatesCDReductions() {
        soulTeammatesCDReductions++;
    }

    public void addLinkTeammatesHealed() {
        linkTeammatesHealed++;
    }

    public boolean hasBoundPlayerSoul(WarlordsEntity warlordsPlayer) {
        for (SoulBoundPlayer soulBindedPlayer : soulBindedPlayers) {
            if (soulBindedPlayer.getBoundPlayer() == warlordsPlayer) {
                if (!soulBindedPlayer.isHitWithSoul()) {
                    soulBindedPlayer.setHitWithSoul(true);
                    playersProcedBySouls.add(warlordsPlayer);
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
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public int getBindDuration() {
        return bindDuration;
    }

    public void setBindDuration(int bindDuration) {
        this.bindDuration = bindDuration;
    }

    public List<WarlordsEntity> getAllProcedPlayers() {
        List<WarlordsEntity> procedPlayers = new ArrayList<>();
        procedPlayers.addAll(playersProcedBySouls);
        procedPlayers.addAll(playersProcedByLink);
        return procedPlayers;
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

    public int getSelfHealing() {
        return selfHealing;
    }

    public void setSelfHealing(int selfHealing) {
        this.selfHealing = selfHealing;
    }

    public int getAllyHealing() {
        return allyHealing;
    }

    public void setAllyHealing(int allyHealing) {
        this.allyHealing = allyHealing;
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
}
