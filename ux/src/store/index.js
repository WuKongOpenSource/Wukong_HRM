import Vue from 'vue'
import Vuex from 'vuex'
import modules from './helper'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules
})

export default store
