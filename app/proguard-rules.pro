-keepclasseswithmembers class **.*$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
-if class **.*$Companion {
  kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class <1>.<2> {
  <1>.<2>$Companion Companion;
}

-dontwarn com.download.library.DownloadTask$DownloadTaskStatus
-dontwarn org.joda.convert.FromString
-dontwarn org.joda.convert.ToString
