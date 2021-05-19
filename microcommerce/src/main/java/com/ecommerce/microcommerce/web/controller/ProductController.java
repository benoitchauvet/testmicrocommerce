package com.ecommerce.microcommerce.web.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

//import com.ecommerce.microcommerce.configuration.SwaggerConfig;
import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;

@RestController
public class ProductController {
	
	@Autowired
	private ProductDao productDao;

    @RequestMapping(value="/Produits", method=RequestMethod.GET)
    public MappingJacksonValue listeProduits() {
    	
    	List<Product> produits = productDao.findAll();
        
    	SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAll();

        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);

        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }
    
    
    @RequestMapping(value="/ProduitsFiltres", method=RequestMethod.GET)
    public MappingJacksonValue listeProduitsFiltres() {
        
    	List<Product> produits = productDao.findAll();
        
    	SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");

        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);

        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);

        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }
    
//    @ApiOperation(value = "Récupère un produit")
    @GetMapping(value = "/Produits/{id}")
    public Product afficherUnProduit(@PathVariable int id) {
    	
    	Product produit = productDao.findById(id);
    	
    	if(produit==null) throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. Écran Bleu si je pouvais.");
    	
    	return produit;
    }
    
    @GetMapping(value = "/test/produits/{prixLimit}")
    public List<Product> testeDeRequetes(@PathVariable int prixLimit) {
        return productDao.chercherUnProduitCher(prixLimit);
    }
    
    @DeleteMapping(value = "/Produits")
    public void supprimerProduit(@RequestBody Product product) {
        productDao.delete(product);
    }
    
    @Operation(summary = "Modifier un produit")
    @ApiResponses(value = {
   		@ApiResponse(responseCode = "200", description = "Produit modifié avec succès")    		
    })
    @PutMapping(value = "/Produits")
    public void modifierProduit(@Valid @RequestBody Product product) {
        productDao.save(product);
    }
    
    @PostMapping(value="/Produits")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) {
        Product productAdded =  productDao.save(product);

        if (productAdded == null)
            return ResponseEntity.noContent().build();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }
	
}
