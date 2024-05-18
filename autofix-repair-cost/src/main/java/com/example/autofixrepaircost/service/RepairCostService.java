package com.example.autofixrepaircost.service;

import com.example.autofixrepaircost.entity.RepairCost;
import com.example.autofixrepaircost.repository.RepairCostRepository;
import com.example.autofixrepaircost.model.Car;
import com.example.autofixrepaircost.model.Repair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Service
public class RepairCostService {
    @Autowired
    RepairCostRepository repairCostRepository;

    @Autowired
    RestTemplate restTemplate;

    //getter
    public ArrayList<RepairCost> getAllRepair(){
        return (ArrayList<RepairCost>) repairCostRepository.findAll();
    }


    public Car getCar(String patent) {
        Car car = restTemplate.getForObject("http://localhost:8001/car/" + patent, Car.class);
        return car;
    }


    //obtiene un solo repair de acuerdo a la patente
    public Repair getRepair(String patent) {
        Repair repair = restTemplate.getForObject("http://localhost:8002/repair/" + patent, Repair.class);
        return repair;
    }




    //get reparaciones para saber cuanto sale la reparacion segun el tipo de reparacion y el tipo de motor

    public double getCost(String patent) {
        double total_price0 = precioSegunReparacionyMotor(patent);
        double total_price1 = IVATOTAL(total_price0); //le saca el iva al costo original
        double total_price2 = DescuentosSegunHora(patent, total_price1);
        //total_price = DescuentoSegunMarca(patent, total_price);
        //comentada el descuento segun marca porque espero usar essa funcion como un boton
        double total_price3 = RecargoPorKilometraje(patent, total_price1);
        double total_price4 = recargoPorAntiguedad(patent, total_price1);
        double total_price5 = recargoPorAtraso(patent, total_price1);
        double total_price = total_price0 + total_price1 + total_price2 + total_price3 + total_price4 + total_price5;
        return total_price;
    }
    //aun no hago recargo por atraso!!!
    //se agrego por atraso, ahora se va a probar



    //hace lo mismo solo que retorna una entidad
    public RepairCost saveCostentity(String patent) {
        double total_price = precioSegunReparacionyMotor(patent);
        total_price= IVATOTAL(total_price); //le saca el iva al costo original
        total_price = DescuentosSegunHora(patent, total_price);
        //total_price = DescuentoSegunMarca(patent, total_price);
        //comentada el descuento segun marca porque espero usar essa funcion como un boton
        total_price = recargoPorAtraso(patent, total_price);
        total_price = RecargoPorKilometraje(patent, total_price);
        total_price = recargoPorAntiguedad(patent, total_price);
        int tiempo = tiempodeTrabajo(patent);

        //los set para el repair entity, aplica descuento sobre descuento
        //si metia en todos el costo del anterior, podria haber error debido a que puede que un descuento no se aplica,
        //por lo que al final solo le coloco el costo con iva para que haga descuentos de acuerdo a ese
        double totalOriginal = precioSegunReparacionyMotor(patent);
        double iva2 = IVASOLO(totalOriginal);
        double ivaiva = IVATOTAL(totalOriginal);
        double deshora = DescuentosSegunHora1(patent, ivaiva);
        System.out.println("este es le valor de dehora: " + deshora);
        double retraso =  recargoPorAtraso1(patent, ivaiva);
        double kilo = RecargoPorKilometraje1(patent, ivaiva);
        double antiguedad = recargoPorAntiguedad1(patent, ivaiva);


        //nuevo repair entity que se retornara
        RepairCost repairEntity = new RepairCost();
        repairEntity.setPatent(patent);
        repairEntity.setTotalOriginal(totalOriginal);
        repairEntity.setIVA(iva2);
        repairEntity.setDiscountPerDay(deshora);
        repairEntity.setDelayCharge(retraso);
        repairEntity.setMileageCharge(kilo);
        repairEntity.setSeniorityCharge(antiguedad);
        repairEntity.setWorkTime(tiempo);

        //sacado arriba normalmente, los set son para obtener los descuentos aplicados
        repairEntity.setTotalAmount(total_price);

        return repairCostRepository.save(repairEntity);
    }

