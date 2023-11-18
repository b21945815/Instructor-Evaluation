package Group4.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import Group4.Entities.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

}