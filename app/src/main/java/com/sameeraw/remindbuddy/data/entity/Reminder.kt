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
    @ColumnInfo(name = "reminder_time")val reminderTime:Long,
    @ColumnInfo(name = "creation_time")val creationTime:Long,
    @ColumnInfo(name = "creator_id")val creatorId:Long,
    @ColumnInfo(name = "reminder_seen" )val reminderSeen:Boolean,
    @ColumnInfo(name = "recurring" )val recurring:Boolean,
    @ColumnInfo(name = "icon")val icon:String,
    @ColumnInfo(name = "image_url")val imageURL:String?,
){


}