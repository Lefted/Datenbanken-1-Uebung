package de.hska.iwii.db1.jpa.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(schema = "g06", name = "kunde")
public class Kunde {

	@Id
	@NotNull
	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "vorname", nullable = false)
	@NotNull
	private String vorname;

	@Column(name = "nachname", nullable = false)
	@NotNull
	private String nachname;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	@Override
	public String toString() {
		return "Kunde [email=" + email + ", vorname=" + vorname + ", nachname=" + nachname + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, nachname, vorname);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Kunde other = (Kunde) obj;
		return Objects.equals(email, other.email) && Objects.equals(nachname, other.nachname)
				&& Objects.equals(vorname, other.vorname);
	}
}
