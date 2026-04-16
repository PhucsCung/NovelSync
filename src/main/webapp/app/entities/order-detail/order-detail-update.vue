<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="novelsyncApp.orderDetail.home.createOrEditLabel"
          data-cy="OrderDetailCreateUpdateHeading"
          v-text="t$('novelsyncApp.orderDetail.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="orderDetail.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="orderDetail.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.orderDetail.quantity')" for="order-detail-quantity"></label>
            <input
              type="number"
              class="form-control"
              name="quantity"
              id="order-detail-quantity"
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
            <label class="form-control-label" v-text="t$('novelsyncApp.orderDetail.salePrice')" for="order-detail-salePrice"></label>
            <input
              type="number"
              class="form-control"
              name="salePrice"
              id="order-detail-salePrice"
              data-cy="salePrice"
              :class="{ valid: !v$.salePrice.$invalid, invalid: v$.salePrice.$invalid }"
              v-model.number="v$.salePrice.$model"
              required
            />
            <div v-if="v$.salePrice.$anyDirty && v$.salePrice.$invalid">
              <small class="form-text text-danger" v-for="error of v$.salePrice.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.orderDetail.order')" for="order-detail-order"></label>
            <select class="form-control" id="order-detail-order" data-cy="order" name="order" v-model="orderDetail.order">
              <option :value="null"></option>
              <option
                :value="orderDetail.order && orderOption.id === orderDetail.order.id ? orderDetail.order : orderOption"
                v-for="orderOption in orders"
                :key="orderOption.id"
              >
                {{ orderOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.orderDetail.book')" for="order-detail-book"></label>
            <select class="form-control" id="order-detail-book" data-cy="book" name="book" v-model="orderDetail.book">
              <option :value="null"></option>
              <option
                :value="orderDetail.book && bookOption.id === orderDetail.book.id ? orderDetail.book : bookOption"
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
<script lang="ts" src="./order-detail-update.component.ts"></script>
