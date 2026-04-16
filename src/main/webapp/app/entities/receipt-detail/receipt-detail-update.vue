<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="novelsyncApp.receiptDetail.home.createOrEditLabel"
          data-cy="ReceiptDetailCreateUpdateHeading"
          v-text="t$('novelsyncApp.receiptDetail.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="receiptDetail.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="receiptDetail.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.receiptDetail.quantity')" for="receipt-detail-quantity"></label>
            <input
              type="number"
              class="form-control"
              name="quantity"
              id="receipt-detail-quantity"
              data-cy="quantity"
              :class="{ valid: !v$.quantity.$invalid, invalid: v$.quantity.$invalid }"
              v-model.number="v$.quantity.$model"
              required
            />
            <div v-if="v$.quantity.$anyDirty && v$.quantity.$invalid">
              <small class="form-text text-danger" v-for="error of v$.quantity.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('novelsyncApp.receiptDetail.purchasePrice')"
              for="receipt-detail-purchasePrice"
            ></label>
            <input
              type="number"
              class="form-control"
              name="purchasePrice"
              id="receipt-detail-purchasePrice"
              data-cy="purchasePrice"
              :class="{ valid: !v$.purchasePrice.$invalid, invalid: v$.purchasePrice.$invalid }"
              v-model.number="v$.purchasePrice.$model"
              required
            />
            <div v-if="v$.purchasePrice.$anyDirty && v$.purchasePrice.$invalid">
              <small class="form-text text-danger" v-for="error of v$.purchasePrice.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.receiptDetail.receipt')" for="receipt-detail-receipt"></label>
            <select class="form-control" id="receipt-detail-receipt" data-cy="receipt" name="receipt" v-model="receiptDetail.receipt">
              <option :value="null"></option>
              <option
                :value="
                  receiptDetail.receipt && inventoryReceiptOption.id === receiptDetail.receipt.id
                    ? receiptDetail.receipt
                    : inventoryReceiptOption
                "
                v-for="inventoryReceiptOption in inventoryReceipts"
                :key="inventoryReceiptOption.id"
              >
                {{ inventoryReceiptOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.receiptDetail.book')" for="receipt-detail-book"></label>
            <select class="form-control" id="receipt-detail-book" data-cy="book" name="book" v-model="receiptDetail.book">
              <option :value="null"></option>
              <option
                :value="receiptDetail.book && bookOption.id === receiptDetail.book.id ? receiptDetail.book : bookOption"
                v-for="bookOption in books"
                :key="bookOption.id"
              >
                {{ bookOption.title }}
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
<script lang="ts" src="./receipt-detail-update.component.ts"></script>
