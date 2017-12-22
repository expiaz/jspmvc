package entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Groupe implements BaseEntity {

    @Id
    @GeneratedValue
	private Integer id;

    @Column(unique = true, nullable = false)
	private String nom;

	@OneToMany(mappedBy="groupe", fetch=FetchType.LAZY)	// LAZY = fetch when needed, EAGER = fetch immediately
	private List<Etudiant> etudiants;

	public Groupe(){
	}

	public Groupe(String nom, List<Etudiant> etudiants) {
		this.nom = nom;
		this.etudiants = etudiants;
	}

	@Override
	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public List<Etudiant> getEtudiants() {
		return etudiants;
	}
}
