<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate @submit.prevent="save()">
        <h2
          id="novelsyncApp.book.home.createOrEditLabel"
          data-cy="BookCreateUpdateHeading"
          v-text="t$('novelsyncApp.book.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="book.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="book.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.book.title')" for="book-title"></label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="book-title"
              data-cy="title"
              :class="{ valid: !v$.title.$invalid, invalid: v$.title.$invalid }"
              v-model="v$.title.$model"
              required
            />
            <div v-if="v$.title.$anyDirty && v$.title.$invalid">
              <small class="form-text text-danger" v-for="error of v$.title.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.book.isbn')" for="book-isbn"></label>
            <input
              type="text"
              class="form-control"
              name="isbn"
              id="book-isbn"
              data-cy="isbn"
              :class="{ valid: !v$.isbn.$invalid, invalid: v$.isbn.$invalid }"
              v-model="v$.isbn.$model"
            />
            <div v-if="v$.isbn.$anyDirty && v$.isbn.$invalid">
              <small class="form-text text-danger" v-for="error of v$.isbn.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.book.price')" for="book-price"></label>
            <input
              type="number"
              class="form-control"
              name="price"
              id="book-price"
              data-cy="price"
              :class="{ valid: !v$.price.$invalid, invalid: v$.price.$invalid }"
              v-model.number="v$.price.$model"
              required
            />
            <div v-if="v$.price.$anyDirty && v$.price.$invalid">
              <small class="form-text text-danger" v-for="error of v$.price.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.book.stockQuantity')" for="book-stockQuantity"></label>
            <input
              type="number"
              class="form-control"
              name="stockQuantity"
              id="book-stockQuantity"
              data-cy="stockQuantity"
              :class="{ valid: !v$.stockQuantity.$invalid, invalid: v$.stockQuantity.$invalid }"
              v-model.number="v$.stockQuantity.$model"
              required
            />
            <div v-if="v$.stockQuantity.$anyDirty && v$.stockQuantity.$invalid">
              <small class="form-text text-danger" v-for="error of v$.stockQuantity.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.book.imageUrl')" for="book-imageUrl"></label>
            <input
              type="text"
              class="form-control"
              name="imageUrl"
              id="book-imageUrl"
              data-cy="imageUrl"
              :class="{ valid: !v$.imageUrl.$invalid, invalid: v$.imageUrl.$invalid }"
              v-model="v$.imageUrl.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.book.category')" for="book-category"></label>
            <select class="form-control" id="book-category" data-cy="category" name="category" v-model="book.category">
              <option :value="null"></option>
              <option
                :value="book.category && categoryOption.id === book.category.id ? book.category : categoryOption"
                v-for="categoryOption in categories"
                :key="categoryOption.id"
              >
                {{ categoryOption.categoryName }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('novelsyncApp.book.author')" for="book-author"></label>
            <select class="form-control" id="book-author" data-cy="author" name="author" v-model="book.author">
              <option :value="null"></option>
              <option
                :value="book.author && authorOption.id === book.author.id ? book.author : authorOption"
                v-for="authorOption in authors"
                :key="authorOption.id"
              >
                {{ authorOption.authorName }}
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
<script lang="ts" src="./book-update.component.ts"></script>
