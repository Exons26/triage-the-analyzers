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
 * TeamSpecs lowercases the search term in Java and the column in the database, then compares the two.
 * Both sides must lowercase the same way. String.toLowerCase() without a Locale uses the JVM's default
 * locale, and in Turkish "I" lowercases to the dotless "ı", so a search for "INTEGRATION" would look
 * for "%ıntegratıon%" and never match a team the database lowercases to "integration".
 */
class TeamSpecsTest {

    private Locale originalLocale;
    private Root<Team> root;
    private CriteriaQuery<?> query;
    private CriteriaBuilder criteriaBuilder;
    private Expression<String> loweredColumn;

    @BeforeEach
    @SuppressWarnings("unchecked")
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
    void restoreLocale() {
        Locale.setDefault(this.originalLocale);
    }

    @Test
    void containsTeamNameLowercasesTheSearchTermTheSameWayUnderATurkishDefaultLocale() {
        Path<?> teamName = mock(Path.class);
        doReturn(teamName).when(this.root).get("teamName");

        TeamSpecs.containsTeamName("INTEGRATION").toPredicate(this.root, this.query, this.criteriaBuilder);

        verify(this.criteriaBuilder).like(this.loweredColumn, "%integration%");
    }

    @Test
    void hasSectionNameLowercasesTheSearchTermTheSameWayUnderATurkishDefaultLocale() {
        Path<?> section = mock(Path.class);
        Path<?> sectionName = mock(Path.class);
        doReturn(section).when(this.root).get("section");
        doReturn(sectionName).when(section).get("sectionName");

        TeamSpecs.hasSectionName("SECTION I").toPredicate(this.root, this.query, this.criteriaBuilder);

        verify(this.criteriaBuilder).equal(this.loweredColumn, "section i");
    }
}
