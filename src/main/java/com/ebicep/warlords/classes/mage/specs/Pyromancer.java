package com.ebicep.warlords.classes.mage.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;

public class Pyromancer extends AbstractMage {

    public Pyromancer() {
        super(
                "Pyromancer",
                5200,
                305,
                20,
                14,
                0,
                new Fireball(),
                new FlameBurst(),
                new TimeWarp(),
                new ArcaneShield(),
                new Inferno()
        );
    }

//    @Override
//    public List<TextComponent> getFormattedData() {
////        Fireball fireball = getWeapon();
////        FlameBurst flameBurst = getRed();
////        TimeWarp timeWarp = getPurple();
////        ArcaneShield arcaneShield = getBlue();
////        Inferno inferno = getOrange();
//
//        List<TextComponent> textComponentList = new ArrayList<>();
//        for (AbstractAbility ability : getAbilities()) {
//            textComponentList.add(new TextComponentBuilder(ability.getName())
//                    .setHoverText(ability.getAbilityInfo().stream()
//                            .map(stringStringPair -> ChatColor.WHITE + stringStringPair.getA() + ": " + ChatColor.GOLD + stringStringPair.getB())
//                            .collect(Collectors.joining("\n"))
//                    )
//                    .getTextComponent());
//        }
////        textComponentList.add(new TextComponentBuilder(ChatColor.GREEN + "Fireball")
////                .setHoverText("Shots Fired: " + ChatColor.GOLD + fireball.getTimesUsed() + "\n" + "Shots Hit: " + ChatColor.GOLD + fireball.getShotsHit())
////                .getTextComponent());
////        textComponentList.add(new TextComponentBuilder(ChatColor.RED + "Flame Burst")
////                .setHoverText("Shots Fired: " + ChatColor.GOLD + flameBurst.getTimesUsed() + "\n" + "Shots Hit: " + ChatColor.GOLD + flameBurst.getShotsHit())
////                .getTextComponent());
////        textComponentList.add(new TextComponentBuilder(ChatColor.LIGHT_PURPLE + "Time Warp")
////                .setHoverText("Times Used: " + ChatColor.GOLD + timeWarp.getTimesUsed() + "\n" + "Times Warped: " + ChatColor.GOLD + timeWarp.getTimesSuccessful())
////                .getTextComponent());
////        textComponentList.add(new TextComponentBuilder(ChatColor.AQUA + "Arcane Shield")
////                .setHoverText("Times Used: " + ChatColor.GOLD + arcaneShield.getTimesUsed() + "\n" +"Times Broken: " + ChatColor.GOLD + arcaneShield.getTimesBroken())
////                .getTextComponent());
////        textComponentList.add(new TextComponentBuilder(ChatColor.GOLD + "Inferno")
////                .setHoverText("Times Used: " + ChatColor.GOLD + inferno.getTimesUsed() + "\n" + "Shots Amplified: " + ChatColor.GOLD + inferno.getShotsAmplified())
////                .getTextComponent());
//
//        return textComponentList;
//    }
}
