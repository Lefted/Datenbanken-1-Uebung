package de.hska.iwii.db1.jpa.model;

import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(schema = "g06", name = "flug")
public class Flug {

	@Id
	@NotNull
	@Column(name = "nummer", nullable = false)
	private String nummer;

	@Temporal(TemporalType.TIME)
	@Column(name = "startzeit")
	@NotNull
	private Date startzeit;

	@Column(name = "start_flughafen", nullable = false)
	@NotNull
	private String startFlughafen;

	public String getNummer() {
		return nummer;
	}

	public void setNummer(String nummer) {
		this.nummer = nummer;
	}

	public Date getStartzeit() {
		return startzeit;
	}

	public void setStartzeit(Date startzeit) {
		this.startzeit = startzeit;
	}

	public String getStartFlughafen() {
		return startFlughafen;
	}

	public void setStartFlughafen(String startFlughafen) {
		this.startFlughafen = startFlughafen;
	}

	@Override
	public String toString() {
		return "Flug [nummer=" + nummer + ", startzeit=" + startzeit + ", startFlughafen=" + startFlughafen + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(nummer, startFlughafen, startzeit);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Flug other = (Flug) obj;
		return Objects.equals(nummer, other.nummer) && Objects.equals(startFlughafen, other.startFlughafen)
				&& Objects.equals(startzeit, other.startzeit);
	}
}
