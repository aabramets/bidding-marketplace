package marketplace.db.repository;

import marketplace.db.entity.BidRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BidRepository extends CrudRepository<BidRecord, Long>  {

    List<BidRecord> findByProjectId(Long projectId);

    @Query("SELECT b FROM BidRecord b WHERE amount = (SELECT MIN(amount) FROM BidRecord WHERE projectId = :projectId) AND projectId = :projectId ORDER BY bidMadeAt")
    List<BidRecord> findLowestRegularBid(@Param("projectId") long projectId);

    @Query("SELECT b FROM BidRecord b WHERE lowestAmount < :bidToOutbid AND projectId = :projectId AND user != :userToOutbid ORDER BY bidMadeAt")
    List<BidRecord> findLowestBidsLessThan(@Param("projectId") long projectId,
                                           @Param("bidToOutbid") double bidToOutbid,
                                           @Param("userToOutbid") String userToOutbid);
}
