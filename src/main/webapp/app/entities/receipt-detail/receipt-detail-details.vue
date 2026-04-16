<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="receiptDetail">
        <h2 class="jh-entity-heading" data-cy="receiptDetailDetailsHeading">
          <span v-text="t$('novelsyncApp.receiptDetail.detail.title')"></span> {{ receiptDetail.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('novelsyncApp.receiptDetail.quantity')"></span>
          </dt>
          <dd>
            <span>{{ receiptDetail.quantity }}</span>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.receiptDetail.purchasePrice')"></span>
          </dt>
          <dd>
            <span>{{ receiptDetail.purchasePrice }}</span>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.receiptDetail.receipt')"></span>
          </dt>
          <dd>
            <div v-if="receiptDetail.receipt">
              <router-link :to="{ name: 'InventoryReceiptView', params: { inventoryReceiptId: receiptDetail.receipt.id } }">{{
                receiptDetail.receipt.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.receiptDetail.book')"></span>
          </dt>
          <dd>
            <div v-if="receiptDetail.book">
              <router-link :to="{ name: 'BookView', params: { bookId: receiptDetail.book.id } }">{{
                receiptDetail.book.title
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="receiptDetail.id"
          :to="{ name: 'ReceiptDetailEdit', params: { receiptDetailId: receiptDetail.id } }"
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

<script lang="ts" src="./receipt-detail-details.component.ts"></script>
