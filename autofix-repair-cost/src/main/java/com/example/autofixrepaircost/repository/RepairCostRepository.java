package com.example.autofixrepaircost.repository;

import com.example.autofixrepaircost.entity.RepairCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairCostRepository extends JpaRepository<RepairCost, Long>  {

    //busca segun patente en repair type
    @Query(value = "SELECT * FROM cost WHERE cost.patent = :patent", nativeQuery = true)
    RepairCost findByPatentrepair(@Param("patent") String patent);

    //lista de repairs
    @Query(value = "SELECT * FROM cost WHERE cost.patent = :patent", nativeQuery = true)
    List<RepairCost> findByPatentrepairfinal(@Param("patent") String patent);


}
