import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ReceiptDetailDetails from './receipt-detail-details.vue';
import ReceiptDetailService from './receipt-detail.service';
import AlertService from '@/shared/alert/alert.service';

type ReceiptDetailDetailsComponentType = InstanceType<typeof ReceiptDetailDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const receiptDetailSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('ReceiptDetail Management Detail Component', () => {
    let receiptDetailServiceStub: SinonStubbedInstance<ReceiptDetailService>;
    let mountOptions: MountingOptions<ReceiptDetailDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      receiptDetailServiceStub = sinon.createStubInstance<ReceiptDetailService>(ReceiptDetailService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          receiptDetailService: () => receiptDetailServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        receiptDetailServiceStub.find.resolves(receiptDetailSample);
        route = {
          params: {
            receiptDetailId: `${123}`,
          },
        };
        const wrapper = shallowMount(ReceiptDetailDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.receiptDetail).toMatchObject(receiptDetailSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        receiptDetailServiceStub.find.resolves(receiptDetailSample);
        const wrapper = shallowMount(ReceiptDetailDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
