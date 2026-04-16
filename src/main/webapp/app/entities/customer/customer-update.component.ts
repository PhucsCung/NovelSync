import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import CustomerService from './customer.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { Customer, type ICustomer } from '@/shared/model/customer.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'CustomerUpdate',
  setup() {
    const customerService = inject('customerService', () => new CustomerService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const customer: Ref<ICustomer> = ref(new Customer());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveCustomer = async customerId => {
      try {
        const res = await customerService().find(customerId);
        customer.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.customerId) {
      retrieveCustomer(route.params.customerId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      fullName: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      phoneNumber: {},
    };
    const v$ = useVuelidate(validationRules, customer as any);
    v$.value.$validate();

    return {
      customerService,
      alertService,
      customer,
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
      if (this.customer.id) {
        this.customerService()
          .update(this.customer)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('novelsyncApp.customer.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.customerService()
          .create(this.customer)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('novelsyncApp.customer.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
