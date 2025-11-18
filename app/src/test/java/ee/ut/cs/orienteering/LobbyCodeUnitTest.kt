package ee.ut.cs.orienteering

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * This unit test verifies that lobby code formatting logic
 * works correctly by trimming spaces, converting text to uppercase,
 * and removing all characters except letters and digits.
 */
class LobbyCodeUnitTest {

    /**
     * Normalizes a raw lobby code input:
     * 1) Removes leading and trailing spaces
     * 2) Converts all characters to uppercase
     * 3) Keeps only letters (A–Z) and digits (0–9)
     */
    private fun normalizeLobbyCode(raw: String): String {
        return raw
            .trim()                      // remove spaces from start and end
            .uppercase()                 // convert to uppercase
            .filter { it.isLetterOrDigit() } // keep only A-Z and 0-9
    }

    @Test
    fun normalizeLobbyCode_trimsUppercasesAndFilters() {
        // Arrange: raw input text with spaces and invalid symbols
        val input = "  ab-12 x  "

        // Act: normalize using the function above
        val normalized = normalizeLobbyCode(input)

        assertEquals("AB12X", normalized)
    }
}
