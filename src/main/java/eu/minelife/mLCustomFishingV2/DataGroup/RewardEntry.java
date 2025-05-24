package eu.minelife.mLCustomFishingV2.DataGroup;

public class RewardEntry {
    private final String command;
    private final String message;

    public RewardEntry(String command, String message) {
        this.command = command;
        this.message = message;
    }

    public String getCommand() {
        return this.command;
    }

    public String getMessage() {
        return this.message;
    }
}
