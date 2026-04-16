<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="author">
        <h2 class="jh-entity-heading" data-cy="authorDetailsHeading">
          <span v-text="t$('novelsyncApp.author.detail.title')"></span> {{ author.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('novelsyncApp.author.authorName')"></span>
          </dt>
          <dd>
            <span>{{ author.authorName }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="author.id" :to="{ name: 'AuthorEdit', params: { authorId: author.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./author-details.component.ts"></script>
