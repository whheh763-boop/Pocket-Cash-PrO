import re

with open('app/src/main/java/com/example/model/Models.kt', 'r') as f:
    content = f.read()

# Add to AppConfig
config_replacement = """    val partnerAppName: String = "SanFlix-Pro",
    val partnerAppUrl: String = "",
    val partnerAppReward: Int = 50,
    val partnerAppIconUrl: String = "",
    val shopCategories: List<String> = listOf("General", "Gaming", "Electronics", "Fashion")
)"""
content = re.sub(r'    val partnerAppName: String = "SanFlix-Pro",\n    val partnerAppUrl: String = "",\n    val partnerAppReward: Int = 50,\n    val partnerAppIconUrl: String = ""\n\)', config_replacement, content)

# Add ShopProduct
shop_product = """
data class ShopProduct(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val affiliateUrl: String = "",
    val category: String = "General"
)
"""
content += shop_product

with open('app/src/main/java/com/example/model/Models.kt', 'w') as f:
    f.write(content)