    public double precioSegunReparacionyMotor(String patent) {
        double total_price = 0;
        String motor = getCar(patent).getMotorType();
        String repairtype = getRepair(patent).getRepairType(); //no existe, debo arreglarlo

        if (motor.toLowerCase().equals("gasolina")) {
            if (repairtype.toLowerCase().contains("reparaciones del sistema de frenos")) {
                total_price = total_price + 120000;
            }
            if (repairtype.toLowerCase().contains("servicio del sistema de refrigeración")) {
                total_price = total_price + 130000;
            }
            if (repairtype.toLowerCase().contains("reparaciones del motor")) {
                total_price = total_price + 350000;

            }
            if (repairtype.toLowerCase().contains("reparaciones de la transmisión")) {
                total_price = total_price + 210000; //4
            }
            if (repairtype.toLowerCase().contains("reparación del sistema eléctrico")) {
                total_price = total_price + 150000;
            }
            if (repairtype.toLowerCase().contains("reparaciones del sistema de escape")) {
                total_price = total_price + 100000;
            }
            if (repairtype.toLowerCase().contains("reparación de neumáticos y ruedas")) {
                total_price = total_price + 100000;
            }
            if (repairtype.toLowerCase().contains("reparaciones de la suspensión y la dirección")) {
                total_price = total_price + 180000;
            }
            if (repairtype.toLowerCase().contains("reparación del sistema de aire acondicionado y calefacción")) {
                total_price = total_price + 150000; //9
            }
            if (repairtype.toLowerCase().contains("reparaciones del sistema de combustible")) {
                total_price = total_price + 130000;
            }
            if (repairtype.toLowerCase().contains("reparación y reemplazo del parabrisas y cristales")) {
                total_price = total_price + 80000;
            }
        }

        if (motor.toLowerCase().equals("diesel")) {
            if (repairtype.toLowerCase().contains("reparaciones del sistema de frenos")) {
                total_price = total_price + 120000;
            }
            if (repairtype.toLowerCase().contains("servicio del sistema de refrigeración")) {
                total_price = total_price + 130000; //2
            }
            if (repairtype.toLowerCase().contains("reparaciones del motor")) {
                total_price = total_price + 450000; //3
            }
            if (repairtype.toLowerCase().contains("reparaciones de la transmisión")) {
                total_price = total_price + 210000; //4
            }
            if (repairtype.toLowerCase().contains("reparación del sistema eléctrico")) {
                total_price = total_price + 150000; //5
            }
            if (repairtype.toLowerCase().contains("reparaciones del sistema de escape")) {
                total_price = total_price + 120000; //6
            }
            if (repairtype.toLowerCase().contains("reparación de neumáticos y ruedas")) {
                total_price = total_price + 100000; //7
            }
            if (repairtype.toLowerCase().contains("reparaciones de la suspensión y la dirección")) {
                total_price = total_price + 180000; //8
            }
            if (repairtype.toLowerCase().contains("reparación del sistema de aire acondicionado y calefacción")) {
                total_price = total_price + 150000; //9
            }
            if (repairtype.toLowerCase().contains("reparaciones del sistema de combustible")) {
                total_price = total_price + 140000; //10
            }
            if (repairtype.toLowerCase().contains("reparación y reemplazo del parabrisas y cristales")) {
                total_price = total_price + 80000;
            }
        }

        if (motor.toLowerCase().equals("híbrido")) {
            if (repairtype.toLowerCase().contains("reparaciones del sistema de frenos")) {
                total_price = total_price + 180000;
            }
            if (repairtype.toLowerCase().contains("servicio del sistema de refrigeración")) {
                total_price = total_price + 190000;
            }
            if (repairtype.toLowerCase().contains("reparaciones del motor")) {
                total_price = total_price + 700000;
            }
            if (repairtype.toLowerCase().contains("reparaciones de la transmisión")) {
                total_price = total_price + 300000; //4
            }
            if (repairtype.toLowerCase().contains("reparación del sistema eléctrico")) {
                total_price = total_price + 200000;
            }
            if (repairtype.toLowerCase().contains("reparaciones del sistema de escape")) {
                total_price = total_price + 450000;
            }
            if (repairtype.toLowerCase().contains("reparación de neumáticos y ruedas")) {
                total_price = total_price + 100000;
            }
            if (repairtype.toLowerCase().contains("reparaciones de la suspensión y la dirección")) {
                total_price = total_price + 210000;
            }
            if (repairtype.toLowerCase().contains("reparación del sistema de aire acondicionado y calefacción")) {
                total_price = total_price + 180000; //9
            }
            if (repairtype.toLowerCase().contains("reparaciones del sistema de combustible")) {
                total_price = total_price + 220000;
            }
            if (repairtype.toLowerCase().contains("reparación y reemplazo del parabrisas y cristales")) {
                total_price = total_price + 80000;
            }
        }

        if (motor.toLowerCase().equals("eléctrico")) {
            if (repairtype.toLowerCase().contains("reparaciones del sistema de frenos")) {
                total_price = total_price + 220000;
            }
            if (repairtype.toLowerCase().contains("servicio del sistema de refrigeración")) {
                total_price = total_price + 230000;
            }
            if (repairtype.toLowerCase().contains("reparaciones del motor")) {
                total_price = total_price + 800000;
            }
            if (repairtype.toLowerCase().contains("reparaciones de la transmisión")) {
                total_price = total_price + 300000; //4
            }
            if (repairtype.toLowerCase().contains("reparación del sistema eléctrico")) {
                total_price = total_price + 250000;
            }
            if (repairtype.toLowerCase().contains("reparaciones del sistema de escape")) {
                total_price = total_price + 0;
            }
            if (repairtype.toLowerCase().contains("reparación de neumáticos y ruedas")) {
                total_price = total_price + 100000;
            }
            if (repairtype.toLowerCase().contains("reparaciones de la suspensión y la dirección")) {
                total_price = total_price + 250000;
            }
            if (repairtype.toLowerCase().contains("reparación del sistema de aire acondicionado y calefacción")) {
                total_price = total_price + 180000; //9
            }
            if (repairtype.toLowerCase().contains("reparaciones del sistema de combustible")) {
                total_price = total_price + 0;
            }
            if (repairtype.toLowerCase().contains("reparación y reemplazo del parabrisas y cristales")) {
                total_price = total_price + 80000;
            }
        }

        else{
            total_price= total_price;
        }
        return total_price;
    }


