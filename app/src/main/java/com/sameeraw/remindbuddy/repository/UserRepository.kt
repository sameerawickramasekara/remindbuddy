package com.sameeraw.remindbuddy.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import com.sameeraw.remindbuddy.data.entity.User
import com.sameeraw.remindbuddy.data.store.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.map


class UserRepository(private val current: Context) {

    companion object{
        val NAME = stringPreferencesKey("NAME")
        val PASSWORD = stringPreferencesKey("PASSWORD")
        val EMAIL = stringPreferencesKey("EMAIL")
        val PINCODE = stringPreferencesKey("PINCODE")
        val KEEPLOGGEDIN = stringPreferencesKey("KEEPLOGGEDIN")
        val ISLOGGEDIN= stringPreferencesKey("ISLOGGEDIN")
    }

    suspend fun insertUser(user:User){
        current.dataStore.edit { userStore->
            userStore[NAME] = user.userName
            userStore[PASSWORD]= user.password
            userStore[PINCODE] = user.pinCode
            userStore[KEEPLOGGEDIN] = user.keepLoggedIn.toString()
            userStore[ISLOGGEDIN] = user.isLoggedIn.toString()
            userStore[EMAIL] = user.email
        }

    }

    suspend fun logoutUser(){
        current.dataStore.edit { userStore->
            userStore[ISLOGGEDIN] = false.toString()

        }

    }

    suspend fun logInUser(keepLoggedIn:Boolean){
        current.dataStore.edit { userStore->
            userStore[KEEPLOGGEDIN] = keepLoggedIn.toString()
            userStore[ISLOGGEDIN] = true.toString()

        }
    }

    fun getKeepLoggedIn():Flow<Boolean> = current.dataStore.data.map {
            userStore-> userStore[KEEPLOGGEDIN].toBoolean()
    }

    fun getLoggedInStatus():Flow<Boolean> = current.dataStore.data.map {
        userStore-> userStore[ISLOGGEDIN].toBoolean()
    }

    fun getUserPIN():Flow<String> = current.dataStore.data.map { userStore->
            userStore[PINCODE]!!
    }

    fun getUserByUserName(): Flow<Pair<String,String>> = current.dataStore.data.map { userStore ->
       Pair(userStore[NAME]!!,userStore[PASSWORD]!!)
    }
}