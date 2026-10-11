package app.lade.parser.di

import app.lade.parser.api.ParserApi
import app.lade.parser.internal.ParserApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ParserModule {

    @Binds
    @Singleton
    internal abstract fun bindApi(impl: ParserApiImpl): ParserApi
}