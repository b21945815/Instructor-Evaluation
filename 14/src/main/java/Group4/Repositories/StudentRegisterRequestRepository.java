package Group4.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import Group4.Entities.StudentRegisterRequest;

public interface StudentRegisterRequestRepository extends JpaRepository<StudentRegisterRequest, Long> {

}
