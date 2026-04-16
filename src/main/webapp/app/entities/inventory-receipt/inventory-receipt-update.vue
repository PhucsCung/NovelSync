<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="novelsyncApp.inventoryReceipt.home.createOrEditLabel"
          data-cy="InventoryReceiptCreateUpdateHeading"
          v-text="t$('novelsyncApp.inventoryReceipt.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="inventoryReceipt.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="inventoryReceipt.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('novelsyncApp.inventoryReceipt.receiptDate')"
              for="inventory-receipt-receiptDate"
            ></label>
            <div class="d-flex">
              <input
                id="inventory-receipt-receiptDate"
                data-cy="receiptDate"
                type="datetime-local"
                class="form-control"
                name="receiptDate"
                :class="{ valid: !v$.receiptDate.$invalid, invalid: v$.receiptDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.receiptDate.$model)"
                @change="updateInstantField('receiptDate', $event)"
              />
            </div>
            <div v-if="v$.receiptDate.$anyDirty && v$.receiptDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.receiptDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('novelsyncApp.inventoryReceipt.totalAmount')"
              for="inventory-receipt-totalAmount"
            ></label>
            <input
              type="number"
              class="form-control"
              name="totalAmount"
              id="inventory-receipt-totalAmount"
              data-cy="totalAmount"
              :class="{ valid: !v$.totalAmount.$invalid, invalid: v$.totalAmount.$invalid }"
              v-model.number="v$.totalAmount.$model"
              required
            />
            <div v-if="v$.totalAmount.$anyDirty && v$.totalAmount.$invalid">
              <small class="form-text text-danger" v-for="error of v$.totalAmount.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.inventoryReceipt.user')" for="inventory-receipt-user"></label>
            <select class="form-control" id="inventory-receipt-user" data-cy="user" name="user" v-model="inventoryReceipt.user">
              <option :value="null"></option>
              <option
                :value="inventoryReceipt.user && userOption.id === inventoryReceipt.user.id ? inventoryReceipt.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.login }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" @click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./inventory-receipt-update.component.ts"></script>
