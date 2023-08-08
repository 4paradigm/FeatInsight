import { createRouter, createWebHashHistory } from 'vue-router'

import OverviewPage from '../components/OverviewPage.vue'
import TablesPage from '../components/TablesPage.vue'
import ImportTable from '../components/ImportTable.vue'
import EntitiesPage from '../components/EntitiesPage.vue'
import CreateEntity from '../components/CreateEntity.vue'
import FeaturesPage from '../components/FeaturesPage.vue'
import FeatureViewsPage from '../components/FeatureViewsPage.vue'
import FeatureServicesPage from '../components/FeatureServicesPage.vue'
import SqlPage from '../components/SqlPage.vue'
import TutorialPage from '../components/TutorialPage.vue'
import TableDetail from '../components/TableDetail.vue'
import DatabaseDetail from '../components/DatabaseDetail.vue'
import EntityDetail from '../components/EntityDetail.vue'
import FeatureDetail from '../components/FeatureDetail.vue'
import FeatureViewDetail from '../components/FeatureViewDetail.vue'
import FeatureServiceDetail from '../components/FeatureServiceDetail.vue'
import FeatureServiceVersionDetail from '../components/FeatureServiceVersionDetail.vue'
import CreateFeature from '../components/CreateFeature.vue'
import DeployFeatureService from '../components/DeployFeatureService.vue'
import TestFeatureService from '../components/TestFeatureService.vue'

const router = createRouter({
  history: createWebHashHistory("/"),
  routes: [
    { path: '/', component: OverviewPage },
    { path: '/tables', component: TablesPage },
    { path: '/tables/import', component: ImportTable },
    { path: '/tables/:db/:name', component: TableDetail, props: true },
    { path: '/databases/:db', component: DatabaseDetail, props: true  },
    { path: '/entities', component: EntitiesPage},
    { path: '/entities/create', component: CreateEntity},
    { path: '/entities/:name', component: EntityDetail, props: true },
    { path: '/features', component: FeaturesPage},
    { path: '/features/create', component: CreateFeature},
    { path: '/features/:featureViewName/:featureName', component: FeatureDetail, props: true },
    { path: '/featureviews', component: FeatureViewsPage},
    { path: '/featureviews/:name', component: FeatureViewDetail, props: true },
    { path: '/featureservices', component: FeatureServicesPage},
    { path: '/featureservices/deploy', component: DeployFeatureService},
    { path: '/featureservices/test', component: TestFeatureService},
    { path: '/featureservices/:name', component: FeatureServiceDetail, props: true },
    { path: '/featureservices/:name/:version', component: FeatureServiceVersionDetail, props: true },
    { path: '/sql', component: SqlPage},
    { path: '/tutorial', component: TutorialPage},
    { path: '/404', redirect: "/" },
    { path: '/:pathMatch(.*)*', redirect: "/404" }
  ]
})

export default router
