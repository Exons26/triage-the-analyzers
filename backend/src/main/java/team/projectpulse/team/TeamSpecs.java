package team.projectpulse.team;

import org.springframework.data.jpa.domain.Specification;

import java.util.Locale;

public final class TeamSpecs {

    private TeamSpecs() {
        // Static Specification factories only; never instantiated.
    }

    public static Specification<Team> containsTeamName(String providedTeamName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("teamName")), "%" + providedTeamName.toLowerCase(Locale.ROOT) + "%");
    }

    public static Specification<Team> hasSectionName(String providedSectionName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("section").get("sectionName")), providedSectionName.toLowerCase(Locale.ROOT));
    }

    public static Specification<Team> belongsToSection(String providedSectionId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("section").get("sectionId"), providedSectionId);
    }

}
