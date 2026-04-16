import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import AuthorService from './author.service';
import { type IAuthor } from '@/shared/model/author.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'AuthorDetails',
  setup() {
    const authorService = inject('authorService', () => new AuthorService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const author: Ref<IAuthor> = ref({});

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

    return {
      alertService,
      author,

      previousState,
      t$: useI18n().t,
    };
  },
});
