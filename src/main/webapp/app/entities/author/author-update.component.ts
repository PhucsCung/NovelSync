import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import AuthorService from './author.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { Author, type IAuthor } from '@/shared/model/author.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AuthorUpdate',
  setup() {
    const authorService = inject('authorService', () => new AuthorService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const author: Ref<IAuthor> = ref(new Author());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveAuthor = async authorId => {
      try {
        const res = await authorService().find(authorId);
        author.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.authorId) {
      retrieveAuthor(route.params.authorId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      authorName: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, author as any);
    v$.value.$validate();

    return {
      authorService,
      alertService,
      author,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.author.id) {
        this.authorService()
          .update(this.author)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('novelsyncApp.author.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.authorService()
          .create(this.author)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('novelsyncApp.author.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
