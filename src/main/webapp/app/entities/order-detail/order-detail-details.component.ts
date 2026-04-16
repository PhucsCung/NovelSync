import { type Ref, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import OrderDetailService from './order-detail.service';
import { type IOrderDetail } from '@/shared/model/order-detail.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'OrderDetailDetails',
  setup() {
    const orderDetailService = inject('orderDetailService', () => new OrderDetailService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const orderDetail: Ref<IOrderDetail> = ref({});

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

    return {
      alertService,
      orderDetail,

      previousState,
      t$: useI18n().t,
    };
  },
});
