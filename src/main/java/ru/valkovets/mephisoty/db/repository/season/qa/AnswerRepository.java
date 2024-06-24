package ru.valkovets.mephisoty.db.repository.season.qa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.Season;
import ru.valkovets.mephisoty.db.model.season.qa.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

}