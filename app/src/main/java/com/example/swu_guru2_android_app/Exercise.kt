package com.example.swu_guru2_android_app

import android.os.Parcel
import android.os.Parcelable
// import kotlin.time.Duration // 이 import는 사용되지 않으므로 제거해도 됩니다.

//운동 요소
data class Exercise(
    val name: String?, // String?으로 변경 (null 허용)
    val category: String?, // "상체", "복근", "하체", "전신" (String?으로 변경)
    val description: String?, // 운동 상세 설명 (String?으로 변경)
    val duration: String?, //운동 시간 (String?으로 변경)
    val caloriesBurned : String? //예상 칼로리 소모량 (String?으로 변경)
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