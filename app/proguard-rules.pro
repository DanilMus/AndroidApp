# Не обфусцировать классы с аннотациями Room (важно!)
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}

# Не обфусцировать Entity и DAO
-keepclassmembers class * {
    @androidx.room.Entity *;
    @androidx.room.Dao *;
}

# GSON
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keepattributes Signature

# ViewModel
-keep class androidx.lifecycle.ViewModel { *; }

# Retrofit/OkHttp (если есть)
# -keep class retrofit2.** { *; }

# Не удалять методы с аннотациями (Room, Gson, LiveData и др.)
-keepattributes *Annotation*

# Android Lifecycle
-keep class androidx.lifecycle.** { *; }
-keep class android.arch.lifecycle.** { *; }

# Json-модели
-keepclassmembers class * {
    <fields>;
}
