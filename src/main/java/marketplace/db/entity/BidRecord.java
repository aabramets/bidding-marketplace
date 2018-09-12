package marketplace.db.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class BidRecord {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private long projectId;
    private String user;
    private double amount;
    private double lowestAmount;
    private LocalDateTime bidMadeAt;

    protected BidRecord() { }

    public BidRecord(long projectId, String user, double amount, double lowestAmount, LocalDateTime bidMadeAt) {
        this.projectId = projectId;
        this.user = user;
        this.amount = amount;
        this.lowestAmount = lowestAmount;
        this.bidMadeAt = bidMadeAt;
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
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
