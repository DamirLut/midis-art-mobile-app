package com.damirlutdev.artapp.network.api

import com.damirlutdev.artapp.model.SearchResponse
import com.damirlutdev.artapp.network.api.ApiClient.client
import io.ktor.client.call.body
import io.ktor.client.request.get

class ApiRepository {

    suspend fun getImages(): SearchResponse = client.get(ApiRoutes.SEARCH).body()

}