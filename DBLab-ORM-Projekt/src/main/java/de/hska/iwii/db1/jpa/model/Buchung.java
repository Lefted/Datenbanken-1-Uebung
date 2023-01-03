package de.hska.iwii.db1.jpa.model;

import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(schema = "g06", name = "buchung")
public class Buchung {

	@Id
	@NotNull
	@Column(name = "id", nullable = false)
	private int id;
	
	@NotNull
	@Min(1)
	@Column(name = "anzahl_gebuchter_plaetze", nullable = false)
	private int anzahlGebuchterPlaetze;
	
	@NotNull
	@Column(name = "buchungs_datum", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date buchungsdatum;
	
	@ManyToOne
	@JoinColumn(name = "kunde_email")
	private Kunde kunde;
	
	@ManyToOne
	@JoinColumn(name = "flug_id")
	private Flug flug;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAnzahlGebuchterPlaetze() {
		return anzahlGebuchterPlaetze;
	}

	public void setAnzahlGebuchterPlaetze(int anzahlGebuchterPlaetze) {
		this.anzahlGebuchterPlaetze = anzahlGebuchterPlaetze;
	}

	public Date getBuchungsdatum() {
		return buchungsdatum;
	}

	public void setBuchungsdatum(Date buchungsdatum) {
		this.buchungsdatum = buchungsdatum;
	}

	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public Flug getFlug() {
		return flug;
	}

	public void setFlug(Flug flug) {
		this.flug = flug;
	}

	@Override
	public String toString() {
		return "Buchung [id=" + id + ", anzahlGebuchterPlaetze=" + anzahlGebuchterPlaetze + ", buchungsdatum="
				+ buchungsdatum + ", kunde=" + kunde + ", flug=" + flug + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(anzahlGebuchterPlaetze, buchungsdatum, flug, id, kunde);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Buchung other = (Buchung) obj;
		return anzahlGebuchterPlaetze == other.anzahlGebuchterPlaetze
				&& Objects.equals(buchungsdatum, other.buchungsdatum) && Objects.equals(flug, other.flug)
				&& id == other.id && Objects.equals(kunde, other.kunde);
	}
}
