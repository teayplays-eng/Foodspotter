package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Contender
import com.example.model.FoodCompetition
import com.example.model.SpotNomination
import com.example.model.UserAccount
import com.example.ui.theme.FoodieAmber
import com.example.ui.theme.FoodieFlamePrimary

enum class ArenaTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    BATTLES("Live Battles", Icons.Default.Whatshot),
    NOMINATE("Nominate Spot", Icons.Default.AddCircleOutline),
    HALL_OF_FAME("Hall of Fame", Icons.Default.EmojiEvents)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompetitionsScreen(
    competitions: List<FoodCompetition>,
    nominations: List<SpotNomination>,
    currentUser: UserAccount?,
    onVote: (String, String) -> Unit,
    onAddDebate: (String, String, String, String, String) -> Unit,
    onLikeDebate: (String, String) -> Unit,
    onNominateSpot: (String, String, String, String) -> Unit,
    onVoteNomination: (String) -> Unit,
    onRequireLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(ArenaTab.BATTLES) }
    var showNominateDialog by remember { mutableStateOf(false) }

    // Nomination form states
    var nomSpotName by remember { mutableStateOf("") }
    var nomDishName by remember { mutableStateOf("") }
    var nomCategory by remember { mutableStateOf("Street Food") }
    var nomReason by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = FoodieAmber,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Foodie Battle Arena",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Text(
                            text = "Vote in local food face-offs & crown neighborhood champions",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Sub-tabs
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                ArenaTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = tab.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(tab.label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("arena_tab_${tab.name}")
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                when (selectedTab) {
                    ArenaTab.BATTLES -> {
                        // User Voting Record Banner
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🏆", fontSize = 20.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Community Foodie Jury",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "Your votes determine which spots win grants and trophies! +50 XP per vote.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                            }
                        }

                        items(competitions, key = { it.id }) { competition ->
                            BattleCard(
                                competition = competition,
                                currentUser = currentUser,
                                onVote = { pick ->
                                    if (currentUser == null) onRequireLogin()
                                    else onVote(competition.id, pick)
                                },
                                onAddDebate = { pick, text ->
                                    if (currentUser == null) onRequireLogin()
                                    else onAddDebate(
                                        competition.id,
                                        currentUser.displayName,
                                        currentUser.avatarEmoji,
                                        pick,
                                        text
                                    )
                                },
                                onLikeDebate = { commentId ->
                                    onLikeDebate(competition.id, commentId)
                                }
                            )
                        }
                    }

                    ArenaTab.NOMINATE -> {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Know an Underrated Hidden Gem?",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Nominate your favorite local street stall, hole-in-the-wall joint, or cozy cafe to enter next week's head-to-head battle.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = {
                                            if (currentUser == null) onRequireLogin()
                                            else showNominateDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("nominate_open_dialog_btn")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Submit Spot Nomination (+60 XP)", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "Trending Community Nominations (${nominations.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        items(nominations, key = { it.id }) { nom ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = nom.category,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = nom.spotName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(text = "Dish: ${nom.dishName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "\"${nom.justification}\" — by ${nom.nominatedBy}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // Upvote button
                                    Button(
                                        onClick = { onVoteNomination(nom.id) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                Icons.Default.ThumbUp,
                                                contentDescription = "Upvote",
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "${nom.votes}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    ArenaTab.HALL_OF_FAME -> {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "🏆 Reigning Champions & Hall of Fame",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Historic winners crowned by the FoodSpotter foodie community.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        item {
                            HallOfFameItem(
                                title = "Winter Ramen Clash Champion",
                                winnerName = "Kagoshima Wagyu Lab",
                                winningDish = "A5 Wagyu Truffle Tonkotsu",
                                finalScore = "64% vs 36% (1,420 total votes)",
                                crownEmoji = "🍜"
                            )
                        }

                        item {
                            HallOfFameItem(
                                title = "Street Taco Royale Winner",
                                winnerName = "Smoky Bull Pitmaster",
                                winningDish = "Oak Smoked Brisket Quesabirria",
                                finalScore = "59% vs 41% (2,180 total votes)",
                                crownEmoji = "🌮"
                            )
                        }

                        item {
                            HallOfFameItem(
                                title = "Morning Pastry Gold Medal",
                                winnerName = "Petite Lumière Bakery",
                                winningDish = "Breton Kouign-Amann",
                                finalScore = "52% vs 48% (980 total votes)",
                                crownEmoji = "🥐"
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog: Nominate a Spot
    if (showNominateDialog) {
        AlertDialog(
            onDismissRequest = { showNominateDialog = false },
            title = { Text("Nominate a Local Spot", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nomSpotName,
                        onValueChange = { nomSpotName = it },
                        label = { Text("Venue / Restaurant Name") },
                        placeholder = { Text("e.g. Mama Linh's Kitchen") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("nom_input_spot_name")
                    )

                    OutlinedTextField(
                        value = nomDishName,
                        onValueChange = { nomDishName = it },
                        label = { Text("Signature Dish to Compete") },
                        placeholder = { Text("e.g. Crispy Pork Banh Mi") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("nom_input_dish_name")
                    )

                    OutlinedTextField(
                        value = nomCategory,
                        onValueChange = { nomCategory = it },
                        label = { Text("Category") },
                        placeholder = { Text("Street Food, Cafe, Ramen...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nomReason,
                        onValueChange = { nomReason = it },
                        label = { Text("Why does this spot deserve to win?") },
                        placeholder = { Text("Unreal crunch, 30-year family recipe, incredible broth...") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("nom_input_reason")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nomSpotName.isNotBlank() && nomDishName.isNotBlank()) {
                            onNominateSpot(
                                nomSpotName,
                                nomDishName,
                                nomCategory,
                                nomReason,
                            )
                            nomSpotName = ""
                            nomDishName = ""
                            nomReason = ""
                            showNominateDialog = false
                        }
                    },
                    modifier = Modifier.testTag("nom_submit_btn")
                ) {
                    Text("Submit Nomination")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNominateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BattleCard(
    competition: FoodCompetition,
    currentUser: UserAccount?,
    onVote: (String) -> Unit,
    onAddDebate: (String, String) -> Unit,
    onLikeDebate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDebates by remember { mutableStateOf(false) }
    var debateText by remember { mutableStateOf("") }
    var selectedTeam by remember { mutableStateOf("A") }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("battle_card_${competition.id}")
    ) {
        Column {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF211D19))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = FoodieFlamePrimary,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = competition.badge,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = FoodieAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ends in ${competition.endsInDays} days",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Title & Subtitle
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = competition.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
                Text(
                    text = competition.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Contender A vs Contender B Split Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Contender A
                    ContenderBox(
                        contender = competition.contenderA,
                        isVoted = competition.userVotedFor == "A",
                        percentage = competition.percentA,
                        onVote = { onVote("A") },
                        modifier = Modifier.weight(1f),
                        testTag = "vote_btn_${competition.id}_A"
                    )

                    // Contender B
                    ContenderBox(
                        contender = competition.contenderB,
                        isVoted = competition.userVotedFor == "B",
                        percentage = competition.percentB,
                        onVote = { onVote("B") },
                        modifier = Modifier.weight(1f),
                        testTag = "vote_btn_${competition.id}_B"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Animated Live Vote Percentage Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${competition.contenderA.spotName} (${competition.percentA}%)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FoodieFlamePrimary
                        )
                        Text(
                            text = "${competition.percentB}% (${competition.contenderB.spotName})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(competition.percentA.toFloat().coerceAtLeast(1f))
                                .fillMaxHeight()
                                .background(FoodieFlamePrimary)
                        )
                        Box(
                            modifier = Modifier
                                .weight(competition.percentB.toFloat().coerceAtLeast(1f))
                                .fillMaxHeight()
                                .background(Color(0xFF1565C0))
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${competition.totalVotes} total verified foodie votes cast",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Community Foodie Debate Accordion
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDebates = !showDebates }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Forum, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Foodie Debates (${competition.debates.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = if (showDebates) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (showDebates) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .animateContentSize()
                    ) {
                        // Existing debate arguments
                        competition.debates.forEach { debate ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(text = debate.authorEmoji, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${debate.author} (Team ${debate.contenderPick})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (debate.contenderPick == "A") FoodieFlamePrimary else Color(0xFF1565C0)
                                            )
                                            Text(text = debate.timeAgo, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(text = debate.text, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { onLikeDebate(debate.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.ThumbUp, contentDescription = "Like", modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("${debate.likes}", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Add debate input
                        Text("Add Your Argument / Take:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            FilterChip(
                                selected = selectedTeam == "A",
                                onClick = { selectedTeam = "A" },
                                label = { Text("Back ${competition.contenderA.spotName}", fontSize = 10.sp) }
                            )
                            FilterChip(
                                selected = selectedTeam == "B",
                                onClick = { selectedTeam = "B" },
                                label = { Text("Back ${competition.contenderB.spotName}", fontSize = 10.sp) }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = debateText,
                                onValueChange = { debateText = it },
                                placeholder = { Text("Why your pick deserves to win...", fontSize = 12.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("debate_input_${competition.id}")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = {
                                    if (debateText.isNotBlank()) {
                                        onAddDebate(selectedTeam, debateText)
                                        debateText = ""
                                    }
                                },
                                modifier = Modifier.testTag("debate_submit_btn_${competition.id}")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Post", tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContenderBox(
    contender: Contender,
    isVoted: Boolean,
    percentage: Int,
    onVote: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isVoted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        modifier = modifier
            .border(
                width = if (isVoted) 2.dp else 1.dp,
                color = if (isVoted) FoodieFlamePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (contender.id == "A") FoodieFlamePrimary else Color(0xFF1565C0),
                    shape = CircleShape
                ) {
                    Text(
                        text = "Side ${contender.id}",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (isVoted) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Voted", tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Voted", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = contender.spotName,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1
            )
            Text(
                text = contender.dishName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = contender.description,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onVote,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVoted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag)
            ) {
                Icon(
                    imageVector = if (isVoted) Icons.Default.Check else Icons.Default.HowToVote,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isVoted) "Voted (${contender.votes})" else "Vote (${contender.votes})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HallOfFameItem(
    title: String,
    winnerName: String,
    winningDish: String,
    finalScore: String,
    crownEmoji: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF8E1)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = crownEmoji, fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Text(text = "$winnerName 👑", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Champion Dish: $winningDish", fontSize = 12.sp)
                Text(text = finalScore, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
