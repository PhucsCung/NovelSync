import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import InventoryReceiptService from './inventory-receipt.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { InventoryReceipt } from '@/shared/model/inventory-receipt.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('InventoryReceipt Service', () => {
    let service: InventoryReceiptService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new InventoryReceiptService();
      currentDate = new Date();
      elemDefault = new InventoryReceipt(123, currentDate, 0);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = { receiptDate: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a InventoryReceipt', async () => {
        const returnedFromService = { id: 123, receiptDate: dayjs(currentDate).format(DATE_TIME_FORMAT), ...elemDefault };
        const expected = { receiptDate: currentDate, ...returnedFromService };

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a InventoryReceipt', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a InventoryReceipt', async () => {
        const returnedFromService = { receiptDate: dayjs(currentDate).format(DATE_TIME_FORMAT), totalAmount: 1, ...elemDefault };

        const expected = { receiptDate: currentDate, ...returnedFromService };
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a InventoryReceipt', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a InventoryReceipt', async () => {
        const patchObject = { receiptDate: dayjs(currentDate).format(DATE_TIME_FORMAT), totalAmount: 1, ...new InventoryReceipt() };
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = { receiptDate: currentDate, ...returnedFromService };
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a InventoryReceipt', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of InventoryReceipt', async () => {
        const returnedFromService = { receiptDate: dayjs(currentDate).format(DATE_TIME_FORMAT), totalAmount: 1, ...elemDefault };
        const expected = { receiptDate: currentDate, ...returnedFromService };
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of InventoryReceipt', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a InventoryReceipt', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a InventoryReceipt', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});
