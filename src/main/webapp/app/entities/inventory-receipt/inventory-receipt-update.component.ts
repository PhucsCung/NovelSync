import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import InventoryReceiptService from './inventory-receipt.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { type IInventoryReceipt, InventoryReceipt } from '@/shared/model/inventory-receipt.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InventoryReceiptUpdate',
  setup() {
    const inventoryReceiptService = inject('inventoryReceiptService', () => new InventoryReceiptService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const inventoryReceipt: Ref<IInventoryReceipt> = ref(new InventoryReceipt());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveInventoryReceipt = async inventoryReceiptId => {
      try {
        const res = await inventoryReceiptService().find(inventoryReceiptId);
        res.receiptDate = new Date(res.receiptDate);
        inventoryReceipt.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.inventoryReceiptId) {
      retrieveInventoryReceipt(route.params.inventoryReceiptId);
    }

    const initRelationships = () => {
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
      receiptDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      totalAmount: {
        required: validations.required(t$('entity.validation.required').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      user: {},
    };
    const v$ = useVuelidate(validationRules, inventoryReceipt as any);
    v$.value.$validate();

    return {
      inventoryReceiptService,
      alertService,
      inventoryReceipt,
      previousState,
      isSaving,
      currentLanguage,
      users,
      v$,
      ...useDateFormat({ entityRef: inventoryReceipt }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.inventoryReceipt.id) {
        this.inventoryReceiptService()
          .update(this.inventoryReceipt)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('novelsyncApp.inventoryReceipt.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.inventoryReceiptService()
          .create(this.inventoryReceipt)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('novelsyncApp.inventoryReceipt.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
