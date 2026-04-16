import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ReceiptDetailUpdate from './receipt-detail-update.vue';
import ReceiptDetailService from './receipt-detail.service';
import AlertService from '@/shared/alert/alert.service';

import InventoryReceiptService from '@/entities/inventory-receipt/inventory-receipt.service';
import BookService from '@/entities/book/book.service';

type ReceiptDetailUpdateComponentType = InstanceType<typeof ReceiptDetailUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const receiptDetailSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ReceiptDetailUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ReceiptDetail Management Update Component', () => {
    let comp: ReceiptDetailUpdateComponentType;
    let receiptDetailServiceStub: SinonStubbedInstance<ReceiptDetailService>;

    beforeEach(() => {
      route = {};
      receiptDetailServiceStub = sinon.createStubInstance<ReceiptDetailService>(ReceiptDetailService);
      receiptDetailServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          receiptDetailService: () => receiptDetailServiceStub,
          inventoryReceiptService: () =>
            sinon.createStubInstance<InventoryReceiptService>(InventoryReceiptService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          bookService: () =>
            sinon.createStubInstance<BookService>(BookService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ReceiptDetailUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.receiptDetail = receiptDetailSample;
        receiptDetailServiceStub.update.resolves(receiptDetailSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(receiptDetailServiceStub.update.calledWith(receiptDetailSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        receiptDetailServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ReceiptDetailUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.receiptDetail = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(receiptDetailServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        receiptDetailServiceStub.find.resolves(receiptDetailSample);
        receiptDetailServiceStub.retrieve.resolves([receiptDetailSample]);

        // WHEN
        route = {
          params: {
            receiptDetailId: `${receiptDetailSample.id}`,
          },
        };
        const wrapper = shallowMount(ReceiptDetailUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.receiptDetail).toMatchObject(receiptDetailSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        receiptDetailServiceStub.find.resolves(receiptDetailSample);
        const wrapper = shallowMount(ReceiptDetailUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
