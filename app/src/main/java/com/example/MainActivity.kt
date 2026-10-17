package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AuthRepository
import com.example.data.FoodSpotRepository
import com.example.model.FoodSpot
import com.example.model.TicketStatus
import com.example.ui.screens.*
import com.example.ui.theme.FoodieFlamePrimary
import com.example.ui.theme.MyApplicationTheme

enum class MainDestination(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    EXPLORE("Explore", Icons.Filled.Restaurant, Icons.Outlined.Restaurant, "nav_explore"),
    MAP("Map", Icons.Filled.Map, Icons.Outlined.Map, "nav_map"),
    ARENA("Arena", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "nav_arena"),
    QUEUES("Waitlists", Icons.Filled.ConfirmationNumber, Icons.Outlined.ConfirmationNumber, "nav_queues"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
}

class MainActivity : ComponentActivity() {
    private val foodRepository = FoodSpotRepository()
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FoodSpotterApp(
                    foodRepository = foodRepository,
                    authRepository = authRepository
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSpotterApp(
    foodRepository: FoodSpotRepository,
    authRepository: AuthRepository
) {
    val spots by foodRepository.spots.collectAsStateWithLifecycle()
    val tickets by foodRepository.tickets.collectAsStateWithLifecycle()
    val crawls by foodRepository.crawls.collectAsStateWithLifecycle()
    val competitions by foodRepository.competitions.collectAsStateWithLifecycle()
    val nominations by foodRepository.nominations.collectAsStateWithLifecycle()

    val currentUser by authRepository.currentUser.collectAsStateWithLifecycle()
    val authError by authRepository.authError.collectAsStateWithLifecycle()

    var currentDestination by remember { mutableStateOf(MainDestination.EXPLORE) }
    var selectedSpotDetail by remember { mutableStateOf<FoodSpot?>(null) }
    var showingBlueprint by remember { mutableStateOf(false) }
    var showingCrawls by remember { mutableStateOf(false) }
    var showAuthModal by remember { mutableStateOf(false) }

    // Subscreen BackHandler
    when {
        selectedSpotDetail != null -> {
            BackHandler { selectedSpotDetail = null }
        }
        showingBlueprint -> {
            BackHandler { showingBlueprint = false }
        }
        showingCrawls -> {
            BackHandler { showingCrawls = false }
        }
    }

    val activeWaitingCount = remember(tickets) {
        tickets.count { it.status == TicketStatus.WAITING || it.status == TicketStatus.CALLING }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (selectedSpotDetail == null && !showingBlueprint && !showingCrawls) {
                NavigationBar(
                    windowInsets = NavigationBarDefaults.windowInsets,
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    MainDestination.values().forEach { destination ->
                        val isSelected = currentDestination == destination

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                currentDestination = destination
                            },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (destination == MainDestination.QUEUES && activeWaitingCount > 0) {
                                            Badge(containerColor = FoodieFlamePrimary) {
                                                Text("$activeWaitingCount", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        } else if (destination == MainDestination.ARENA) {
                                            Badge(containerColor = FoodieFlamePrimary) {
                                                Text("LIVE", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                        contentDescription = destination.title
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = FoodieFlamePrimary,
                                selectedTextColor = FoodieFlamePrimary,
                                indicatorColor = FoodieFlamePrimary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.testTag(destination.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        when {
            selectedSpotDetail != null -> {
                val spot = selectedSpotDetail!!
                val liveSpot = spots.find { it.id == spot.id } ?: spot

                SpotDetailScreen(
                    spot = liveSpot,
                    onBack = { selectedSpotDetail = null },
                    onBookmarkToggle = { foodRepository.toggleBookmark(liveSpot.id) },
                    onCheckIn = {
                        foodRepository.checkInAtSpot(liveSpot.id)
                        authRepository.recordCheckIn()
                    },
                    onJoinWaitlist = { guestName, partySize, seatingPref ->
                        val ticket = foodRepository.joinWaitlist(liveSpot.id, guestName, partySize, seatingPref)
                        if (ticket != null) {
                            selectedSpotDetail = null
                            currentDestination = MainDestination.QUEUES
                        }
                    },
                    onAddReview = { author, rating, comment, dish ->
                        foodRepository.addReview(liveSpot.id, author, rating, comment, dish)
                        authRepository.addXp(100)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            showingBlueprint -> {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("FoodSpotter Blueprint") },
                            navigationIcon = {
                                IconButton(onClick = { showingBlueprint = false }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                                }
                            }
                        )
                    }
                ) { bpPadding ->
                    BlueprintScreen(modifier = Modifier.padding(bpPadding))
                }
            }

            showingCrawls -> {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Curated Food Crawls") },
                            navigationIcon = {
                                IconButton(onClick = { showingCrawls = false }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                                }
                            }
                        )
                    }
                ) { crawlPadding ->
                    FoodCrawlsScreen(
                        crawls = crawls,
                        allSpots = spots,
                        onToggleCrawl = { foodRepository.toggleCrawlProgress(it) },
                        onSpotClick = { selectedSpotDetail = it },
                        modifier = Modifier.padding(crawlPadding)
                    )
                }
            }

            else -> {
                Crossfade(
                    targetState = currentDestination,
                    modifier = Modifier.fillMaxSize()
                ) { destination ->
                    when (destination) {
                        MainDestination.EXPLORE -> {
                            ExploreScreen(
                                spots = spots,
                                activeTickets = tickets,
                                currentUser = currentUser,
                                onSpotClick = { selectedSpotDetail = it },
                                onBookmarkToggle = { foodRepository.toggleBookmark(it) },
                                onOpenTickets = { currentDestination = MainDestination.QUEUES },
                                onOpenCrawls = { showingCrawls = true },
                                onOpenMap = { currentDestination = MainDestination.MAP },
                                onOpenArena = { currentDestination = MainDestination.ARENA },
                                onOpenAuth = { showAuthModal = true },
                                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                            )
                        }

                        MainDestination.MAP -> {
                            MapScreen(
                                spots = spots,
                                onSpotClick = { selectedSpotDetail = it },
                                onJoinWaitlist = { spot ->
                                    selectedSpotDetail = spot
                                },
                                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                            )
                        }

                        MainDestination.ARENA -> {
                            CompetitionsScreen(
                                competitions = competitions,
                                nominations = nominations,
                                currentUser = currentUser,
                                onVote = { compId, pick ->
                                    foodRepository.voteInCompetition(compId, pick)
                                    authRepository.recordCompetitionVote()
                                },
                                onAddDebate = { compId, author, emoji, pick, text ->
                                    foodRepository.addDebateComment(compId, author, emoji, pick, text)
                                    authRepository.addXp(25)
                                },
                                onLikeDebate = { compId, commentId ->
                                    foodRepository.likeDebateComment(compId, commentId)
                                },
                                onNominateSpot = { name, dish, cat, reason ->
                                    val authorName = currentUser?.displayName ?: "Anonymous Foodie"
                                    foodRepository.nominateSpot(name, dish, cat, reason, authorName)
                                    authRepository.addXp(60)
                                },
                                onVoteNomination = { nomId ->
                                    foodRepository.voteForNomination(nomId)
                                    authRepository.addXp(15)
                                },
                                onRequireLogin = {
                                    showAuthModal = true
                                },
                                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                            )
                        }

                        MainDestination.QUEUES -> {
                            QueueTicketsScreen(
                                tickets = tickets,
                                onAdvanceQueue = { foodRepository.advanceQueue(it) },
                                onCancelTicket = { foodRepository.cancelTicket(it) },
                                onExploreSpots = { currentDestination = MainDestination.EXPLORE },
                                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                            )
                        }

                        MainDestination.PROFILE -> {
                            ProfileScreen(
                                currentUser = currentUser,
                                bookmarkedSpots = spots.filter { it.isBookmarked },
                                onOpenAuth = { showAuthModal = true },
                                onLogout = { authRepository.logout() },
                                onSpotClick = { selectedSpotDetail = it },
                                onOpenBlueprint = { showingBlueprint = true },
                                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                            )
                        }
                    }
                }
            }
        }

        // Global Auth Modal (Login / Sign Up)
        AuthDialog(
            isOpen = showAuthModal,
            errorMessage = authError,
            onDismiss = {
                authRepository.clearError()
                showAuthModal = false
            },
            onLogin = { email, pass ->
                authRepository.login(email, pass)
            },
            onSignUp = { name, email, pass, cuisines ->
                authRepository.signUp(name, email, pass, cuisines)
            },
            onGoogleLogin = {
                authRepository.loginWithGoogle()
            },
            onGuestLogin = {
                authRepository.loginAsGuest()
            }
        )
    }
}
