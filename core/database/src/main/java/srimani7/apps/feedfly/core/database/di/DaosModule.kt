package srimani7.apps.feedfly.core.database.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import srimani7.apps.feedfly.core.database.AppDatabase

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {
    @Provides
    fun providesFeedDao(database: AppDatabase) = database.feedDao()

    @Provides
    fun providesArticleDao(database: AppDatabase) = database.articleDao()

    @Provides
    fun providesPrivateSpaceDao(database: AppDatabase) = database.privateSpaceDao()
}