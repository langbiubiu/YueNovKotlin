package com.yuenov.kotlin.open.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.yuenov.kotlin.open.application.MyApplication
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.lang.IllegalArgumentException

/**
 * created by xinghe
 *
 * date : 2021/12/11 09:47
 *
 * version :
 *
 * 同步获取数据
 * [DataStoreUtils.getData]
 *
 * @github https://github.com/Microhx/utils_code
 */

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dataStore_setting",
    produceMigrations = { context -> listOf(SharedPreferencesMigration(context, "app_info")) })

val globalDataStore: DataStore<Preferences> = MyApplication.appContext.dataStore

object DataStoreUtils {

    /**
     * 异步获取数据
     * */
    @Suppress("UNCHECKED_CAST")
    fun <Value> getData(key: String, defaultValue: Value): Value {
        val result = when (defaultValue) {
            is Int -> readIntData(key, defaultValue)
            is Float -> readFloatData(key, defaultValue)
            is Double -> readDoubleData(key, defaultValue)
            is Boolean -> readBoolean(key, defaultValue)
            is String -> readString(key, defaultValue)
            is Long -> readLong(key, defaultValue)

            else -> throw IllegalArgumentException("can not find the $key type")
        }

        return result as Value
    }

    /**
     * 同步获取数据
     * */
    @Suppress("UNCHECKED_CAST")
    fun <Value> getSyncData(key: String, defaultValue: Value): Flow<Value> {
        val result = when (defaultValue) {
            is Int -> readSyncIntData(key, defaultValue)
            is Long -> readSyncLongData(key, defaultValue)
            is Float -> readSyncFloatData(key, defaultValue)
            is Double -> readSyncDoubleData(key, defaultValue)
            is Boolean -> readSyncBooleanData(key, defaultValue)
            is String -> readSyncStringData(key, defaultValue)
            else -> throw IllegalArgumentException("can Not find the $key type")

        }

        return result as Flow<Value>
    }

    /**
     * 异步获取Json数据
     */
    fun <Value> getJsonData(key: String, clazz: Class<Value>): Value {
        return try {
            val result = getData(key, "")
            Gson().fromJson(result, clazz)
        } catch (ex: Exception) {
            ex.printStackTrace()
            clazz.newInstance()
        }
    }

    /**
     * 同步获取Json数据
     */
    fun <Value> getJsonSyncData(key: String, clazz: Class<Value>): Flow<Value> {
        return getSyncData(key, "")
            .map {
                try {
                    Gson().fromJson(it, clazz)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    clazz.newInstance()
                }
            }
    }

    /**
     * 异步输入数据
     */
    fun <Value> putData(key: String, value: Value) {
        when (value) {
            is Int -> saveIntData(key, value)
            is Long -> saveLongData(key, value)
            is Float -> saveFloatData(key, value)
            is Double -> saveDoubleData(key, value)
            is Boolean -> saveBoolean(key, value)
            is String -> saveString(key, value)
            else -> throw IllegalArgumentException("unSupport $value type !!!")
        }
    }

    /**
     * 同步输入数据
     */
    suspend fun <Value> putSyncData(key: String, value: Value) {
        when (value) {
            is Int -> saveSyncIntData(key, value)
            is Long -> saveSyncLongData(key, value)
            is Float -> saveSyncFloatData(key, value)
            is Double -> saveSyncDoubleData(key, value)
            is Boolean -> saveSyncBoolean(key, value)
            is String -> saveSyncString(key, value)
            else -> throw IllegalArgumentException("unSupport $value type !!!")
        }
    }

