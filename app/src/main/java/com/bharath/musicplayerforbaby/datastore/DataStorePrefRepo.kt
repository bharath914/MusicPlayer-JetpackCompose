package com.bharath.musicplayerforbaby.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "SettingsPref")

class DataStorePrefRepo(context: Context) {

    private object PreferenceKey {
        val sortKey = intPreferencesKey(name = "SortKey")
        val LastPlayedSong = stringPreferencesKey(name = "MediaId")

    }

    private val dataStore = context.dataStore



    suspend fun saveMediaId(mediaId:String){
        dataStore.edit {
            it[PreferenceKey.LastPlayedSong] = mediaId
        }
    }



    suspend fun saveSortKey(i: Int) {
        dataStore.edit {
            it[PreferenceKey.sortKey] = i
        }
    }
    suspend fun readSortKey():Flow<Int>{
        return dataStore.data.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map {
            val option =it[PreferenceKey.sortKey] ?: 0
            option
        }
    }
    suspend fun readMediaId():Flow<String>{
        return dataStore.data.catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map {
            val option =it[PreferenceKey.LastPlayedSong] ?: ""
            option
        }
    }

}