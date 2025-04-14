package com.mowieinc.mowiekotlin

object ProductManager {
    private val products = listOf(
        Product(
            label = "Mowie's Best - Mow, Edge, Trim & Bag $50",
            productId = "000000",
            name = "Mowie's Best",
            description = "Mow, Edge, Trim & Bag",
            frequency = "One Time",
            price = 5000
        ),
        Product(
            label = "Mowie's Best - Mow, Edge, Trim & Bag $50",
            productId = "000001",
            name = "Mowie's Best",
            description = "Mow, Edge, Trim & Bag",
            frequency = "Weekly",
            price = 5000
        ),
        Product(
            label = "Mowie's Best - Mow, Edge, Trim & Bag $50",
            productId = "000002",
            name = "Mowie's Best",
            description = "Mow, Edge, Trim & Bag",
            frequency = "Bi-Weekly",
            price = 5000
        ),
        Product(
            label = "Mowie's Best - Mow, Edge, Trim & Bag $50",
            productId = "000003",
            name = "Mowie's Best",
            description = "Mow, Edge, Trim & Bag",
            frequency = "Monthly",
            price = 5000
        ),
        Product(
            label = "Clean Cut - Mow, Edging, Trim $40",
            productId = "000004",
            name = "Clean Cut",
            description = "Mow, Edging & Trim",
            frequency = "One Time",
            price = 4000
        ),
        Product(
            label = "Clean Cut - Mow, Edging, Trim $40",
            productId = "000005",
            name = "Clean Cut",
            description = "Mow, Edging & Trim",
            frequency = "Weekly",
            price = 4000
        ),
        Product(
            label = "Clean Cut - Mow, Edging, Trim $40",
            productId = "000006",
            name = "Clean Cut",
            description = "Mow, Edging & Trim",
            frequency = "Bi-Weekly",
            price = 4000
        ),
        Product(
            label = "Clean Cut - Mow, Edging, Trim $40",
            productId = "000007",
            name = "Clean Cut",
            description = "Mow, Edging & Trim",
            frequency = "Monthly",
            price = 4000
        ),
        Product(
            label = "Quick Cut - Mow $28",
            productId = "000008",
            name = "Quick Cut",
            description = "Mow",
            frequency = "One Time",
            price = 2800
        ),
        Product(
            label = "Quick Cut - Mow $28",
            productId = "000009",
            name = "Quick Cut",
            description = "Mow",
            frequency = "Weekly",
            price = 2800
        ),
        Product(
            label = "Quick Cut - Mow $28",
            productId = "000010",
            name = "Quick Cut",
            description = "Mow",
            frequency = "Bi-Weekly",
            price = 2800
        ),
        Product(
            label = "Quick Cut - Mow $28",
            productId = "000011",
            name = "Quick Cut",
            description = "Mow",
            frequency = "Monthly",
            price = 2800
        ),
        Product(
            label = "Garbage Pickup $250",
            productId = "000012",
            name = "Garbage Pickup",
            description = "Garbage Pickup",
            frequency = "One Time",
            price = 25000
        ),
        Product(
            label = "Garbage Pickup $250",
            productId = "000013",
            name = "Garbage Pickup",
            description = "Garbage Pickup",
            frequency = "Weekly",
            price = 25000
        ),
        Product(
            label = "Garbage Pickup $250",
            productId = "000014",
            name = "Garbage Pickup",
            description = "Garbage Pickup",
            frequency = "Bi-Weekly",
            price = 25000
        ),
        Product(
            label = "Garbage Pickup $250",
            productId = "000015",
            name = "Garbage Pickup",
            description = "Garbage Pickup",
            frequency = "Monthly",
            price = 25000
        ),
        Product(
            label = "Yard Cleanup $112.50",
            productId = "000016",
            name = "Yard Cleanup",
            description = "Yard Cleanup",
            frequency = "One Time",
            price = 11250
        ),
        Product(
            label = "Yard Cleanup $112.50",
            productId = "000017",
            name = "Yard Cleanup",
            description = "Yard Cleanup",
            frequency = "Weekly",
            price = 11250
        ),
        Product(
            label = "Yard Cleanup $112.50",
            productId = "000018",
            name = "Yard Cleanup",
            description = "Yard Cleanup",
            frequency = "Bi-Weekly",
            price = 11250
        ),
        Product(
            label = "Yard Cleanup $112.50",
            productId = "000019",
            name = "Yard Cleanup",
            description = "Yard Cleanup",
            frequency = "Monthly",
            price = 11250
        ),
        Product(
            label = "Leaf Cleanup $75",
            productId = "000020",
            name = "Leaf Cleanup",
            description = "Leaf Cleanup",
            frequency = "One Time",
            price = 7500
        ),
        Product(
            label = "Leaf Cleanup $75",
            productId = "000021",
            name = "Leaf Cleanup",
            description = "Leaf Cleanup",
            frequency = "Weekly",
            price = 7500
        ),
        Product(
            label = "Leaf Cleanup $75",
            productId = "000022",
            name = "Leaf Cleanup",
            description = "Leaf Cleanup",
            frequency = "Bi-Weekly",
            price = 7500
        ),
        Product(
            label = "Leaf Cleanup $75",
            productId = "000023",
            name = "Leaf Cleanup",
            description = "Leaf Cleanup",
            frequency = "Monthly",
            price = 7500
        ),
        Product(
            label = "Edging $30",
            productId = "000024",
            name = "Edging",
            description = "Bush Edging",
            frequency = "One Time",
            price = 3000
        ),
        Product(
            label = "Edging $30",
            productId = "000025",
            name = "Edging",
            description = "Bush Edging",
            frequency = "Weekly",
            price = 3000
        ),
        Product(
            label = "Edging $30",
            productId = "000026",
            name = "Edging",
            description = "Bush Edging",
            frequency = "Bi-Weekly",
            price = 3000
        ),
        Product(
            label = "Edging $30",
            productId = "000027",
            name = "Edging",
            description = "Bush Edging",
            frequency = "Monthly",
            price = 3000
        ),
        Product(
            label = "Bagging Fee $15",
            productId = "000028",
            name = "Bagging",
            description = "Bagging clippings and debris",
            frequency = "One Time",
            price = 1500
        ),
        Product(
            label = "Bagging Fee $15",
            productId = "000029",
            name = "Bagging",
            description = "Bagging clippings and debris",
            frequency = "Weekly",
            price = 1500
        ),
        Product(
            label = "Bagging Fee $15",
            productId = "000030",
            name = "Bagging",
            description = "Bagging clippings and debris",
            frequency = "Bi-Weekly",
            price = 1500
        ),
        Product(
            label = "Bagging Fee $15",
            productId = "000031",
            name = "Bagging",
            description = "Bagging clippings and debris",
            frequency = "Monthly",
            price = 1500
        ),
        Product(
            label = "Bush Trimming Starting at $25",
            productId = "000032",
            name = "Bush Trimming",
            description = "Bush trimming",
            frequency = "One Time",
            price = 2500
        ),
        Product(
            label = "Bush Trimming Starting at $25",
            productId = "000033",
            name = "Bush Trimming",
            description = "Bush trimming",
            frequency = "Weekly",
            price = 2500
        ),
        Product(
            label = "Bush Trimming Starting at $25",
            productId = "000034",
            name = "Bush Trimming",
            description = "Bush trimming",
            frequency = "Bi-Weekly",
            price = 2500
        ),
        Product(
            label = "Bush Trimming Starting at $25",
            productId = "000035",
            name = "Bush Trimming",
            description = "Bush trimming",
            frequency = "Monthly",
            price = 2500
        ),
        Product(
            label = "Mowie's Best - Mow, Edge, Trim & Bag $60",
            productId = "001000",
            name = "Mowie's Best",
            description = "Full lawn care including mowing, edging, trimming, and bagging",
            frequency = "One Time",
            price = 6000
        ),
        Product(
            label = "Mowie's Best - Mow, Edge, Trim & Bag $60",
            productId = "001001",
            name = "Mowie's Best",
            description = "Full lawn care including mowing, edging, trimming, and bagging",
            frequency = "Weekly",
            price = 6000
        ),
        Product(
            label = "Mowie's Best - Mow, Edge, Trim & Bag $60",
            productId = "001002",
            name = "Mowie's Best",
            description = "Full lawn care including mowing, edging, trimming, and bagging",
            frequency = "Bi-Weekly",
            price = 6000
        ),
        Product(
            label = "Mowie's Best - Mow, Edge, Trim & Bag $60",
            productId = "001003",
            name = "Mowie's Best",
            description = "Full lawn care including mowing, edging, trimming, and bagging",
            frequency = "Monthly",
            price = 6000
        ),
        Product(
            label = "Clean Cut - Mow, Edging, Trim $50",
            productId = "001004",
            name = "Clean Cut",
            description = "Mowing, edging, and trimming service",
            frequency = "One Time",
            price = 5000
        ),
        Product(
            label = "Clean Cut - Mow, Edging, Trim $50",
            productId = "001005",
            name = "Clean Cut",
            description = "Mowing, edging, and trimming service",
            frequency = "Weekly",
            price = 5000
        ),
        Product(
            label = "Clean Cut - Mow, Edging, Trim $50",
            productId = "001006",
            name = "Clean Cut",
            description = "Mowing, edging, and trimming service",
            frequency = "Bi-Weekly",
            price = 5000
        ),
        Product(
            label = "Clean Cut - Mow, Edging, Trim $50",
            productId = "001007",
            name = "Clean Cut",
            description = "Mowing, edging, and trimming service",
            frequency = "Monthly",
            price = 5000
        ),
        Product(
            label = "Quick Cut - Mow $38",
            productId = "001008",
            name = "Quick Cut",
            description = "Basic mowing service",
            frequency = "One Time",
            price = 3800
        ),
        Product(
            label = "Quick Cut - Mow $38",
            productId = "001009",
            name = "Quick Cut",
            description = "Basic mowing service",
            frequency = "Weekly",
            price = 3800
        ),
        Product(
            label = "Quick Cut - Mow $38",
            productId = "001010",
            name = "Quick Cut",
            description = "Basic mowing service",
            frequency = "Bi-Weekly",
            price = 3800
        ),
        Product(
            label = "Quick Cut - Mow $38",
            productId = "001011",
            name = "Quick Cut",
            description = "Basic mowing service",
            frequency = "Monthly",
            price = 3800
        ),
        Product(
            label = "Yard Cleanup $187.50",
            productId = "001012",
            name = "Yard Cleanup",
            description = "Complete yard cleanup",
            frequency = "One Time",
            price = 18750
        ),
        Product(
            label = "Yard Cleanup $187.50",
            productId = "001013",
            name = "Yard Cleanup",
            description = "Complete yard cleanup",
            frequency = "Weekly",
            price = 18750
        ),
        Product(
            label = "Yard Cleanup $187.50",
            productId = "001014",
            name = "Yard Cleanup",
            description = "Complete yard cleanup",
            frequency = "Bi-Weekly",
            price = 18750
        ),
        Product(
            label = "Yard Cleanup $187.50",
            productId = "001015",
            name = "Yard Cleanup",
            description = "Complete yard cleanup",
            frequency = "Monthly",
            price = 18750
        ),
        Product(
            label = "Leaf Cleanup $120",
            productId = "001016",
            name = "Leaf Cleanup",
            description = "Seasonal leaf cleanup",
            frequency = "One Time",
            price = 12000
        ),
        Product(
            label = "Leaf Cleanup $120",
            productId = "001017",
            name = "Leaf Cleanup",
            description = "Seasonal leaf cleanup",
            frequency = "Weekly",
            price = 12000
        ),
        Product(
            label = "Leaf Cleanup $120",
            productId = "001018",
            name = "Leaf Cleanup",
            description = "Seasonal leaf cleanup",
            frequency = "Bi-Weekly",
            price = 12000
        ),
        Product(
            label = "Leaf Cleanup $120",
            productId = "001019",
            name = "Leaf Cleanup",
            description = "Seasonal leaf cleanup",
            frequency = "Monthly",
            price = 12000
        ),
        Product(
            label = "Mowie's Best - Mow, Edge, Trim & Bag $70",
            productId = "010000",
            name = "Mowie's Best",
            description = "Full lawn care including mowing, edging, trimming, and bagging",
            frequency = "One Time",
            price = 7000
        ),
        Product(
            label = "Mowie's Best - Mow, Edge, Trim & Bag $70",
            productId = "010001",
            name = "Mowie's Best",
            description = "Full lawn care including mowing, edging, trimming, and bagging",
            frequency = "Weekly",
            price = 7000
        ),
        Product(
            label = "Mowie's Best - Mow, Edge, Trim & Bag $70",
            productId = "010002",
            name = "Mowie's Best",
            description = "Full lawn care including mowing, edging, trimming, and bagging",
            frequency = "Bi-Weekly",
            price = 7000
        ),
        Product(
            label = "Mowie's Best - Mow, Edge, Trim & Bag $70",
            productId = "010003",
            name = "Mowie's Best",
            description = "Full lawn care including mowing, edging, trimming, and bagging",
            frequency = "Monthly",
            price = 7000
        ),
        Product(
            label = "Clean Cut - Mow, Edging, Trim $60",
            productId = "010004",
            name = "Clean Cut",
            description = "Mowing, edging, and trimming service",
            frequency = "One Time",
            price = 6000
        ),
        Product(
            label = "Clean Cut - Mow, Edging, Trim $60",
            productId = "010005",
            name = "Clean Cut",
            description = "Mowing, edging, and trimming service",
            frequency = "Weekly",
            price = 6000
        ),
        Product(
            label = "Clean Cut - Mow, Edging, Trim $60",
            productId = "010006",
            name = "Clean Cut",
            description = "Mowing, edging, and trimming service",
            frequency = "Bi-Weekly",
            price = 6000
        ),
        Product(
            label = "Clean Cut - Mow, Edging, Trim $60",
            productId = "010007",
            name = "Clean Cut",
            description = "Mowing, edging, and trimming service",
            frequency = "Monthly",
            price = 6000
        )

    // ... other products
    )

    fun getProductByLabel(label: String, frequency: String): Product? {
        return products.find { it.label == label && it.frequency == frequency }
    }

    fun getProductsByLabel(label: String): List<Product> {
        return products.filter { it.label.equals(label, ignoreCase = true) }
    }
}