package ru.debajo.srrradio.common.di

object CommonApiHolder : ExtDependenciesModuleApiHolder<CommonApi, CommonDependencies>() {
    override fun buildApi(dependencies: CommonDependencies): CommonApi = CommonModule.Impl(dependencies)
}
