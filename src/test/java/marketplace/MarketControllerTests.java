package marketplace;

import marketplace.db.entity.ProjectRecord;
import marketplace.db.repository.ProjectRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MarketControllerTests {

    private final DateTimeFormatter returnDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private final DateTimeFormatter paramDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void createProject() throws Exception {

        String name = "testProject";
        String description = "test project description";
        String maxBudget = "999.999";
        LocalDateTime biddingEndDate = LocalDateTime.now().plusSeconds(10);

        String biddingEndDateParam = paramDateFormatter.format(biddingEndDate);
        String biddingEndDateReturn = returnDateFormatter.format(biddingEndDate);

        this.mockMvc.perform(
                post("/projects")
                .param("name", name)
                .param("description", description)
                .param("maxBudget", maxBudget)
                .param("biddingEndDate", biddingEndDateParam))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.maxBudget").value(maxBudget))
                .andExpect(jsonPath("$.biddingEndDate").value(biddingEndDateReturn))
                .andExpect(jsonPath("$.winningUser").value("NO WINNERS"))
                .andExpect(jsonPath("$.lowestBid").value("-1.0"));
    }

    @Test
    public void getProjectWithoutBids() throws Exception {

        String name = "testProjectWithoutBids";
        String description = "test project without bids";
        double maxBudget = 999.999;
        LocalDateTime biddingEndDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(10);

        String returnBiddingEndDate = returnDateFormatter.format(biddingEndDate);

        ProjectRecord saved = projectRepository.save(new ProjectRecord(name, description, maxBudget, biddingEndDate));

        this.mockMvc.perform(
                get(String.format("/projects/%d", saved.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.maxBudget").value(maxBudget))
                .andExpect(jsonPath("$.biddingEndDate").value(returnBiddingEndDate))
                .andExpect(jsonPath("$.lowestBid").value("-1.0"));

    }

    @Test
    public void getNonExistentProject() throws Exception {
        this.mockMvc.perform(
                get(String.format("/projects/%d", Long.MAX_VALUE)))
                .andExpect(status().isInternalServerError());

    }

    @Test
    public void bidOnProject() throws Exception {

        String name = "testProjectWithBids";
        String description = "test project with bids";
        double maxBudget = 999.999;
        LocalDateTime biddingEndDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(10);

        ProjectRecord saved = projectRepository.save(new ProjectRecord(name, description, maxBudget, biddingEndDate));

        String projectId = String.valueOf(saved.getId());
        ResultActions perform = this.mockMvc.perform(
                post("/bid")
                        .param("project-id", projectId)
                        .param("user", "user1")
                        .param("amount", "100"));
        perform
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(projectId))
                .andExpect(jsonPath("$.amount").value("100.0"));

    }

    @Test
    public void bidOnNonExistentProject() throws Exception {

        bidWithApi(String.valueOf(Long.MAX_VALUE), "user1", "100")
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void bidOnProjectLate() throws Exception {

        String name = "testProjectWithBids";
        String description = "test project with bids";
        double maxBudget = 999.999;
        LocalDateTime biddingEndDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(1);

        ProjectRecord saved = projectRepository.save(new ProjectRecord(name, description, maxBudget, biddingEndDate));

        Thread.sleep(2000);

        String projectId = String.valueOf(saved.getId());
        bidWithApi(projectId, "user1", "100")
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void bidOnProjectAndFindWinning() throws Exception {

        String name = "testProjectWithBids";
        String description = "test project with bids";
        double maxBudget = 999.999;
        String maxBudgetString = "999.999";
        LocalDateTime biddingEndDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(100);
        String biddingEndDateReturn = returnDateFormatter.format(biddingEndDate);

        ProjectRecord saved = projectRepository.save(new ProjectRecord(name, description, maxBudget, biddingEndDate));

        String projectId = String.valueOf(saved.getId());
        bidWithApi(projectId, "user1", "100")
                .andDo(print())
                .andExpect(status().isOk());

        bidWithApi(projectId, "user1", "110")
                .andDo(print())
                .andExpect(status().isOk());

        bidWithApi(projectId, "user2", "200")
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                get(String.format("/projects/%d", saved.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.maxBudget").value(maxBudgetString))
                .andExpect(jsonPath("$.biddingEndDate").value(biddingEndDateReturn))
                .andExpect(jsonPath("$.winningUser").value("user1"))
                .andExpect(jsonPath("$.lowestBid").value("100.0"))
                .andExpect(jsonPath("$.finished").value("false"));

    }

    @Test
    public void autoBidOnProjectAndFindWinning() throws Exception {

        String name = "testProjectWithAutoBids";
        String description = "test project with auto-bids";
        double maxBudget = 999.999;
        String maxBudgetString = "999.999";
        LocalDateTime biddingEndDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(100);
        String biddingEndDateReturn = returnDateFormatter.format(biddingEndDate);

        ProjectRecord saved = projectRepository.save(new ProjectRecord(name, description, maxBudget, biddingEndDate));

        String projectId = String.valueOf(saved.getId());
        bidWithApi(projectId, "user1", "100")
                .andDo(print())
                .andExpect(status().isOk());

        bidWithApi(projectId, "user2", "110", "90")
                .andDo(print())
                .andExpect(status().isOk());

        bidWithApi(projectId, "user3", "200", "80")
                .andDo(print())
                .andExpect(status().isOk());

        this.mockMvc.perform(
                get(String.format("/projects/%d", saved.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.maxBudget").value(maxBudgetString))
                .andExpect(jsonPath("$.biddingEndDate").value(biddingEndDateReturn))
                .andExpect(jsonPath("$.winningUser").value("user2"))
                .andExpect(jsonPath("$.lowestBid").value("99.0"))
                .andExpect(jsonPath("$.finished").value("false"));

    }

    private ResultActions bidWithApi(String projectId, String user, String amount) throws Exception {
        return this.mockMvc.perform(
                post("/bid")
                        .param("project-id", projectId)
                        .param("user", user)
                        .param("amount", amount));
    }

    private ResultActions bidWithApi(String projectId, String user, String amount, String lowestAmount) throws Exception {
        return this.mockMvc.perform(
                post("/bid")
                        .param("project-id", projectId)
                        .param("user", user)
                        .param("amount", amount)
                        .param("lowest-amount", lowestAmount));
    }


}
