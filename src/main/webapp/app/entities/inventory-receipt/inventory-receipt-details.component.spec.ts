import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import InventoryReceiptDetails from './inventory-receipt-details.vue';
import InventoryReceiptService from './inventory-receipt.service';
import AlertService from '@/shared/alert/alert.service';

type InventoryReceiptDetailsComponentType = InstanceType<typeof InventoryReceiptDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const inventoryReceiptSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('InventoryReceipt Management Detail Component', () => {
    let inventoryReceiptServiceStub: SinonStubbedInstance<InventoryReceiptService>;
    let mountOptions: MountingOptions<InventoryReceiptDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      inventoryReceiptServiceStub = sinon.createStubInstance<InventoryReceiptService>(InventoryReceiptService);

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
          inventoryReceiptService: () => inventoryReceiptServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        inventoryReceiptServiceStub.find.resolves(inventoryReceiptSample);
        route = {
          params: {
            inventoryReceiptId: `${123}`,
          },
        };
        const wrapper = shallowMount(InventoryReceiptDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.inventoryReceipt).toMatchObject(inventoryReceiptSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        inventoryReceiptServiceStub.find.resolves(inventoryReceiptSample);
        const wrapper = shallowMount(InventoryReceiptDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
