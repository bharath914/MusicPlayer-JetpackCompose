package com.bharath.musicplayerforbaby.presentation.signIn

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bharath.musicplayerforbaby.R
import com.bharath.musicplayerforbaby.navigation.NavConst
import com.bharath.musicplayerforbaby.presentation.viewmodel.SignInViewModel
import com.bharath.musicplayerforbaby.ui.theme.firasansFamily
import com.bharath.musicplayerforbaby.ui.theme.ptSansFamily
import com.bharath.musicplayerforbaby.ui.theme.signInButtonColor
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SignInScreen(
    navHostController: NavHostController
) {
    val viewmodel = viewModel<SignInViewModel>()
    val state = viewmodel.state.collectAsState()
    val context = LocalContext.current
    val googleAuthUiClient by lazy {
        GoogleAuthClient(
            context = context.applicationContext,
            oneTapClient = Identity.getSignInClient(
                context.applicationContext
            )
        )
    }
    val scope = rememberCoroutineScope()


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {

        var txtColor = Color.Black
        if (isSystemInDarkTheme()) {
            txtColor = Color.White
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {


                    Text(
                        text = "Welcome !",
                        fontSize = 48.sp,
                        fontFamily = firasansFamily,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Medium,
                        color = signInButtonColor
                    )
                    Text(
                        text = "Sign In to Continue",
                        fontFamily = ptSansFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 24.sp,
                        modifier = Modifier.alpha(0.7f),
                        color = txtColor
                    )
                }
            }


            Box(modifier = Modifier.size(350.dp)) {

                GlideImage(
                    model = null,
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                ) {
                    it.load(R.raw.sign_in_illustration)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)

                }
            }
            Spacer(modifier = Modifier.height(50.dp))


            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),

                onResult = { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        viewmodel.viewModelScope.launch {
                            val signInResult = googleAuthUiClient.signInWithIntent(
                                intent = result.data ?: return@launch
                            )

                            viewmodel.onSignInResult(signInResult)
                            navHostController.navigate(NavConst.Home)
                            navHostController.popBackStack()
                        }
                    }
                }
            )


            val isSignin = viewmodel.isSingingIn.collectAsState()
            Box(
                modifier = Modifier
                    .height(IntrinsicSize.Max)
                    .width(IntrinsicSize.Max), contentAlignment = Alignment.Center
            ) {


                if (!isSignin.value) {
                    Button(
                        onClick = {


                            scope.launch {


                                viewmodel.updateSigningInReq(true)
                                val intentSender = googleAuthUiClient.signIn()


                                launcher.launch(
                                    IntentSenderRequest.Builder(
                                        intentSender ?: return@launch
                                    ).build()
                                )


                                viewmodel.updateSigningInReq(false)
                            }

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = signInButtonColor
                        ),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {


                            Text(
                                text = "Sign in",
                                fontSize = 22.sp,
                                color = txtColor,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Image(
                                painterResource(id = R.drawable.google_logo_search_new_svgrepo_com),
                                contentDescription = ""
                            )
                        }
                    }
                } else {
                    CircularProgressIndicator(
                        color = signInButtonColor,

                        )
                }
            }
        }
    }
    if (state.value.isSignInSuccessful) {
        navHostController.navigate(NavConst.Home)
        navHostController.popBackStack()
    }

}


@Preview
@Composable
fun Preview() {
    SignInScreen(navHostController = rememberNavController())
}