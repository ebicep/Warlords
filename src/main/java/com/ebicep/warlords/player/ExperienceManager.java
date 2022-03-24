package com.ebicep.warlords.player;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.FutureMessageManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.text.DecimalFormat;
import java.util.*;

public class ExperienceManager {

    public static final Map<Integer, Long> levelExperience;
    public static final Map<Long, Integer> experienceLevel;
    public static final DecimalFormat currentExperienceDecimalFormat = new DecimalFormat("#,###.#");
    public static final HashMap<UUID, LinkedHashMap<String, Long>> cachedPlayerExpSummary = new HashMap<>();

    static {
        //caching all levels/experience
        Map<Integer, Long> levelExperienceNew = new HashMap<>();
        Map<Long, Integer> experienceLevelNew = new HashMap<>();
        for (int i = 0; i < 201; i++) {
            long exp = (long) calculateExpFromLevel(i);
            levelExperienceNew.put(i, exp);
            experienceLevelNew.put(exp, i);
        }

        levelExperience = Collections.unmodifiableMap(levelExperienceNew);
        experienceLevel = Collections.unmodifiableMap(experienceLevelNew);

        currentExperienceDecimalFormat.setDecimalSeparatorAlwaysShown(false);
    }

    private static final Map<String, int[]> awardOrder = new LinkedHashMap<String, int[]>() {{
        put("wins", new int[]{1000, 750, 500});
        put("losses", new int[]{200, 150, 100});
        put("kills", new int[]{850, 600, 350});
        put("assists", new int[]{850, 600, 350});
        put("deaths", new int[]{200, 150, 100});
        put("dhp", new int[]{1000, 750, 500});
        put("dhp_per_game", new int[]{1000, 750, 500});
        put("damage", new int[]{850, 600, 350});
        put("healing", new int[]{850, 600, 350});
        put("absorbed", new int[]{850, 600, 350});
        put("flags_captured", new int[]{600, 400, 200});
        put("flags_returned", new int[]{600, 400, 200});
    }};

    public static void awardWeeklyExperience(Document weeklyDocument) {
        if (DatabaseManager.playerService == null) {
            System.out.println("WARNING - Could not give weekly experience bonus - playerService is null");
            return;
        }

        HashMap<String, Document> futureMessageDocuments = new HashMap<>();
        BulkOperations operations = DatabaseManager.playerService.bulkOps();

        awardOrder.forEach((key, rewards) -> {
            String name = weeklyDocument.getEmbedded(Arrays.asList(key, "name"), String.class);
            List<Document> top = weeklyDocument.getEmbedded(Arrays.asList(key, "top"), new ArrayList<>());
            for (int i = 0; i < top.size(); i++) {
                Document topDocument = top.get(i);
                String[] names = topDocument.getString("names").split(",");
                String[] uuids = topDocument.getString("uuids").split(",");
                for (int j = 0; j < uuids.length; j++) {
                    int experienceGain = rewards[i];
                    if (!futureMessageDocuments.containsKey(uuids[j])) {
                        futureMessageDocuments.put(uuids[j], new Document("uuid", uuids[j])
                                .append("name", names[j])
                                .append("centered", true)
                                .append("messages", new ArrayList<>(Arrays.asList(ChatColor.BLUE + "---------------------------------------------------",
                                        ChatColor.GREEN + "Weekly Experience Bonus\n "))
                                ).append("total_experience_gain", 0L)
                        );
                    }
                    Document previousDocument = futureMessageDocuments.get(uuids[j]);
                    previousDocument.getList("messages", String.class).add(ChatColor.YELLOW + "#" + (i + 1) + ". " + ChatColor.AQUA + name + ChatColor.WHITE + ": " + ChatColor.DARK_GRAY + "+" + ChatColor.DARK_AQUA + experienceGain + ChatColor.GOLD + " Universal Experience");
                    previousDocument.put("total_experience_gain", previousDocument.getLong("total_experience_gain") + experienceGain);
                }
            }
        });
        futureMessageDocuments.forEach((s, document) -> {
            long expGain = document.getLong("total_experience_gain");
            Query query = new Query().addCriteria(Criteria.where("uuid").is(document.getString("uuid")));
            Update update = new Update().inc("experience", expGain);
            operations.updateOne(query, update);
            document.getList("messages", String.class).add(ChatColor.GOLD + "Total Experience Gain" + ChatColor.WHITE + ": " + ChatColor.DARK_GRAY + "+" + ChatColor.DARK_AQUA + expGain);
            document.getList("messages", String.class).addAll(Collections.singletonList(ChatColor.BLUE + "---------------------------------------------------"));
        });

        FutureMessageManager.addNewFutureMessageDocuments(new ArrayList<>(futureMessageDocuments.values()));
        System.out.println(operations.execute().getModifiedCount() + " players were given weekly experience bonuses");
    }

