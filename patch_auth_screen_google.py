import re

with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'r') as f:
    content = f.read()

# Add imports
imports = """
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import android.util.Log
"""

content = content.replace("import android.widget.Toast", "import android.widget.Toast" + imports)

google_btn = """
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Google Sign In
                    val coroutineScope = rememberCoroutineScope()
                    val context = LocalContext.current
                    
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isLoading = true
                                try {
                                    val credentialManager = CredentialManager.create(context)
                                    val googleIdOption = GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(false)
                                        .setServerClientId("144641270230-2n2vpu2nps9e8pb69erfocrqj9c0f7pl.apps.googleusercontent.com")
                                        .setAutoSelectEnabled(true)
                                        .build()

                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()

                                    val result = credentialManager.getCredential(
                                        request = request,
                                        context = context
                                    )

                                    val credential = result.credential
                                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                        viewModel.loginWithGoogle(googleIdTokenCredential.idToken)
                                        onNavigateToHome()
                                    } else {
                                        Log.e("Auth", "Unexpected type of credential")
                                        Toast.makeText(context, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Log.e("Auth", "Google Signin Error", e)
                                    // Only show toast if it's not a cancellation
                                    if (!e.javaClass.name.contains("GetCredentialCancellationException")) {
                                        Toast.makeText(context, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Text(
                                "Sign in with Google",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
"""

if "Sign in with Google" not in content:
    content = content.replace("Spacer(modifier = Modifier.height(20.dp))\n                    Row {", google_btn + "\n                    Spacer(modifier = Modifier.height(20.dp))\n                    Row {")

with open('app/src/main/java/com/example/ui/screens/AuthScreen.kt', 'w') as f:
    f.write(content)
