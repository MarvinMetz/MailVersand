package de.marvinmetz.mailversand;

import jakarta.mail.internet.InternetAddress;

public class Beleg {
	String   belegnummer;
	String   kundennummer;
	String   vorname;
	String   nachname;
	String   anrede;
	InternetAddress   mail;
	String   belegFile;
	
	
	public Beleg(String belegnummer, String kundennummer, String vorname, String nachname, String anrede, String belegFile, InternetAddress mail) {
		this.belegnummer = belegnummer;
		this.kundennummer = kundennummer;
		this.vorname = vorname;
		this.nachname = nachname;
		this.anrede = anrede;
		this.belegFile = belegFile;
		this.mail = mail;
	}

	public String getBelegnummer() {
		return this.belegnummer;
	}

	public String getKundennummer() {
		return this.kundennummer;
	}

	public String getVorname() {
		return this.vorname;
	}

	public String getNachname() {
		return this.nachname;
	}

	public String getAnrede() {
		return this.anrede;
	}

	public String getBelegFile() {
		return this.belegFile;
	}
	
	public InternetAddress getMail() {
		return this.mail;
	}

}
