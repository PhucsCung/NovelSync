package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.OrderDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderDetail entity.
 */
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    default Optional<OrderDetail> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<OrderDetail> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<OrderDetail> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select orderDetail from OrderDetail orderDetail left join fetch orderDetail.book",
        countQuery = "select count(orderDetail) from OrderDetail orderDetail"
    )
    Page<OrderDetail> findAllWithToOneRelationships(Pageable pageable);

    @Query("select orderDetail from OrderDetail orderDetail left join fetch orderDetail.book")
    List<OrderDetail> findAllWithToOneRelationships();

    @Query("select orderDetail from OrderDetail orderDetail left join fetch orderDetail.book where orderDetail.id =:id")
    Optional<OrderDetail> findOneWithToOneRelationships(@Param("id") Long id);
}
