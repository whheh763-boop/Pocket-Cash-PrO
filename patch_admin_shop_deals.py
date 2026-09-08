import re

with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'r') as f:
    content = f.read()

shop_ui = """
                Text("Shopping Deals Controls", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                
                var shopCategories by remember { mutableStateOf(config.shopCategories.joinToString(", ")) }
                
                OutlinedTextField(
                    value = shopCategories,
                    onValueChange = { shopCategories = it },
                    label = { Text("Shop Categories (comma separated)") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                var newShopTitle by remember { mutableStateOf("") }
                var newShopImageUrl by remember { mutableStateOf("") }
                var newShopAffiliateUrl by remember { mutableStateOf("") }
                var newShopCategory by remember { mutableStateOf("General") }
                var isAddingShopProduct by remember { mutableStateOf(false) }

                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Add New Shop Product", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                        OutlinedTextField(value = newShopTitle, onValueChange = { newShopTitle = it }, label = { Text("Product Title") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newShopImageUrl, onValueChange = { newShopImageUrl = it }, label = { Text("Image URL") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newShopAffiliateUrl, onValueChange = { newShopAffiliateUrl = it }, label = { Text("Affiliate URL") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newShopCategory, onValueChange = { newShopCategory = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                        
                        Button(
                            onClick = {
                                if (newShopTitle.isNotEmpty() && newShopAffiliateUrl.isNotEmpty()) {
                                    isAddingShopProduct = true
                                    val p = com.example.model.ShopProduct(
                                        title = newShopTitle,
                                        imageUrl = newShopImageUrl,
                                        affiliateUrl = newShopAffiliateUrl,
                                        category = newShopCategory
                                    )
                                    viewModel.addShopProduct(p, onSuccess = {
                                        isAddingShopProduct = false
                                        newShopTitle = ""
                                        newShopImageUrl = ""
                                        newShopAffiliateUrl = ""
                                        android.widget.Toast.makeText(context, "Added Product", android.widget.Toast.LENGTH_SHORT).show()
                                    }, onError = {
                                        isAddingShopProduct = false
                                        android.widget.Toast.makeText(context, "Failed to add", android.widget.Toast.LENGTH_SHORT).show()
                                    })
                                }
                            },
                            enabled = !isAddingShopProduct,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text("Save Product")
                        }
                    }
                }
"""

content = content.replace("                            Button(\n                onClick = {\n                    isSaving = true", shop_ui + "\n                            Button(\n                onClick = {\n                    isSaving = true")

with open('app/src/main/java/com/example/ui/screens/AdminPanelScreen.kt', 'w') as f:
    f.write(content)
