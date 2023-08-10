package com.sampleweatherapp

import android.app.Application
import androidx.test.runner.AndroidJUnitRunner
import org.junit.runner.manipulation.Ordering

class MockTestRunner : AndroidJUnitRunner() {

//    override fun newApplication(cl: ClassLoader?, className: String?,
//                                context: Ordering.Context?): Application {
//        return super.newApplication(cl, MemeTestApp::class.java.name, context)
//    }
}