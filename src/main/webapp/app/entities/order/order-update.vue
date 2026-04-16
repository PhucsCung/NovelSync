<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="novelsyncApp.order.home.createOrEditLabel"
          data-cy="OrderCreateUpdateHeading"
          v-text="t$('novelsyncApp.order.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="order.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="order.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.order.orderDate')" for="order-orderDate"></label>
            <div class="d-flex">
              <input
                id="order-orderDate"
                data-cy="orderDate"
                type="datetime-local"
                class="form-control"
                name="orderDate"
                :class="{ valid: !v$.orderDate.$invalid, invalid: v$.orderDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.orderDate.$model)"
                @change="updateInstantField('orderDate', $event)"
              />
            </div>
            <div v-if="v$.orderDate.$anyDirty && v$.orderDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.orderDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.order.totalAmount')" for="order-totalAmount"></label>
            <input
              type="number"
              class="form-control"
              name="totalAmount"
              id="order-totalAmount"
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
            <label class="form-control-label" v-text="t$('novelsyncApp.order.status')" for="order-status"></label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="order-status"
              data-cy="status"
            >
              <option
                v-for="orderStatus in orderStatusValues"
                :key="orderStatus"
                :value="orderStatus"
                :label="t$('novelsyncApp.OrderStatus.' + orderStatus)"
              >
                {{ orderStatus }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.order.customer')" for="order-customer"></label>
            <select class="form-control" id="order-customer" data-cy="customer" name="customer" v-model="order.customer">
              <option :value="null"></option>
              <option
                :value="order.customer && customerOption.id === order.customer.id ? order.customer : customerOption"
                v-for="customerOption in customers"
                :key="customerOption.id"
              >
                {{ customerOption.fullName }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.order.user')" for="order-user"></label>
            <select class="form-control" id="order-user" data-cy="user" name="user" v-model="order.user">
              <option :value="null"></option>
              <option
                :value="order.user && userOption.id === order.user.id ? order.user : userOption"
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
<script lang="ts" src="./order-update.component.ts"></script>
