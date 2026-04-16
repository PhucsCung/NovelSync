<template>
  <div>
    <h2 id="page-heading" data-cy="EmployeeHeading">
      <span v-text="t$('novelsyncApp.employee.home.title')" id="employee-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('novelsyncApp.employee.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'EmployeeCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-employee"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('novelsyncApp.employee.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && employees && employees.length === 0">
      <span v-text="t$('novelsyncApp.employee.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="employees && employees.length > 0">
      <table class="table table-striped" aria-describedby="employees">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('fullName')">
              <span v-text="t$('novelsyncApp.employee.fullName')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'fullName'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('hireDate')">
              <span v-text="t$('novelsyncApp.employee.hireDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'hireDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('salary')">
              <span v-text="t$('novelsyncApp.employee.salary')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'salary'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('user.login')">
              <span v-text="t$('novelsyncApp.employee.user')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'user.login'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="employee in employees" :key="employee.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'EmployeeView', params: { employeeId: employee.id } }">{{ employee.id }}</router-link>
            </td>
            <td>{{ employee.fullName }}</td>
            <td>{{ formatDateShort(employee.hireDate) || '' }}</td>
            <td>{{ employee.salary }}</td>
            <td>
              {{ employee.user ? employee.user.login : '' }}
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'EmployeeView', params: { employeeId: employee.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'EmployeeEdit', params: { employeeId: employee.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(employee)"
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
        <span id="novelsyncApp.employee.delete.question" data-cy="employeeDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-employee-heading" v-text="t$('novelsyncApp.employee.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-employee"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeEmployee()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="employees && employees.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./employee.component.ts"></script>
