import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import InventoryReceipt from './inventory-receipt.vue';
import InventoryReceiptService from './inventory-receipt.service';
import AlertService from '@/shared/alert/alert.service';

type InventoryReceiptComponentType = InstanceType<typeof InventoryReceipt>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('InventoryReceipt Management Component', () => {
    let inventoryReceiptServiceStub: SinonStubbedInstance<InventoryReceiptService>;
    let mountOptions: MountingOptions<InventoryReceiptComponentType>['global'];

    beforeEach(() => {
      inventoryReceiptServiceStub = sinon.createStubInstance<InventoryReceiptService>(InventoryReceiptService);
      inventoryReceiptServiceStub.retrieve.resolves({ headers: {} });

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
          inventoryReceiptService: () => inventoryReceiptServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        inventoryReceiptServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(InventoryReceipt, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(inventoryReceiptServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.inventoryReceipts[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(InventoryReceipt, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(inventoryReceiptServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: InventoryReceiptComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(InventoryReceipt, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        inventoryReceiptServiceStub.retrieve.reset();
        inventoryReceiptServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        inventoryReceiptServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(inventoryReceiptServiceStub.retrieve.called).toBeTruthy();
        expect(comp.inventoryReceipts[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(inventoryReceiptServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        inventoryReceiptServiceStub.retrieve.reset();
        inventoryReceiptServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(inventoryReceiptServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.inventoryReceipts[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(inventoryReceiptServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        inventoryReceiptServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeInventoryReceipt();
        await comp.$nextTick(); // clear components

        // THEN
        expect(inventoryReceiptServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(inventoryReceiptServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
