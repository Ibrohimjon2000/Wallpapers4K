package uz.mobiler.lesson75.networking

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import uz.mobiler.lesson75.models.ImageModel

interface ApiService {

    @GET("api")
    suspend fun getPaging3Images(
        @Query("key") key: String,
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Response<ImageModel>
}