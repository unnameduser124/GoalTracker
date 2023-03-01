package com.example.goaltracker.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.goaltracker.database.GoalDatabaseConstants.CLEAR_SESSION_TABLE_DATA
import com.example.goaltracker.database.GoalDatabaseConstants.CLEAR_TIME_GOAL_TABLE_DATA
import com.example.goaltracker.database.GoalDatabaseConstants.CREATE_SESSION_TABLE_QUERY
import com.example.goaltracker.database.GoalDatabaseConstants.CREATE_TIME_GOAL_TABLE_QUERY
import com.example.goaltracker.database.GoalDatabaseConstants.DATABASE_NAME
import com.example.goaltracker.database.GoalDatabaseConstants.DATABASE_VERSION

open class GoalDatabase(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TIME_GOAL_TABLE_QUERY)
        db?.execSQL(CREATE_SESSION_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {  }

    fun clearData(){
        val db = this.writableDatabase

        db.execSQL(CLEAR_TIME_GOAL_TABLE_DATA)
        db.execSQL(CLEAR_SESSION_TABLE_DATA)
    }

}
