import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import OrderDetailDetails from './order-detail-details.vue';
import OrderDetailService from './order-detail.service';
import AlertService from '@/shared/alert/alert.service';

type OrderDetailDetailsComponentType = InstanceType<typeof OrderDetailDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const orderDetailSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('OrderDetail Management Detail Component', () => {
    let orderDetailServiceStub: SinonStubbedInstance<OrderDetailService>;
    let mountOptions: MountingOptions<OrderDetailDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      orderDetailServiceStub = sinon.createStubInstance<OrderDetailService>(OrderDetailService);

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
          orderDetailService: () => orderDetailServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        orderDetailServiceStub.find.resolves(orderDetailSample);
        route = {
          params: {
            orderDetailId: `${123}`,
          },
        };
        const wrapper = shallowMount(OrderDetailDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.orderDetail).toMatchObject(orderDetailSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        orderDetailServiceStub.find.resolves(orderDetailSample);
        const wrapper = shallowMount(OrderDetailDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
