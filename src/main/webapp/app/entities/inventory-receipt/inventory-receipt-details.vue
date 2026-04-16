<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="inventoryReceipt">
        <h2 class="jh-entity-heading" data-cy="inventoryReceiptDetailsHeading">
          <span v-text="t$('novelsyncApp.inventoryReceipt.detail.title')"></span> {{ inventoryReceipt.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('novelsyncApp.inventoryReceipt.receiptDate')"></span>
          </dt>
          <dd>
            <span v-if="inventoryReceipt.receiptDate">{{ formatDateLong(inventoryReceipt.receiptDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.inventoryReceipt.totalAmount')"></span>
          </dt>
          <dd>
            <span>{{ inventoryReceipt.totalAmount }}</span>
          </dd>
          <dt>
            <span v-text="t$('novelsyncApp.inventoryReceipt.user')"></span>
          </dt>
          <dd>
            {{ inventoryReceipt.user ? inventoryReceipt.user.login : '' }}
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="inventoryReceipt.id"
          :to="{ name: 'InventoryReceiptEdit', params: { inventoryReceiptId: inventoryReceipt.id } }"
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

<script lang="ts" src="./inventory-receipt-details.component.ts"></script>
