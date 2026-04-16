package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ReceiptDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReceiptDetail entity.
 */
@Repository
public interface ReceiptDetailRepository extends JpaRepository<ReceiptDetail, Long> {
    default Optional<ReceiptDetail> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ReceiptDetail> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ReceiptDetail> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select receiptDetail from ReceiptDetail receiptDetail left join fetch receiptDetail.book",
        countQuery = "select count(receiptDetail) from ReceiptDetail receiptDetail"
    )
    Page<ReceiptDetail> findAllWithToOneRelationships(Pageable pageable);

    @Query("select receiptDetail from ReceiptDetail receiptDetail left join fetch receiptDetail.book")
    List<ReceiptDetail> findAllWithToOneRelationships();

    @Query("select receiptDetail from ReceiptDetail receiptDetail left join fetch receiptDetail.book where receiptDetail.id =:id")
    Optional<ReceiptDetail> findOneWithToOneRelationships(@Param("id") Long id);
}
