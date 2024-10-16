package com.example.chtaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.example.chtaapp.ui.theme.ChtaAppTheme
import com.zegocloud.zimkit.services.ZIMKit
import com.zegocloud.zimkit.services.ZIMKitConfig

class MainActivity : FragmentActivity() {

    private var openConversation by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        init()
        setContent {
            ChtaAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Screen(modifier = Modifier.padding(paddingValues = innerPadding))
                }
            }
        }
    }

    @Composable
    fun Screen(modifier: Modifier = Modifier){
        if (openConversation){
            ConversationScreen()
        }else {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Button(onClick = { connectUser() }) {
                    Text(text = "Open Conversation")
                }   
            }
        }
    }

    @Composable
    fun ConversationScreen(modifier: Modifier = Modifier){
        val fragmentManager = androidx.compose.runtime.remember {
            this@MainActivity.supportFragmentManager
        }
        val fragment = androidx.compose.runtime.remember {
            com.zegocloud.zimkit.components.conversation.ui.ZIMKitConversationFragment()
        }
        androidx.compose.ui.viewinterop.AndroidView(
            modifier = modifier,
            factory = {
                android.widget.FrameLayout(it).apply {
                    id = android.view.View.generateViewId()
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = {
                fragmentManager.beginTransaction().replace(
                    it.id,fragment
                ).commit()
            }
        )
    }

    private fun connectUser(){
        val userId = "user2"
        val userName = "user2"
        val userImage = "https://storage.zego.im/IMKit/avatar/avatar-0.png"

        ZIMKit.connectUser(userId, userName, userImage){ Info ->
            if (Info.code == im.zego.zim.enums.ZIMErrorCode.SUCCESS){
                openConversation = true
                startChat("user1")
            } else {
                android.widget.Toast.makeText(
                    this,
                    "Connect user failed: ${Info.message}",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun init(){
        val appID = 0L
        val appSign = ""
        ZIMKit.initWith(application, appID, appSign, ZIMKitConfig())
        ZIMKit.initNotifications()
    }

    private fun startChat(userId : String){
        com.zegocloud.zimkit.common.ZIMKitRouter.toMessageActivity(
            this,
            userId,
            com.zegocloud.zimkit.common.enums.ZIMKitConversationType.ZIMKitConversationTypePeer
        )
    }
}
