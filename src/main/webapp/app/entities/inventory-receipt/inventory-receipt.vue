<template>
  <div>
    <h2 id="page-heading" data-cy="InventoryReceiptHeading">
      <span v-text="t$('novelsyncApp.inventoryReceipt.home.title')" id="inventory-receipt-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('novelsyncApp.inventoryReceipt.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'InventoryReceiptCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-inventory-receipt"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('novelsyncApp.inventoryReceipt.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && inventoryReceipts && inventoryReceipts.length === 0">
      <span v-text="t$('novelsyncApp.inventoryReceipt.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="inventoryReceipts && inventoryReceipts.length > 0">
      <table class="table table-striped" aria-describedby="inventoryReceipts">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('receiptDate')">
              <span v-text="t$('novelsyncApp.inventoryReceipt.receiptDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'receiptDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('totalAmount')">
              <span v-text="t$('novelsyncApp.inventoryReceipt.totalAmount')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'totalAmount'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('user.login')">
              <span v-text="t$('novelsyncApp.inventoryReceipt.user')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'user.login'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="inventoryReceipt in inventoryReceipts" :key="inventoryReceipt.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'InventoryReceiptView', params: { inventoryReceiptId: inventoryReceipt.id } }">{{
                inventoryReceipt.id
              }}</router-link>
            </td>
            <td>{{ formatDateShort(inventoryReceipt.receiptDate) || '' }}</td>
            <td>{{ inventoryReceipt.totalAmount }}</td>
            <td>
              {{ inventoryReceipt.user ? inventoryReceipt.user.login : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link
                  :to="{ name: 'InventoryReceiptView', params: { inventoryReceiptId: inventoryReceipt.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link
                  :to="{ name: 'InventoryReceiptEdit', params: { inventoryReceiptId: inventoryReceipt.id } }"
                  custom
                  v-slot="{ navigate }"
                >
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(inventoryReceipt)"
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
          id="novelsyncApp.inventoryReceipt.delete.question"
          data-cy="inventoryReceiptDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-inventoryReceipt-heading" v-text="t$('novelsyncApp.inventoryReceipt.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-inventoryReceipt"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeInventoryReceipt()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="inventoryReceipts && inventoryReceipts.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./inventory-receipt.component.ts"></script>