    //a este se le agrega segun hora y dia
    public double DescuentosSegunHora(String patent, double total_price) {
        // ahora veo si aplica el descuento segun la hora de ingreso
        double total_price_hour=0;
        int hour = getRepair(patent).getAdmissionHour();//hora para determinar si se le aplica descuento por hora de llegada
        String day = getRepair(patent).getAdmissionDateDayName().toLowerCase();//dia para determinar si se le aplica descuento por dia de llegada
        if (9 < hour && hour < 12 ) {//agregar que se entre lunes y jueves
           if(day.toLowerCase().equals("jueves")  || day.toLowerCase().equals("lunes")) {
               total_price_hour = total_price * 0.1;
               total_price = total_price - total_price_hour;
               System.out.println("El descuento aplicado por la hora: " + total_price_hour);
           }
        }
        else {
            System.out.println("el descuento total es aplicado "+ total_price+ "y el descuento" +total_price_hour);
            total_price = total_price;
        }
        System.out.println("Precio total de la reparación con descuento por hora: " + total_price);
        return total_price;
    }


    //descuento segun marca, aun tengo dudas de este y correo blabla
    public double DescuentoSegunMarca(String patent, double total_price) {
        //descuento segun marca
        int toyota_des = 5;
        int ford_des = 2;
        int hyundai_des = 1;
        int honda_des= 7;
        String brand = getCar(patent).getBrand();
        if (brand.toLowerCase().equals("toyota") && toyota_des>0) {
            total_price = total_price - 70000;
            toyota_des = toyota_des-1;
            System.out.println("El descuento aplicado por la marca Toyota: " + total_price);
        }
        if (brand.toLowerCase().equals("ford") && ford_des >0) {
            total_price = total_price - 50000;
            ford_des = ford_des-1;
            System.out.println("El descuento aplicado por la marca Ford: " + total_price);
        }
        if (brand.toLowerCase().equals("hyundai") && hyundai_des >0) {
            total_price = total_price - 30000;
            hyundai_des = hyundai_des -1;
            System.out.println("El descuento aplicado por la marca Hyundai: " + total_price);
        }
        if (brand.toLowerCase().equals("honda") && honda_des>0) {
            total_price = total_price - 40000;
            honda_des = honda_des-1;
            System.out.println("El descuento aplicado por la marca Honda: " + total_price);
        }

        else {
            //no se aplica descuento
            total_price = total_price;
            System.out.println("No se aplicó descuento por marca");
        }
        System.out.println("Precio total de la reparación con descuento por marca: " + total_price);
        return total_price;
    }



