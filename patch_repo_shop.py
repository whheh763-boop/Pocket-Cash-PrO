import re

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'r') as f:
    content = f.read()

shop_methods = """
    private val shopRef = db.collection("shop_products")

    fun getShopProductsFlow(): Flow<List<ShopProduct>> = callbackFlow {
        val listener = shopRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val products = snapshot.documents.mapNotNull { it.toObject(ShopProduct::class.java) }
                trySend(products)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun addShopProduct(product: ShopProduct) {
        val ref = shopRef.document()
        val newProduct = product.copy(id = ref.id)
        ref.set(newProduct).await()
    }

    suspend fun deleteShopProduct(id: String) {
        shopRef.document(id).delete().await()
    }
"""

content = content.replace("private val configRef = db.collection(\"config\").document(\"appSettings\")", "private val configRef = db.collection(\"config\").document(\"appSettings\")\n" + shop_methods)

with open('app/src/main/java/com/example/model/FirebaseRepository.kt', 'w') as f:
    f.write(content)
