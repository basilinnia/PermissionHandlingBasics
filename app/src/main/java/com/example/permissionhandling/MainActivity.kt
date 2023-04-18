package com.example.permissionhandling

import android.Manifest
import android.content.pm.PermissionInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.permissionhandling.ui.theme.PermissionHandlingTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionHandlingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PermissionHandling()
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandling() {
    val permissionState =  rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET)
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, effect ={
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionState.launchMultiplePermissionRequest()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    })

    Column (modifier = Modifier.fillMaxSize()){
        permissionState.permissions.forEach { perm->
            when(perm.permission) {
                Manifest.permission.RECORD_AUDIO -> {
                    when {
                        perm.hasPermission -> {
                            Text(text = "Audio has permission")
                        }
                        perm.shouldShowRationale-> {
                            Text(text = "Audio permission is needed to translating speech")
                        }
                        !perm.shouldShowRationale && !perm.hasPermission -> {
                            Text(text = "Audio permission was permanently denied. You can enable it in the app settings ")
                        }
                    }
                }
                Manifest.permission.INTERNET -> {
                    when {
                        perm.hasPermission -> {
                            Text(text = "Internet has permission")
                        }
                        perm.shouldShowRationale-> {
                            Text(text = "Internet permission is needed to translating speech into text")
                        }
                        !perm.shouldShowRationale && !perm.hasPermission -> {
                            Text(text = "Internet permission was permanently denied. You can enable it in the app settings ")
                        }
                    }
                }
            }
        }
    }
}
