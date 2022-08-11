package com.example.boombox.di

import android.content.Context
import com.example.boombox.data.network.BoomboxRepository
import com.example.boombox.data.network.RemoteDataSource
import com.example.boombox.media.AudioPlayerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  fun provideBoomboxApi(dataSource: RemoteDataSource): BoomboxRepository {
    return BoomboxRepository(dataSource.buildTracksApi())
  }

  @Provides
  fun provideAudioManager(@ApplicationContext context: Context) = AudioPlayerManager(context)
}
