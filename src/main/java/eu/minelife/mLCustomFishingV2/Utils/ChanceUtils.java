package eu.minelife.mLCustomFishingV2.Utils;

import java.util.concurrent.ThreadLocalRandom;

public class ChanceUtils {
    private static final int RESOLUTION = 100000000;

    public static boolean checkChance(double percent) {
        double fraction = percent / 100.0D;
        int threshold = (int)Math.floor(fraction * 1.0E8D);
        if (threshold <= 0) {
            double rollDouble = ThreadLocalRandom.current().nextDouble();
            return rollDouble < fraction;
        } else {
            int roll = ThreadLocalRandom.current().nextInt(100000000);
            return roll < threshold;
        }
    }

    public static ThreadLocalRandom getRandom() {
        return ThreadLocalRandom.current();
    }
}