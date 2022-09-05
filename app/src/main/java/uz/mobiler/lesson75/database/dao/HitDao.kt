package uz.mobiler.lesson75.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import uz.mobiler.lesson75.database.entity.HitEntity

@Dao
interface HitDao {

    @Query("select * from hitentity")
    fun getAllHits(): List<HitEntity>

    @Insert
    fun addHitEntity(hit: HitEntity)

    @Delete
    fun deleteHitEntity(hit: HitEntity)
}