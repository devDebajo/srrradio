package ru.debajo.srrradio.common.di

abstract class ExtDependenciesModuleApiHolder<Api : ModuleApi, Dependencies> : ModuleApiHolder<Api>() {

    private var extDependencies: Dependencies? = null

    fun init(dependencies: Dependencies) {
        extDependencies = dependencies
    }

    abstract fun buildApi(dependencies: Dependencies): Api

    final override fun buildApi(): Api = buildApi(extDependencies!!)
}
