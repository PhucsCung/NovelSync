<template>
  <div>
    <h2 id="page-heading" data-cy="CustomerHeading">
      <span v-text="t$('novelsyncApp.customer.home.title')" id="customer-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('novelsyncApp.customer.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'CustomerCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-customer"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('novelsyncApp.customer.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && customers && customers.length === 0">
      <span v-text="t$('novelsyncApp.customer.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="customers && customers.length > 0">
      <table class="table table-striped" aria-describedby="customers">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('fullName')">
              <span v-text="t$('novelsyncApp.customer.fullName')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'fullName'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('phoneNumber')">
              <span v-text="t$('novelsyncApp.customer.phoneNumber')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'phoneNumber'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="customer in customers" :key="customer.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'CustomerView', params: { customerId: customer.id } }">{{ customer.id }}</router-link>
            </td>
            <td>{{ customer.fullName }}</td>
            <td>{{ customer.phoneNumber }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'CustomerView', params: { customerId: customer.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'CustomerEdit', params: { customerId: customer.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(customer)"
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
        <span id="novelsyncApp.customer.delete.question" data-cy="customerDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-customer-heading" v-text="t$('novelsyncApp.customer.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-customer"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeCustomer()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="customers && customers.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./customer.component.ts"></script>
