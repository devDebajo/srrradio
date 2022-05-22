package ru.debajo.srrradio.common.di

abstract class PureModuleApiHolder<Api : ModuleApi> : ModuleApiHolder<Api, NoModuleDependencies>() {
    override val dependencies: NoModuleDependencies = NoModuleDependencies
}

