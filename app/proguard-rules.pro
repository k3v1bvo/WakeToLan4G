# WakeToLan4G Proguard Rules
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* *;
}
-keep class com.k3v1bvo.waketolan4g.data.** { *; }
