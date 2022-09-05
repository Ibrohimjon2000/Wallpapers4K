package uz.mobiler.lesson75.paging3

import androidx.paging.PagingSource
import androidx.paging.PagingState
import uz.mobiler.lesson75.database.entity.HitEntity
import uz.mobiler.lesson75.networking.ApiClient
import uz.mobiler.lesson75.networking.ApiService

class ImageDataPagingDataSource(private val query: String, val random: Boolean) :
    PagingSource<Int, HitEntity>() {

    private val API_KEY = "29692758-3546f67589de885075929c7b7"
    private val apiService = ApiClient.getRetrofit().create(ApiService::class.java)

    override fun getRefreshKey(state: PagingState<Int, HitEntity>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HitEntity> {
        try {
            var nextPageNumber = params.key ?: 1
            val response = apiService.getPaging3Images(API_KEY, query, nextPageNumber, 10)
            if (response.isSuccessful) {
                if (random) {
                    return LoadResult.Page(
                        response.body()?.hits?.shuffled() ?: emptyList(),
                        null,
                        ++nextPageNumber
                    )
                } else {
                    return LoadResult.Page(
                        response.body()?.hits ?: emptyList(),
                        null,
                        ++nextPageNumber
                    )
                }
            } else {
                return LoadResult.Error(Throwable("Error"))
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}