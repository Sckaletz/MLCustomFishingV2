package eu.minelife.mLCustomFishingV2.DataGroup;

import java.util.List;

public class ChanceGroup {
    private final double percent;
    private final List<RewardEntry> rewards;

    public ChanceGroup(double percent, List<RewardEntry> rewards) {
        this.percent = percent;
        this.rewards = rewards;
    }

    public double getPercent() {
        return this.percent;
    }

    public List<RewardEntry> getRewards() {
        return this.rewards;
    }
}