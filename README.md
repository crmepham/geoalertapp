<<<<<<< HEAD
FATAL EXCEPTION: main
                                                                 Process: crm.geoalertapp, PID: 13857
                                                                 java.lang.RuntimeException: Unable to start receiver crm.geoalertapp.crm.geoalertapp.utilities.LocationUpdateReceiver: java.lang.NullPointerException: Attempt to invoke virtual method 'double android.location.Location.getLatitude()' on a null object reference
                                                                     at android.app.ActivityThread.handleReceiver(ActivityThread.java:2586)
                                                                     at android.app.ActivityThread.access$1700(ActivityThread.java:144)
                                                                     at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1355)
                                                                     at android.os.Handler.dispatchMessage(Handler.java:102)
                                                                     at android.os.Looper.loop(Looper.java:135)
                                                                     at android.app.ActivityThread.main(ActivityThread.java:5221)
                                                                     at java.lang.reflect.Method.invoke(Native Method)
                                                                     at java.lang.reflect.Method.invoke(Method.java:372)
                                                                     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:899)
                                                                     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:694)
                                                                  Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'double android.location.Location.getLatitude()' on a null object reference
                                                                     at updateLatLong(LocationHelper.java:37)
                                                                     at crm.geoalertapp.crm.geoalertapp.utilities.LocationHelper.<init>(LocationHelper.java:27)
                                                                     at crm.geoalertapp.crm.geoalertapp.utilities.LocationUpdateReceiver.onReceive(LocationUpdateReceiver.java:23)
                                                                     at android.app.ActivityThread.handleReceiver(ActivityThread.java:2579)
                                                                     at android.app.ActivityThread.access$1700(ActivityThread.java:144) 
                                                                     at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1355) 
                                                                     at android.os.Handler.dispatchMessage(Handler.java:102) 
                                                                     at android.os.Looper.loop(Looper.java:135) 
                                                                     at android.app.ActivityThread.main(ActivityThread.java:5221) 
                                                                     at java.lang.reflect.Method.invoke(Native Method) 
                                                                     at java.lang.reflect.Method.invoke(Method.java:372) 
                                                                     at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:899) 
                                                                     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:694) 
=======
<p align="center">
<img src="http://i.imgur.com/AFsAEJs.png" alt="Geo alert app">
</p>

<p align="center">
<strong>geoalertapp</strong>
</p>
<p align="center">
A personal safety alert app that notifies friends when you are in trouble.
</p>

<hr>
<p>
Before you go out for the evening, or before your walk home, you can activate the GeoAlert sensor. Then if you are ever in trouble you can simply shake your phone and the alert will be activated, this will update your status and send a notification to all of your contacts. Your contacts can then see exactly where you activated the signal, and continue to track your location.</p>
<hr>
<h3>Pre-requisites</h3>
Android OS version 5.0+
<hr>
<h3>Features</h3>
1) View your contacts precise location (update every 15 minutes).<br>
2) Shake your phone to activate an alarm which will update your location and send a notification to all of your contacts.<br>
3) Full profile functionality to provide crucial identification and medical information.<br>
4) Activate the alert offline. The alert will keep trying to notify your friends until it has successfully done so.<br>
<hr>
<h3>How to use</h3>
1) Download the app from the Google Play store <a href="https://play.google.com/store/apps/details?id=crm.geoalertapp&hl=en_GB">here</a>.<br>
2) Open the app and register an account, once complete you will be automatically logged in. Make sure you are connected to the internet.<br>
3) Fill in all of the profile information and add an image.<br>
4) In the settings you can test the shake sensitivity to get it just right.<br>
5) Add trusted close friends and family via the contacts sections.<br>
6) Activate the sensor before you leave, and de-activate it when you arrive safely.<br>
>>>>>>> f58dbb8ef2961517d74f0651ba7034cdb45d9d4f
