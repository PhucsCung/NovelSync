<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="orderDetail">
        <h2 class="jh-entity-heading" data-cy="orderDetailDetailsHeading">
          <span v-text="t$('novelsyncApp.orderDetail.detail.title')"></span> {{ orderDetail.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('novelsyncApp.orderDetail.quantity')"></span>
          </dt>
          <dd>
            <span>{{ orderDetail.quantity }}</span>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.orderDetail.salePrice')"></span>
          </dt>
          <dd>
            <span>{{ orderDetail.salePrice }}</span>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.orderDetail.order')"></span>
          </dt>
          <dd>
            <div v-if="orderDetail.order">
              <router-link :to="{ name: 'OrderView', params: { orderId: orderDetail.order.id } }">{{ orderDetail.order.id }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.orderDetail.book')"></span>
          </dt>
          <dd>
            <div v-if="orderDetail.book">
              <router-link :to="{ name: 'BookView', params: { bookId: orderDetail.book.id } }">{{ orderDetail.book.title }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="orderDetail.id"
          :to="{ name: 'OrderDetailEdit', params: { orderDetailId: orderDetail.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./order-detail-details.component.ts"></script>
