import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import AuthorDetails from './author-details.vue';
import AuthorService from './author.service';
import AlertService from '@/shared/alert/alert.service';

type AuthorDetailsComponentType = InstanceType<typeof AuthorDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const authorSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Author Management Detail Component', () => {
    let authorServiceStub: SinonStubbedInstance<AuthorService>;
    let mountOptions: MountingOptions<AuthorDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      authorServiceStub = sinon.createStubInstance<AuthorService>(AuthorService);

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
          authorService: () => authorServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        authorServiceStub.find.resolves(authorSample);
        route = {
          params: {
            authorId: `${123}`,
          },
        };
        const wrapper = shallowMount(AuthorDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.author).toMatchObject(authorSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        authorServiceStub.find.resolves(authorSample);
        const wrapper = shallowMount(AuthorDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
