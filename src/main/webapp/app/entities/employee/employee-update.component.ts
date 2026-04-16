import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import EmployeeService from './employee.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import { Employee, type IEmployee } from '@/shared/model/employee.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'EmployeeUpdate',
  setup() {
    const employeeService = inject('employeeService', () => new EmployeeService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const employee: Ref<IEmployee> = ref(new Employee());
    const userService = inject('userService', () => new UserService());
    const users: Ref<Array<any>> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveEmployee = async employeeId => {
      try {
        const res = await employeeService().find(employeeId);
        res.hireDate = new Date(res.hireDate);
        employee.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.employeeId) {
      retrieveEmployee(route.params.employeeId);
    }

    const initRelationships = () => {
      userService()
        .retrieve()
        .then(res => {
          users.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      fullName: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      hireDate: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      salary: {
        required: validations.required(t$('entity.validation.required').toString()),
        min: validations.minValue(t$('entity.validation.min', { min: 0 }).toString(), 0),
      },
      user: {},
    };
    const v$ = useVuelidate(validationRules, employee as any);
    v$.value.$validate();

    return {
      employeeService,
      alertService,
      employee,
      previousState,
      isSaving,
      currentLanguage,
      users,
      v$,
      ...useDateFormat({ entityRef: employee }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.employee.id) {
        this.employeeService()
          .update(this.employee)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('novelsyncApp.employee.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.employeeService()
          .create(this.employee)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('novelsyncApp.employee.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
