package ru.valkovets.mephisoty.db.repository.season.qa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.valkovets.mephisoty.db.model.season.qa.Answer;
import ru.valkovets.mephisoty.db.model.season.qa.Question;
import ru.valkovets.mephisoty.db.repository.BasicRepository;

@Repository
public interface QuestionRepository extends BasicRepository<Question> {

}