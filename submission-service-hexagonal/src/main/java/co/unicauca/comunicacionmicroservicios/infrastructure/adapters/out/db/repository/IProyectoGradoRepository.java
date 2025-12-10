/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.comunicacionmicroservicios.infrastructure.adapters.out.db.repository;
/**
 *
 * @author USUARIO
 */
import co.unicauca.comunicacionmicroservicios.domain.model.ProyectoGrado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProyectoGradoRepository extends JpaRepository<ProyectoGrado, Integer> {
    //long countByDirector_Id(Integer directorId);
 }
