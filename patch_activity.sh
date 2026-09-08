sed -i '/import com.example.ui.screens.SpinWheelScreen/a \
import com.example.ui.screens.AdminPanelScreen\
' app/src/main/java/com/example/MainActivity.kt

sed -i '/composable("spin") {/i \
                composable("admin_panel") {\
                    AdminPanelScreen(\
                        viewModel = mainViewModel,\
                        onBack = { rootNavController.popBackStack() }\
                    )\
                }\
' app/src/main/java/com/example/MainActivity.kt
