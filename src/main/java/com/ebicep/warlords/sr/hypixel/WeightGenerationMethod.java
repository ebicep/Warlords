package com.ebicep.warlords.sr.hypixel;

import java.util.concurrent.ThreadLocalRandom;

public interface WeightGenerationMethod {

    double MAX_WEIGHT = 4;
    double MIN_WEIGHT = .43;
    WeightGenerationMethod DEFAULT_RANDOM = new Random();
    WeightGenerationMethod DEFAULT_NORMAL_DISTRIBUTION = new NormalDistribution();
    WeightGenerationMethod DEFAULT_CUSTOM = new Custom();
    WeightGenerationMethod DEFAULT_CUSTOM_NORMAL_DISTRIBUTION = new CustomNormalDistribution();
    WeightGenerationMethod[] VALUES = {DEFAULT_RANDOM, DEFAULT_NORMAL_DISTRIBUTION, DEFAULT_CUSTOM, DEFAULT_CUSTOM_NORMAL_DISTRIBUTION};

    private static double clamp(double value) {
        return clamp(value, MIN_WEIGHT, MAX_WEIGHT);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double lerp(double min, double max, double ratio) {
        return min + (max - min) * ratio;
    }

    double generateRandomWeight();

    class Random implements WeightGenerationMethod {
        @Override
        public double generateRandomWeight() {
            return ThreadLocalRandom.current().nextDouble(MIN_WEIGHT, MAX_WEIGHT);
        }
    }

    class NormalDistribution implements WeightGenerationMethod {
        private static final double DEFAULT_MEAN = (MAX_WEIGHT + MIN_WEIGHT) / 2;
        private static final double DEFAULT_STANDARD_DEVIATION = 1.2;
        private final double mean;
        private final double standardDeviation;

        public NormalDistribution() {
            this(DEFAULT_MEAN, DEFAULT_STANDARD_DEVIATION);
        }

        public NormalDistribution(double mean, double standardDeviation) {
            this.mean = mean;
            this.standardDeviation = standardDeviation;
        }

        @Override
        public double generateRandomWeight() {
            return clamp(ThreadLocalRandom.current().nextGaussian() * standardDeviation + mean);
        }
    }

    class Custom implements WeightGenerationMethod {
        @Override
        public double generateRandomWeight() {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int last100Wins = random.nextInt(0, 101);
            int last10Wins = clamp(random.nextInt(0, 11), 0, last100Wins);
            double weight = clamp(last100Wins / (100.0 - last100Wins));
            double last10WinLoss = last10Wins / 10.0;
//            System.out.println(last100Wins + " - " + weight + " - " + last10WinLoss);
            return weight * (1 + Math.sqrt(last10WinLoss));
        }
    }

    class CustomNormalDistribution implements WeightGenerationMethod {
        private static final double DEFAULT_MEAN = 50; // for generating last 100 wins
        private static final double DEFAULT_STANDARD_DEVIATION = 25; // for generating last 100 wins
        private final double mean;
        private final double standardDeviation;

        public CustomNormalDistribution() {
            this(DEFAULT_MEAN, DEFAULT_STANDARD_DEVIATION);
        }

        public CustomNormalDistribution(double mean, double standardDeviation) {
            this.mean = mean;
            this.standardDeviation = standardDeviation;
        }

        @Override
        public double generateRandomWeight() {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int last100Wins = (int) clamp(random.nextGaussian() * standardDeviation + mean, 0, 101);
            int last10Wins = clamp(random.nextInt(0, 11), 0, last100Wins);
            double weight = clamp(last100Wins / (100.0 - last100Wins));
            double last10WinLoss = last10Wins / 10.0;
//            System.out.println(last100Wins + " - " + weight + " - " + last10WinLoss);
            return weight * (1 + Math.sqrt(last10WinLoss));
        }
    }
}
