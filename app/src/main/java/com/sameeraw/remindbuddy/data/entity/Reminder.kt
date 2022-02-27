package com.sameeraw.remindbuddy.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminder",

)
data class Reminder (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long? =null,
    @ColumnInfo(name = "title") val title:String,
    @ColumnInfo(name = "message") val message:String,
    @ColumnInfo(name = "location_x")val locationX:String?,
    @ColumnInfo(name = "location_y")val locationY:String?,
    @ColumnInfo(name = "reminder_time")val reminderTime:Long?,
    @ColumnInfo(name = "creation_time")val creationTime:Long?,
    @ColumnInfo(name = "creator_id")val creatorId:Long,
    @ColumnInfo(name = "reminder_seen" )val reminderSeen:Boolean,
    @ColumnInfo(name = "notify" )val notify:Boolean = true,
    @ColumnInfo(name = "onTime" )val onTime:Boolean = true,
    @ColumnInfo(name = "5min" )val fiveMin:Boolean = false,
    @ColumnInfo(name = "10min" )val tenMin:Boolean = false,
    @ColumnInfo(name = "15min" )val fifteenMin:Boolean = false,
    @ColumnInfo(name = "30min" )val thirtyMin:Boolean = false,
    @ColumnInfo(name = "icon")val icon:String,
    @ColumnInfo(name = "image_url")val imageURL:String?,
){


}