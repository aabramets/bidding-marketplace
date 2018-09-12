package marketplace;

import marketplace.api.entity.Bid;
import marketplace.api.entity.Project;
import marketplace.db.entity.BidRecord;
import marketplace.db.entity.ProjectRecord;
import marketplace.db.repository.BidRepository;
import marketplace.db.repository.ProjectRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
@RestController
public class MarketplaceController {

    private static final String NO_WINNERS = "NO WINNERS";
    private static final double DEFAULT_BID = -1;

    private final ProjectRepository projectRepository;
    private final BidRepository bidRepository;

    public MarketplaceController(ProjectRepository projectRepository, BidRepository bidRepository) {
        this.projectRepository = projectRepository;
        this.bidRepository = bidRepository;
    }

    @PostMapping("/projects")
    public Project createProject(@RequestParam(value="name") String name,
                                 @RequestParam(value="description") String description,
                                 @RequestParam(value="maxBudget") double maxBudget,
                                 @RequestParam(value="biddingEndDate") @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") LocalDateTime biddingEndDate) {
        ProjectRecord saved = projectRepository.save(
                new ProjectRecord(name, description, maxBudget, biddingEndDate));
        return new Project(saved, NO_WINNERS, DEFAULT_BID);
    }

    @GetMapping("/projects/{id}")
    public Project getProject(@PathVariable Long id) {
        ProjectRecord dbProject = projectRepository.findById(id)
                .orElseThrow(() -> new MarketplaceException(String.format("ProjectRecord not found for id = %d", id)));

        List<BidRecord> lowestBids = bidRepository.findLowestBid(id);
        if (lowestBids.size() == 0) {
            return new Project(dbProject, NO_WINNERS, DEFAULT_BID);
        } else {
            BidRecord lowestBid = lowestBids.get(0);
            return new Project(dbProject, lowestBid.getUser(), lowestBid.getAmount());
        }
    }

    @PostMapping("/bid")
    public Bid bidProject(@RequestParam(value="project-id") Long projectId,
                          @RequestParam(value="user") String user,
                          @RequestParam(value="amount") double amount) {
        LocalDateTime biddingEndDate = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new MarketplaceException(String.format("Project with id %d not found", projectId)))
                .getBiddingEndDate();
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(biddingEndDate)) throw new MarketplaceException(String.format("Bidding finished on this project (%d)", projectId));

        BidRecord saved = bidRepository.save(
                new BidRecord(projectId, user, amount, now));
        return new Bid(saved);
    }

/*
    @PostMapping("/auto-bid")
    public Bid autoBidProject(@RequestParam(value="project-id") Long projectId,
                          @RequestParam(value="lowest-amount") double lowestAmount) {
// add autobid field
    }
*/
}
