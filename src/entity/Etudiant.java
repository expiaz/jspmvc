package entity;

import javax.persistence.*;

@Entity
public class Etudiant implements BaseEntity {

    @Id
    @GeneratedValue
	private Integer id;

    @Column(nullable = false)
	private String prenom;

    @Column(nullable = false)
	private String nom;

    @Column
    private int absences;

    @ManyToOne
    private Groupe groupe;

	public Etudiant(){

	}
	
	public Etudiant(String nom, String prenom) {
		this.prenom = prenom;
		this.nom = nom;
	}

	@Override
	public int getId() {
		return id;
	}

    @Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

    public int getAbsences() {
        return absences;
    }

    public void setAbsences(int absences) {
        this.absences = absences;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public void setGroupe(Groupe groupe) {
	    this.groupe.getEtudiants().remove(this);
        this.groupe = groupe;
        if (!this.groupe.getEtudiants().contains(this)) {
            this.groupe.getEtudiants().add(this);
        }
    }
}
