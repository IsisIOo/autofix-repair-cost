package com.example.autofixrepaircost.controller;


import com.example.autofixrepaircost.entity.RepairCost;
import com.example.autofixrepaircost.service.RepairCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repairs")
@CrossOrigin("*")

public class RepairCostController {
    @Autowired
    RepairCostService repairCostService;

    //Obtener todas las reparaciones
    @GetMapping("/")
    //obtiene todas las reparaciones
    public ResponseEntity<List<RepairCost>> listRepair() {
        List<RepairCost> repair = repairCostService.getAllRepair();
        return ResponseEntity.ok(repair);
    }

    //ESTA ES LA IMPORTANTE
    @PostMapping("/{patent}")
    public ResponseEntity<RepairCost> saveRepair(@PathVariable String patent) {
        RepairCost repairNew = repairCostService.saveCostentity(patent);
        return ResponseEntity.ok(repairNew);
    }

    //DE AQUI PA ABAJO NO FUNCIONA
    //obtiene descuento y recargos individualmente
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Boolean> deleteRepairById(@PathVariable Long id) throws Exception {
        var isDeleted = repairCostService.deleteRepair(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all/{patent}")
    public ResponseEntity<List<RepairCost>> listRepairByPatent(@PathVariable String patent) {
        List<RepairCost> repair = repairCostService.getRepairByPatentfinal(patent);
        return ResponseEntity.ok(repair);
    }

}
