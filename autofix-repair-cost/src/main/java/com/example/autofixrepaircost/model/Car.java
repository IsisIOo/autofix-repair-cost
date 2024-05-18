package com.example.autofixrepaircost.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Car {
    private Long id; //no lo asigno

    private String patent; //patente de letras y numeros

    private String brand; //marca del vehiculo

    private String model; //modelo del vehiculo suzuki swift -> swift modelo

    private String type; //tipo de vehiculo, Sedan, Hatchback, SUV, Pickup, Furgoneta

    private int productionYear; //año de fabricacion

    private String motorType; //tipo de motor gasolina, diesel, hibrido y electrico

    private int numberSeats; //numero de asientos

    private int kilometers; //kilometraje del vehiculo
}
