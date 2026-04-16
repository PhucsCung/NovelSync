<template>
  <div>
    <h2 id="page-heading" data-cy="OrderDetailHeading">
      <span v-text="t$('novelsyncApp.orderDetail.home.title')" id="order-detail-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('novelsyncApp.orderDetail.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'OrderDetailCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-order-detail"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('novelsyncApp.orderDetail.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && orderDetails && orderDetails.length === 0">
      <span v-text="t$('novelsyncApp.orderDetail.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="orderDetails && orderDetails.length > 0">
      <table class="table table-striped" aria-describedby="orderDetails">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('quantity')">
              <span v-text="t$('novelsyncApp.orderDetail.quantity')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'quantity'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('salePrice')">
              <span v-text="t$('novelsyncApp.orderDetail.salePrice')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'salePrice'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('order.id')">
              <span v-text="t$('novelsyncApp.orderDetail.order')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'order.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('book.title')">
              <span v-text="t$('novelsyncApp.orderDetail.book')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'book.title'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="orderDetail in orderDetails" :key="orderDetail.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'OrderDetailView', params: { orderDetailId: orderDetail.id } }">{{ orderDetail.id }}</router-link>
            </td>
            <td>{{ orderDetail.quantity }}</td>
            <td>{{ orderDetail.salePrice }}</td>
            <td>
              <div v-if="orderDetail.order">
                <router-link :to="{ name: 'OrderView', params: { orderId: orderDetail.order.id } }">{{ orderDetail.order.id }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="orderDetail.book">
                <router-link :to="{ name: 'BookView', params: { bookId: orderDetail.book.id } }">{{ orderDetail.book.title }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'OrderDetailView', params: { orderDetailId: orderDetail.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'OrderDetailEdit', params: { orderDetailId: orderDetail.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(orderDetail)"
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
          id="novelsyncApp.orderDetail.delete.question"
          data-cy="orderDetailDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-orderDetail-heading" v-text="t$('novelsyncApp.orderDetail.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-orderDetail"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeOrderDetail()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="orderDetails && orderDetails.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./order-detail.component.ts"></script>
