package srimani7.apps.feedfly.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import srimani7.apps.feedfly.core.data.repository.LabelRepository
import srimani7.apps.feedfly.core.data.repository.impl.LabelRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    internal abstract fun bindsLabelRepository(impl: LabelRepositoryImpl): LabelRepository

}