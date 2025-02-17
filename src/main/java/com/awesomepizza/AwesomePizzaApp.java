package com.awesomepizza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import java.util.concurrent.*;
import java.util.*;

//uso porta 8081 perche la porta 8080 e gia in  uso
@SpringBootApplication
public class AwesomePizzaApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AwesomePizzaApp.class)
            .web(WebApplicationType.SERVLET)
            .properties("server.port=8081")  
            .run(args);
    }
}
//cerazione ordine
@RestController
@RequestMapping("/ordini")
class ControllerOrdini {
    private final ServizioOrdini servizioOrdini;

    public ControllerOrdini(ServizioOrdini servizioOrdini) {
        this.servizioOrdini = servizioOrdini;
    }

    @PostMapping
    public Ordine creaOrdine(@RequestBody RichiestaOrdine richiesta) {
        return servizioOrdini.aggiungiOrdine(richiesta.getPizza());
    }

    @GetMapping("/{id}")
    public Ordine ottieniStatoOrdine(@PathVariable String id) {
        return servizioOrdini.ottieniOrdine(id);
    }

    @GetMapping
    public List<Ordine> ottieniTuttiGliOrdini() {
        return servizioOrdini.ottieniTuttiGliOrdini();  
    }

    @PutMapping("/{id}/preparare")
    public Ordine preparaOrdine(@PathVariable String id) {
        return servizioOrdini.preparaOrdine(id); 
    }

    @PutMapping("/{id}/completare")
    public Ordine completaOrdine(@PathVariable String id) {
        return servizioOrdini.completaOrdine(id); 
    }
}
//tipo di ordine
class Ordine {
    private final String id;
    private final String tipoPizza;
    private String stato;

    public Ordine(String id, String tipoPizza) {
        this.id = id;
        this.tipoPizza = tipoPizza;
        this.stato = "In attesa";
    }

    public String getId() { return id; }
    public String getPizza() { return tipoPizza; }
    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }
}
//tipo richiesta pizza
class RichiestaOrdine {
    private String tipoPizza;
    public String getPizza() { return tipoPizza; }
    public void setTipoPizza(String tipoPizza) { this.tipoPizza = tipoPizza; }
}
//aggiunta della pizza in una lista
@Service
class ServizioOrdini {
    private final ConcurrentLinkedQueue<Ordine> codaOrdini = new ConcurrentLinkedQueue<>();
    private final Map<String, Ordine> mappaOrdini = new ConcurrentHashMap<>();
    private boolean ordineInPreparazione = false;

    public Ordine aggiungiOrdine(String tipoPizza) {
        String id = UUID.randomUUID().toString();
        Ordine ordine = new Ordine(id, tipoPizza);
        codaOrdini.add(ordine);
        mappaOrdini.put(id, ordine);
        return ordine;
    }
	//stampa ordini
    public Ordine ottieniOrdine(String id) {
        return mappaOrdini.get(id);
    }

    public List<Ordine> ottieniTuttiGliOrdini() {
        return new ArrayList<>(mappaOrdini.values());
    }
	//preppara ordini
	public Ordine preparaOrdine(String id) {
		Ordine ordine = mappaOrdini.get(id);
		
		 synchronized (this) {
			if (ordineInPreparazione) {
				return null; 
			}
			
			if ("In attesa".equals(ordine.getStato())) {
				 ordine.setStato("In preparazione");
				ordineInPreparazione = true;
			} else {
				return null;
			}
		}
		return ordine;  
	}
	//completa ordini
	public Ordine completaOrdine(String id) {
		Ordine ordine = mappaOrdini.get(id);
		synchronized (this){
			if ("In preparazione".equals(ordine.getStato())) {
				ordine.setStato("Completato");
				ordineInPreparazione = false;
				codaOrdini.poll();
			} else {
				return null;
			}
		}
		return ordine; 
	}

}
