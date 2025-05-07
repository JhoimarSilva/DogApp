<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/rootLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#2E2E2E"
    tools:context=".MainActivity">

    <!-- Imagen cabeza de perro -->
    <ImageView
        android:id="@+id/dogHead"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:src="@drawable/dog_head"
        android:layout_alignParentTop="true"
        android:layout_alignParentEnd="true"
        android:layout_margin="16dp"
        android:scaleType="fitCenter"
        android:contentDescription="Dog Head" />

    <!-- Título DogApp -->
    <TextView
        android:id="@+id/title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="DogApp"
        android:textColor="#FFFFFF"
        android:textSize="28sp"
        android:textStyle="bold"
        android:layout_below="@id/dogHead"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="8dp"/>

    <!-- Huella animada (Lottie) -->
    <com.airbnb.lottie.LottieAnimationView
        android:id="@+id/fingerprintAnimation"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_centerHorizontal="true"
        android:layout_alignParentBottom="true"
        android:layout_marginBottom="32dp"
        app:lottie_autoPlay="true"
        app:lottie_loop="true"
        app:lottie_rawRes="@raw/fingerprint_anim"
        android:clickable="true"/>
</RelativeLayout>
