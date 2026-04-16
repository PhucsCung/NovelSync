import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import OrderDetailUpdate from './order-detail-update.vue';
import OrderDetailService from './order-detail.service';
import AlertService from '@/shared/alert/alert.service';

import OrderService from '@/entities/order/order.service';
import BookService from '@/entities/book/book.service';

type OrderDetailUpdateComponentType = InstanceType<typeof OrderDetailUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const orderDetailSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<OrderDetailUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('OrderDetail Management Update Component', () => {
    let comp: OrderDetailUpdateComponentType;
    let orderDetailServiceStub: SinonStubbedInstance<OrderDetailService>;

    beforeEach(() => {
      route = {};
      orderDetailServiceStub = sinon.createStubInstance<OrderDetailService>(OrderDetailService);
      orderDetailServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          orderDetailService: () => orderDetailServiceStub,
          orderService: () =>
            sinon.createStubInstance<OrderService>(OrderService, {
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
        const wrapper = shallowMount(OrderDetailUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.orderDetail = orderDetailSample;
        orderDetailServiceStub.update.resolves(orderDetailSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(orderDetailServiceStub.update.calledWith(orderDetailSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        orderDetailServiceStub.create.resolves(entity);
        const wrapper = shallowMount(OrderDetailUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.orderDetail = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(orderDetailServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        orderDetailServiceStub.find.resolves(orderDetailSample);
        orderDetailServiceStub.retrieve.resolves([orderDetailSample]);

        // WHEN
        route = {
          params: {
            orderDetailId: `${orderDetailSample.id}`,
          },
        };
        const wrapper = shallowMount(OrderDetailUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.orderDetail).toMatchObject(orderDetailSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        orderDetailServiceStub.find.resolves(orderDetailSample);
        const wrapper = shallowMount(OrderDetailUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
