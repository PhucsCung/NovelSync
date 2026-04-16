<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="customer">
        <h2 class="jh-entity-heading" data-cy="customerDetailsHeading">
          <span v-text="t$('novelsyncApp.customer.detail.title')"></span> {{ customer.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('novelsyncApp.customer.fullName')"></span>
          </dt>
          <dd>
            <span>{{ customer.fullName }}</span>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.customer.phoneNumber')"></span>
          </dt>
          <dd>
            <span>{{ customer.phoneNumber }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="customer.id" :to="{ name: 'CustomerEdit', params: { customerId: customer.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./customer-details.component.ts"></script>
