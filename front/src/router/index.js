import { createRouter, createWebHistory } from 'vue-router';
import AppLayout from '@/layout/AppLayout.vue';
import ParticipantLayout from '@/layout/participant/ParticipantLayout.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'index',
      component: () => import('@/views/pages/public/Index.vue')
    },
    {
      path: '/logout',
      name: 'logout',
      component: () => import('@/views/pages/public/Logout.vue')
    },
    {
      path: '/notfound',
      name: 'notfound',
      component: () => import('@/views/pages/public/NotFound.vue')
    },
    {
      path: '/denied',
      name: 'accessDenied',
      component: () => import('@/views/pages/public/Access.vue')
    },
    {
      path: '/error',
      name: 'error',
      component: () => import('@/views/pages/public/Error.vue')
    },
    {
      path: '/participant',
      component: ParticipantLayout,
      children: [
        {
          path: '/participant/me',
          name: 'me',
          component: () => import('@/views/pages/participant/Me.vue')
        }
      ]
    },
    {
      path: '/',
      component: AppLayout,
      children: [
        {
          path: '/admin/achievements',
          name: 'achievements',
          component: () => import('@/views/pages/admin/AchievementsView.vue')
        },
        {
          path: '/admin/seasons',
          name: 'seasons',
          component: () => import('@/views/pages/admin/SeasonCrud.vue')
        },
        {
          path: '/admin/season/:id',
          name: 'season',
          component: () => import('@/views/pages/admin/SeasonAdminView.vue')
        },
        {
          path: '/admin/stages',
          name: 'stages',
          component: () => import('@/views/pages/admin/StageCrud.vue')
        },
        {
          path: '/admin/stage/:id',
          name: 'stage',
          component: () => import('@/views/pages/admin/StageAdminView.vue')
        },
        {
          path: '/admin/criterias',
          name: 'criterias',
          component: () => import('@/views/pages/admin/CriteriaCrud.vue')
        },
        {
          path: '/admin/criteria/:id',
          name: 'criteria',
          component: () => import('@/views/pages/admin/CriteriaAdminView.vue')
        },
        {
          path: '/admin/criteriascores/',
          name: 'criteriascores',
          component: () => import('@/views/pages/admin/CriteriaScoreTable.vue')
        },
        {
          path: '/admin/seasonstagesscores/',
          name: 'seasonstagesscores',
          component: () => import('@/views/pages/admin/SeasonStagesScoreTable.vue')
        },
        {
          path: '/admin/stages/participants/',
          name: 'participants',
          component: () => import('@/views/pages/admin/stages/ParticipantCrud.vue')
        },
        {
          path: '/admin/stages/video/',
          name: 'video',
          component: () => import('@/views/pages/admin/stages/VideoCrud.vue')
        },
        {
          path: '/admin/stages/dictant/',
          name: 'dictant',
          component: () => import('@/views/pages/admin/stages/DictantCrud.vue')
        },
        {
          path: '/expert/stagecriteriasscores/',
          name: 'stagecriteriasscores',
          component: () => import('@/views/pages/expert/StageCriteriasScoreTable.vue')
        },
        {
          path: '/admin/stageschedules/',
          name: 'stageschedules',
          component: () => import('@/views/pages/admin/StageScheduleCrud.vue')
        },
        {
          path: '/admin/stages/wirepark/',
          name: 'wirepark',
          component: () => import('@/views/pages/admin/stages/WireparkCrud.vue')
        }
      ]
    },
    {
      path: '/:catchAll(.*)',
      redirect: '/notfound'
    }
  ]
});

export default router;
