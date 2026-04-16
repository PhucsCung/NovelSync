<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="novelsyncApp.customer.home.createOrEditLabel"
          data-cy="CustomerCreateUpdateHeading"
          v-text="t$('novelsyncApp.customer.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="customer.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="customer.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.customer.fullName')" for="customer-fullName"></label>
            <input
              type="text"
              class="form-control"
              name="fullName"
              id="customer-fullName"
              data-cy="fullName"
              :class="{ valid: !v$.fullName.$invalid, invalid: v$.fullName.$invalid }"
              v-model="v$.fullName.$model"
              required
            />
            <div v-if="v$.fullName.$anyDirty && v$.fullName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.fullName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.customer.phoneNumber')" for="customer-phoneNumber"></label>
            <input
              type="text"
              class="form-control"
              name="phoneNumber"
              id="customer-phoneNumber"
              data-cy="phoneNumber"
              :class="{ valid: !v$.phoneNumber.$invalid, invalid: v$.phoneNumber.$invalid }"
              v-model="v$.phoneNumber.$model"
            />
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
<script lang="ts" src="./customer-update.component.ts"></script>
