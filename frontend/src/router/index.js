import { createRouter, createWebHashHistory } from 'vue-router'

import BigScreenPage from '@/components/BigScreenPage.vue'
import DatabasesAndTablesPage from '@/components/table/DatabasesAndTablesPage.vue'
import FeaturesAndFeatureViewsPage from '@/components/feature/FeaturesAndFeatureViewsPage.vue'
import FeatureServicesPage from '@/components/featureservice/FeatureServicesPage.vue'
import TutorialPage from '@/components/TutorialPage.vue'
import TableDetail from '@/components/table/TableDetail.vue'
import DatabaseDetail from '@/components/database/DatabaseDetail.vue'
import FeatureDetail from '@/components/feature/FeatureDetail.vue'
import FeatureViewDetail from '@/components/featureview/FeatureViewDetail.vue'
import FeatureServiceDetail from '@/components/featureservice/FeatureServiceDetail.vue'
import FeatureServiceVersionDetail from '@/components/featureservice/FeatureServiceVersionDetail.vue'
import TestFeatureService from '@/components/TestFeatureService.vue'
import DagPage from '@/components/DagPage.vue'
import OfflineJobsPage from '@/components/offlinejob/OfflineJobsPage.vue'
import OfflineJobDetail from '@/components/offlinejob/OfflineJobDetail.vue'
import OfflineJobLog from '@/components/offlinejob/OfflineJobLog.vue'
import OnlineDevelopment from '@/components/OnlineDevelopment.vue'
import OfflineDevelopment from '@/components/OfflineDevelopment.vue'
import OfflineSamplesPage from '@/components/offlinesample/OfflineSamplesPage.vue'
import OfflineSampleDetail from '@/components/offlinesample/OfflineSampleDetail.vue'
import OfflineSamplesAndJobsPage from '@/components/offlinesample/OfflineSamplesAndJobsPage.vue'
import CreateOfflineSampleResult from '@/components/result/CreateOfflineSampleResult.vue'
import CreateFeatureServiceResult from '@/components/result/CreateFeatureServiceResult.vue'
import CreateTableResult from '@/components/result/CreateTableResult.vue'
import OfflineJobResult from '@/components/result/OfflineJobResult.vue'
import ImportDataMenu from '@/components/importdata/ImportDataMenu.vue'
import ImportDataDatabases from '@/components/importdata/ImportDataDatabases.vue'
import ImportDataTables from '@/components/importdata/ImportDataTables.vue'
import ImportDataOnline from '@/components/importdata/ImportDataOnline.vue'
import ImportDataOffline from '@/components/importdata/ImportDataOffline.vue'


const router = createRouter({
  history: createWebHashHistory("/"),
  routes: [
    { path: '/', component: BigScreenPage },
    { path: '/tables', component: DatabasesAndTablesPage },
    { path: '/tables/:db/:name', component: TableDetail, props: true },
    { path: '/tables/:name/:version/createresult', component: CreateTableResult, props: true },    
    { path: '/databases/:db', component: DatabaseDetail, props: true  },
    { path: '/features', component: FeaturesAndFeatureViewsPage},
    { path: '/features/:featureViewName/:featureName', component: FeatureDetail, props: true },
    { path: '/featureviews/:name', component: FeatureViewDetail, props: true },
    { path: '/featureservices', component: FeatureServicesPage},
    { path: '/featureservices/test', component: TestFeatureService},
    { path: '/featureservices/:name', component: FeatureServiceDetail, props: true },
    { path: '/featureservices/:name/:version', component: FeatureServiceVersionDetail, props: true },
    { path: '/featureservices/:name/:version/result', component: CreateFeatureServiceResult, props: true },
    { path: '/offlinepage', component: OfflineSamplesAndJobsPage},
    { path: '/offlinejobs', component: OfflineJobsPage},
    { path: '/offlinejobs/:id', component: OfflineJobDetail, props: true },
    { path: '/offlinejobs/:id/log', component: OfflineJobLog, props: true },
    { path: '/offlinejobs/:id/result', component: OfflineJobResult, props: true },
    { path: '/developprocess/import', component: ImportDataMenu, props: true,
      children: [
        { path: 'databases', component: ImportDataDatabases, props: true },
        { path: 'tables', component: ImportDataTables, props: true },
        { path: 'online', component: ImportDataOnline, props: true },
        { path: 'offline', component: ImportDataOffline, props: true }
      ]
    },
    { path: '/developprocess/online', component: OnlineDevelopment, props: true },
    { path: '/developprocess/offline', component: OfflineDevelopment, props: true },
    { path: '/dag', name: 'DagPage', component: DagPage, props: true},
    { path: '/tutorial', component: TutorialPage},
    { path: '/offlinesamples', component: OfflineSamplesPage},
    { path: '/offlinesamples/:id', component: OfflineSampleDetail, props: true },
    { path: '/offlinesamples/:id/result', component: CreateOfflineSampleResult, props: true },
    { path: '/404', redirect: "/" },
    //{ path: '/:pathMatch(.*)*', redirect: "/404" }
  ]
})

export default router
