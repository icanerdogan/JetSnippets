package com.ibrahimcanerdogan.jetsnippets.card

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.random.Random

data class CryptoCurrency(
    val id: String,
    val name: String,
    val symbol: String,
    var price: Double,
    var holdingAmount: Double,
    var priceChangePercentage: Double
)

@Preview
@Composable
fun CryptoPortfolioTracker() {
    var cryptoList by remember {
        mutableStateOf(listOf(
            CryptoCurrency("btc", "Bitcoin", "BTC", 50000.0, 0.5, 2.5),
            CryptoCurrency("eth", "Ethereum", "ETH", 3000.0, 5.0, -1.2),
            CryptoCurrency("sol", "Solana", "SOL", 100.0, 20.0, 4.7)
        ))
    }

    LaunchedEffect(Unit) {
        while(true) {
            delay(3000)
            cryptoList = cryptoList.map { crypto ->
                crypto.copy(
                    price = crypto.price * (1 + Random.nextDouble(-0.05, 0.05)),
                    priceChangePercentage = Random.nextDouble(-5.0, 5.0)
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        PortfolioSummaryCard(cryptoList)
        Spacer(modifier = Modifier.height(16.dp))
        CryptoListSection(cryptoList)
    }
}

@Composable
private fun PortfolioSummaryCard(
    cryptoList: List<CryptoCurrency>
) {
    val totalValue = cryptoList.sumOf { it.price * it.holdingAmount }
    val totalChangePercentage = cryptoList.map { it.priceChangePercentage }.average()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Portfolio Value",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = "$${String.format("%.2f", totalValue)}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (totalChangePercentage >= 0) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                    contentDescription = "Change Indicator",
                    tint = if (totalChangePercentage >= 0) Color.Green else Color.Red
                )
                Text(
                    text = "${String.format("%.2f", totalChangePercentage.absoluteValue)}%",
                    color = if (totalChangePercentage >= 0) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun CryptoListSection(cryptoList: List<CryptoCurrency>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cryptoList) { crypto ->
            CryptoCurrencyItem(crypto)
        }
    }
}

@Composable
private fun CryptoCurrencyItem(crypto: CryptoCurrency) {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, rotate ->
                    scale *= zoom
                    rotation += rotate
                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2C2C2C)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when(crypto.symbol) {
                        "BTC" -> Icons.Filled.CurrencyBitcoin
                        "ETH" -> Icons.Filled.Payments
                        else -> Icons.Filled.Payments
                    },
                    contentDescription = crypto.name,
                    tint = when(crypto.symbol) {
                        "BTC" -> Color(0xFFF7931A)
                        "ETH" -> Color(0xFF627EEA)
                        else -> Color.White
                    },
                    modifier = Modifier.size(40.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = crypto.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = "${crypto.symbol} | ${crypto.holdingAmount} Tokens",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${String.format("%.2f", crypto.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (crypto.priceChangePercentage >= 0)
                            Icons.Filled.ArrowUpward
                        else
                            Icons.Filled.ArrowDownward,
                        contentDescription = "Price Change",
                        tint = if (crypto.priceChangePercentage >= 0) Color.Green else Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${String.format("%.2f", crypto.priceChangePercentage.absoluteValue)}%",
                        color = if (crypto.priceChangePercentage >= 0) Color.Green else Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}