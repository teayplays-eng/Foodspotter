package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AuthRepository
import com.example.data.FoodSpotRepository
import com.example.model.TicketStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("FoodSpotter", appName)
    }

    @Test
    fun `repository loads initial food spots and handles waitlist`() {
        val repository = FoodSpotRepository()
        val spots = repository.spots.value
        assertTrue("Expected at least 5 initial spots", spots.size >= 5)

        // Join waitlist at first spot
        val targetSpot = spots.first()
        val ticket = repository.joinWaitlist(
            spotId = targetSpot.id,
            guestName = "Foodie Sam",
            partySize = 3,
            seatingPreference = "Indoor"
        )
        assertNotNull("Expected valid queue ticket", ticket)
        assertEquals("Foodie Sam", ticket?.guestName)
        assertEquals(3, ticket?.partySize)
        assertEquals(TicketStatus.WAITING, ticket?.status)

        // Advance queue
        repository.advanceQueue(ticket!!.ticketId)
        val updatedTickets = repository.tickets.value
        val updatedTicket = updatedTickets.find { it.ticketId == ticket.ticketId }
        assertNotNull(updatedTicket)
    }

    @Test
    fun `repository toggles bookmark and check in`() {
        val repository = FoodSpotRepository()
        val initialSpot = repository.spots.value.first()
        val initialBookmark = initialSpot.isBookmarked

        repository.toggleBookmark(initialSpot.id)
        val toggledSpot = repository.spots.value.first { it.id == initialSpot.id }
        assertEquals(!initialBookmark, toggledSpot.isBookmarked)

        val initialCheckIns = repository.checkIns.value
        repository.checkInAtSpot(initialSpot.id)
        assertEquals(initialCheckIns + 1, repository.checkIns.value)
    }

    @Test
    fun `auth repository handles signup login and guest access`() {
        val authRepo = AuthRepository()
        assertNotNull("Default user should be active", authRepo.currentUser.value)

        // Log out
        authRepo.logout()
        assertNull("Current user should be null after logout", authRepo.currentUser.value)

        // Sign Up new user
        val signedUp = authRepo.signUp(
            name = "Ramen Queen",
            email = "ramen.queen@example.com",
            pass = "secret123",
            selectedCuisines = listOf("Ramen & Bowls", "Street Food")
        )
        assertTrue("Sign up should succeed", signedUp)
        assertEquals("Ramen Queen", authRepo.currentUser.value?.displayName)
        assertEquals("ramen.queen@example.com", authRepo.currentUser.value?.email)

        // Guest Login
        authRepo.loginAsGuest()
        assertTrue("User should be in guest mode", authRepo.currentUser.value?.isGuest == true)
    }

    @Test
    fun `food competitions handle voting debate and nominations`() {
        val foodRepo = FoodSpotRepository()
        val comps = foodRepo.competitions.value
        assertTrue("Competitions should be seeded", comps.isNotEmpty())

        val targetComp = comps.first()
        val initialVotesA = targetComp.contenderA.votes

        // Vote for contender A
        foodRepo.voteInCompetition(targetComp.id, "A")
        val updatedComp = foodRepo.competitions.value.first { it.id == targetComp.id }
        assertEquals("A", updatedComp.userVotedFor)
        assertEquals(initialVotesA + 1, updatedComp.contenderA.votes)

        // Add debate comment
        foodRepo.addDebateComment(targetComp.id, "Critic Leo", "🌮", "A", "Unbeatable crunch!")
        val debateComp = foodRepo.competitions.value.first { it.id == targetComp.id }
        assertTrue("Debate comment should be added", debateComp.debates.any { it.author == "Critic Leo" })

        // Submit Spot Nomination
        val initialNomCount = foodRepo.nominations.value.size
        foodRepo.nominateSpot("Auntie Mei's Dumplings", "Pan-fried Pork Potstickers", "Street Food", "Crispy bottom perfection", "Foodie Sam")
        assertEquals(initialNomCount + 1, foodRepo.nominations.value.size)
    }
}
