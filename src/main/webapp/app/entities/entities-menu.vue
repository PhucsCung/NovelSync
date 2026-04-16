<template>
  <div>
    <b-dropdown-item to="/category">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.category')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/author">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.author')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/book">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.book')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/inventory-receipt">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.inventoryReceipt')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/receipt-detail">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.receiptDetail')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/customer">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.customer')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/order">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.order')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/order-detail">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.orderDetail')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/employee">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.employee')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
