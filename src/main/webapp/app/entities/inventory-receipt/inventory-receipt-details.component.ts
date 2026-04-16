import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import InventoryReceiptService from './inventory-receipt.service';
import { useDateFormat } from '@/shared/composables';
import { type IInventoryReceipt } from '@/shared/model/inventory-receipt.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'InventoryReceiptDetails',
  setup() {
    const dateFormat = useDateFormat();
    const inventoryReceiptService = inject('inventoryReceiptService', () => new InventoryReceiptService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const inventoryReceipt: Ref<IInventoryReceipt> = ref({});

    const retrieveInventoryReceipt = async inventoryReceiptId => {
      try {
        const res = await inventoryReceiptService().find(inventoryReceiptId);
        inventoryReceipt.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.inventoryReceiptId) {
      retrieveInventoryReceipt(route.params.inventoryReceiptId);
    }

    return {
      ...dateFormat,
      alertService,
      inventoryReceipt,

      previousState,
      t$: useI18n().t,
    };
  },
});
