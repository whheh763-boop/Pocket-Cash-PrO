with open('app/src/main/java/com/example/ui/screens/WalletScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("val convertedValue = (userState.coinBalance / 1000.0) * userState.country.exchangeRatePer1000", "val rate = if (userState.country == com.example.model.Country.NEPAL) appConfig.coinValuePer1000 * 1.6 else appConfig.coinValuePer1000\n    val convertedValue = (userState.coinBalance / 1000.0) * rate")

with open('app/src/main/java/com/example/ui/screens/WalletScreen.kt', 'w') as f:
    f.write(content)
