package de.hska.iwii.db1.jpa;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hska.iwii.db1.jpa.bootstrap.OracleConnectionWrapper;
import de.hska.iwii.db1.jpa.model.Buchung;
import de.hska.iwii.db1.jpa.model.Flug;
import de.hska.iwii.db1.jpa.model.Kunde;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAApplication {
	private EntityManagerFactory entityManagerFactory;

	public JPAApplication() {
		Logger.getLogger("org.hibernate").setLevel(Level.ALL);
		entityManagerFactory = Persistence.createEntityManagerFactory("DB1");
	}

	public void testFlights() {
		EntityManager em = entityManagerFactory.createEntityManager();
		
		em.getTransaction().begin();
		
		Kunde kunde1 = new Kunde();
		kunde1.setEmail("maier.tomas@hka.de");
		kunde1.setNachname("maier");
		kunde1.setVorname("tomas");
		
		Kunde kunde2 = new Kunde();
		kunde2.setEmail("lang.fred@hka.de");
		kunde2.setNachname("lang");
		kunde2.setVorname("fred");
		
		Flug flug1 = new Flug();
		flug1.setNummer("A");
		flug1.setStartFlughafen("Berlin");
		flug1.setStartzeit(new Date());
		
		Flug flug2 = new Flug();
		flug2.setNummer("B");
		flug2.setStartFlughafen("Berlin");
		flug2.setStartzeit(new Date());

		Flug flug3 = new Flug();
		flug3.setNummer("C");
		flug3.setStartFlughafen("Dortmund");
		flug3.setStartzeit(new Date());
		
		Buchung buchung1 = new Buchung();
		buchung1.setId(0);
		buchung1.setAnzahlGebuchterPlaetze(2);
		buchung1.setBuchungsdatum(new Date());
		buchung1.setFlug(flug1);
		buchung1.setKunde(kunde1);
		
		Buchung buchung2 = new Buchung();
		buchung2.setId(1);
		buchung2.setAnzahlGebuchterPlaetze(2);
		buchung2.setBuchungsdatum(new Date());
		buchung2.setFlug(flug2);
		buchung2.setKunde(kunde2);
		
		em.persist(flug1);
		em.persist(flug2);
		em.persist(flug3);
		em.persist(kunde1);
		em.persist(kunde2);
		em.persist(buchung1);
		em.persist(buchung2);

		
		em.getTransaction().commit();
		
        em.close();
        entityManagerFactory.close();
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public static void main(String[] args) {
		OracleConnectionWrapper instance = OracleConnectionWrapper.getInstance();
		try {
			instance.establishSSHTunnel();
			JPAApplication app = new JPAApplication();
			app.testFlights();
		} finally {
//			instance.closeOracleSSHTunnel();
		}
	}
}
