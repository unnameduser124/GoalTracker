package com.example.goaltracker.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.goaltracker.dataClasses.DataGoalSession
import com.example.goaltracker.dataClasses.DataTimeGoal
import com.example.goaltracker.database.GoalDatabaseConstants.SessionTable.GOAL_ID
import com.example.goaltracker.database.GoalDatabaseConstants.SessionTable.SESSION_DATE
import com.example.goaltracker.database.GoalDatabaseConstants.SessionTable.TABLE_NAME
import com.example.goaltracker.database.GoalDatabaseConstants.SessionTable.TIME_AMOUNT
import com.example.goaltracker.goal.GoalSession
import java.util.*

class SessionDatabaseService(context: Context): GoalDatabase(context) {

    fun addSession(session: GoalSession){
        val db = this.writableDatabase
        val goalSession = DataGoalSession(session)

        val contentValues = ContentValues().apply{
            put(TIME_AMOUNT, goalSession.timeAmount)
            put(SESSION_DATE, goalSession.date)
            put(GOAL_ID, goalSession.goalID)
        }

        db.insert(TABLE_NAME, null, contentValues)
    }

    fun getSessionByID(id: Long): DataGoalSession?{
        val db = this.readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            TIME_AMOUNT,
            SESSION_DATE,
            GOAL_ID
        )
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        with(cursor){
            while(moveToNext()) {
                val goalID = getLong(getColumnIndexOrThrow(GOAL_ID))
                val sessionID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val timeAmount = getDouble(getColumnIndexOrThrow(TIME_AMOUNT))
                val date = getLong(getColumnIndexOrThrow(SESSION_DATE))

                return DataGoalSession(
                    sessionID,
                    date,
                    timeAmount,
                    goalID
                )
            }
        }
        return null
    }

    fun updateSessionByID(id: Long, session: GoalSession){
        val db = this.writableDatabase

        val dataSession = DataGoalSession(session)

        val contentValues = ContentValues().apply{
            put(TIME_AMOUNT, dataSession.timeAmount)
            put(SESSION_DATE, dataSession.date)
            put(GOAL_ID, dataSession.goalID)
        }

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }

    fun deleteSessionByID(id: Long){
        val db = this.writableDatabase

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf(id.toString())

        db.delete(TABLE_NAME, selection, selectionArgs)
    }

    fun getSessionsForGoal(goalID: Long): MutableList<GoalSession>{
        val db = this.readableDatabase

        val projection = arrayOf(
            BaseColumns._ID,
            TIME_AMOUNT,
            SESSION_DATE,
            GOAL_ID
        )
        val selection = "$GOAL_ID = ?"
        val selectionArgs = arrayOf(goalID.toString())

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val sessionList = mutableListOf<GoalSession>()

        with(cursor){
            while(moveToNext()) {
                val sessionID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val timeAmount = getDouble(getColumnIndexOrThrow(TIME_AMOUNT))
                val date = getLong(getColumnIndexOrThrow(SESSION_DATE))

                val goalSession = GoalSession(DataGoalSession(
                    sessionID,
                    date,
                    timeAmount,
                    goalID
                ))
                sessionList.add(goalSession)
            }
        }
        return sessionList
    }

    fun getCurrentTimeForGoal(goalID: Long): Double{
        val db = this.readableDatabase

        val projection = arrayOf("SUM(${TIME_AMOUNT}) as SUMA")

        val selection = "$GOAL_ID = ?"
        val selectionArgs = arrayOf(goalID.toString())

        val cursor = db.query(TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null)

        with(cursor){
            while(moveToNext()){
                return getDouble(getColumnIndexOrThrow("SUMA"))
            }
        }
        return 0.0
    }

    fun deleteSessionByGoalID(goalID: Long) {
        val db = this.writableDatabase

        val selection = "${GOAL_ID} = ?"

        val selectionArgs = arrayOf(goalID.toString())

        db.delete(TABLE_NAME, selection, selectionArgs)
    }

    fun getDailyDurationList(startDate: Long, endDate: Long): List<Pair<Calendar, Double>>{
        val db = this.readableDatabase
        val durationColumn = "Duration"

        val durationList = mutableListOf<Pair<Calendar, Double>>()

        val projection = arrayOf(
            "SUM($TIME_AMOUNT) as $durationColumn",
            SESSION_DATE
        )

        val groupBy = SESSION_DATE

        val selection = "$SESSION_DATE >= ? AND $SESSION_DATE <= ?"
        val selectionArgs = arrayOf(startDate.toString(), endDate.toString())

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            groupBy,
            null,
            null
        )

        with(cursor){
            while(moveToNext()){
                val duration = getDouble(getColumnIndexOrThrow(durationColumn))
                val sessionDate = Calendar.getInstance().apply{ timeInMillis = getLong(getColumnIndexOrThrow(SESSION_DATE)) }

                durationList.add(Pair(sessionDate, duration))
            }
        }
        return durationList
    }
}