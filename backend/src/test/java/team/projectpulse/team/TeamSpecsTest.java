package team.projectpulse.team;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * TeamSpecs lowercases the search term in Java and the column in the database (criteriaBuilder.lower), so the
 * two must agree. String.toLowerCase() with no Locale uses the JVM's default locale: under Turkish, "I" lowercases
 * to dotless "ı", and a search for "INTEGRATION" never matches a team the database lowercases to "integration".
 * These tests run under a Turkish default locale and check that the term reaches the query lowercased the
 * locale-neutral way.
 */
@SuppressWarnings("unchecked")
class TeamSpecsTest {

    private Locale originalLocale;

    private Root<Team> root;
    private CriteriaQuery<?> query;
    private CriteriaBuilder criteriaBuilder;
    private Expression<String> loweredColumn;

    @BeforeEach
    void setUp() {
        this.originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));

        this.root = mock(Root.class);
        this.query = mock(CriteriaQuery.class);
        this.criteriaBuilder = mock(CriteriaBuilder.class);
        this.loweredColumn = mock(Expression.class);
        doReturn(this.loweredColumn).when(this.criteriaBuilder).lower(any());
    }

    @AfterEach
    void tearDown() {
        Locale.setDefault(this.originalLocale);
    }

    @Test
    void containsTeamNameLowercasesTheSearchTermIndependentlyOfTheDefaultLocale() {
        Path<Object> teamName = mock(Path.class);
        doReturn(teamName).when(this.root).get("teamName");

        TeamSpecs.containsTeamName("INTEGRATION").toPredicate(this.root, this.query, this.criteriaBuilder);

        verify(this.criteriaBuilder).like(this.loweredColumn, "%integration%");
    }

    @Test
    void hasSectionNameLowercasesTheSectionNameIndependentlyOfTheDefaultLocale() {
        Path<Object> section = mock(Path.class);
        Path<Object> sectionName = mock(Path.class);
        doReturn(section).when(this.root).get("section");
        doReturn(sectionName).when(section).get("sectionName");

        TeamSpecs.hasSectionName("SECTION I").toPredicate(this.root, this.query, this.criteriaBuilder);

        verify(this.criteriaBuilder).equal(this.loweredColumn, "section i");
    }

}