    /**
     * 异步输入Json数据
     */
    fun <Value> putJsonData(key: String, value: Value) {
        try {
            putData(key, Gson().toJson(value))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * 同步输入Json数据
     */
    suspend fun <Value> putJsonSyncData(key: String, value: Value) {
        try {
            putSyncData(key, Gson().toJson(value))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun saveString(key: String, value: String) = runBlocking {
        saveSyncString(key, value)
    }

    private suspend fun saveSyncString(key: String, value: String) {
        globalDataStore.edit { mutablePreferences ->
            mutablePreferences[stringPreferencesKey(key)] = value
        }
    }

    private fun saveBoolean(key: String, value: Boolean) =
        runBlocking { saveSyncBoolean(key, value) }

    private suspend fun saveSyncBoolean(key: String, value: Boolean) {
        globalDataStore.edit { mutablePreferences ->
            mutablePreferences[booleanPreferencesKey(key)] = value
        }
    }

    private fun saveDoubleData(key: String, value: Double) = runBlocking {
        saveSyncDoubleData(key, value)
    }

    private suspend fun saveSyncDoubleData(key: String, value: Double) {
        globalDataStore.edit { mutablePreferences ->
            mutablePreferences[doublePreferencesKey(key)] = value
        }
    }

    private fun saveFloatData(key: String, value: Float) =
        runBlocking { saveSyncFloatData(key, value) }

    private suspend fun saveSyncFloatData(key: String, value: Float) {
        globalDataStore.edit { mutablePreferences ->
            mutablePreferences[floatPreferencesKey(key)] = value
        }
    }

    private fun saveLongData(key: String, value: Long) =
        runBlocking { saveSyncLongData(key, value) }

    private suspend fun saveSyncLongData(key: String, value: Long) {
        globalDataStore.edit { mutablePreferences ->
            mutablePreferences[longPreferencesKey(key)] = value
        }
    }

    private fun saveIntData(key: String, value: Int) = runBlocking { saveSyncIntData(key, value) }

    private suspend fun saveSyncIntData(key: String, value: Int) {
        globalDataStore.edit { mutablePreferences ->
            mutablePreferences[intPreferencesKey(key)] = value
        }
    }

    private fun readSyncStringData(key: String, defaultValue: String): Flow<String> =
        globalDataStore.data.catch {
            checkCollectorAction(it, this)

        }.map { it[stringPreferencesKey(key)] ?: defaultValue }

    private fun readSyncBooleanData(key: String, defaultValue: Boolean): Flow<Boolean> =
        globalDataStore.data.catch {

            checkCollectorAction(it, this)

        }.map { it[booleanPreferencesKey(key)] ?: defaultValue }

    private fun readSyncDoubleData(key: String, defaultValue: Double): Flow<Double> =
        globalDataStore.data.catch {
            checkCollectorAction(it, this)
        }.map { it[doublePreferencesKey(key)] ?: defaultValue }

    private fun readSyncFloatData(key: String, defaultValue: Float): Flow<Float> =
        globalDataStore.data.catch {

            checkCollectorAction(it, this)

        }.map { it[floatPreferencesKey(key)] ?: defaultValue }

    private fun readSyncLongData(key: String, defaultValue: Long): Flow<Long> =
        globalDataStore.data.catch {

            checkCollectorAction(it, this)

        }.map { it[longPreferencesKey(key)] ?: defaultValue }

    private fun readSyncIntData(key: String, defaultValue: Int): Flow<Int> =
        globalDataStore.data.catch {

            checkCollectorAction(it, this)

        }.map { it[intPreferencesKey(key)] ?: defaultValue }

    private fun readIntData(key: String, defaultValue: Int): Int {
        var resultValue = defaultValue

        runBlocking {
            globalDataStore.data.first {
                resultValue = it[intPreferencesKey(key)] ?: resultValue
                true
            }
        }

        return resultValue
    }

    private fun readFloatData(key: String, defaultValue: Float): Float {
        var resultValue = defaultValue

        runBlocking {
            globalDataStore.data.first {
                resultValue = it[floatPreferencesKey(key)] ?: resultValue
                true
            }
        }

        return resultValue
    }

    private fun readDoubleData(key: String, defaultValue: Double): Double {
        var resultValue = defaultValue

        runBlocking {
            globalDataStore.data.first {
                resultValue = it[doublePreferencesKey(key)] ?: resultValue
                true
            }
        }

        return resultValue
    }

    private fun readBoolean(key: String, defaultValue: Boolean): Boolean {
        var resultValue = defaultValue

        runBlocking {
            globalDataStore.data.first {
                resultValue = it[booleanPreferencesKey(key)] ?: resultValue
                true
            }
        }

        return resultValue
    }

    private fun readString(key: String, defaultValue: String): String {
        var resultValue = defaultValue

        runBlocking {
            globalDataStore.data.first {
                resultValue = it[stringPreferencesKey(key)] ?: defaultValue

                true
            }
        }

        return resultValue
    }

    private fun readLong(key: String, defaultValue: Long): Long {
        var resultValue = defaultValue

        runBlocking {
            globalDataStore.data.first {
                resultValue = it[longPreferencesKey(key)] ?: resultValue
                true
            }
        }

        return resultValue

    }

    fun readSetString(key: String, defaultValue: Set<String> = HashSet()): Set<String> {
        var resultValue = defaultValue

        runBlocking {
            globalDataStore.data.first {
                resultValue = it[stringSetPreferencesKey(key)] ?: defaultValue
                true
            }
        }

        return resultValue
    }

    fun readSyncSetString(key: String, defaultValue: Set<String> = HashSet()): Flow<Set<String>> =
        globalDataStore.data.catch { e ->
            checkCollectorAction(e, this)

        }.map { it[stringSetPreferencesKey(key)] ?: defaultValue }

    fun writeSetString(key: String, value: Set<String>) = runBlocking {
        writeSyncSetString(key, value)
    }

    suspend fun writeSyncSetString(key: String, value: Set<String>) {
        globalDataStore.edit { mutablePreferences ->
            mutablePreferences[stringSetPreferencesKey(key)] = value
        }
    }

    suspend fun clear() {
        globalDataStore.edit { it.clear() }
    }

    fun clearSync() {
        runBlocking {
            clear()
        }
    }

    private suspend fun checkCollectorAction(e: Throwable, collector: FlowCollector<Preferences>) {

        if (e is IOException) {
            e.printStackTrace()
            collector.emit(emptyPreferences())
        } else {
            throw  e
        }
    }


}