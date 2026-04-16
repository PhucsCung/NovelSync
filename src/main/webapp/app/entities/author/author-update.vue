<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="novelsyncApp.author.home.createOrEditLabel"
          data-cy="AuthorCreateUpdateHeading"
          v-text="t$('novelsyncApp.author.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="author.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="author.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.author.authorName')" for="author-authorName"></label>
            <input
              type="text"
              class="form-control"
              name="authorName"
              id="author-authorName"
              data-cy="authorName"
              :class="{ valid: !v$.authorName.$invalid, invalid: v$.authorName.$invalid }"
              v-model="v$.authorName.$model"
              required
            />
            <div v-if="v$.authorName.$anyDirty && v$.authorName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.authorName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
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
<script lang="ts" src="./author-update.component.ts"></script>
