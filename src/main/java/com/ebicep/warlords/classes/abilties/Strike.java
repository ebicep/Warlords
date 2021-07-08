package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.paladin.specs.avenger.Avenger;
import com.ebicep.warlords.classes.paladin.specs.crusader.Crusader;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;
import com.ebicep.warlords.classes.warrior.specs.berserker.Berserker;
import com.ebicep.warlords.classes.warrior.specs.defender.Defender;
import com.ebicep.warlords.classes.warrior.specs.revenant.Revenant;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.ClassesSkillBoosts;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.Utils;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Strike extends AbstractAbility {

    public Strike(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    @Override
    public void updateDescription(Player player) {
        Classes selected = Classes.getSelected(player);
        if (selected == Classes.AVENGER) {
            description = "§7Strike the targeted enemy player,\n" +
                    "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                    "§7and removing §e6 §7energy.";
        } else if (selected == Classes.CRUSADER) {
            description = "§7Strike the targeted enemy player,\n" +
                    "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " damage\n" +
                    "§7and restoring §e24 §7energy to two nearby\n" +
                    "§7within §e10 §7blocks.";
        } else if (selected == Classes.PROTECTOR) {
            int boost = Classes.getSelectedBoost(player) == ClassesSkillBoosts.PROTECTOR_STRIKE ? 120 : 100;
            int selfBoost = Classes.getSelectedBoost(player) == ClassesSkillBoosts.PROTECTOR_STRIKE ? 70 : 50;
            description = "§7Strike the targeted enemy player,\n" +
                    "§7causing §c261 §7- §c352 §7damage\n" +
                    "§7and healing two nearby allies for\n" +
                    "§a" + boost + "% §7of the damage done. Also\n" +
                    "§7heals yourself by §a" + selfBoost + "% §7of the\n" +
                    "§7damage done.";
        } else if (selected == Classes.BERSERKER) {
            description = "§7Strike the targeted enemy player,\n" +
                    "§7causing §c" + Math.floor(-minDamageHeal) + " §7- §c" + Math.floor(-maxDamageHeal) + " §7damage\n" +
                    "§7and §cwounding §7them for §63 §7seconds.\n" +
                    "§7A wounded player receives §c35% §7less\n" +
                    "§7healing for the duration of the effect.";
        } else if (selected == Classes.DEFENDER) {
            description = "§7Strike the targeted enemy player,\n" +
                    "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                    "§7and §cwounding §7them for §63 §7seconds.\n" +
                    "§7A wounded player receives §c25% §7less\n" +
                    "§7healing for the duration of the effect.";
        } else if (selected == Classes.REVENANT) {
            description = "§7Strike the targeted enemy player,\n" +
                    "§7causing §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage\n" +
                    "§7and §ccrippling §7them for §63 §7seconds.\n" +
                    "§7A §ccrippled §7player deals §c12.5% §7less\n" +
                    "§7damage for the duration of the effect.";
        }
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {
        PlayerFilter.entitiesAround(wp, 4, 4, 4)
            .aliveEnemiesOf(wp)
            .closestFirst(wp)
            .requireLineOfSight(wp)
            .first((nearPlayer) -> {
            if (Utils.getLookingAt(player, nearPlayer.getEntity()) && Utils.hasLineOfSight(player, nearPlayer.getEntity())) {
                PacketPlayOutAnimation playOutAnimation = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 0);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(playOutAnimation);
                wp.subtractEnergy(energyCost);

                //PALADIN
                if (wp.getSpec() instanceof Avenger || wp.getSpec() instanceof Crusader || wp.getSpec() instanceof Protector) {
                    for (Player player1 : player.getWorld().getPlayers()) {
                        player1.playSound(player.getLocation(), "paladin.paladinstrike.activation", 2, 1);
                    }
                    //check consecrate then boost dmg
                    if (wp.getSpec() instanceof Avenger) {
                        if (standingOnConsecrate(player, nearPlayer.getEntity())) {
                            nearPlayer.addHealth(wp, name, (minDamageHeal * 1.2f), (maxDamageHeal * 1.2f), critChance, critMultiplier);
                        } else {
                            nearPlayer.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        }
                        nearPlayer.subtractEnergy(6);
                        if (!wp.getCooldownManager().getCooldown(AvengersWrath.class).isEmpty()) {
                            for (WarlordsPlayer nearNearPlayer : PlayerFilter
                                    .entitiesAround(nearPlayer, 5, 3, 5)
                                    .aliveEnemiesOf(wp)
                                    .closestFirst(nearPlayer)
                                    .excluding(nearPlayer)
                                    .limit(2)
                            ) {
                                System.out.println("NEAR NEAR HIT " + nearNearPlayer);
                                //checking if player is in consecrate
                                if (standingOnConsecrate(player, nearNearPlayer.getEntity())) {
                                    nearNearPlayer.addHealth(wp, name, minDamageHeal * 1.2f, maxDamageHeal * 1.2f, critChance, critMultiplier);
                                } else {
                                    nearNearPlayer.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                                }
                                nearNearPlayer.subtractEnergy(6);
                            }
                        }
                    } else if (wp.getSpec() instanceof Crusader) {
                        //checking if player is in consecrate
                        if (standingOnConsecrate(player, nearPlayer)) {
                            nearPlayer.addHealth(wp, name, minDamageHeal * 1.15f, maxDamageHeal * 1.15f, critChance, critMultiplier);
                        } else {
                            nearPlayer.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        }
                        //reloops near players to give energy to
                        PlayerFilter.entitiesAround(wp, 10.0D, 10.0D, 10.0D)
                            .aliveTeammatesOfExcludingSelf(wp)
                            .closestFirst(wp)
                            .limit(2)
                            .forEach((nearTeamPlayer) ->
                                nearTeamPlayer.addEnergy(wp, name, 24)
                            );
                    } else if (wp.getSpec() instanceof Protector) {
                        if (standingOnConsecrate(player, nearPlayer)) {
                            nearPlayer.addHealth(wp, name, minDamageHeal * 1.15f, maxDamageHeal * 1.15f, critChance, critMultiplier);
                        } else {
                            nearPlayer.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
                        }
                    }

                } else if (wp.getSpec() instanceof Berserker || wp.getSpec() instanceof Defender || wp.getSpec() instanceof Revenant) {
                    Warlords.getPlayer(nearPlayer).getCooldownManager().addCooldown(Strike.this.getClass(),
                            new Strike(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier),
                            wp.getSpec() instanceof Revenant ? "CRIP" : "WND", 3, wp, CooldownTypes.DEBUFF);

                    nearPlayer.addHealth(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);

                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        player1.playSound(player.getLocation(), "warrior.mortalstrike.impact", 2, 1);
                    }
                }
            }
        });
    }

    private boolean standingOnConsecrate(Player owner, WarlordsPlayer standing) {
        return standingOnConsecrate(owner, standing.getEntity());
    }
    private boolean standingOnConsecrate(Player owner, LivingEntity standing) {
        for (Entity entity : owner.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Consecrate - " + owner.getName())) {
                if (entity.getLocation().clone().add(0, 2, 0).distanceSquared(standing.getLocation()) < 5 * 5.25) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
}
