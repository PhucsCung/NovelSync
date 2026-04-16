import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import ReceiptDetailService from './receipt-detail.service';
import { type IReceiptDetail } from '@/shared/model/receipt-detail.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ReceiptDetailDetails',
  setup() {
    const receiptDetailService = inject('receiptDetailService', () => new ReceiptDetailService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const receiptDetail: Ref<IReceiptDetail> = ref({});

    const retrieveReceiptDetail = async receiptDetailId => {
      try {
        const res = await receiptDetailService().find(receiptDetailId);
        receiptDetail.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.receiptDetailId) {
      retrieveReceiptDetail(route.params.receiptDetailId);
    }

    return {
      alertService,
      receiptDetail,

      previousState,
      t$: useI18n().t,
    };
  },
});
