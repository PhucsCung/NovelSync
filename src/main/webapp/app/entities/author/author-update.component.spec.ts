import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import AuthorUpdate from './author-update.vue';
import AuthorService from './author.service';
import AlertService from '@/shared/alert/alert.service';

type AuthorUpdateComponentType = InstanceType<typeof AuthorUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const authorSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<AuthorUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Author Management Update Component', () => {
    let comp: AuthorUpdateComponentType;
    let authorServiceStub: SinonStubbedInstance<AuthorService>;

    beforeEach(() => {
      route = {};
      authorServiceStub = sinon.createStubInstance<AuthorService>(AuthorService);
      authorServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          authorService: () => authorServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(AuthorUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.author = authorSample;
        authorServiceStub.update.resolves(authorSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(authorServiceStub.update.calledWith(authorSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        authorServiceStub.create.resolves(entity);
        const wrapper = shallowMount(AuthorUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.author = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(authorServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        authorServiceStub.find.resolves(authorSample);
        authorServiceStub.retrieve.resolves([authorSample]);

        // WHEN
        route = {
          params: {
            authorId: `${authorSample.id}`,
          },
        };
        const wrapper = shallowMount(AuthorUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.author).toMatchObject(authorSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        authorServiceStub.find.resolves(authorSample);
        const wrapper = shallowMount(AuthorUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
