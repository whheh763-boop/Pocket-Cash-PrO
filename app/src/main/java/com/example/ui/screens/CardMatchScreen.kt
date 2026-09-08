package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ads.AdsManager
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Colors Matching the HTML
private val MatchBgDark = Color(0xFF030407)
private val MatchGlassBg = Color(0xFF161B26).copy(alpha = 0.6f)
private val MatchGlassBorder = Color.White.copy(alpha = 0.08f)
private val MatchGlassBorderLight = Color.White.copy(alpha = 0.15f)
private val MatchPrimary = Color(0xFF6366F1)
private val MatchSecondary = Color(0xFF22D3EE)
private val MatchGold = Color(0xFFFACC15)
private val MatchGreen = Color(0xFF22C55E)
private val MatchRed = Color(0xFFEF4444)
private val MatchTextMuted = Color(0xFF94A3B8)

data class LevelConfig(val pairs: Int, val columns: Int)
private val levelConfigs = mapOf(
    1 to LevelConfig(3, 3),
    2 to LevelConfig(3, 3),
    3 to LevelConfig(4, 4),
    4 to LevelConfig(4, 4),
    5 to LevelConfig(4, 4),
    6 to LevelConfig(6, 3),
    7 to LevelConfig(6, 3),
    8 to LevelConfig(6, 3),
    9 to LevelConfig(8, 4),
    10 to LevelConfig(8, 4)
)

