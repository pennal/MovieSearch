import Vue from 'vue'
import Router from 'vue-router'
import ResultsView from '@/components/ResultsView'
import SearchView from '@/components/SearchView'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'App',
      component: SearchView
    },
    {
      path: "/results",
      name: "Results",
      component: ResultsView
    }
  ]
})
