package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PspGameDao {
    @Query("SELECT * FROM psp_games ORDER BY title ASC")
    fun getAllGames(): Flow<List<PspGameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: PspGameEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGames(games: List<PspGameEntity>)

    @Update
    suspend fun updateGame(game: PspGameEntity)

    @Query("DELETE FROM psp_games WHERE id = :id")
    suspend fun deleteGameById(id: String)
}
