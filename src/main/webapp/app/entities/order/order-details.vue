<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="order">
        <h2 class="jh-entity-heading" data-cy="orderDetailsHeading">
          <span v-text="t$('novelsyncApp.order.detail.title')"></span> {{ order.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('novelsyncApp.order.orderDate')"></span>
          </dt>
          <dd>
            <span v-if="order.orderDate">{{ formatDateLong(order.orderDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.order.totalAmount')"></span>
          </dt>
          <dd>
            <span>{{ order.totalAmount }}</span>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.order.status')"></span>
          </dt>
          <dd>
            <span v-text="t$('novelsyncApp.OrderStatus.' + order.status)"></span>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.order.customer')"></span>
          </dt>
          <dd>
            <div v-if="order.customer">
              <router-link :to="{ name: 'CustomerView', params: { customerId: order.customer.id } }">{{
                order.customer.fullName
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.order.user')"></span>
          </dt>
          <dd>
            {{ order.user ? order.user.login : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="order.id" :to="{ name: 'OrderEdit', params: { orderId: order.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./order-details.component.ts"></script>
