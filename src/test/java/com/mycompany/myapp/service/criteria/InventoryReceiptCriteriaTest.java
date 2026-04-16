package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class InventoryReceiptCriteriaTest {

    @Test
    void newInventoryReceiptCriteriaHasAllFiltersNullTest() {
        var inventoryReceiptCriteria = new InventoryReceiptCriteria();
        assertThat(inventoryReceiptCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void inventoryReceiptCriteriaFluentMethodsCreatesFiltersTest() {
        var inventoryReceiptCriteria = new InventoryReceiptCriteria();

        setAllFilters(inventoryReceiptCriteria);

        assertThat(inventoryReceiptCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void inventoryReceiptCriteriaCopyCreatesNullFilterTest() {
        var inventoryReceiptCriteria = new InventoryReceiptCriteria();
        var copy = inventoryReceiptCriteria.copy();

        assertThat(inventoryReceiptCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(inventoryReceiptCriteria)
        );
    }

    @Test
    void inventoryReceiptCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var inventoryReceiptCriteria = new InventoryReceiptCriteria();
        setAllFilters(inventoryReceiptCriteria);

        var copy = inventoryReceiptCriteria.copy();

        assertThat(inventoryReceiptCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(inventoryReceiptCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var inventoryReceiptCriteria = new InventoryReceiptCriteria();

        assertThat(inventoryReceiptCriteria).hasToString("InventoryReceiptCriteria{}");
    }

    private static void setAllFilters(InventoryReceiptCriteria inventoryReceiptCriteria) {
        inventoryReceiptCriteria.id();
        inventoryReceiptCriteria.receiptDate();
        inventoryReceiptCriteria.totalAmount();
        inventoryReceiptCriteria.userId();
        inventoryReceiptCriteria.distinct();
    }

    private static Condition<InventoryReceiptCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReceiptDate()) &&
                condition.apply(criteria.getTotalAmount()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<InventoryReceiptCriteria> copyFiltersAre(
        InventoryReceiptCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReceiptDate(), copy.getReceiptDate()) &&
                condition.apply(criteria.getTotalAmount(), copy.getTotalAmount()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
