package marketplace.api.entity;

import marketplace.db.entity.ProjectRecord;

import java.time.LocalDateTime;

public class Project {

    private final long id;
    private final String name;
    private final String description;
    private final double maxBudget;
    private final LocalDateTime biddingEndDate;
    private final String winningUser;
    private final double lowestBid;

    public Project(long id, String name, String description, double maxBudget, LocalDateTime biddingEndDate, String winningUser, double lowestBid) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxBudget = maxBudget;
        this.biddingEndDate = biddingEndDate;
        this.winningUser = winningUser;
        this.lowestBid = lowestBid;

    }

    public Project(ProjectRecord record, String winningUser, Double lowestBid) {
        this.id = record.getId();
        this.name = record.getName();
        this.description = record.getDescription();
        this.maxBudget = record.getMaxBudget();
        this.biddingEndDate = record.getBiddingEndDate();
        this.winningUser = winningUser;
        this.lowestBid = lowestBid;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getMaxBudget() {
        return maxBudget;
    }

    public LocalDateTime getBiddingEndDate() {
        return biddingEndDate;
    }

    public String getWinningUser() {
        return winningUser;
    }

    public double getLowestBid() {
        return lowestBid;
    }

    public boolean isFinished() {
        return LocalDateTime.now().isAfter(biddingEndDate);
    }
}
