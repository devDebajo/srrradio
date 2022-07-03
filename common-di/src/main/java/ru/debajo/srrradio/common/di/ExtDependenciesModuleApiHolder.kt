package ru.debajo.srrradio.common.di

abstract class ExtDependenciesModuleApiHolder<Api : ModuleApi, Dependencies : ModuleDependencies> : ModuleApiHolder<Api, Dependencies>() {

    private var extDependencies: Dependencies? = null

    final override val dependencies: Dependencies
        get() = extDependencies!!

    fun init(dependencies: Dependencies) {
        extDependencies = dependencies
    }
}
