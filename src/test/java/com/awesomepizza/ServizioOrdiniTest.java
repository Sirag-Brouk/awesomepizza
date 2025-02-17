package com.awesomepizza;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServizioOrdiniTest {
    private ServizioOrdini servizioOrdini;

    @BeforeEach
    void setUp() {
        servizioOrdini = new ServizioOrdini();
    }
	//test per controllare che ogni ordine venga aggiunto correttamente
    @Test
    void testAggiungiOrdine() {
        Ordine ordine = servizioOrdini.aggiungiOrdine("Margherita");
        assertNotNull(ordine);
        assertEquals("Margherita", ordine.getPizza());
        assertEquals("In attesa", ordine.getStato());
    }
	//test per controllare che ogni ordine puo esser prepararto solo se nessun'altro e in preparazione
    @Test
    void testPreparaOrdine() {
        Ordine ordine = servizioOrdini.aggiungiOrdine("Margherita");
        Ordine ordinePreparato = servizioOrdini.preparaOrdine(ordine.getId());

        assertNotNull(ordinePreparato);
        assertEquals("In preparazione", ordinePreparato.getStato());
    }

    @Test
    void testNonPermettePiùOrdiniInPreparazione() {
        Ordine ordine1 = servizioOrdini.aggiungiOrdine("Margherita");
        Ordine ordine2 = servizioOrdini.aggiungiOrdine("Diavola");

        servizioOrdini.preparaOrdine(ordine1.getId());
        Ordine risultato = servizioOrdini.preparaOrdine(ordine2.getId());

        assertNull(risultato, "Non dovrebbe permettere di preparare un secondo ordine");
    }
	//test per controllare che ogni ordine in preparazione puo essere completato
    @Test
    void testCompletaOrdine() {
        Ordine ordine = servizioOrdini.aggiungiOrdine("Margherita");
        servizioOrdini.preparaOrdine(ordine.getId());
        Ordine ordineCompletato = servizioOrdini.completaOrdine(ordine.getId());

        assertNotNull(ordineCompletato);
        assertEquals("Completato", ordineCompletato.getStato());
    }
}
