package com.example.data

import com.example.model.*

object BlueprintSpecs {

    val systemArchitectureOverview = """
    FoodSpotter Full-Stack Cloud Architecture
    ================================================
    [Mobile Clients (Android Compose M3)]
            │  ▲
      HTTPS │  │ WebSocket (WSS - Real-time Queue Stream)
            ▼  │
    [Cloud API Gateway / Envoy Proxy] (Rate Limiting, Auth Validation, JWT)
            │
            ├─► [Discovery & Search Microservice] (Go / Kotlin Spring)
            │      ├─ Spatial Index: PostGIS (ST_DWithin, GiST Index) + Redis Geohash
            │      └─ Full-Text Search: Elasticsearch / pg_trgm for culinary tags & menus
            │
            ├─► [Real-Time Virtual Queue Engine] (Node.js / Elixir OTP)
            │      ├─ In-Memory Line State: Redis Streams & Sorted Sets (ZSET)
            │      ├─ Event Bus: Kafka / RabbitMQ for table calls & push alerts
            │      └─ Push Delivery: Firebase Cloud Messaging (FCM) & WebSocket Server
            │
            └─► [Social & Food Crawl Engine] (Kotlin / Java 21)
                   ├─ Social Feed, Check-ins, Foodie XP Gamification, Reputation Score
                   └─ Graph Traversal for optimized Walking Food Trails
    """.trimIndent()

    val postgresDdlSchema = """
-- 1. SPOTS TABLE (Restaurants, Cafes, Street Food)
CREATE TABLE spots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(140) UNIQUE NOT NULL,
    category VARCHAR(50) NOT NULL, -- street_food, cafe, night_market, etc.
    tagline VARCHAR(255),
    price_level VARCHAR(5) NOT NULL DEFAULT '$', -- $, $$, $$$
    rating NUMERIC(3, 2) DEFAULT 5.00 CHECK (rating >= 1.0 AND rating <= 5.0),
    review_count INT DEFAULT 0,
    check_in_count INT DEFAULT 0,
    address TEXT NOT NULL,
    district VARCHAR(80) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    geom GEOMETRY(Point, 4326) GENERATED ALWAYS AS (ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)) STORED,
    opening_hours JSONB NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX idx_spots_geom ON spots USING GIST(geom);
CREATE INDEX idx_spots_category ON spots(category);
CREATE INDEX idx_spots_rating ON spots(rating DESC);

-- 2. SIGNATURE DISHES TABLE
CREATE TABLE signature_dishes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    spot_id UUID NOT NULL REFERENCES spots(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    price_cents INT NOT NULL,
    description TEXT,
    spice_level SMALLINT DEFAULT 0 CHECK (spice_level BETWEEN 0 AND 3),
    is_vegetarian BOOLEAN DEFAULT FALSE,
    is_chef_special BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
CREATE INDEX idx_dishes_spot ON signature_dishes(spot_id);

-- 3. VIRTUAL QUEUES & TICKETS TABLE
CREATE TABLE queue_tickets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_code VARCHAR(16) NOT NULL, -- e.g. FS-104
    spot_id UUID NOT NULL REFERENCES spots(id) ON DELETE CASCADE,
    user_id UUID,
    guest_name VARCHAR(80) NOT NULL,
    party_size SMALLINT NOT NULL CHECK (party_size > 0),
    seating_pref VARCHAR(30) DEFAULT 'Any', -- indoor, patio, bar
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING', -- WAITING, CALLING, SEATED, CANCELLED, NO_SHOW
    queue_position INT NOT NULL,
    estimated_wait_minutes INT NOT NULL,
    called_at TIMESTAMPTZ,
    seated_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
CREATE INDEX idx_queue_spot_status ON queue_tickets(spot_id, status);

-- 4. FOODIE REVIEWS TABLE
CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    spot_id UUID NOT NULL REFERENCES spots(id) ON DELETE CASCADE,
    user_id UUID NOT NULL,
    author_name VARCHAR(80) NOT NULL,
    rating NUMERIC(2, 1) NOT NULL CHECK (rating BETWEEN 1.0 AND 5.0),
    comment TEXT NOT NULL,
    highlighted_dish VARCHAR(100),
    likes_count INT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW()
);
CREATE INDEX idx_reviews_spot ON reviews(spot_id, created_at DESC);

-- 5. FOOD CRAWLS & ITINERARIES
CREATE TABLE food_crawls (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(120) NOT NULL,
    description TEXT NOT NULL,
    theme_badge VARCHAR(40),
    curator VARCHAR(80),
    stops_order JSONB NOT NULL, -- Array of Spot UUIDs in sequence
    estimated_duration_minutes INT,
    total_distance_km NUMERIC(4, 2),
    created_at TIMESTAMPTZ DEFAULT NOW()
);
    """.trimIndent()