private val gameIcons = listOf(
    Icons.Default.Star,
    Icons.Default.Favorite,
    Icons.Default.Lightbulb,
    Icons.Default.ThumbUp,
    Icons.Default.Face,
    Icons.Default.Build,
    Icons.Default.Settings,
    Icons.Default.ShoppingCart,
    Icons.Default.DirectionsCar,
    Icons.Default.Flight
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardMatchScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    val uiState by viewModel.userState.collectAsState()
    val scope = rememberCoroutineScope()

    var currentLevel by remember { mutableIntStateOf(1) }
    var lives by remember { mutableIntStateOf(5) }
    val maxLives = 5
    
    var cards by remember { mutableStateOf<List<ImageVector>>(emptyList()) }
    var flippedIndices by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var matchedIndices by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var isBoardLocked by remember { mutableStateOf(false) }
    
    var showWinModal by remember { mutableStateOf(false) }
    var showLoseModal by remember { mutableStateOf(false) }
    var showAdModal by remember { mutableStateOf(false) }
    var adTimer by remember { mutableIntStateOf(5) }
    var canSkipAd by remember { mutableStateOf(false) }

    fun initGame(resetLives: Boolean = true) {
        if (resetLives) lives = maxLives
        flippedIndices = emptySet()
        matchedIndices = emptySet()
        isBoardLocked = false
        showWinModal = false
        showLoseModal = false
        showAdModal = false

        val config = levelConfigs[currentLevel] ?: LevelConfig(8, 4)
        val selectedIcons = gameIcons.shuffled().take(config.pairs)
        val deck = (selectedIcons + selectedIcons).shuffled()
        cards = deck
    }

    LaunchedEffect(Unit) {
        initGame(true)
    }

    // Ad Timer Logic
    LaunchedEffect(showAdModal) {
        if (showAdModal) {
            adTimer = 5
            canSkipAd = false
            while (adTimer > 0) {
                delay(1000)
                adTimer -= 1
            }
            canSkipAd = true
        }
    }

    fun onCardClick(index: Int) {
        if (isBoardLocked || flippedIndices.contains(index) || matchedIndices.contains(index) || lives <= 0) return

        val newFlipped = flippedIndices + index
        flippedIndices = newFlipped

        if (newFlipped.size == 2) {
            isBoardLocked = true
            val list = newFlipped.toList()
            val card1 = cards[list[0]]
            val card2 = cards[list[1]]

            if (card1 == card2) {
                scope.launch {
                    delay(300)
                    matchedIndices = matchedIndices + list
                    flippedIndices = emptySet()
                    isBoardLocked = false
                    
                    val config = levelConfigs[currentLevel] ?: LevelConfig(8, 4)
                    if (matchedIndices.size == config.pairs * 2) {
                        delay(500)
                        viewModel.addCoins(100, "Card Match Level $currentLevel")
                        showWinModal = true
                    }
                }
            } else {
                lives -= 1
                scope.launch {
                    delay(800)
                    flippedIndices = emptySet()
                    if (lives <= 0) {
                        showLoseModal = true
                    } else {
                        isBoardLocked = false
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MatchBgDark)
    ) {
        // Auras
        Box(
            modifier = Modifier
                .offset(x = (-50).dp, y = (-50).dp)
                .size(250.dp)
                .blur(100.dp)
                .background(MatchPrimary.copy(alpha = 0.25f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-50).dp, y = (-80).dp)
                .size(280.dp)
                .blur(100.dp)
                .background(MatchSecondary.copy(alpha = 0.25f), CircleShape)
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.05f))) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.Dashboard, contentDescription = null, tint = MatchSecondary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Card Match", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                }
                
                // Wallet Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MatchGold.copy(alpha = 0.15f))
                        .border(1.dp, MatchGold.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MatchGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${uiState.coinBalance}", color = MatchGold, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Game Stats
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MatchGlassBg)
                        .border(1.dp, MatchGlassBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MatchSecondary.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Level $currentLevel/10", color = MatchSecondary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in 1..maxLives) {
                            Icon(
                                if (i <= lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (i <= lives) MatchRed else MatchRed.copy(alpha = 0.3f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cards Grid Arena
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MatchGlassBg)
                        .border(1.dp, MatchGlassBorderLight, RoundedCornerShape(24.dp))
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val columns = levelConfigs[currentLevel]?.columns ?: 4
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(cards) { index, icon ->
                            val isFlipped = flippedIndices.contains(index) || matchedIndices.contains(index)
                            val isMatched = matchedIndices.contains(index)
                            
                            val rotation by animateFloatAsState(
                                targetValue = if (isFlipped) 180f else 0f,
                                animationSpec = tween(400)
                            )

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .graphicsLayer {
                                        rotationY = rotation
                                        cameraDistance = 12f * density
                                    }
                                    .clickable { onCardClick(index) }
                            ) {
                                if (rotation <= 90f) {
                                    // Front (Question Mark)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color.White.copy(alpha = 0.04f))
                                            .border(1.dp, MatchGlassBorderLight, RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.HelpOutline, contentDescription = null, tint = MatchSecondary, modifier = Modifier.size(28.dp))
                                    }
                                } else {
                                    // Back (Icon)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                if (isMatched) MatchGreen.copy(alpha = 0.2f) else MatchPrimary.copy(alpha = 0.2f)
                                            )
                                            .border(
                                                1.dp, 
                                                if (isMatched) MatchGreen else MatchPrimary, 
                                                RoundedCornerShape(14.dp)
                                            )
                                            .graphicsLayer { rotationY = 180f }
                                            .let {
                                                if (isMatched) it.shadow(15.dp, RoundedCornerShape(14.dp), spotColor = MatchGreen) else it
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(icon, contentDescription = null, tint = if (isMatched) MatchGreen else Color.White, modifier = Modifier.size(32.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Win Modal
        if (showWinModal) {
            Dialog(onDismissRequest = { /* forced action */ }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MatchBgDark.copy(alpha = 0.96f))
                        .border(1.dp, MatchGlassBorder, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(75.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(MatchGold, Color(0xFFD97706))))
                                .shadow(30.dp, CircleShape, spotColor = MatchGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.Black, modifier = Modifier.size(34.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (currentLevel >= 10) {
                            Text("CONGRATULATIONS!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Saare 10 Levels complete ho gaye!", color = MatchTextMuted, fontSize = 14.sp, textAlign = TextAlign.Center)
                        } else {
                            Text("Level $currentLevel Cleared!", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Aapko mile +100 Coins! Agla level unlocked.", color = MatchTextMuted, fontSize = 14.sp, textAlign = TextAlign.Center)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = {
                                if (currentLevel >= 10) {
                                    currentLevel = 1
                                } else {
                                    currentLevel++
                                }
                                initGame(true)
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.linearGradient(listOf(MatchSecondary, MatchPrimary)), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (currentLevel >= 10) "RESTART GAME" else "AGLA LEVEL", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        // Lose Modal
        if (showLoseModal) {
            Dialog(onDismissRequest = { /* forced action */ }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MatchBgDark.copy(alpha = 0.96f))
                        .border(1.dp, MatchGlassBorder, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(75.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(MatchRed, Color(0xFF991B1B))))
                                .shadow(30.dp, CircleShape, spotColor = MatchRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.HeartBroken, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Out of Lives!", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Ad dekhein aur +3 extra hearts le kar wahi se khelna jari rakhein!", color = MatchTextMuted, fontSize = 14.sp, textAlign = TextAlign.Center)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Button(
                            onClick = {
                                showLoseModal = false
                                showAdModal = true
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.linearGradient(listOf(MatchSecondary, MatchPrimary)), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("WATCH AD (+3 HEARTS)", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        OutlinedButton(
                            onClick = { initGame(true) },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MatchGlassBorderLight)
                        ) {
                            Text("RETRY LEVEL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Ad Modal
        if (showAdModal) {
            Dialog(onDismissRequest = { /* forced action */ }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MatchBgDark.copy(alpha = 0.96f))
                        .border(1.dp, MatchGlassBorder, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = MatchGold, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Ad Loading...", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Aapke extra hearts unlock ho rahe hain!", color = MatchTextMuted, fontSize = 14.sp, textAlign = TextAlign.Center)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (!canSkipAd) {
                            Text("Ad ends in ${adTimer}s...", color = MatchSecondary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        } else {
                            Button(
                                onClick = {
                                    activity?.let {
                                        AdsManager.showRewardedAd(
                                            activity = it,
                                            onRewardEarned = {
                                                showAdModal = false
                                                lives = 3
                                                flippedIndices = emptySet()
                                                isBoardLocked = false
                                                Toast.makeText(context, "+3 Hearts Added!", Toast.LENGTH_SHORT).show()
                                            },
                                            onAdDismissed = {
                                                showAdModal = false
                                                lives = 3
                                                flippedIndices = emptySet()
                                                isBoardLocked = false
                                            }
                                        )
                                    }
                                },
                                modifier = Modifier.height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Brush.linearGradient(listOf(MatchSecondary, MatchPrimary)), RoundedCornerShape(14.dp))
                                        .padding(horizontal = 28.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("CLAIM +3 HEARTS", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
