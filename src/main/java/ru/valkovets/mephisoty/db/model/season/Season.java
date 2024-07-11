package ru.valkovets.mephisoty.db.model.season;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import ru.valkovets.mephisoty.api.dto.season.SeasonDto;
import ru.valkovets.mephisoty.db.model.season.scoring.SeasonScore;
import ru.valkovets.mephisoty.db.model.superclass.TdrseEntity;
import ru.valkovets.mephisoty.settings.AllowState;
import ru.valkovets.mephisoty.settings.ValidationConst;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "season")
@NamedEntityGraph(name = "season_full", attributeNodes = {
        @NamedAttributeNode("createdAt"),
        @NamedAttributeNode("modifiedAt"),
        @NamedAttributeNode("createdBy"),
        @NamedAttributeNode("modifiedBy"),
        @NamedAttributeNode("stages")
})
public class Season extends TdrseEntity {

@JsonManagedReference
@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "season", orphanRemoval = true)
private Set<Stage> stages = new LinkedHashSet<>();

@Length(max = 1000)
@Column(name = "formula", length = 1000)
@Pattern(regexp = ValidationConst.FORMULA_PATTERN)
private String seasonResultFormula; // math + Season stages

@NotNull
@Builder.Default
//@Enumerated(EnumType.STRING)
@Column(name = "season_visibility", nullable = false)
private String seasonVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name();
@NotNull
@Builder.Default
//@Enumerated(EnumType.STRING)
@Column(name = "score_visibility", nullable = false)
private String scoreVisibility = AllowState.DISALLOW_ALL_FOR_PARTICIPANTS.name();

public static Season createFrom(final SeasonDto seasonDto) {
    return Season.builder()
                 .comment(seasonDto.comment())
                 .title(seasonDto.title())
                 .description(seasonDto.description())
                 .rules(seasonDto.rules())
                 .start(seasonDto.start())
                 .end(seasonDto.end())
                 .seasonResultFormula(seasonDto.seasonResultFormula())
                 .seasonVisibility(seasonDto.seasonVisibility().name())
                 .scoreVisibility(seasonDto.scoreVisibility().name())
                 .build();
}

@Transient
public AllowState getSeasonVisibilityEnum() {
    return AllowState.valueOf(seasonVisibility);
}

@NotNull
@Builder.Default
@OneToMany(fetch = FetchType.LAZY, mappedBy = "season", orphanRemoval = true)
private Set<SeasonScore> seasonScores = new LinkedHashSet<>();

@Transient
public AllowState getScoreVisibilityEnum() {
    return AllowState.valueOf(scoreVisibility);
}

public Season editFrom(final SeasonDto seasonDto) {
    setComment(seasonDto.comment());
    setTitle(seasonDto.title());
    setDescription(seasonDto.description());
    setRules(seasonDto.rules());
    setStart(seasonDto.start());
    setEnd(seasonDto.end());
    setSeasonResultFormula(seasonDto.seasonResultFormula());
    setSeasonVisibility(seasonDto.seasonVisibility().name());
    setScoreVisibility(seasonDto.scoreVisibility().name());
    return this;
}
}