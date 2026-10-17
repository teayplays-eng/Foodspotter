package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FoodSpotRepository {

    private val _spots = MutableStateFlow<List<FoodSpot>>(emptyList())
    val spots: StateFlow<List<FoodSpot>> = _spots.asStateFlow()

    private val _tickets = MutableStateFlow<List<QueueTicket>>(emptyList())
    val tickets: StateFlow<List<QueueTicket>> = _tickets.asStateFlow()

    private val _crawls = MutableStateFlow<List<FoodCrawl>>(emptyList())
    val crawls: StateFlow<List<FoodCrawl>> = _crawls.asStateFlow()

    private val _competitions = MutableStateFlow<List<FoodCompetition>>(emptyList())
    val competitions: StateFlow<List<FoodCompetition>> = _competitions.asStateFlow()

    private val _nominations = MutableStateFlow<List<SpotNomination>>(emptyList())
    val nominations: StateFlow<List<SpotNomination>> = _nominations.asStateFlow()

    private val _userXp = MutableStateFlow(380)
    val userXp: StateFlow<Int> = _userXp.asStateFlow()

    private val _checkIns = MutableStateFlow(8)
    val checkIns: StateFlow<Int> = _checkIns.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val initialSpots = listOf(
            FoodSpot(
                id = "spot-1",
                name = "Uncle Jin's Roast Haven",
                tagline = "Crisp Crackling Pork & 40-Year Heritage Char Siu",
                category = FoodCategory.STREET_FOOD,
                rating = 4.9f,
                reviewCount = 1420,
                priceLevel = PriceLevel.BUDGET,
                distanceKm = 0.4,
                queueLevel = QueueLevel.FAST,
                estimatedWaitMinutes = 8,
                queuePartiesCount = 3,
                address = "48 Lantern Alley, Chinatown",
                district = "Old Chinatown",
                openingHours = "10:30 AM – 8:00 PM (Or until sold out)",
                isOpenNow = true,
                isBookmarked = true,
                checkInCount = 520,
                coordinates = Coordinates(lat = 37.7749, lng = -122.4194, mapX = 0.28f, mapY = 0.35f),
                drawableResName = "hero_nightmarket",
                signatureDishes = listOf(
                    SignatureDish("d1", "Crispy Roast Pork Belly", "$11.50", "Ultra-crisp crackling skin with tender five-spice pork", 1, isChefSpecial = true),
                    SignatureDish("d2", "Honey Glazed Char Siu", "$12.00", "Charcoal roasted BBQ pork caramelized with wild honey", 0, isChefSpecial = true),
                    SignatureDish("d3", "Ginger Scallion Egg Noodles", "$8.50", "Springy egg noodles tossed in aromatic ginger scallion oil", 0, isVegetarian = true)
                ),
                reviews = listOf(
                    FoodieReview("r1", "Marcus Chen", "Top 1% Local Eater", 5.0f, "2 hrs ago", "The crackling on the pork belly sounds like breaking glass. Best char siu in the whole county!", "Crispy Roast Pork Belly", 28),
                    FoodieReview("r2", "Sarah Jenkins", "Night Market Guide", 4.8f, "Yesterday", "Fast moving line, friendly staff, and the chili oil on the side gives it an unforgettable kick.", null, 14)
                ),
                foodieTip = "Arrive around 11:15 AM to get first dibs on the roasted duck breast before the lunch crowd descends.",
                specialTags = listOf("Michelin Bib Gourmand", "Cash & Tap", "Outdoor Benches", "Iconic Recipe")
            ),
            FoodSpot(
                id = "spot-2",
                name = "Velvet & Ember Micro-Roastery",
                tagline = "Artisan Single-Origin Espresso & Flaky Cardamom Buns",
                category = FoodCategory.CAFE,
                rating = 4.9f,
                reviewCount = 980,
                priceLevel = PriceLevel.MODERATE,
                distanceKm = 0.8,
                queueLevel = QueueLevel.NO_WAIT,
                estimatedWaitMinutes = 4,
                queuePartiesCount = 1,
                address = "120 Factory Row, Arts District",
                district = "Arts District",
                openingHours = "7:00 AM – 6:00 PM",
                isOpenNow = true,
                isBookmarked = true,
                checkInCount = 890,
                coordinates = Coordinates(lat = 37.7760, lng = -122.4170, mapX = 0.65f, mapY = 0.25f),
                drawableResName = "hero_cafe",
                signatureDishes = listOf(
                    SignatureDish("d4", "Smoked Vanilla Flat White", "$6.00", "Double ristretto with smoked Madagascar vanilla & silky microfoam", 0, isChefSpecial = true),
                    SignatureDish("d5", "Swedish Cardamom Knot", "$5.50", "Hand-twisted laminated pastry with crushed aromatic cardamom pearls", 0, isVegetarian = true, isChefSpecial = true),
                    SignatureDish("d6", "Ethiopian Yirgacheffe Pour-Over", "$7.25", "Bright jasmine notes, bergamot peach finish brewed via V60", 0, isVegetarian = true)
                ),
                reviews = listOf(
                    FoodieReview("r3", "Elena Rostova", "Coffee Critic", 5.0f, "1 day ago", "Sunlight through the warehouse windows with this cardamom knot is absolute perfection.", "Swedish Cardamom Knot", 42),
                    FoodieReview("r4", "David K.", "Daily Commuter", 4.7f, "3 days ago", "Fast barista bar, quiet corner tables with power plugs.", null, 9)
                ),
                foodieTip = "Ask for the off-menu seasonal Gesha pour-over on weekends.",
                specialTags = listOf("In-House Roaster", "Dog Friendly Patio", "Fast Wi-Fi", "Specialty Coffee")
            ),
            FoodSpot(
                id = "spot-3",
                name = "Neon Tiger Night Market",
                tagline = "Bustling Hawker Alley with Sizzling Street Skewers",
                category = FoodCategory.NIGHT_MARKET,
                rating = 4.8f,
                reviewCount = 2840,
                priceLevel = PriceLevel.BUDGET,
                distanceKm = 1.4,
                queueLevel = QueueLevel.BUSY,
                estimatedWaitMinutes = 25,
                queuePartiesCount = 9,
                address = "Pier 14 Boardwalk, Harbor Front",
                district = "Harbor Front",
                openingHours = "5:00 PM – 1:30 AM",
                isOpenNow = true,
                isBookmarked = false,
                checkInCount = 2140,
                coordinates = Coordinates(lat = 37.7710, lng = -122.4110, mapX = 0.82f, mapY = 0.68f),
                drawableResName = "hero_nightmarket",
                signatureDishes = listOf(
                    SignatureDish("d7", "Sizzling Cumin Lamb Skewers", "$10.00", "Charcoal-grilled lamb loin crusted in toasted Xinjiang cumin & chili flakes", 2, isChefSpecial = true),
                    SignatureDish("d8", "Golden Crispy Pork Belly Buns", "$9.50", "Fluffy steamed bao stuffed with pickled mustard greens & crunchy pork", 1, isChefSpecial = true),
                    SignatureDish("d9", "Thai Basil Clam Stir-fry", "$13.00", "Wok-tossed Manila clams in sweet chili jam & holy basil", 2)
                ),
                reviews = listOf(
                    FoodieReview("r5", "Kenzo T.", "Night Market Master", 4.9f, "4 hrs ago", "The atmosphere is unmatched! Join the virtual queue as soon as you enter the pier.", "Sizzling Cumin Lamb Skewers", 53)
                ),
                foodieTip = "Queue for skewers first, grab cold draft beer at the middle tap stall while you wait.",
                specialTags = listOf("Late Night", "Live Acoustic", "Craft Beers", "Vibrant Vibes")
            ),
            FoodSpot(
                id = "spot-4",
                name = "Kagoshima Wagyu & Ramen Lab",
                tagline = "24-Hour Collagen Broth & Torched A5 Wagyu Slices",
                category = FoodCategory.NOODLE_BAR,
                rating = 4.9f,
                reviewCount = 1890,
                priceLevel = PriceLevel.MODERATE,
                distanceKm = 1.1,
                queueLevel = QueueLevel.MODERATE,
                estimatedWaitMinutes = 18,
                queuePartiesCount = 6,
                address = "77 Sakura Way, Japantown",
                district = "Japantown",
                openingHours = "11:30 AM – 10:00 PM",
                isOpenNow = true,
                isBookmarked = false,
                checkInCount = 940,
                coordinates = Coordinates(lat = 37.7780, lng = -122.4220, mapX = 0.22f, mapY = 0.18f),
                drawableResName = null,
                signatureDishes = listOf(
                    SignatureDish("d10", "A5 Wagyu Truffle Tonkotsu", "$22.50", "Velvety pork bone broth, torched Wagyu beef, black truffle paste, ajitsuke tamago", 0, isChefSpecial = true),
                    SignatureDish("d11", "Spicy Garlic Tan Tan Men", "$17.00", "Sesame chili broth, minced spiced pork, bok choy, spring onions", 2, isChefSpecial = true),
                    SignatureDish("d12", "Crispy Kurobuta Gyoza", "$8.50", "Handmade pan-fried dumplings with yuzu ponzu dip", 0)
                ),
                reviews = listOf(
                    FoodieReview("r6", "Jessica Wong", "Ramen Connoisseur", 5.0f, "Yesterday", "The noodles have that ideal al dente chew and the Wagyu literally melts on your tongue.", "A5 Wagyu Truffle Tonkotsu", 37)
                ),
                foodieTip = "Order noodle firmness 'Katame' (firm) for the best texture in hot tonkotsu broth.",
                specialTags = listOf("Handmade Noodles", "Craft Broth", "Bar Seating", "Solo-Diner Friendly")
            ),
            FoodSpot(
                id = "spot-5",
                name = "Smoky Bull Pitmaster Taqueria",
                tagline = "16-Hour Oak Smoked Brisket Quesabirria with Rich Consomé",
                category = FoodCategory.STREET_FOOD,
                rating = 4.8f,
                reviewCount = 1630,
                priceLevel = PriceLevel.BUDGET,
                distanceKm = 1.9,
                queueLevel = QueueLevel.FAST,
                estimatedWaitMinutes = 10,
                queuePartiesCount = 4,
                address = "310 Mission Boulevard, South District",
                district = "Mission South",
                openingHours = "11:00 AM – 11:00 PM",
                isOpenNow = true,
                isBookmarked = false,
                checkInCount = 1120,
                coordinates = Coordinates(lat = 37.7680, lng = -122.4180, mapX = 0.42f, mapY = 0.78f),
                drawableResName = "hero_nightmarket",
                signatureDishes = listOf(
                    SignatureDish("d13", "Brisket Quesabirria Tacos (3x)", "$14.00", "Handmade corn tortillas dipped in chili oil, melted Oaxaca cheese, brisket, consomé cup", 2, isChefSpecial = true),
                    SignatureDish("d14", "Smoked Pork Carnitas Bowl", "$12.50", "Citrus confit pork shoulder, cilantro lime rice, black beans, charred salsa verde", 1),
                    SignatureDish("d15", "Agua de Horchata Fresca", "$4.50", "House-made cinnamon rice milk over crushed ice", 0, isVegetarian = true)
                ),
                reviews = listOf(
                    FoodieReview("r7", "Mateo Gomez", "Street Taco Enthusiast", 4.9f, "3 days ago", "Dipping that cheesy crunchy taco into the bubbling consomé is a religious experience.", "Brisket Quesabirria Tacos (3x)", 45)
                ),
                foodieTip = "Squeeze fresh lime directly into the consomé broth before your first dip.",
                specialTags = listOf("Handmade Tortillas", "Smoked Meats", "Outdoor Patio", "Late Night")
            ),
            FoodSpot(
                id = "spot-6",
                name = "Petite Lumière Artisan Bakery",
                tagline = "Golden Kouign-Amann & Crisp Sourdough Batards",
                category = FoodCategory.DESSERT_BAKERY,
                rating = 4.9f,
                reviewCount = 820,
                priceLevel = PriceLevel.MODERATE,
                distanceKm = 2.3,
                queueLevel = QueueLevel.NO_WAIT,
                estimatedWaitMinutes = 3,
                queuePartiesCount = 1,
                address = "18 Promenade Way, Marina Heights",
                district = "Marina Heights",
                openingHours = "6:30 AM – 3:30 PM",
                isOpenNow = true,
                isBookmarked = false,
                checkInCount = 610,
                coordinates = Coordinates(lat = 37.7800, lng = -122.4130, mapX = 0.75f, mapY = 0.12f),
                drawableResName = "hero_cafe",
                signatureDishes = listOf(
                    SignatureDish("d16", "Breton Kouign-Amann", "$5.75", "Salted caramel laminated pastry with crackling spun-sugar base", 0, isVegetarian = true, isChefSpecial = true),
                    SignatureDish("d17", "Wild Honey & Pistachio Tart", "$7.00", "Toasted pistachio frangipane, orange blossom water, raw clover honey", 0, isVegetarian = true),
                    SignatureDish("d18", "Rustic 48-Hour Sourdough Loaf", "$9.00", "Crispy blistered crust with open airy crumb, organic heirloom wheat", 0, isVegetarian = true)
                ),
                reviews = listOf(
                    FoodieReview("r8", "Claire B.", "Pastry Chef", 5.0f, "Yesterday", "Butter quality is unmatched—they import directly from Normandy. The kouign-amann is sublime.", "Breton Kouign-Amann", 31)
                ),
                foodieTip = "Warm the kouign-amann in an oven for 2 minutes if taking home for maximum caramel crunch.",
                specialTags = listOf("Organic Wheat", "Normandy Butter", "Early Birds", "Takeaway Window")
            )
        )

        val initialCrawls = listOf(
            FoodCrawl(
                id = "crawl-1",
                title = "Midnight Street Food Odyssey",
                description = "Traverse the sizzling heart of Old Town & Pier 14. Taste iconic crackling pork, spicy wok clams, and charcoal skewers.",
                themeBadge = "🔥 Night Owl Trail",
                estimatedDuration = "2 hrs 30 mins",
                totalDistance = "1.8 km",
                stopSpotIds = listOf("spot-1", "spot-5", "spot-3"),
                highlights = listOf("Char Siu Pork", "Quesabirria Tacos", "Cumin Skewers"),
                author = "Curated by Chef Kenji",
                completedStops = 0,
                isStarted = false
            ),
            FoodCrawl(
                id = "crawl-2",
                title = "Artisan Cafe & Sweet Escapes",
                description = "Morning sanctuary tour visiting the top micro-roasteries and world-class pastry ateliers in the Arts District.",
                themeBadge = "☕ Morning Ritual",
                estimatedDuration = "1 hr 45 mins",
                totalDistance = "1.2 km",
                stopSpotIds = listOf("spot-2", "spot-6"),
                highlights = listOf("Smoked Vanilla Latte", "Cardamom Knots", "Breton Kouign-Amann"),
                author = "Curated by Barista Guild",
                completedStops = 0,
                isStarted = false
            ),
            FoodCrawl(
                id = "crawl-3",
                title = "Comfort Broth & Noodle Run",
                description = "Rich 24-hr ramen, sizzling wok noodles, and comforting heritage bites across culinary enclaves.",
                themeBadge = "🍜 Noodle Lovers",
                estimatedDuration = "2 hrs 15 mins",
                totalDistance = "2.1 km",
                stopSpotIds = listOf("spot-4", "spot-1"),
                highlights = listOf("Truffle Wagyu Tonkotsu", "Ginger Scallion Noodles"),
                author = "Curated by Local Foodies",
                completedStops = 0,
                isStarted = false
            )
        )

        // Seed 1 active sample ticket for immediate interactivity
        val initialTicket = QueueTicket(
            ticketId = "FS-088",
            spotId = "spot-3",
            spotName = "Neon Tiger Night Market",
            spotAddress = "Pier 14 Boardwalk, Harbor Front",
            guestName = "Alex Rivera",
            partySize = 2,
            seatingPreference = "Outdoor Patio",
            initialPosition = 5,
            currentPosition = 3,
            estimatedWaitMinutes = 14,
            status = TicketStatus.WAITING
        )

        _spots.value = initialSpots
        _crawls.value = initialCrawls
        _tickets.value = listOf(initialTicket)

        val initialCompetitions = listOf(
            FoodCompetition(
                id = "comp-1",
                title = "The Golden Crackle Showdown",
                subtitle = "Heritage Charcoal Pork Belly vs Neon Tiger Steamed Bao",
                category = "Street Food",
                badge = "🔥 Main Event of the Week",
                endsInDays = 3,
                prizePool = "$1,000 Culinary Grant + Golden Cleaver Trophy",
                isFeatured = true,
                contenderA = Contender(
                    id = "A",
                    spotId = "spot-1",
                    spotName = "Uncle Jin's Roast Haven",
                    dishName = "Crispy Crackling Roast Pork",
                    description = "40-year secret spice blend, blistered crunchy skin, slow-roasted over lychee wood charcoal.",
                    chefQuote = "\"When you bite, it should sound like thunder.\" — Uncle Jin",
                    votes = 642
                ),
                contenderB = Contender(
                    id = "B",
                    spotId = "spot-3",
                    spotName = "Neon Tiger Night Market",
                    dishName = "Crispy Pork Belly Steamed Buns",
                    description = "Pillowy warm lotus leaf bao, pickled mustard greens, crushed peanut sugar, torch-glazed pork.",
                    chefQuote = "\"The contrast of soft cloud bun and shattering pork is unmatched.\" — Chef Wei",
                    votes = 518
                ),
                userVotedFor = null,
                debates = listOf(
                    DebateComment("deb-1", "Marcus Chen", "🍜", "A", "Uncle Jin's five-spice ratio is unbeatable. 40 years of mastery cannot be faked!", 24, "1 hr ago"),
                    DebateComment("deb-2", "Elena R.", "☕", "B", "The pickled greens in the Neon Tiger bao cut the richness so well. Team Bao all the way!", 17, "3 hrs ago")
                )
            ),
            FoodCompetition(
                id = "comp-2",
                title = "Artisan Pastry & Coffee Face-Off",
                subtitle = "Swedish Cardamom Knot vs Breton Kouign-Amann",
                category = "Bakery & Cafes",
                badge = "☕ Morning Duel",
                endsInDays = 5,
                prizePool = "Roaster of the Year Feature + $500 Coffee Gear",
                isFeatured = false,
                contenderA = Contender(
                    id = "A",
                    spotId = "spot-2",
                    spotName = "Velvet & Ember",
                    dishName = "Swedish Cardamom Knot",
                    description = "Freshly crushed green cardamom seeds, pearl sugar crystals, paired with single-origin Gesha.",
                    chefQuote = "\"Aromatic warmth that wakes up all five senses.\"",
                    votes = 389
                ),
                contenderB = Contender(
                    id = "B",
                    spotId = "spot-6",
                    spotName = "Petite Lumière",
                    dishName = "Breton Kouign-Amann",
                    description = "Hand-laminated Normandy butter pastry with crackling spun-sugar caramelized base.",
                    chefQuote = "\"Butter and sugar in absolute harmonic balance.\"",
                    votes = 412
                ),
                userVotedFor = null,
                debates = listOf(
                    DebateComment("deb-3", "Claire B.", "🥐", "B", "That caramelized crunchy base on the Kouign-Amann is world class.", 31, "Yesterday")
                )
            ),
            FoodCompetition(
                id = "comp-3",
                title = "Birria Tacos vs Wagyu Ramen",
                subtitle = "Consomé Dipped Tacos vs 24-Hour Collagen Broth",
                category = "Comfort Broth",
                badge = "⚡ Heavyweight Clash",
                endsInDays = 6,
                prizePool = "People's Choice Food Spot Banner",
                isFeatured = false,
                contenderA = Contender(
                    id = "A",
                    spotId = "spot-5",
                    spotName = "Smoky Bull Pitmaster",
                    dishName = "Smoked Brisket Quesabirria",
                    description = "16-hour oak smoked brisket, crispy cheese crust, bubbling spiced consomé dip.",
                    chefQuote = "\"Dip deep and drink the broth.\"",
                    votes = 529
                ),
                contenderB = Contender(
                    id = "B",
                    spotId = "spot-4",
                    spotName = "Kagoshima Wagyu Lab",
                    dishName = "A5 Wagyu Truffle Tonkotsu",
                    description = "24-hr rich pork bone collagen, torched Japanese Wagyu, black summer truffle paste.",
                    chefQuote = "\"Every spoonful is pure velvet umami.\"",
                    votes = 502
                ),
                userVotedFor = null,
                debates = listOf(
                    DebateComment("deb-4", "David K.", "🔥", "A", "The smokiness from the oak wood changes the entire birria game.", 19, "2 days ago")
                )
            )
        )

        val initialNominations = listOf(
            SpotNomination("nom-1", "Mama Linh's Banh Mi", "Crispy Pork Belly Banh Mi", "Street Food", "Best baguette crust in the bay area, pâté is heavenly.", "Chef Kenji", 46),
            SpotNomination("nom-2", "Golden Dragon Claypot", "Salted Fish & Chicken Claypot", "Iconic Eats", "Crispy bottom rice crackling that you can hear across the room.", "Sarah J.", 38),
            SpotNomination("nom-3", "Tora Yakitori Bar", "Tare-Glazed Tsukune Meatball", "Night Market", "Raw egg yolk dip and charcoal smoke perfection.", "Mateo G.", 29)
        )

        _competitions.value = initialCompetitions
        _nominations.value = initialNominations
    }

    fun toggleBookmark(spotId: String) {
        _spots.update { currentList ->
            currentList.map { spot ->
                if (spot.id == spotId) {
                    spot.copy(isBookmarked = !spot.isBookmarked)
                } else {
                    spot
                }
            }
        }
    }

    fun joinWaitlist(
        spotId: String,
        guestName: String,
        partySize: Int,
        seatingPreference: String
    ): QueueTicket? {
        val spot = _spots.value.find { it.id == spotId } ?: return null
        val nextNum = (100..999).random()
        val newTicket = QueueTicket(
            ticketId = "FS-$nextNum",
            spotId = spot.id,
            spotName = spot.name,
            spotAddress = spot.address,
            guestName = guestName.ifBlank { "Guest" },
            partySize = partySize,
            seatingPreference = seatingPreference,
            initialPosition = spot.queuePartiesCount + 1,
            currentPosition = spot.queuePartiesCount + 1,
            estimatedWaitMinutes = (spot.queuePartiesCount + 1) * 4,
            status = TicketStatus.WAITING
        )

        _tickets.update { listOf(newTicket) + it }
        // Update spot queue count
        _spots.update { list ->
            list.map { s ->
                if (s.id == spotId) {
                    s.copy(
                        queuePartiesCount = s.queuePartiesCount + 1,
                        estimatedWaitMinutes = s.estimatedWaitMinutes + 4
                    )
                } else s
            }
        }
        return newTicket
    }

    fun advanceQueue(ticketId: String) {
        _tickets.update { list ->
            list.map { ticket ->
                if (ticket.ticketId == ticketId) {
                    when {
                        ticket.currentPosition > 1 -> {
                            ticket.copy(
                                currentPosition = ticket.currentPosition - 1,
                                estimatedWaitMinutes = maxOf(2, ticket.estimatedWaitMinutes - 4)
                            )
                        }
                        ticket.currentPosition == 1 && ticket.status == TicketStatus.WAITING -> {
                            ticket.copy(
                                currentPosition = 0,
                                estimatedWaitMinutes = 0,
                                status = TicketStatus.CALLING
                            )
                        }
                        ticket.status == TicketStatus.CALLING -> {
                            ticket.copy(status = TicketStatus.SEATED)
                        }
                        else -> ticket
                    }
                } else ticket
            }
        }
    }

    fun cancelTicket(ticketId: String) {
        _tickets.update { list ->
            list.map { ticket ->
                if (ticket.ticketId == ticketId) {
                    ticket.copy(status = TicketStatus.CANCELLED)
                } else ticket
            }
        }
    }

    fun checkInAtSpot(spotId: String) {
        _checkIns.update { it + 1 }
        _userXp.update { it + 50 }
        _spots.update { list ->
            list.map { spot ->
                if (spot.id == spotId) {
                    spot.copy(checkInCount = spot.checkInCount + 1)
                } else spot
            }
        }
    }

    fun addReview(spotId: String, authorName: String, rating: Float, comment: String, dish: String?) {
        val newReview = FoodieReview(
            id = "rev-${System.currentTimeMillis()}",
            authorName = authorName.ifBlank { "You" },
            authorBadge = "Verified Foodie",
            rating = rating,
            timeAgo = "Just now",
            comment = comment,
            highlightedDish = dish,
            likesCount = 1
        )
        _userXp.update { it + 100 }
        _spots.update { list ->
            list.map { spot ->
                if (spot.id == spotId) {
                    val updatedReviews = listOf(newReview) + spot.reviews
                    val newRating = (spot.rating * spot.reviewCount + rating) / (spot.reviewCount + 1)
                    spot.copy(
                        reviews = updatedReviews,
                        reviewCount = spot.reviewCount + 1,
                        rating = String.format("%.1f", newRating).toFloatOrNull() ?: spot.rating
                    )
                } else spot
            }
        }
    }

    fun toggleCrawlProgress(crawlId: String) {
        _crawls.update { list ->
            list.map { crawl ->
                if (crawl.id == crawlId) {
                    if (!crawl.isStarted) {
                        crawl.copy(isStarted = true, completedStops = 1)
                    } else if (crawl.completedStops < crawl.stopSpotIds.size) {
                        crawl.copy(completedStops = crawl.completedStops + 1)
                    } else {
                        crawl.copy(completedStops = 0, isStarted = false)
                    }
                } else crawl
            }
        }
        _userXp.update { it + 75 }
    }

    fun voteInCompetition(competitionId: String, pick: String) {
        _competitions.update { list ->
            list.map { comp ->
                if (comp.id == competitionId) {
                    val previousVote = comp.userVotedFor
                    if (previousVote == pick) {
                        comp // already voted for this contender
                    } else {
                        var votesA = comp.contenderA.votes
                        var votesB = comp.contenderB.votes
                        if (previousVote == "A") votesA--
                        if (previousVote == "B") votesB--

                        if (pick == "A") votesA++
                        if (pick == "B") votesB++

                        comp.copy(
                            userVotedFor = pick,
                            contenderA = comp.contenderA.copy(votes = votesA),
                            contenderB = comp.contenderB.copy(votes = votesB)
                        )
                    }
                } else comp
            }
        }
        _userXp.update { it + 50 }
    }

    fun addDebateComment(competitionId: String, author: String, authorEmoji: String, pick: String, text: String) {
        val newComment = DebateComment(
            id = "deb-${System.currentTimeMillis()}",
            author = author.ifBlank { "You" },
            authorEmoji = authorEmoji,
            contenderPick = pick,
            text = text,
            likes = 1,
            timeAgo = "Just now"
        )
        _competitions.update { list ->
            list.map { comp ->
                if (comp.id == competitionId) {
                    comp.copy(debates = listOf(newComment) + comp.debates)
                } else comp
            }
        }
        _userXp.update { it + 25 }
    }

    fun likeDebateComment(competitionId: String, commentId: String) {
        _competitions.update { list ->
            list.map { comp ->
                if (comp.id == competitionId) {
                    val updatedDebates = comp.debates.map { deb ->
                        if (deb.id == commentId) deb.copy(likes = deb.likes + 1) else deb
                    }
                    comp.copy(debates = updatedDebates)
                } else comp
            }
        }
    }

    fun nominateSpot(spotName: String, dishName: String, category: String, reason: String, author: String) {
        val newNomination = SpotNomination(
            id = "nom-${System.currentTimeMillis()}",
            spotName = spotName,
            dishName = dishName,
            category = category,
            justification = reason,
            nominatedBy = author.ifBlank { "Anonymous Foodie" },
            votes = 1
        )
        _nominations.update { listOf(newNomination) + it }
        _userXp.update { it + 60 }
    }

    fun voteForNomination(nominationId: String) {
        _nominations.update { list ->
            list.map { nom ->
                if (nom.id == nominationId) nom.copy(votes = nom.votes + 1) else nom
            }
        }
        _userXp.update { it + 15 }
    }
}
