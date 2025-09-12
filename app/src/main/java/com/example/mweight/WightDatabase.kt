package com.example.mweight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "weight_entries")
data class WeightEntryData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weight: Float,
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
)


@Dao
interface WeightDao {

    @Insert
    suspend fun insert(entry: WeightEntryData)

    @Query("SELECT * FROM weight_entries ORDER BY date DESC, id DESC")
    fun getAllEntries(): Flow<List<WeightEntryData>>

    @Query("DELETE FROM weight_entries WHERE id = :entryId")
    suspend fun deleteEntry(entryId: Long)
}


@Database(entities = [WeightEntryData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weightDao(): WeightDao
}


class WeightRepository(private val weightEntryDao: WeightDao) {
    val allEntries: Flow<List<WeightEntryData>> = weightEntryDao.getAllEntries()

    suspend fun insert(entry: WeightEntryData) {
        weightEntryDao.insert(entry)
    }

    suspend fun delete(id: Long) {
        weightEntryDao.deleteEntry(id)
    }
}


class WeightViewModel(private val repository: WeightRepository) : ViewModel() {
    val allEntries: Flow<List<WeightEntryData>> = repository.allEntries

    fun addEntry(value: Float, date: String) {
        viewModelScope.launch {
            repository.insert(WeightEntryData(weight = value, date = date))
        }
    }

    fun deleteEntry(id: Long) {
        viewModelScope.launch {
            repository.delete(id = id)
        }
    }
}


class WeightViewModelFactory(private val repository: WeightRepository) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(WeightViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeightViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

