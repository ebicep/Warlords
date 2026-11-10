package com.ebicep.warlords.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class BuildInfo {

    private static final String UNKNOWN = "unknown";

    private static String version = UNKNOWN;
    private static String commit = UNKNOWN;
    private static String commitShort = UNKNOWN;
    private static String branch = UNKNOWN;
    private static String commitTime = UNKNOWN;
    private static String buildTime = UNKNOWN;
    private static boolean dirty;

    private BuildInfo() {
    }

    public static void load(Class<?> anchor) {
        try (InputStream inputStream = anchor.getResourceAsStream("/build-info.properties")) {
            if (inputStream == null) {
                return;
            }
            Properties properties = new Properties();
            properties.load(inputStream);
            version = properties.getProperty("version", UNKNOWN);
            commit = properties.getProperty("commit", UNKNOWN);
            commitShort = properties.getProperty("commitShort", UNKNOWN);
            branch = properties.getProperty("branch", UNKNOWN);
            commitTime = properties.getProperty("commitTime", UNKNOWN);
            buildTime = properties.getProperty("buildTime", UNKNOWN);
            dirty = Boolean.parseBoolean(properties.getProperty("dirty", "false"));
        } catch (IOException ignored) {
        }
    }

    public static String getVersion() {
        return version;
    }

    public static String getCommit() {
        return commit;
    }

    public static String getCommitShort() {
        return commitShort;
    }

    public static String getBranch() {
        return branch;
    }

    public static String getCommitTime() {
        return commitTime;
    }

    public static String getBuildTime() {
        return buildTime;
    }

    public static boolean isDirty() {
        return dirty;
    }
}
