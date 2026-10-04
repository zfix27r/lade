package app.lade.backup.internal.di

import app.lade.backup.api.BackupPort
import app.lade.backup.internal.BackupCoordinator
import app.lade.backup.internal.BackupCoordinatorImpl
import app.lade.backup.internal.BackupPortImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BackupModule {

    @Binds
    @Singleton
    internal abstract fun bindBackupPort(impl: BackupPortImpl): BackupPort

    @Binds
    @Singleton
    internal abstract fun bindBackupCoordinator(impl: BackupCoordinatorImpl): BackupCoordinator
}