package marketplace.api.entity;

import marketplace.db.entity.BidRecord;

import java.time.LocalDateTime;

public class Bid {

    private final long id;
    private final long projectId;
    private final String user;
    private final double amount;
    private final double lowestAmount;
    private final LocalDateTime bidMadeAt;

    public Bid(long id, long projectId, String user, double amount, double lowestAmount, LocalDateTime bidMadeAt) {
        this.id = id;
        this.projectId = projectId;
        this.user = user;
        this.amount = amount;
        this.lowestAmount = lowestAmount;
        this.bidMadeAt = bidMadeAt;
    }

    public Bid(BidRecord record) {
        this.id = record.getId();
        this.projectId = record.getProjectId();
        this.user = record.getUser();
        this.amount = record.getAmount();
        this.lowestAmount = record.getLowestAmount();
        this.bidMadeAt = record.getBidMadeAt();
    }

    public long getId() {
        return id;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }

    public double getLowestAmount() {
        return lowestAmount;
    }

    public LocalDateTime getBidMadeAt() {
        return bidMadeAt;
    }
}