    public double RecargoPorKilometraje(String patent, double total_price) {
        //recargo por kilometraje
        String type1 = getCar(patent).getType();
        int km = getCar(patent).getKilometers();
        if (type1.toLowerCase().equals("sedan")) {
            if (km <= 5000) {
                total_price = total_price;
                }
            if (5001 < km && km <= 12000) {
                double total_price_km = total_price * 0.03;
                total_price = total_price + total_price_km;
                }
            if (12001 < km && km <= 25000) {
                double total_price_km = total_price * 0.07;
                total_price = total_price + total_price_km;
                }
            if (25001 < km && km <= 40000) {
                double total_price_km = total_price * 0.12;
                total_price = total_price + total_price_km;
                }
            if (40000 < km) {
                double total_price_km = total_price * 0.2;
                total_price = total_price + total_price_km;
                }
        }

        if (type1.toLowerCase().equals("hatchback")) {
            if (km < 5000) {
                total_price = total_price;
            }
            if (5001 < km && km < 12000) {
                double total_price_km = total_price * 0.03;
                total_price = total_price + total_price_km;
            }
            if (12001 < km && km < 25000) {
                double total_price_km = total_price * 0.07;
                total_price = total_price + total_price_km;
                 }
            if (25001 < km && km < 40000) {
                double total_price_km = total_price * 0.12;
                total_price = total_price + total_price_km;
                }
            if (40000 < km) {
                double total_price_km = total_price * 0.2;
                total_price = total_price + total_price_km;
                }
        }

        if (type1.toLowerCase().equals("suv")) {
            if (km < 5000) {
                total_price = total_price;
                }
            if (5001 < km && km < 12000) {
                double total_price_km = total_price * 0.05;
                total_price = total_price + total_price_km;
                }
            if (12001 < km && km < 25000) {
                double total_price_km = total_price * 0.09;
                total_price = total_price + total_price_km;
                }
            if (25001 < km && km < 40000) {
                double total_price_km = total_price * 0.12;
                total_price = total_price + total_price_km;
                 }
            if (40000 < km) {
                double total_price_km = total_price * 0.2;
                total_price = total_price + total_price_km;
                }
        }

        if (type1.toLowerCase().equals("pickup")) {
            if (km < 5000) {
                total_price = total_price;
                }
            if (5001 < km && km < 12000) {
                double total_price_km = total_price * 0.05;
                total_price = total_price + total_price_km;
                }
            if (12001 < km && km < 25000) {
                double total_price_km = total_price * 0.09;
                total_price = total_price + total_price_km;
                 }
            if (25001 < km && km < 40000) {
                double total_price_km = total_price * 0.12;
                total_price = total_price + total_price_km;
                }
            if (40000 < km) {
                double total_price_km = total_price * 0.2;
                total_price = total_price + total_price_km;
                 }
        }

        if (type1.toLowerCase().equals("furgoneta")) {
            if (km < 5000) {
                total_price = total_price;
                }
            if (5001 < km && km < 12000) {
                double total_price_km = total_price * 0.05;
                total_price = total_price + total_price_km;
            }
            if (12001 < km && km < 25000) {
                double total_price_km = total_price * 0.09;
                total_price = total_price + total_price_km;
                }
            if (25001 < km && km < 40000) {
                double total_price_km = total_price * 0.12;
                total_price = total_price + total_price_km;
                }
            if (40000 < km) {
                double total_price_km = total_price * 0.2;
                total_price = total_price + total_price_km;
                }
        }
        else{
            total_price= total_price;
        }
        return total_price;
    }


