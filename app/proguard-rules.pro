# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#OKhttp RULES START
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
#OKhttp RULES END

-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8

#picasso rules START
-dontwarn com.squareup.okhttp.**
#picasso rules END

-keepclassmembers public class * {
    @com.google.gson.annotations.Expose *;
}

-keepclassmembers public class * {
    @com.google.gson.annotations.SerializedName *;
}

-keepattributes Signature
-keepattributes *Annotation*

-dontwarn okio.**
-dontwarn retrofit2.**

-keep class retrofit2.** { *; }



