import { Route } from '@vaadin/router';
import Role from './generated/com/vaadin/example/sightseeing/data/Role';
import { appStore } from './stores/app-store';
import './views/map/map-view';

export type ViewRoute = Route & {
  title?: string;
  icon?: string;
  requiresLogin?: boolean;
  rolesAllowed?: Role[];
  children?: ViewRoute[];
};

export const hasAccess = (route: Route) => {
  const viewRoute = route as ViewRoute;
  if (viewRoute.requiresLogin && !appStore.loggedIn) {
    return false;
  }

  if (viewRoute.rolesAllowed) {
    return viewRoute.rolesAllowed.some((role) => appStore.isUserInRole(role));
  }
  return true;
};

export const views: ViewRoute[] = [
  // place routes below (more info https://hilla.dev/docs/routing)
  {
    path: '',
    component: 'map-view',
    requiresLogin: true,
    icon: '',
    title: '',
    action: async (_context, _command) => {
      if (!hasAccess(_context.route)) {
        return _command.redirect('login');
      }
      return;
    },
  },
  {
    path: 'map',
    component: 'map-view',
    requiresLogin: true,
    icon: 'la la-globe',
    title: 'Map',
    action: async (_context, _command) => {
      if (!hasAccess(_context.route)) {
        return _command.redirect('login');
      }
      return;
    },
  },
  {
    path: 'Places',
    component: 'places-view',
    rolesAllowed: [Role.ADMIN],
    icon: 'la la-columns',
    title: 'Places',
    action: async (_context, _command) => {
      if (!hasAccess(_context.route)) {
        return _command.redirect('login');
      }
      await import('./views/places/places-view');
      return;
    },
  },
  {
    path: 'tags',
    component: 'tags-view',
    icon: 'la la-columns',
    title: 'Tags',
    action: async (_context, _command) => {
      await import('./views/tags/tags-view');
      return;
    },
  },
];
export const routes: ViewRoute[] = [
  ...views,
  {
    path: 'login',
    component: 'login-view',
    icon: '',
    title: 'Login',
    action: async (_context, _command) => {
      await import('./views/login/login-view');
      return;
    },
  },
];
