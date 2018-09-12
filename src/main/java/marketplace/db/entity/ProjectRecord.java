package marketplace.db.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class ProjectRecord {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String name;
    private String description;
    private double maxBudget;
    private LocalDateTime biddingEndDate;

    protected ProjectRecord() {}

    public ProjectRecord(String name, String description, double maxBudget, LocalDateTime biddingEndDate) {
        this.name = name;
        this.description = description;
        this.maxBudget = maxBudget;
        this.biddingEndDate = biddingEndDate;
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
}
