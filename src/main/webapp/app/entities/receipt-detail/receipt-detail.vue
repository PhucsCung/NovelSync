<template>
  <div>
    <h2 id="page-heading" data-cy="ReceiptDetailHeading">
      <span v-text="t$('novelsyncApp.receiptDetail.home.title')" id="receipt-detail-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('novelsyncApp.receiptDetail.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'ReceiptDetailCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-receipt-detail"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('novelsyncApp.receiptDetail.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && receiptDetails && receiptDetails.length === 0">
      <span v-text="t$('novelsyncApp.receiptDetail.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="receiptDetails && receiptDetails.length > 0">
      <table class="table table-striped" aria-describedby="receiptDetails">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('quantity')">
              <span v-text="t$('novelsyncApp.receiptDetail.quantity')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'quantity'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('purchasePrice')">
              <span v-text="t$('novelsyncApp.receiptDetail.purchasePrice')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'purchasePrice'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('receipt.id')">
              <span v-text="t$('novelsyncApp.receiptDetail.receipt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'receipt.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('book.title')">
              <span v-text="t$('novelsyncApp.receiptDetail.book')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'book.title'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="receiptDetail in receiptDetails" :key="receiptDetail.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ReceiptDetailView', params: { receiptDetailId: receiptDetail.id } }">{{
                receiptDetail.id
              }}</router-link>
            </td>
            <td>{{ receiptDetail.quantity }}</td>
            <td>{{ receiptDetail.purchasePrice }}</td>
            <td>
              <div v-if="receiptDetail.receipt">
                <router-link :to="{ name: 'InventoryReceiptView', params: { inventoryReceiptId: receiptDetail.receipt.id } }">{{
                  receiptDetail.receipt.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="receiptDetail.book">
                <router-link :to="{ name: 'BookView', params: { bookId: receiptDetail.book.id } }">{{
                  receiptDetail.book.title
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'ReceiptDetailView', params: { receiptDetailId: receiptDetail.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'ReceiptDetailEdit', params: { receiptDetailId: receiptDetail.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(receiptDetail)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span
          id="novelsyncApp.receiptDetail.delete.question"
          data-cy="receiptDetailDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-receiptDetail-heading" v-text="t$('novelsyncApp.receiptDetail.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-receiptDetail"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeReceiptDetail()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="receiptDetails && receiptDetails.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./receipt-detail.component.ts"></script>
