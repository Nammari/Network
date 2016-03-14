# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/Android Studio.app/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#support
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-dontwarn android.support.**

# LoganSquare
-keep class com.bluelinelabs.logansquare.** { *; }
-keep @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep class **$$JsonObjectMapper { *; }

# Jackson
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

-keep class com.pmak8.network.beans.** {*;}

-keepclassmembers class com.pmak8.network.beans.**{ *; }
-keep class com.pmak8.network.ui.**{*;}

-keep class com.pmak8.network.util.**{*;}
-keepattributes Signature
-assumenosideeffects class android.util.Log {
   public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
   public static int e(...);
}

-keep class com.pmak8.network.logger.Logger
-keepclasseswithmembers class com.pmak8.network.logger.Logger{*;}
-keep class com.pmak8.network.loader.**{*;}

-keep class com.pmak8.network.ui.**
-keepattributes InnerClasses