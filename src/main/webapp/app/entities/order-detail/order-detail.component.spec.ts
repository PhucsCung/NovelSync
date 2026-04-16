import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import OrderDetail from './order-detail.vue';
import OrderDetailService from './order-detail.service';
import AlertService from '@/shared/alert/alert.service';

type OrderDetailComponentType = InstanceType<typeof OrderDetail>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('OrderDetail Management Component', () => {
    let orderDetailServiceStub: SinonStubbedInstance<OrderDetailService>;
    let mountOptions: MountingOptions<OrderDetailComponentType>['global'];

    beforeEach(() => {
      orderDetailServiceStub = sinon.createStubInstance<OrderDetailService>(OrderDetailService);
      orderDetailServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          orderDetailService: () => orderDetailServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        orderDetailServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(OrderDetail, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(orderDetailServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.orderDetails[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(OrderDetail, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(orderDetailServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: OrderDetailComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(OrderDetail, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        orderDetailServiceStub.retrieve.reset();
        orderDetailServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        orderDetailServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(orderDetailServiceStub.retrieve.called).toBeTruthy();
        expect(comp.orderDetails[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(orderDetailServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        orderDetailServiceStub.retrieve.reset();
        orderDetailServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(orderDetailServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.orderDetails[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(orderDetailServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        orderDetailServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeOrderDetail();
        await comp.$nextTick(); // clear components

        // THEN
        expect(orderDetailServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(orderDetailServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
