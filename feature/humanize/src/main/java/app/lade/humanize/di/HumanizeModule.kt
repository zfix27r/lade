package app.lade.humanize.di

import app.lade.humanize.api.Humanize
import app.lade.humanize.internal.HumanizeImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class HumanizeModule {

    @Binds
    @Singleton
    internal abstract fun bindHumanize(impl: HumanizeImpl): Humanize
}