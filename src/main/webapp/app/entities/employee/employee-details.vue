<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="employee">
        <h2 class="jh-entity-heading" data-cy="employeeDetailsHeading">
          <span v-text="t$('novelsyncApp.employee.detail.title')"></span> {{ employee.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('novelsyncApp.employee.fullName')"></span>
          </dt>
          <dd>
            <span>{{ employee.fullName }}</span>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.employee.hireDate')"></span>
          </dt>
          <dd>
            <span v-if="employee.hireDate">{{ formatDateLong(employee.hireDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.employee.salary')"></span>
          </dt>
          <dd>
            <span>{{ employee.salary }}</span>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.employee.user')"></span>
          </dt>
          <dd>
            {{ employee.user ? employee.user.login : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="employee.id" :to="{ name: 'EmployeeEdit', params: { employeeId: employee.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./employee-details.component.ts"></script>
