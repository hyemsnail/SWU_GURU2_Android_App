package com.example.swu_guru2_android_app

import android.os.Parcel
import android.os.Parcelable

//운동 요소
data class Exercise(
    val name: String?,
    val category: String?,
    val description: String?,
    val duration: String?,
    val caloriesBurned : String?
) : Parcelable {

    // Parcelable 구현을 위한 보조 생성자
    constructor(parcel: Parcel) : this(
        parcel.readString(), // name
        parcel.readString(), // category
        parcel.readString(), // description
        parcel.readString(), // duration
        parcel.readString()  // caloriesBurned
    )

    // 객체를 Parcel에 쓰는 메서드
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeString(duration)
        parcel.writeString(caloriesBurned)
    }

    // Parcelable 콘텐츠 유형을 반환 (대부분 0)
    override fun describeContents(): Int {
        return 0
    }

    // Parcelable 객체를 생성하기 위한 CREATOR 객체
    companion object CREATOR : Parcelable.Creator<Exercise> {
        override fun createFromParcel(parcel: Parcel): Exercise {
            return Exercise(parcel)
        }

        override fun newArray(size: Int): Array<Exercise?> {
            return arrayOfNulls(size)
        }
    }
}