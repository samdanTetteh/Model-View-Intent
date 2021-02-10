# Model-View-Intent

### Overview
This was my first attempt at [MVI](https://www.raywenderlich.com/817602-mvi-architecture-for-android-tutorial-getting-started)
with this [course](https://www.raywenderlich.com/266607-mvi-on-android) on [raywenderlich](https://www.raywenderlich.com).

I picked up a starter code from the course which I continued to finish by following the course to implement the MVI
architecture. Although I can say I understand the general benefits of MVI, I will try my hands on building app from scratch
with this architecture where I am sure I can better compare MVI with other architectures such as [MVVM]("https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel#:~:text=Model%E2%80%93view%E2%80%93viewmodel%20(MVVM,is%20not%20dependent%20on%20any").
I will linking the all [here]("") once is its ready. However coming from the MVVM side of things the MVI architecture is little bit of a learning curve for me.
One fun fact about MVI though is that it is said to be MVP done right.

### Creaturemon App Functionality
This is a simple app which gives the user the ability to create creatures with different attributes such as strength, intelligence...etc
Once creature has been created is is saved locally to the Sql light database on the device and then displayed in a list. There is no involvement
of web services here.

### Features
<ol>
<li>Architecture is [MVI](https://www.raywenderlich.com/817602-mvi-architecture-for-android-tutorial-getting-started)</li>
<li>Reactive programming with [RxJava](https://github.com/ReactiveX/RxJava)</li>
<li>Local database manipulation with [Room](https://developer.android.com/jetpack/androidx/releases/room)</li>
<li>App lifecycle management with [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) </li>
</ol>

### Screenshots
![](/images/1.jpg "All Creatures Screen")
![](/images/2.jpg "Add Creature Screen")






