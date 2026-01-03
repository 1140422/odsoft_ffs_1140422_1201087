package pt.psoft.g1.psoftg1.lendingmanagement.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FineTest {

    private final int fineValuePerDayInCents = 100;

    @Test
    void ensureFineCannotBeCreatedIfNoDelay() {
        Lending lending = mock(Lending.class);
        when(lending.getDaysDelayed()).thenReturn(0);
        when(lending.getFineValuePerDayInCents()).thenReturn(fineValuePerDayInCents);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Fine(lending));
        assertEquals("Lending is not overdue", ex.getMessage());
    }

    @Test
    void ensureFineIsCreatedCorrectlyWhenDelayed() {
        Lending lending = mock(Lending.class);
        when(lending.getDaysDelayed()).thenReturn(3);
        when(lending.getFineValuePerDayInCents()).thenReturn(fineValuePerDayInCents);

        Fine fine = new Fine(lending);

        assertEquals(fineValuePerDayInCents, fine.getFineValuePerDayInCents());
        assertEquals(3 * fineValuePerDayInCents, fine.getCentsValue());
        assertEquals(lending, fine.getLending());
    }

    @Test
    void ensureFineThrowsIfLendingIsNull() {
        assertThrows(NullPointerException.class, () -> new Fine(null));
    }

    @Test
    void ensureFineHasZeroPkInitially() {
        Lending lending = mock(Lending.class);
        when(lending.getDaysDelayed()).thenReturn(2);
        when(lending.getFineValuePerDayInCents()).thenReturn(fineValuePerDayInCents);

        Fine fine = new Fine(lending);
        assertNull(fine.getPk());
    }
}