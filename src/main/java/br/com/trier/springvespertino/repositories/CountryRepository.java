package br.com.trier.springvespertino.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.trier.springvespertino.models.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {

}
