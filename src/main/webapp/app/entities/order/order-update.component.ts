import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import OrderService from './order.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import CustomerService from '@/entities/customer/customer.service';
import { type ICustomer } from '@/shared/model/customer.model';
import UserService from '@/entities/user/user.service';
import { type IOrder, Order } from '@/shared/model/order.model';
import { OrderStatus } from '@/shared/model/enumerations/order-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderUpdate',
  setup() {
    const orderService = inject('orderService', () => new OrderService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const order: Ref<IOrder> = ref(new Order());

    const customerService = inject('customerService', () => new CustomerService());

    const customers: Ref<ICustomer[]> = ref([]);
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const orderStatusValues: Ref<string[]> = ref(Object.keys(OrderStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveOrder = async orderId => {
      try {
        const res = await orderService().find(orderId);
        res.orderDate = new Date(res.orderDate);
        order.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.orderId) {
      retrieveOrder(route.params.orderId);
    }

    const initRelationships = () => {
      customerService()
        .retrieve()
        .then(res => {
          customers.value = res.data;
        });
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      orderDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      totalAmount: {
        required: validations.required(t$('entity.validation.required').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      status: {},
      customer: {},
      user: {},
    };
    const v$ = useVuelidate(validationRules, order as any);
    v$.value.$validate();

    return {
      orderService,
      alertService,
      order,
      previousState,
      orderStatusValues,
      isSaving,
      currentLanguage,
      customers,
      users,
      v$,
      ...useDateFormat({ entityRef: order }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.order.id) {
        this.orderService()
          .update(this.order)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('novelsyncApp.order.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.orderService()
          .create(this.order)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('novelsyncApp.order.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
