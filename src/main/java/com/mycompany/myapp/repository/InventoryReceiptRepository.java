package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.InventoryReceipt;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the InventoryReceipt entity.
 */
@Repository
public interface InventoryReceiptRepository extends JpaRepository<InventoryReceipt, Long>, JpaSpecificationExecutor<InventoryReceipt> {
    @Query("select inventoryReceipt from InventoryReceipt inventoryReceipt where inventoryReceipt.user.login = ?#{authentication.name}")
    List<InventoryReceipt> findByUserIsCurrentUser();

    default Optional<InventoryReceipt> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<InventoryReceipt> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<InventoryReceipt> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select inventoryReceipt from InventoryReceipt inventoryReceipt left join fetch inventoryReceipt.user",
        countQuery = "select count(inventoryReceipt) from InventoryReceipt inventoryReceipt"
    )
    Page<InventoryReceipt> findAllWithToOneRelationships(Pageable pageable);

    @Query("select inventoryReceipt from InventoryReceipt inventoryReceipt left join fetch inventoryReceipt.user")
    List<InventoryReceipt> findAllWithToOneRelationships();

    @Query(
        "select inventoryReceipt from InventoryReceipt inventoryReceipt left join fetch inventoryReceipt.user where inventoryReceipt.id =:id"
    )
    Optional<InventoryReceipt> findOneWithToOneRelationships(@Param("id") Long id);
}
