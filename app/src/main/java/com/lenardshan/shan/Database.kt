package com.lenardshan.shan

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.RealmUUID
import io.realm.kotlin.types.annotations.PrimaryKey

open class Person : RealmObject {
    @PrimaryKey
    var _id: RealmUUID = RealmUUID.random()
    var name: String? = null
    var age: String? = null
}