import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ReceiptDetailService from './receipt-detail.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import InventoryReceiptService from '@/entities/inventory-receipt/inventory-receipt.service';
import { type IInventoryReceipt } from '@/shared/model/inventory-receipt.model';
import BookService from '@/entities/book/book.service';
import { type IBook } from '@/shared/model/book.model';
import { type IReceiptDetail, ReceiptDetail } from '@/shared/model/receipt-detail.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ReceiptDetailUpdate',
  setup() {
    const receiptDetailService = inject('receiptDetailService', () => new ReceiptDetailService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const receiptDetail: Ref<IReceiptDetail> = ref(new ReceiptDetail());

    const inventoryReceiptService = inject('inventoryReceiptService', () => new InventoryReceiptService());

    const inventoryReceipts: Ref<IInventoryReceipt[]> = ref([]);

    const bookService = inject('bookService', () => new BookService());

    const books: Ref<IBook[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {
      inventoryReceiptService()
        .retrieve()
        .then(res => {
          inventoryReceipts.value = res.data;
        });
      bookService()
        .retrieve()
        .then(res => {
          books.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      quantity: {
        required: validations.required(t$('entity.validation.required').toString()),
        integer: validations.integer(t$('entity.validation.number').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 1 }).toString(), 1),
      },
      purchasePrice: {
        required: validations.required(t$('entity.validation.required').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      receipt: {},
      book: {},
    };
    const v$ = useVuelidate(validationRules, receiptDetail as any);
    v$.value.$validate();

    return {
      receiptDetailService,
      alertService,
      receiptDetail,
      previousState,
      isSaving,
      currentLanguage,
      inventoryReceipts,
      books,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.receiptDetail.id) {
        this.receiptDetailService()
          .update(this.receiptDetail)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('novelsyncApp.receiptDetail.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.receiptDetailService()
          .create(this.receiptDetail)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('novelsyncApp.receiptDetail.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