    public static LinkedHashMap<String, Long> getExpFromGameStats(WarlordsPlayer warlordsPlayer, boolean recalculate) {
        if (!recalculate && cachedPlayerExpSummary.containsKey(warlordsPlayer.getUuid()) && cachedPlayerExpSummary.get(warlordsPlayer.getUuid()) != null) {
            return cachedPlayerExpSummary.get(warlordsPlayer.getUuid());
        }

        boolean isCompGame = warlordsPlayer.getGame().getAddons().contains(GameAddon.PRIVATE_GAME);
        float multiplier = 1;
        //pubs
        if (!isCompGame) {
            multiplier *= .1;
        }
        //duels
        if (warlordsPlayer.getGame().getGameMode() == GameMode.DUEL) {
            multiplier *= .1;
        }

        // TODO add check here for game ending in a draw
        boolean won = warlordsPlayer.getGame().getPoints(warlordsPlayer.getTeam()) > warlordsPlayer.getGame().getPoints(warlordsPlayer.getTeam().enemy());
        long winLossExp = won ? 500 : 250;
        long kaExp = 5L * (warlordsPlayer.getMinuteStats().total().getKills() + warlordsPlayer.getMinuteStats().total().getAssists());

        double damageMultiplier;
        double healingMultiplier;
        double absorbedMultiplier;
        Specializations specializations = warlordsPlayer.getSpecClass();
        if (specializations.specType == SpecType.DAMAGE) {
            damageMultiplier = .80;
            healingMultiplier = .10;
            absorbedMultiplier = .10;
        } else if (specializations.specType == SpecType.HEALER) {
            damageMultiplier = .275;
            healingMultiplier = .65;
            absorbedMultiplier = .75;
        } else { //tank
            damageMultiplier = .575;
            healingMultiplier = .1;
            absorbedMultiplier = .325;
        }
        double calculatedDHP = warlordsPlayer.getMinuteStats().total().getDamage() * damageMultiplier + warlordsPlayer.getMinuteStats().total().getHealing() * healingMultiplier + warlordsPlayer.getMinuteStats().total().getAbsorbed() * absorbedMultiplier;
        long dhpExp = (long) (calculatedDHP / 500L);
        long flagCapExp = warlordsPlayer.getFlagsCaptured() * 150L;
        long flagRetExp = warlordsPlayer.getFlagsReturned() * 50L;

        LinkedHashMap<String, Long> expGain = new LinkedHashMap<>();
        expGain.put(won ? "Win" : "Loss", (long) (winLossExp * multiplier));
        if (kaExp != 0) {
            expGain.put("Kills/Assists", (long) (kaExp * multiplier));
        }
        if (dhpExp != 0) {
            expGain.put("DHP", (long) (dhpExp * multiplier));
        }
        if (flagCapExp != 0) {
            expGain.put("Flags Captured", (long) (flagCapExp * multiplier));
        }
        if (flagRetExp != 0) {
            expGain.put("Flags Returned", (long) (flagRetExp * multiplier));
        }

        try {
            DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(warlordsPlayer.getUuid(), PlayersCollections.DAILY);
            if (isCompGame) {
                switch (databasePlayer.getCompStats().getPlays()) {
                    case 0:
                        expGain.put("First Game of the Day", 500L);
                        break;
                    case 1:
                        expGain.put("Second Game of the Day", 250L);
                        break;
                    case 2:
                        expGain.put("Third Game of the Day", 100L);
                        break;
                }
            } else {
                switch (databasePlayer.getPubStats().getPlays()) {
                    case 0:
                        expGain.put("First Game of the Day", 50L);
                        break;
                    case 1:
                        expGain.put("Second Game of the Day", 25L);
                        break;
                    case 2:
                        expGain.put("Third Game of the Day", 10L);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cachedPlayerExpSummary.put(warlordsPlayer.getUuid(), expGain);
        return expGain;
    }

    public static long getSpecExpFromSummary(LinkedHashMap<String, Long> expSummary) {
        return expSummary.values().stream().mapToLong(Long::longValue).sum()
                - expSummary.getOrDefault("First Game of the Day", 0L)
                - expSummary.getOrDefault("Second Game of the Day", 0L)
                - expSummary.getOrDefault("Third Game of the Day", 0L);
    }

//    private int getTotalAverageDHP(String classSpec) {
//        long totalAverageDHP = 0;
//        int totalPlayers = 0;
//        for (Document document1 : playersInformation.find()) {
//            long averageDHP = getAverageDHP(document1, classSpec);
//            totalAverageDHP += averageDHP;
//            if (averageDHP != 0) {
//                totalPlayers++;
//            }
//        }
//        return (int) (totalAverageDHP / totalPlayers);
//    }
//
//    private int getTotalAverageDHPSelected(String classSpec, String selected) {
//        long totalAverageDHP = 0;
//        int totalPlayers = 0;
//        for (Document document1 : playersInformation.find()) {
//            long averageDHP = getAverageSelectedDHP(document1, classSpec, selected);
//            totalAverageDHP += averageDHP;
//            if (averageDHP != 0) {
//                totalPlayers++;
//            }
//        }
//        return (int) (totalAverageDHP / totalPlayers);
//    }
//
//    private long getAverageDHP(Document document, String classSpec) {
//        long dhp = (long) getDocumentInfoWithDotNotation(document, classSpec + ".damage") + (long) getDocumentInfoWithDotNotation(document, classSpec + ".healing") + (long) getDocumentInfoWithDotNotation(document, classSpec + ".absorbed");
//        int plays = (int) getDocumentInfoWithDotNotation(document, classSpec + ".wins") + (int) getDocumentInfoWithDotNotation(document, classSpec + ".losses");
//        return plays == 0 ? 0 : dhp / plays;
//    }
//
//    private long getAverageSelectedDHP(Document document, String classSpec, String selected) {
//        long selectedDHP = (long) getDocumentInfoWithDotNotation(document, classSpec + "." + selected);
//        int plays = (int) getDocumentInfoWithDotNotation(document, classSpec + ".wins") + (int) getDocumentInfoWithDotNotation(document, classSpec + ".losses");
//        return plays == 0 ? 0 : selectedDHP / plays;
//    }
//
//    public static long getCalculatedExp(Document document, String key) {
//        //500 per win
//        //250 per loss
//        //5 per kills/assist
//        //1 per 500 dhp based on multiplier
//        //150 per cap
//        //50 per ret
//
//        long exp = 0;
//
//        double damageMultiplier;
//        double healingMultiplier;
//        double absorbedMultiplier;
//
//        Classes classes = Classes.getClass(key.substring(key.indexOf(".") + 1));
//        if (classes.specType == SpecType.DAMAGE) {
//            damageMultiplier = .80;
//            healingMultiplier = .10;
//            absorbedMultiplier = .10;
//        } else if (classes.specType == SpecType.HEALER) {
//            damageMultiplier = .275;
//            healingMultiplier = .65;
//            absorbedMultiplier = .75;
//        } else { //tank
//            damageMultiplier = .575;
//            healingMultiplier = .1;
//            absorbedMultiplier = .325;
//        }
//
//        int wins = (int) getDocumentInfoWithDotNotation(document, key + ".wins");
//        int losses = (int) getDocumentInfoWithDotNotation(document, key + ".losses");
//        int kills = (int) getDocumentInfoWithDotNotation(document, key + ".kills");
//        int assists = (int) getDocumentInfoWithDotNotation(document, key + ".assists");
//        long damage = (long) getDocumentInfoWithDotNotation(document, key + ".damage");
//        long healing = (long) getDocumentInfoWithDotNotation(document, key + ".healing");
//        long absorbed = (long) getDocumentInfoWithDotNotation(document, key + ".absorbed");
//        int caps = (int) getDocumentInfoWithDotNotation(document, key + ".flags_captured");
//        int rets = (int) getDocumentInfoWithDotNotation(document, key + ".flags_returned");
//
//        double calculatedDHP = damage * damageMultiplier + healing * healingMultiplier + absorbed * absorbedMultiplier;
//
//        exp += wins * 500L;
//        exp += losses * 250L;
//        exp += (kills + assists) * 5L;
//        exp += calculatedDHP / 500;
//        exp += caps * 150L;
//        exp += rets * 50L;
//
//        return exp;
//    }

    public static long getExperienceForClass(UUID uuid, Classes classes) {
        if (DatabaseManager.playerService == null) return 0;
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
        return databasePlayer == null ? 0L : databasePlayer.getClass(classes).getExperience();
    }

    public static int getLevelForClass(UUID uuid, Classes classes) {
        return (int) calculateLevelFromExp(getExperienceForClass(uuid, classes));
    }

    public static long getExperienceForSpec(UUID uuid, Specializations spec) {
        return getExperienceFromSpec(uuid, spec);
    }

    public static int getLevelForSpec(UUID uuid, Specializations spec) {
        return (int) calculateLevelFromExp(getExperienceFromSpec(uuid, spec));
    }

    public static long getUniversalLevel(UUID uuid) {
        if (DatabaseManager.playerService == null) return 0;
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
        return databasePlayer == null ? 0L : databasePlayer.getExperience();
    }

    private static long getExperienceFromSpec(UUID uuid, Specializations specializations) {
        if (DatabaseManager.playerService == null) return 0;
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
        return databasePlayer == null ? 0L : databasePlayer.getSpec(specializations).getExperience();
    }

    public static String getLevelString(int level) {
        return level < 10 ? "0" + level : String.valueOf(level);
    }

    public static String getProgressString(long currentExperience, int nextLevel) {
        String progress = ChatColor.GRAY + "Progress to Level " + nextLevel + ": " + ChatColor.YELLOW;

        long experience = currentExperience - levelExperience.get(nextLevel - 1);
        long experienceNeeded = levelExperience.get(nextLevel) - levelExperience.get(nextLevel - 1);
        double progressPercentage = (double) experience / experienceNeeded * 100;

        progress += NumberFormat.formatOptionalTenths(progressPercentage) + "%\n" + ChatColor.GREEN;
        int greenBars = (int) Math.round(progressPercentage * 20 / 100);
        for (int i = 0; i < greenBars; i++) {
            progress += "-";
        }
        progress += ChatColor.WHITE;
        for (int i = greenBars; i < 20; i++) {
            progress += "-";
        }
        progress += " " + ChatColor.YELLOW + currentExperienceDecimalFormat.format(experience) + ChatColor.GOLD + "/" + ChatColor.YELLOW + NumberFormat.getSimplifiedNumber(experienceNeeded);

        return progress;
    }

    public static double calculateLevelFromExp(long exp) {
        return Math.sqrt(exp / 25.0);
    }

    public static double calculateExpFromLevel(int level) {
        return Math.pow(level, 2) * 25;
    }

    public static void giveExperienceBar(Player player) {
        //long experience = warlordsPlayersDatabase.getCollection("Players_Information_Test").find().filter(eq("uuid", player.getUniqueId().toString())).first().getLong("experience");
        long experience = getUniversalLevel(player.getUniqueId());
        int level = (int) calculateLevelFromExp(experience);
        player.setLevel(level);
        player.setExp((float) (experience - levelExperience.get(level)) / (levelExperience.get(level + 1) - levelExperience.get(level)));
    }

    public static void giveLevelUpMessage(Player player, long expBefore, long expAfter) {
        int levelBefore = (int) calculateLevelFromExp(expBefore);
        int levelAfter = (int) calculateLevelFromExp(expAfter);
        if (levelBefore != levelAfter) {
            ChatUtils.sendMessage(player, true, ChatColor.GREEN.toString() + ChatColor.BOLD + ChatColor.MAGIC + "   " + ChatColor.AQUA + ChatColor.BOLD + " LEVEL UP! " + ChatColor.DARK_GRAY + ChatColor.BOLD + "[" + ChatColor.GRAY + ChatColor.BOLD + levelBefore + ChatColor.DARK_GRAY + ChatColor.BOLD + "]" + ChatColor.GREEN + ChatColor.BOLD + " > " + ChatColor.DARK_GRAY + ChatColor.BOLD + "[" + ChatColor.GRAY + ChatColor.BOLD + levelAfter + ChatColor.DARK_GRAY + ChatColor.BOLD + "] " + ChatColor.GREEN + ChatColor.MAGIC + ChatColor.BOLD + "   ");
        }
    }
}

//Pyromancer
//Average DHP: 181046
//Average Damage: 136237
//Average Healing: 10815
//Average Absorbed: 33992
//Cryomancer
//Average DHP: 178083
//Average Damage: 73422
//Average Healing: 10124
//Average Absorbed: 94534
//Aquamancer
//Average DHP: 236444
//Average Damage: 33120
//Average Healing: 168925
//Average Absorbed: 34397
//Berserker
//Average DHP: 189292
//Average Damage: 131381
//Average Healing: 39767
//Average Absorbed: 18142
//Defender
//Average DHP: 150763
//Average Damage: 106301
//Average Healing: 8983
//Average Absorbed: 35477
//Revenant
//Average DHP: 230796
//Average Damage: 93791
//Average Healing: 121953
//Average Absorbed: 15050
//Avenger
//Average DHP: 209579
//Average Damage: 164426
//Average Healing: 29768
//Average Absorbed: 15384
//Crusader
//Average DHP: 177555
//Average Damage: 105825
//Average Healing: 30094
//Average Absorbed: 41634
//Protector
//Average DHP: 274068
//Average Damage: 106314
//Average Healing: 156904
//Average Absorbed: 10849
//Thunderlord
//Average DHP: 204834
//Average Damage: 156515
//Average Healing: 18486
//Average Absorbed: 29832
//Spiritguard
//Average DHP: 286025
//Average Damage: 170642
//Average Healing: 51879
//Average Absorbed: 63503
//Earthwarden
//Average DHP: 228875
//Average Damage: 96707
//Average Healing: 111221
//Average Absorbed: 20945
//DPS
//Average DHP: 196187
//Average Damage: 147139
//Average Healing: 24709
//Average Absorbed: 24337
//Damage Ratio: 0.7499945842694052
//Healing Ratio: 0.1259456821335685
//Absorbed Ratio: 0.12405208785971601
//TANK
//Average DHP: 198106
//Average Damage: 114047
//Average Healing: 25270
//Average Absorbed: 58787
//Damage Ratio: 0.5756878244782478
//Healing Ratio: 0.12755765207098202
//Absorbed Ratio: 0.2967444278708674
//HEALER
//Average DHP: 242545
//Average Damage: 82483
//Average Healing: 139750
//Average Absorbed: 20310
//Damage Ratio: 0.3400719245750544
//Healing Ratio: 0.5761830500019068
//Absorbed Ratio: 0.08373781028939901
//