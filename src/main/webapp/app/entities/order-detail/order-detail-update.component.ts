import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import OrderDetailService from './order-detail.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import OrderService from '@/entities/order/order.service';
import { type IOrder } from '@/shared/model/order.model';
import BookService from '@/entities/book/book.service';
import { type IBook } from '@/shared/model/book.model';
import { type IOrderDetail, OrderDetail } from '@/shared/model/order-detail.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderDetailUpdate',
  setup() {
    const orderDetailService = inject('orderDetailService', () => new OrderDetailService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const orderDetail: Ref<IOrderDetail> = ref(new OrderDetail());

    const orderService = inject('orderService', () => new OrderService());

    const orders: Ref<IOrder[]> = ref([]);

    const bookService = inject('bookService', () => new BookService());

    const books: Ref<IBook[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveOrderDetail = async orderDetailId => {
      try {
        const res = await orderDetailService().find(orderDetailId);
        orderDetail.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.orderDetailId) {
      retrieveOrderDetail(route.params.orderDetailId);
    }

    const initRelationships = () => {
      orderService()
        .retrieve()
        .then(res => {
          orders.value = res.data;
        });
      bookService()
        .retrieve()
        .then(res => {
          books.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      quantity: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 1 }).toString(), 1),
      },
      salePrice: {
        required: validations.required(t$('entity.validation.required').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      order: {},
      book: {},
    };
    const v$ = useVuelidate(validationRules, orderDetail as any);
    v$.value.$validate();

    return {
      orderDetailService,
      alertService,
      orderDetail,
      previousState,
      isSaving,
      currentLanguage,
      orders,
      books,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.orderDetail.id) {
        this.orderDetailService()
          .update(this.orderDetail)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('novelsyncApp.orderDetail.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.orderDetailService()
          .create(this.orderDetail)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('novelsyncApp.orderDetail.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