    val firestoreSchemaJson = """
{
  "collections": {
    "spots": {
      "document_id": "auto_generated",
      "fields": {
        "name": "String",
        "category": "String (street_food | cafe | night_market | noodle_bar)",
        "rating": "Number",
        "reviewCount": "Number",
        "priceLevel": "String",
        "location": "GeoPoint (lat, lng)",
        "geohash": "String (precision 7 for 150m cell query)",
        "queueStatus": {
          "level": "String (NO_WAIT | FAST | MODERATE | BUSY)",
          "activeWaitlistCount": "Number",
          "averageWaitMinutes": "Number",
          "lastUpdated": "Timestamp"
        },
        "foodieTip": "String",
        "signatureDishes": "Array<Object>"
      }
    },
    "queues": {
      "document_id": "{spotId}/tickets/{ticketId}",
      "fields": {
        "ticketCode": "String",
        "guestName": "String",
        "partySize": "Number",
        "seatingPreference": "String",
        "status": "String (WAITING | CALLING | SEATED | CANCELLED)",
        "position": "Number",
        "estimatedWaitMinutes": "Number",
        "joinedAt": "Timestamp",
        "calledAt": "Timestamp?"
      }
    },
    "crawls": {
      "document_id": "auto_generated",
      "fields": {
        "title": "String",
        "theme": "String",
        "stops": "Array<String: spotId>",
        "curator": "String",
        "likes": "Number"
      }
    }
  }
}
    """.trimIndent()

    val apiEndpoints = listOf(
        ApiEndpoint(
            method = "GET",
            path = "/api/v1/spots/nearby?lat=37.77&lng=-122.41&radius=3000&category=all",
            summary = "Fetch culinary spots within radius with live queue telemetry",
            responseSample = """
{
  "count": 6,
  "spots": [
    {
      "id": "spot-1",
      "name": "Uncle Jin's Roast Haven",
      "category": "STREET_FOOD",
      "distance_km": 0.4,
      "rating": 4.9,
      "queue": { "level": "FAST", "est_wait_min": 8, "parties_ahead": 3 }
    }
  ]
}
            """.trimIndent()
        ),
        ApiEndpoint(
            method = "POST",
            path = "/api/v1/spots/{spotId}/queue/join",
            summary = "Join real-time virtual waitlist and acquire digital ticket",
            requestSample = """
{
  "guest_name": "Alex Rivera",
  "party_size": 2,
  "seating_preference": "Outdoor Patio"
}
            """.trimIndent(),
            responseSample = """
{
  "ticket_id": "FS-104",
  "spot_id": "spot-3",
  "queue_position": 4,
  "estimated_wait_minutes": 16,
  "status": "WAITING",
  "qr_payload": "foodspotter://ticket/FS-104"
}
            """.trimIndent()
        ),
        ApiEndpoint(
            method = "WS",
            path = "wss://api.foodspotter.com/v1/queues/{ticketId}/live",
            summary = "WebSocket duplex channel for live queue updates, table calls & ETA",
            responseSample = """
{
  "event": "QUEUE_ADVANCED",
  "current_position": 2,
  "estimated_wait_minutes": 8,
  "status": "WAITING"
}
            """.trimIndent()
        ),
        ApiEndpoint(
            method = "POST",
            path = "/api/v1/spots/{spotId}/reviews",
            summary = "Submit authenticated review with photo upload & dish highlight",
            requestSample = """
{
  "rating": 5.0,
  "comment": "Unbelievable crackling pork belly. Best in town!",
  "highlighted_dish": "Crispy Roast Pork Belly"
}
            """.trimIndent(),
            responseSample = """
{
  "review_id": "rev-89312",
  "xp_awarded": 100,
  "new_user_level": "Top 1% Local Eater"
}
            """.trimIndent()
        )
    )

    val coreFeatureSpecs = listOf(
        "1. Dynamic Queue Engine: Uses Redis Sorted Sets (ZSET) ranked by entry timestamp with dynamic table-turn estimation based on historical dining duration per party size.",
        "2. Geospatial Proximity Clustering: Implements PostGIS ST_DWithin and client-side map canvas bounding-box culling for 60fps vector map performance.",
        "3. Live Virtual Waitlist & Grace Window: Generates unique tamper-proof tickets with 5-minute table grace periods and instant push notification alerting.",
        "4. Food Crawl Route Engine: Solves traveling foodie graph optimization to order spots by geographic proximity, avoiding backtracking.",
        "5. Social Proof & Foodie Trust Graph: Check-in verification requires device geofencing (< 50m) to unlock verified badges and prevent fake reviews."
    )
}
