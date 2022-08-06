package com.example.boombox.di

import com.example.boombox.data.network.BoomboxApi
import com.example.boombox.data.network.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides
  fun provideBoomboxApi(dataSource: RemoteDataSource): BoomboxApi {
    return dataSource.buildBoomboxApi()
  }
}
