# App
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers enum com.makeitez.acsalesapp.** { *; } # GSON serialization
-renamesourcefileattribute SourceFile
-repackageclasses 'com.makeitez.acsalesapp'
-allowaccessmodification

# Realm
-keep class com.makeitez.acsalesapp.models.**
-keep class com.makeitez.acsalesapp.models.api.request.** { *; }
-keepnames public class * extends io.realm.RealmObject
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class *
-dontwarn javax.**
-dontwarn io.realm.**

# Gson
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }

# OkHttp
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontnote okhttp3.internal.Platform

# Retrofit
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-dontwarn retrofit2.-KotlinExtensions

# org.apache (https://stackoverflow.com/a/35742739/554336)
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** { volatile <fields>; }

# Navigation host
-keepnames class androidx.navigation.fragment.NavHostFragment
-keepnames class com.makeitez.acsalesapp.screens.salesorder.ViewSalesOrderMode
-keepnames class com.makeitez.acsalesapp.screens.salesorder.previousorder.PreviousOrdersViewType