    public double recargoPorAntiguedad(String patent, double total_price) {
        //recargo por antiguedad
        double total_price_year =0;
        int year_car = getCar(patent).getProductionYear();
        String type1 = getCar(patent).getType();
        if (type1.toLowerCase().equals("sedan")) {
            if ((2024 - year_car) <= 5) {
                total_price = total_price;
                 }

            if ((2024 - year_car) >= 6 && (2024 - year_car) <= 10) {
                total_price_year = total_price * 0.05;
                total_price = total_price + total_price_year;
                }

            if ((2024 - year_car) >= 11 && (2024 - year_car) <= 15) {
                total_price_year = total_price * 0.09;
                total_price = total_price + total_price_year;
                }

            if ((2024 - year_car) >= 16) {
                total_price_year = total_price * 0.15;
                total_price = total_price + total_price_year;
                }
        }

        if (type1.toLowerCase().equals("hatchback")) {
            if ((2024 - year_car) <= 5) {
                total_price = total_price;
                }

            if ((2024 - year_car) >= 6 && (2024 - year_car) <= 10) {
                total_price_year = total_price * 0.05;
                total_price = total_price + total_price_year;
                }

            if ((2024 - year_car) >= 11 && (2024 - year_car) <= 15) {
                total_price_year = total_price * 0.09;
                total_price = total_price + total_price_year;
                }

            if ((2024 - year_car) >= 16) {
                total_price_year = total_price * 0.15;
                total_price = total_price + total_price_year;
                }
        }

        if (type1.toLowerCase().equals("suv")) {
            if ((2024 - year_car) <= 5) {
                total_price = total_price;
                }

            if ((2024 - year_car) >= 6 && (2024 - year_car) <= 10) {
                total_price_year = total_price * 0.07;
                total_price = total_price + total_price_year;
                }

            if ((2024 - year_car) >= 11 && (2024 - year_car) <= 15) {
                total_price_year = total_price * 0.11;
                total_price = total_price + total_price_year;
                }

            if ((2024 - year_car) >= 16) {
                total_price_year = total_price * 0.2;
                total_price = total_price + total_price_year;
                }
        }

        if (type1.toLowerCase().equals("pickup") ) {
            if ((2024 - year_car) <= 5) {
                total_price = total_price;
                 }

            if ((2024 - year_car) >= 6 && (2024 - year_car) <= 10) {
                total_price_year = total_price * 0.07;
                total_price = total_price + total_price_year;
                }

            if ((2024 - year_car) >= 11 && (2024 - year_car) <= 15) {
                total_price_year = total_price * 0.11;
                total_price = total_price + total_price_year;
                }

            if ((2024 - year_car) >= 16) {
                total_price_year = total_price * 0.2;
                total_price = total_price + total_price_year;
                System.out.println("El recargo aplicado a Pickup por antiguedad sobre 16 años: " + total_price_year);
            }
        }

        if (type1.toLowerCase().equals("furgoneta")) {
            if ((2024 - year_car) <= 5) {
                total_price = total_price;
                }

            if ((2024 - year_car) >= 6 && (2024 - year_car) <= 10) {
                total_price_year = total_price * 0.07;
                total_price = total_price + total_price_year;
                }

            if ((2024 - year_car) >= 11 && (2024 - year_car) <= 15) {
                total_price_year = total_price * 0.11;
                total_price = total_price + total_price_year;
                }

            if ((2024 - year_car) >= 16) {
                total_price_year = total_price * 0.2;
                total_price = total_price + total_price_year;
                }
        }
        else {
            total_price = total_price;
        }
        System.out.println("valor del cargo aplicado: " +total_price_year+"valor total:" + total_price);
        return total_price;
    }

    //total price siendo el valor original de las reparaciones aplicadas
    public double IVATOTAL(double total_price){
        double iva = total_price * 0.19;
        total_price = total_price + iva;
        System.out.println("El IVA aplicado a la reparación: " + iva);
        System.out.println("Precio total de la reparación con IVA: " + total_price);
        return total_price;
    }


    //saca solo el iva para hacerle set
    public double IVASOLO(double total_price){
        double iva = total_price * 0.19;
        return iva;
    }

    //encontrar segun tipo de reparacion
    public RepairCost getRepairByPatent(String patent){
        return repairCostRepository.findByPatentrepair(patent);
    }


