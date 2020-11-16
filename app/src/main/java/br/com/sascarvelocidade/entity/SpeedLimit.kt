package br.com.sascarvelocidade.entity

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "speedlimit")
data class SpeedLimit(
    @SerializedName("viaId")
    @PrimaryKey
    var viaId: Int,

    @ColumnInfo(name = "viaName")
    @SerializedName("viaName")
    @Nullable
    var viaName: String?,

    @ColumnInfo(name = "latitude")
    @SerializedName("latitude")
    @NonNull
    var latitude: Double,

    @ColumnInfo(name = "longitude")
    @SerializedName("longitude")
    @NonNull
    var longitude: Double,

    @SerializedName("speedLimit")
    @ColumnInfo(name = "speedLimit")
    @NonNull
    var speedLimit: Int,

    @SerializedName("direction")
    @ColumnInfo(name = "direction")
    @NonNull
    var direction: String
)