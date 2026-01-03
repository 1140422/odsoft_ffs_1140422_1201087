package pt.psoft.g1.psoftg1.genremanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    private Genre genre;
    private static final String GENRE_NAME = "Science Fiction";

    @BeforeEach
    void setUp() {
        genre = new Genre(GENRE_NAME);
    }

    @Test
    void shouldReturnGenreWhenFound() {
        // given
        when(genreRepository.findByString(GENRE_NAME)).thenReturn(Optional.of(genre));

        // when
        Optional<Genre> result = genreService.findByString(GENRE_NAME);

        // then
        assertTrue(result.isPresent());
        assertEquals(GENRE_NAME, result.get().getGenre());
        verify(genreRepository, times(1)).findByString(GENRE_NAME);
    }

    @Test
    void shouldReturnEmptyWhenGenreNotFound() {
        // given
        when(genreRepository.findByString("NonExistent")).thenReturn(Optional.empty());

        // when
        Optional<Genre> result = genreService.findByString("NonExistent");

        // then
        assertFalse(result.isPresent());
        verify(genreRepository, times(1)).findByString("NonExistent");
    }

    @Test
    void shouldReturnAllGenres() {
        // given
        Genre genre1 = new Genre("Fiction");
        Genre genre2 = new Genre("Non-Fiction");
        List<Genre> genres = List.of(genre1, genre2);

        when(genreRepository.findAll()).thenReturn(genres);

        // when
        Iterable<Genre> result = genreService.findAll();

        // then
        assertNotNull(result);
        List<Genre> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        assertEquals(2, resultList.size());
        verify(genreRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoGenresExist() {
        // given
        when(genreRepository.findAll()).thenReturn(new ArrayList<>());

        // when
        Iterable<Genre> result = genreService.findAll();

        // then
        assertNotNull(result);
        List<Genre> resultList = new ArrayList<>();
        result.forEach(resultList::add);
        assertTrue(resultList.isEmpty());
    }

    @Test
    void shouldReturnTop5GenresByBookCount() {
        // given
        GenreBookCountDTO dto1 = new GenreBookCountDTO("Fiction", 100L);
        GenreBookCountDTO dto2 = new GenreBookCountDTO("Science", 80L);
        List<GenreBookCountDTO> dtos = List.of(dto1, dto2);
        Page<GenreBookCountDTO> page = new PageImpl<>(dtos);

        when(genreRepository.findTop5GenreByBookCount(any(Pageable.class))).thenReturn(page);

        // when
        List<GenreBookCountDTO> result = genreService.findTopGenreByBooks();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Fiction", result.get(0).getGenre());
        assertEquals(100L, result.get(0).getBookCount());

        verify(genreRepository, times(1)).findTop5GenreByBookCount(any(Pageable.class));
    }

    @Test
    void shouldReturnEmptyListWhenNoGenresFound() {
        // given
        Page<GenreBookCountDTO> emptyPage = new PageImpl<>(new ArrayList<>());
        when(genreRepository.findTop5GenreByBookCount(any(Pageable.class))).thenReturn(emptyPage);

        // when
        List<GenreBookCountDTO> result = genreService.findTopGenreByBooks();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSaveGenreSuccessfully() {
        // given
        when(genreRepository.save(genre)).thenReturn(genre);

        // when
        Genre result = genreService.save(genre);

        // then
        assertNotNull(result);
        assertEquals(GENRE_NAME, result.getGenre());
        verify(genreRepository, times(1)).save(genre);
    }

    @Test
    void shouldReturnLendingsPerMonthForLastYear() {
        // given
        GenreLendingsPerMonthDTO dto1 = mock(GenreLendingsPerMonthDTO.class);
        GenreLendingsPerMonthDTO dto2 = mock(GenreLendingsPerMonthDTO.class);
        List<GenreLendingsPerMonthDTO> dtos = List.of(dto1, dto2);

        when(genreRepository.getLendingsPerMonthLastYearByGenre()).thenReturn(dtos);

        // when
        List<GenreLendingsPerMonthDTO> result = genreService.getLendingsPerMonthLastYearByGenre();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(genreRepository, times(1)).getLendingsPerMonthLastYearByGenre();
    }

    @Test
    void shouldReturnEmptyListWhenNoDataAvailable() {
        // given
        when(genreRepository.getLendingsPerMonthLastYearByGenre()).thenReturn(new ArrayList<>());

        // when
        List<GenreLendingsPerMonthDTO> result = genreService.getLendingsPerMonthLastYearByGenre();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return average lendings for given month")
    void shouldReturnAverageLendingsForGivenMonth() {
        // given
        GetAverageLendingsQuery query = new GetAverageLendingsQuery(2024, 1);
        pt.psoft.g1.psoftg1.shared.services.Page page =
                new pt.psoft.g1.psoftg1.shared.services.Page(1, 10);
        LocalDate month = LocalDate.of(2024, 1, 1);

        GenreLendingsDTO dto = mock(GenreLendingsDTO.class);
        List<GenreLendingsDTO> dtos = List.of(dto);

        when(genreRepository.getAverageLendingsInMonth(month, page)).thenReturn(dtos);

        // when
        List<GenreLendingsDTO> result = genreService.getAverageLendings(query, page);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(genreRepository, times(1)).getAverageLendingsInMonth(month, page);
    }

    @Test
    void shouldUseDefaultPageWhenPageIsNull() {
        // given
        GetAverageLendingsQuery query = new GetAverageLendingsQuery(2024, 1);
        LocalDate month = LocalDate.of(2024, 1, 1);

        when(genreRepository.getAverageLendingsInMonth(any(LocalDate.class),
                any(pt.psoft.g1.psoftg1.shared.services.Page.class))).thenReturn(new ArrayList<>());

        // when
        genreService.getAverageLendings(query, null);

        // then
        verify(genreRepository).getAverageLendingsInMonth(eq(month), argThat(p ->
                p.getNumber() == 1 && p.getLimit() == 10
        ));
    }

    @Test
    @DisplayName("Should return average duration for valid date range")
    void shouldReturnAverageDurationForValidDateRange() {
        // given
        String start = "2024-01-01";
        String end = "2024-12-31";
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        GenreLendingsPerMonthDTO dto = mock(GenreLendingsPerMonthDTO.class);
        List<GenreLendingsPerMonthDTO> dtos = List.of(dto);

        when(genreRepository.getLendingsAverageDurationPerMonth(startDate, endDate))
                .thenReturn(dtos);

        // when
        List<GenreLendingsPerMonthDTO> result =
                genreService.getLendingsAverageDurationPerMonth(start, end);

        // then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(genreRepository, times(1))
                .getLendingsAverageDurationPerMonth(startDate, endDate);
    }

    @Test
    @DisplayName("Should throw exception for malformed date string")
    void shouldThrowExceptionForMalformedDateString() {
        // given
        String start = "not-a-date";
        String end = "2024-12-31";

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> genreService.getLendingsAverageDurationPerMonth(start, end));
    }

    @Test
    void shouldThrowExceptionWhenStartDateAfterEndDate() {
        // given
        String start = "2024-12-31";
        String end = "2024-01-01";

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> genreService.getLendingsAverageDurationPerMonth(start, end));

        assertTrue(exception.getMessage().contains("Start date cannot be after end date"));
        verify(genreRepository, never())
                .getLendingsAverageDurationPerMonth(any(), any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenListIsEmpty() {
        // given
        String start = "2024-01-01";
        String end = "2024-12-31";
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);

        when(genreRepository.getLendingsAverageDurationPerMonth(startDate, endDate))
                .thenReturn(new ArrayList<>());

        // when & then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> genreService.getLendingsAverageDurationPerMonth(start, end));

        assertTrue(exception.getMessage().contains("No objects match"));
    }
}
