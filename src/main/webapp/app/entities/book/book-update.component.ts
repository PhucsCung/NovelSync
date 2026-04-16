import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import BookService from './book.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CategoryService from '@/entities/category/category.service';
import { type ICategory } from '@/shared/model/category.model';
import AuthorService from '@/entities/author/author.service';
import { type IAuthor } from '@/shared/model/author.model';
import { Book, type IBook } from '@/shared/model/book.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'BookUpdate',
  setup() {
    const bookService = inject('bookService', () => new BookService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const book: Ref<IBook> = ref(new Book());

    const categoryService = inject('categoryService', () => new CategoryService());

    const categories: Ref<ICategory[]> = ref([]);

    const authorService = inject('authorService', () => new AuthorService());

    const authors: Ref<IAuthor[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveBook = async bookId => {
      try {
        const res = await bookService().find(bookId);
        book.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.bookId) {
      retrieveBook(route.params.bookId);
    }

    const initRelationships = () => {
      categoryService()
        .retrieve()
        .then(res => {
          categories.value = res.data;
        });
      authorService()
        .retrieve()
        .then(res => {
          authors.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      title: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      isbn: {},
      price: {
        required: validations.required(t$('entity.validation.required').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      stockQuantity: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      imageUrl: {},
      category: {},
      author: {},
    };
    const v$ = useVuelidate(validationRules, book as any);
    v$.value.$validate();

    return {
      bookService,
      alertService,
      book,
      previousState,
      isSaving,
      currentLanguage,
      categories,
      authors,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.book.id) {
        this.bookService()
          .update(this.book)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('novelsyncApp.book.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.bookService()
          .create(this.book)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('novelsyncApp.book.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
