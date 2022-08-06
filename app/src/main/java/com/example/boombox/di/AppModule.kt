package com.example.boombox.di

import android.content.Context
import com.example.boombox.data.network.BoomboxApi
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
  fun provideBoomboxApi(dataSource: RemoteDataSource): BoomboxApi {
    return dataSource.buildBoomboxApi()
  }

  @Provides
  fun provideAudioManager(@ApplicationContext context: Context) = AudioPlayerManager(context)
}
