package app.lade.agenda.di

import app.lade.agenda.api.AgendaApi
import app.lade.agenda.data.AgendaApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AgendaApiModule {
	@Binds
	@Singleton
	abstract fun bindAgendaApi(impl: AgendaApiImpl): AgendaApi
}