    //funciones para sacar el descuento
    public double recargoPorAntiguedad1(String patent, double total_price) {
        //recargo por antiguedad
        int year_car = getCar(patent).getProductionYear();
        double total_price_year = 0;
        String type1 = getCar(patent).getType();
        if (type1.toLowerCase().equals("sedan")) {
            if ((2024 - year_car) <= 5) {
                System.out.println("No se aplicó recargo por antiguedad bajo 5 años");
            }

            if ((2024 - year_car) >= 6 && (2024 - year_car) <= 10) {
                total_price_year = total_price * 0.05;
            }

            if ((2024 - year_car) >= 11 && (2024 - year_car) <= 15) {
                total_price_year = total_price * 0.09;
            }

            if ((2024 - year_car) >= 16) {
                total_price_year = total_price * 0.15;
            }
        }

        if (type1.toLowerCase().equals("hatchback")) {
            if ((2024 - year_car) <= 5) {
                System.out.println("No se aplicó recargo por antiguedad bajo 5 años");
            }

            if ((2024 - year_car) >= 6 && (2024 - year_car) <= 10) {
                total_price_year = total_price * 0.05;
            }

            if ((2024 - year_car) >= 11 && (2024 - year_car) <= 15) {
                total_price_year = total_price * 0.09;
            }

            if ((2024 - year_car) >= 16) {
                total_price_year = total_price * 0.15;
            }
        }

        if (type1.toLowerCase().equals("suv")) {
            if ((2024 - year_car) <= 5) {
                System.out.println("No se aplicó recargo por antiguedad bajo 5 años");
            }

            if ((2024 - year_car) >= 6 && (2024 - year_car) <= 10) {
                total_price_year = total_price * 0.07;
            }

            if ((2024 - year_car) >= 11 && (2024 - year_car) <= 15) {
                total_price_year = total_price * 0.11;
            }

            if ((2024 - year_car) >= 16) {
                total_price_year = total_price * 0.2;
            }
        }

        if (type1.toLowerCase().equals("pickup")) {
            if ((2024 - year_car) <= 5) {
                System.out.println("No se aplicó recargo por antiguedad bajo 5 años");
            }

            if ((2024 - year_car) >= 6 && (2024 - year_car) <= 10) {
                total_price_year = total_price * 0.07;
            }

            if ((2024 - year_car) >= 11 && (2024 - year_car) <= 15) {
                total_price_year = total_price * 0.11;
            }

            if ((2024 - year_car) >= 16) {
                total_price_year = total_price * 0.2;
            }
        }

        if (type1.toLowerCase().equals("furgoneta")) {
            if ((2024 - year_car) <= 5) {
                System.out.println("No se aplicó recargo por antiguedad bajo 5 años");
            }

            if ((2024 - year_car) >= 6 && (2024 - year_car) <= 10) {
                total_price_year = total_price * 0.07;
            }

            if ((2024 - year_car) >= 11 && (2024 - year_car) <= 15) {
                total_price_year = total_price * 0.11;
            }

            if ((2024 - year_car) >= 16) {
                total_price_year = total_price * 0.2;
            }
        }
        System.out.println("el recargo por antiguedad es:" + total_price_year);
        return total_price_year;
    }




    public double RecargoPorKilometraje1(String patent, double total_price) {
        //recargo por kilometraje
        double total_price_km=0;
        String type1 = getCar(patent).getType();
        int km = getCar(patent).getKilometers();
        if (type1.toLowerCase().equals("sedan")) {
            if (km <= 5000) {
                System.out.println("No se aplicó recargo por kilometraje bajo 5000");
            }
            if (5001 < km && km <= 12000) {
                 total_price_km = total_price * 0.03;
            }
            if (12001 < km && km <= 25000) {
                 total_price_km = total_price * 0.07;
            }
            if (25001 < km && km <= 40000) {
                 total_price_km = total_price * 0.12;
            }
            if (40000 < km) {
                 total_price_km = total_price * 0.2;
            }
        }

        if (type1.toLowerCase().equals("hatchback")) {
            if (km < 5000) {
                System.out.println("No se aplicó recargo por kilometraje bajo 5000 11111");
            }
            if (5001 < km && km < 12000) {
                 total_price_km = total_price * 0.03;
            }
            if (12001 < km && km < 25000) {
                 total_price_km = total_price * 0.07;
            }
            if (25001 < km && km < 40000) {
                 total_price_km = total_price * 0.12;
            }
            if (40000 < km) {
                 total_price_km = total_price * 0.2;
            }
        }
        if (type1.toLowerCase().equals("suv")) {
            if (km < 5000) {
                System.out.println("No se aplicó recargo por kilometraje bajo 5000 11111");
            }
            if (5001 < km && km < 12000) {
                 total_price_km = total_price * 0.05;
            }
            if (12001 < km && km < 25000) {
                 total_price_km = total_price * 0.09;
            }
            if (25001 < km && km < 40000) {
                 total_price_km = total_price * 0.12;
            }
            if (40000 < km) {
                 total_price_km = total_price * 0.2;
            }
        }
        if (type1.toLowerCase().equals("pickup")) {
            if (km < 5000) {
                System.out.println("No se aplicó recargo por kilometraje bajo 5000");
            }
            if (5001 < km && km < 12000) {
                 total_price_km = total_price * 0.05;
            }

            if (12001 < km && km < 25000) {
                 total_price_km = total_price * 0.09;
            }
            if (25001 < km && km < 40000) {
                 total_price_km = total_price * 0.12;
            }
            if (40000 < km) {
                 total_price_km = total_price * 0.2;
            }
        }
        if (type1.toLowerCase().equals("furgoneta")) {
            if (km < 5000) {
                System.out.println("No se aplicó recargo por kilometraje bajo 5000");
            }
            if (5001 < km && km < 12000) {
                 total_price_km = total_price * 0.05;
            }
            if (12001 < km && km < 25000) {
                 total_price_km = total_price * 0.09;
            }
            if (25001 < km && km < 40000) {
                 total_price_km = total_price * 0.12;
            }
            if (40000 < km) {
                 total_price_km = total_price * 0.2;
            }
        }
        System.out.println("recargo km eeeesss :" + total_price_km);
        return total_price_km;
    }

