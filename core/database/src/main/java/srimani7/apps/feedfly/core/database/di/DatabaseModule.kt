package srimani7.apps.feedfly.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import srimani7.apps.feedfly.core.database.AppDatabase
import srimani7.apps.feedfly.core.database.AppDatabase.Companion.MIGRATION_7_8
import srimani7.apps.feedfly.core.database.AppDatabase.Companion.MIGRATION_8_9
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesAppDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context = context,
        klass = AppDatabase::class.java, name = "daily-database"
    ).addMigrations(MIGRATION_7_8, MIGRATION_8_9)
        .build()
}