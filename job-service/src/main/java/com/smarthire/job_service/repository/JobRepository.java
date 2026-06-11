package com.smarthire.job_service.repository;

import com.smarthire.job_service.model.job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<job,Integer>
{
    @Query("Select p from job p where LOWER(p.title) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
            "LOWER(p.company) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
    "LOWER(p.skills) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " + "LOWER(p.description) LIKE LOWER(CONCAT('%',:keyword,'%'))")
    public List<job> searchjobs(@Param("keyword") String keyword);

    List<job> findByLocationIgnoreCase(String location);
    List<job> findByCompanyIgnoreCase(String company);
    List<job> findBySalaryBetween(double minSalary,double maxSalary);
    List<job> findByIsActiveTrue();
    List<job> findByJobTypeIgnoreCase(String jobType);
    List<job> findByExperienceIgnoreCase(String experience);


}
