<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="novelsyncApp.employee.home.createOrEditLabel"
          data-cy="EmployeeCreateUpdateHeading"
          v-text="t$('novelsyncApp.employee.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="employee.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="employee.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.employee.fullName')" for="employee-fullName"></label>
            <input
              type="text"
              class="form-control"
              name="fullName"
              id="employee-fullName"
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
            <label class="form-control-label" v-text="t$('novelsyncApp.employee.hireDate')" for="employee-hireDate"></label>
            <div class="d-flex">
              <input
                id="employee-hireDate"
                data-cy="hireDate"
                type="datetime-local"
                class="form-control"
                name="hireDate"
                :class="{ valid: !v$.hireDate.$invalid, invalid: v$.hireDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.hireDate.$model)"
                @change="updateInstantField('hireDate', $event)"
              />
            </div>
            <div v-if="v$.hireDate.$anyDirty && v$.hireDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.hireDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.employee.salary')" for="employee-salary"></label>
            <input
              type="number"
              class="form-control"
              name="salary"
              id="employee-salary"
              data-cy="salary"
              :class="{ valid: !v$.salary.$invalid, invalid: v$.salary.$invalid }"
              v-model.number="v$.salary.$model"
              required
            />
            <div v-if="v$.salary.$anyDirty && v$.salary.$invalid">
              <small class="form-text text-danger" v-for="error of v$.salary.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.employee.user')" for="employee-user"></label>
            <select class="form-control" id="employee-user" data-cy="user" name="user" v-model="employee.user">
              <option :value="null"></option>
              <option
                :value="employee.user && userOption.id === employee.user.id ? employee.user : userOption"
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
<script lang="ts" src="./employee-update.component.ts"></script>