    //estas funciones eran para obtener el descuento solo
    public double DescuentosSegunHora1(String patent, double total_price) {
        // ahora veo si aplica el descuento segun la hora de ingreso
        //agregar dia
        double total_price_hour = 0;
        int hour = getRepair(patent).getAdmissionHour();//hora para determinar si se le aplica descuento por hora de llegada
        String day = getRepair(patent).getAdmissionDateDayName().toLowerCase();//dia para determinar si se le aplica descuento por dia de llegada
        if (9 < hour && hour < 12 ) {//agregar que se entre lunes y jueves
            if(day.equals("jueves")  ||  day.equals("lunes")) {
                total_price_hour = total_price * 0.1;
                System.out.println("El descuento aplicado por la hora: " + total_price_hour);
                return total_price_hour;
            }
        }
        return total_price_hour;
    }

    //borra repair por id
    public boolean deleteRepair(Long id) throws Exception {
        try{
            repairCostRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

//obtiene array list de las reparaciones de una sola patente
    public ArrayList<RepairCost> getRepairByPatentfinal(@PathVariable String patent){
        return (ArrayList<RepairCost>) repairCostRepository.findByPatentrepairfinal(patent);
    }


    //este es para obtener el monto de retraso
    public double recargoPorAtraso1(String patent, double total_price) {
        double recargo_total =0;
        //admision, pero creo que no es necesario
        int hora_admision = getRepair(patent).getAdmissionHour();
        int dia_admision = getRepair(patent).getClientDateDay();
        int mes_admision = getRepair(patent).getClientDateMonth();

        //fechas de retiro indicadas por el taller
        int dia_retiro_taller = getRepair(patent).getDepartureDateDay();
        int mes_retiro_taller =getRepair(patent).getDepartureDateMonth();
        int hora_retiro_taller = getRepair(patent).getDepartureHour();

        //fecha retirada por el cliente
        int hora_retiro_cliente = getRepair(patent).getClientHour();
        int dia_retiro_cliente =getRepair(patent).getClientDateDay();
        int mes_retiro_cliente = getRepair(patent).getClientDateMonth();

        //si el retiro del cliente es mayor al retiro del taller es pq está atrasado por dias
        if ((dia_retiro_cliente - dia_retiro_taller) > 0 && mes_retiro_cliente == mes_retiro_taller) {
            int retraso = dia_retiro_cliente - dia_retiro_taller;

            //corresponde a la cantidad de dias de retraso por 5% de cada dia que se demoro
            recargo_total = retraso * 0.05 * total_price;
            System.out.println("el cliente esta retrasado por " + retraso + "DIAS y su recargo es:" + recargo_total );
            return recargo_total;

        }
        //atrasado por meses y dias
        if ((dia_retiro_cliente - dia_retiro_taller) > 0 && (mes_retiro_cliente - mes_retiro_taller) > 0) {
            int retraso = dia_retiro_cliente - dia_retiro_taller;
            int retraso_meses = mes_retiro_cliente - mes_retiro_taller;
            System.out.println("el cliente esta retrasado por " + retraso + "dias y " + retraso_meses + "meses");
            //voy a considerar que los meses solo tienen 30 dias

            //retraso de dias simples
            double recargo_dias = retraso * 0.05 * total_price;
            //por ejemplo, si son 3 meses de diferencia, para sacar los dias seria 3 *30 =90 dias y cada dia tiene 0.05 de recargo
            double recargo_meses = retraso_meses * 30 * 0.05 * total_price;

            recargo_total = recargo_dias + retraso_meses;
            return recargo_total;
        }
        return recargo_total;
    }

    public int tiempodeTrabajo(String patent) {
        int dias_demora =0;
        //necesito el dia que llega y luego el dia que se solicita retirar, pues seria lo que se demora en arreglar la cosa
        int hora_admision = getRepair(patent).getAdmissionHour();
        int dia_admision = getRepair(patent).getAdmissionDateDay();
        int mes_admision = getRepair(patent).getAdmissionDateMonth();

        //fechas de retiro indicadas por el taller
        int dia_retiro_taller = getRepair(patent).getDepartureDateDay();
        int mes_retiro_taller = getRepair(patent).getDepartureDateMonth();
        int hora_retiro_taller = getRepair(patent).getDepartureHour();


        //si el retiro del cliente es mayor al retiro del taller es pq está atrasado
        if ((dia_retiro_taller - dia_admision) > 0 && mes_admision == mes_retiro_taller) {
            dias_demora = dia_retiro_taller - dia_admision;
            System.out.println("el auto se demoro en arreglarse " + dias_demora + "dias");
            //corresponde a la cantidad de dias de retraso por 5% de cada dia que se demoro
        }
        if ((dia_retiro_taller-dia_admision) > 0 && (mes_retiro_taller-mes_admision) > 0) {
            int reparo = dia_retiro_taller-dia_admision;
            int reparo_meses = mes_retiro_taller-mes_admision;
            System.out.println("el auto se demoro por " + reparo + "dias y " + reparo_meses + "meses");
            //voy a considerar que los meses solo tienen 30 dias
            dias_demora = reparo + 30*reparo_meses;
            //mas dias = mas se demora
        }
        System.out.println("los dias de retraso son:" + dias_demora);
        return dias_demora;
    }

    public double recargoPorAtraso(String patent, double total_price) {
        //admision, pero creo que no es necesario
        int hora_admision = getRepair(patent).getAdmissionHour();
        int dia_admision = getRepair(patent).getClientDateDay();
        int mes_admision = getRepair(patent).getClientDateMonth();

        //fechas de retiro indicadas por el taller
        int dia_retiro_taller = getRepair(patent).getDepartureDateDay();
        int mes_retiro_taller = getRepair(patent).getDepartureDateMonth();
        int hora_retiro_taller = getRepair(patent).getDepartureHour();

        //fecha retirada por el cliente
        int hora_retiro_cliente = getRepair(patent).getClientHour();
        int dia_retiro_cliente = getRepair(patent).getClientDateDay();
        int mes_retiro_cliente = getRepair(patent).getClientDateMonth();

        //si el retiro del cliente es mayor al retiro del taller es pq está atrasado
        if ((dia_retiro_cliente - dia_retiro_taller) > 0 && mes_retiro_cliente == mes_retiro_taller) {
            int retraso = dia_retiro_cliente - dia_retiro_taller;
            System.out.println("el cliente esta retrasado por " + retraso + "DIAS");
            //corresponde a la cantidad de dias de retraso por 5% de cada dia que se demoro
            double recargo_dias = retraso * 0.05 * total_price;
            total_price = total_price + recargo_dias;

        }
        if ((dia_retiro_cliente - dia_retiro_taller) > 0 && (mes_retiro_cliente - mes_retiro_taller) > 0) {
            int retraso = dia_retiro_cliente - dia_retiro_taller;
            int retraso_meses = mes_retiro_cliente - mes_retiro_taller;
            System.out.println("el cliente esta retrasado por " + retraso + "dias y " + retraso_meses + "meses");
            //voy a considerar que los meses solo tienen 30 dias

            //retraso de dias simples
            double recargo_dias = retraso * 0.05 * total_price;
            //por ejemplo, si son 3 meses de diferencia, para sacar los dias seria 3 *30 =90 dias y cada dia tiene 0.05 de recargo
            double recargo_meses = retraso_meses * 30 * 0.05 * total_price;

            System.out.println("cargo:"+recargo_meses);

            double recargo_total = recargo_dias + retraso_meses;
            total_price = total_price + recargo_total;
        }
        return total_price;
    }
}

