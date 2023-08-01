package az.nonzero.repeat.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoredData(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("StoredData")
        val HIGH_SCORE = stringPreferencesKey("high_score")
        val LAST_QUES = stringPreferencesKey("last_question")
    }
    val loadScore: Flow<Any> = context.dataStore.data
        .map { preferences ->
            preferences[HIGH_SCORE] ?: 0
        }
    val loadQues: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_QUES] ?: 1
        } as Flow<Int>
    suspend fun editScore(score: Int) {
        context.dataStore.edit { preferences ->
            preferences[HIGH_SCORE] = score.toString()
        }
    }
    suspend fun editQues(last_ques: Int) {
        context.dataStore.edit { preferences ->
            preferences[LAST_QUES] = last_ques.toString()
        }
    }
}